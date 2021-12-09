import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionAreaConocimiento } from '@core/models/pii/invencion-area-conocimiento';
import { IInvencionDocumento } from '@core/models/pii/invencion-documento';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { IInvencionPalabraClave } from '@core/models/pii/invencion-palabra-clave';
import { IInvencionSectorAplicacion } from '@core/models/pii/invencion-sector-aplicacion';
import { IPeriodoTitularidad } from '@core/models/pii/periodo-titularidad';
import { IPeriodoTitularidadTitular } from '@core/models/pii/periodo-titularidad-titular';
import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IAreaConocimiento } from '@core/models/sgo/area-conocimiento';
import { FormFragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { InvencionDocumentoService } from '@core/services/pii/invencion/invencion-documento/invencion-documento.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { PeriodoTitularidadService } from '@core/services/pii/invencion/periodo-titularidad/periodo-titularidad.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, forkJoin, from, Observable, of, Subject } from 'rxjs';
import { catchError, map, mergeMap, switchMap, take, takeLast, tap } from 'rxjs/operators';

export interface IInvencionAreaConocimientoListado extends IInvencionAreaConocimiento {
  niveles: IAreaConocimiento[];
  nivelesTexto: string;
  nivelSeleccionado: IAreaConocimiento;
}

interface IInvencionDatosGeneralesFragmentStatus {
  hasChangesSectoresAplicacion: boolean;
  hasChangesAreasConocimiento: boolean;
  hasChangesDocumentos?: boolean;
}

const CREATE_FRAGMENT_STATUS_INITIAL_DATA: IInvencionDatosGeneralesFragmentStatus = {
  hasChangesSectoresAplicacion: false,
  hasChangesAreasConocimiento: false,
  hasChangesDocumentos: false,
} as const;

const EDIT_FRAGMENT_STATUS_INITIAL_DATA: IInvencionDatosGeneralesFragmentStatus = {
  hasChangesSectoresAplicacion: false,
  hasChangesAreasConocimiento: false,
} as const;

export class InvencionDatosGeneralesFragment extends FormFragment<IInvencion> {

  private sectoresAplicacion$ = new BehaviorSubject<StatusWrapper<IInvencionSectorAplicacion>[]>([]);
  private areasConocimiento$ = new BehaviorSubject<StatusWrapper<IInvencionAreaConocimientoListado>[]>([]);
  private documentos$ = new BehaviorSubject<StatusWrapper<IInvencionDocumento>[]>([]);
  private invencion: IInvencion;
  private fragmentStatus: IInvencionDatosGeneralesFragmentStatus;
  public inventores: IInvencionInventor[];

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly invencionService: InvencionService,
    private readonly proyectoService: ProyectoService,
    private readonly areaConocimientoService: AreaConocimientoService,
    private readonly invencionDocumentoService: InvencionDocumentoService,
    private readonly isEditPerm: boolean,
    private readonly periodoTitularidadService: PeriodoTitularidadService,
    private readonly palabraClaveService: PalabraClaveService
  ) {
    super(key, true);
    this.invencion = {} as IInvencion;
    this.invencion.activo = true;
    this.fragmentStatus = this.isEdit() ? EDIT_FRAGMENT_STATUS_INITIAL_DATA : CREATE_FRAGMENT_STATUS_INITIAL_DATA;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      id: new FormControl({ value: '', disabled: true }),
      titulo: new FormControl('', [Validators.maxLength(250)]),
      fechaComunicacion: new FormControl(DateTime.now()),
      descripcion: new FormControl('', [Validators.maxLength(2000)]),
      tipoProteccion: new FormControl(null, [Validators.required]),
      subtipoProteccion: new FormControl(null),
      proyecto: new FormControl(null),
      comentarios: new FormControl('', [Validators.maxLength(2000)]),
      palabrasClave: new FormControl(null)
    });

    if (!this.hasEditPerm()) {
      form.disable();
    }

    return form;
  }

  protected buildPatch(invencion: IInvencion): { [key: string]: any; } {
    const result = {
      id: invencion.id,
      titulo: invencion.titulo,
      fechaComunicacion: invencion.fechaComunicacion,
      descripcion: invencion.descripcion,
      tipoProteccion: invencion.tipoProteccion.padre ?? invencion.tipoProteccion,
      subtipoProteccion: invencion.tipoProteccion.padre !== null ? invencion.tipoProteccion : null,
      proyecto: invencion.proyecto,
      comentarios: invencion.comentarios
    };
    this.invencion = invencion;

    return result;
  }

  protected initializer(key: number): Observable<IInvencion> {

    return forkJoin({
      invencion: this.invencionService.findById(key),
      sectoresAplicacion: this.loadSectoresAplicacion(key),
      areasConocimiento: this.loadAreasConocimiento(key),
      palabrasClave: this.invencionService.findPalabrasClave(key).pipe(
        map(({ items }) => items.map(invencionPalabraClave => invencionPalabraClave.palabraClave)))
    }).pipe(
      tap(({ palabrasClave }) => this.getFormGroup().controls.palabrasClave.setValue(palabrasClave)),
      tap(({ sectoresAplicacion }) => this.sectoresAplicacion$.next(sectoresAplicacion)),
      tap(({ areasConocimiento }) => this.areasConocimiento$.next(areasConocimiento)),
      switchMap(({ invencion }) => {
        if (invencion.proyecto?.id) {
          return this.proyectoService.findById(invencion.proyecto.id).pipe(
            map(proyecto =>
              ({ ...invencion, proyecto: proyecto })
            ));
        } else {
          return of(invencion);
        }
      })
    );
  }

  private loadSectoresAplicacion(invencionId: number): Observable<StatusWrapper<IInvencionSectorAplicacion>[]> {
    return this.invencionService.findSectoresAplicacion(invencionId).pipe(
      map(invencionSectoresAplicacion => invencionSectoresAplicacion.map(
        invencionSectorAplicacion => new StatusWrapper<IInvencionSectorAplicacion>(invencionSectorAplicacion))
      )
    );
  }

  private loadAreasConocimiento(invencionId: number): Observable<StatusWrapper<IInvencionAreaConocimientoListado>[]> {
    let areasConocimientoListado: StatusWrapper<IInvencionAreaConocimientoListado>[] = [];
    const areasConocimientoListado$ = new Subject<StatusWrapper<IInvencionAreaConocimientoListado>[]>();
    this.subscriptions.push(this.invencionService.findAreasConocimiento(invencionId).pipe(
      map(response => response.map(invencionAreaConocimiento => {
        const invencionAreaConocimientoListado: IInvencionAreaConocimientoListado = {
          id: invencionAreaConocimiento.id,
          invencion: invencionAreaConocimiento.invencion,
          areaConocimiento: invencionAreaConocimiento.areaConocimiento,
          nivelSeleccionado: invencionAreaConocimiento.areaConocimiento,
          niveles: undefined,
          nivelesTexto: ''
        };
        return invencionAreaConocimientoListado;
      })),
      switchMap((result) => {
        return from(result).pipe(
          mergeMap((invencionAreaConocimientoListado) => {
            return this.areaConocimientoService.findById(invencionAreaConocimientoListado.nivelSeleccionado.id).pipe(
              map((areaConocimiento) => {
                invencionAreaConocimientoListado.nivelSeleccionado = areaConocimiento;
                invencionAreaConocimientoListado.niveles = [areaConocimiento];
              }),
              switchMap(() => {
                return this.getNiveles(invencionAreaConocimientoListado);
              })
            );
          })
        );
      })
    ).subscribe((proyectoAreaConocimiento) => {
      proyectoAreaConocimiento.nivelesTexto = proyectoAreaConocimiento.niveles
        .slice(1, proyectoAreaConocimiento.niveles.length)
        .reverse()
        .map(area => area.nombre).join(' - ');
      areasConocimientoListado.push(new StatusWrapper<IInvencionAreaConocimientoListado>(proyectoAreaConocimiento));
    },
      err => { },
      () => {
        areasConocimientoListado$.next(areasConocimientoListado);
      }));

    return areasConocimientoListado$.pipe(take(1));
  }

  private getNiveles(invencionAreaConocimientoListado: IInvencionAreaConocimientoListado):
    Observable<IInvencionAreaConocimientoListado> {
    const lastLevel = invencionAreaConocimientoListado.niveles[invencionAreaConocimientoListado.niveles.length - 1];
    if (!lastLevel.padreId) {
      return of(invencionAreaConocimientoListado);
    }

    return this.areaConocimientoService.findById(lastLevel.padreId).pipe(
      switchMap(area => {
        invencionAreaConocimientoListado.niveles.push(area);
        return this.getNiveles(invencionAreaConocimientoListado);
      })
    );
  }

  getValue(): IInvencion {
    const form = this.getFormGroup().value;
    const invencion = this.invencion;
    invencion.titulo = form.titulo;
    invencion.fechaComunicacion = form.fechaComunicacion;
    invencion.descripcion = form.descripcion;
    invencion.tipoProteccion = form.subtipoProteccion ?? form.tipoProteccion;
    invencion.proyecto = form.proyecto;
    invencion.comentarios = form.comentarios;

    return invencion;
  }

  hasEditPerm(): boolean {
    return this.isEditPerm;
  }

  getSectoresAplicacion$(): Observable<StatusWrapper<IInvencionSectorAplicacion>[]> {
    return this.sectoresAplicacion$.asObservable();
  }

  getAreasConocimiento$(): Observable<StatusWrapper<IInvencionAreaConocimientoListado>[]> {
    return this.areasConocimiento$.asObservable();
  }

  getDocumentos$(): Observable<StatusWrapper<IInvencionDocumento>[]> {
    return this.documentos$.asObservable();
  }

  saveOrUpdate(): Observable<string | number | void> {
    const invencion = this.getValue();
    const observable$ = this.isEdit() ? this.update(invencion) : this.create(invencion);

    return observable$.pipe(
      map((result: IInvencion) => {
        this.invencion = result;
        return result?.id;
      })
    );
  }

  private create(invencion: IInvencion): Observable<IInvencion> {
    let cascade = of(void 0);
    if (this.formGroupStatus$.value.changes) {
      cascade = cascade.pipe(
        switchMap(() => this.createInvencion(invencion))
      );
    }

    if (this.hasChangesInvecionDatosGeneralesFragmentPart('hasChangesSectoresAplicacion')) {
      cascade = cascade.pipe(
        mergeMap((createdInvencion: IInvencion) => this.saveOrUpdateSectoresAplicacion(createdInvencion))
      );
    }

    if (this.hasChangesInvecionDatosGeneralesFragmentPart('hasChangesAreasConocimiento')) {
      cascade = cascade.pipe(
        mergeMap((createdInvencion: IInvencion) => this.saveOrUpdateAreaConocimiento(createdInvencion))
      );
    }

    if (this.hasChangesInvecionDatosGeneralesFragmentPart('hasChangesDocumentos')) {
      cascade = cascade.pipe(
        mergeMap((createdInvencion: IInvencion) => this.saveDocumento(createdInvencion))
      );
    }

    cascade = cascade.pipe(
      mergeMap((createdInvencion: IInvencion) => this.persistPeriodoTitularidad(createdInvencion))
    );

    if (this.getFormGroup().controls.palabrasClave.dirty) {
      cascade = cascade.pipe(
        mergeMap((createdInvencion: IInvencion) => this.saveOrUpdatePalabrasClave(createdInvencion))
      );
    }

    return cascade;
  }

  private update(invencion: IInvencion): Observable<IInvencion> {
    let cascade = of(void 0);
    if (this.formGroupStatus$.value.changes) {
      cascade = cascade.pipe(
        switchMap(() => this.updateInvencion(invencion))
      );
    } else {
      cascade = cascade.pipe(
        switchMap(() => of(invencion))
      );
    }

    if (this.hasChangesInvecionDatosGeneralesFragmentPart('hasChangesSectoresAplicacion')) {
      cascade = cascade.pipe(
        mergeMap((updatedInvencion: IInvencion) => this.saveOrUpdateSectoresAplicacion(updatedInvencion))
      );
    }

    if (this.hasChangesInvecionDatosGeneralesFragmentPart('hasChangesAreasConocimiento')) {
      cascade = cascade.pipe(
        mergeMap((updatedInvencion: IInvencion) => this.saveOrUpdateAreaConocimiento(updatedInvencion))
      );
    }

    if (this.getFormGroup().controls.palabrasClave.dirty) {
      cascade = cascade.pipe(
        mergeMap((updatedInvencion: IInvencion) => this.saveOrUpdatePalabrasClave(updatedInvencion))
      );
    }

    return cascade;
  }

  private createInvencion(invencion: IInvencion): Observable<IInvencion> {
    return this.invencionService.create(invencion).pipe(
      tap((result: IInvencion) => {
        this.invencion = result;
        this.refreshInitialState(true);
        this.setChangesInvencionDatosGeneralesFragment();
      })
    );
  }

  private persistPeriodoTitularidad(invencion: IInvencion): Observable<IInvencion> {
    const areAllTitularesPropiosSinEntidadAdicional = this.inventores.every(
      ({ inventor }) => !(!!inventor.entidad) && inventor.personalPropio && !!inventor.entidadPropia);
    // Sólo se genera periodo titularidad automáticamente si
    // todos los inventores pertenecen a la universidad y no están asociados a ninguna otra empresa
    if (areAllTitularesPropiosSinEntidadAdicional) {
      return this.periodoTitularidadService.create(this.buildPeriodoTitularidadAtNow(invencion)).pipe(
        switchMap(newPeriodoTitularidad => {
          return this.createPeriodosTitularidadTitulares(newPeriodoTitularidad);
        })
      );
    }

    return of(invencion);
  }

  private buildPeriodoTitularidadAtNow(invencion: IInvencion): IPeriodoTitularidad {
    return {
      fechaInicio: DateTime.now(),
      invencion: invencion,
    } as IPeriodoTitularidad;
  }

  private createPeriodosTitularidadTitulares(periodoTitularidad: IPeriodoTitularidad): Observable<IInvencion> {

    const titulares: IPeriodoTitularidadTitular[] = [];
    for (let [key, value] of this.groupInventoresForEntities()) {
      titulares.push(this.buildPeriodoTitularidadTitular(value, key, periodoTitularidad))
    }

    if (titulares.length === 0) {
      return of(periodoTitularidad.invencion);
    }
    return this.persistsPeriodosTitularidadTitulares(titulares, periodoTitularidad);
  }

  private persistsPeriodosTitularidadTitulares(
    titulares: IPeriodoTitularidadTitular[], newPeriodoTitularidad: IPeriodoTitularidad
  ): Observable<IInvencion> {
    if (!titulares) {
      return EMPTY;
    }
    return this.periodoTitularidadService.bulkSaveOrUpdatePeriodoTitularidadTitulares(newPeriodoTitularidad.id, titulares).pipe(
      map(result => newPeriodoTitularidad.invencion),
      catchError(error => {
        this.logger.error(error);
        return of(error);
      })
    );
  }

  private buildPeriodoTitularidadTitular(
    participacionPct: number, entidadRef: string, periodoTitularidad: IPeriodoTitularidad
  ): IPeriodoTitularidadTitular {

    return {
      participacion: participacionPct,
      titular: { id: entidadRef } as IEmpresa,
      periodoTitularidad
    } as IPeriodoTitularidadTitular;
  }

  private groupInventoresForEntities(): Map<string, number> {

    const titularesMap = new Map<string, number>();

    this.inventores.forEach(inventor => {
      const currentPercent = titularesMap.get(inventor.inventor.entidadPropia.id);
      if (currentPercent) {
        titularesMap.set(inventor.inventor.entidadPropia.id, inventor.participacion + currentPercent);
      } else {
        titularesMap.set(inventor.inventor.entidadPropia.id, inventor.participacion);
      }
    });

    return titularesMap;
  }

  private updateInvencion(invencion: IInvencion): Observable<IInvencion> {
    return this.invencionService.update(Number(this.getKey()), invencion).pipe(
      tap((result: IInvencion) => {
        this.invencion = result;
      })
    );
  }

  private saveOrUpdateSectoresAplicacion(invencion: IInvencion): Observable<IInvencion> {
    const invencionSectoresAplicacionToSave = this.sectoresAplicacion$.value.map(wrapper => {
      wrapper.value.invencion = invencion;
      return wrapper.value;
    }
    );

    return this.invencionService.updateSectoresAplicacion(invencion.id, invencionSectoresAplicacionToSave)
      .pipe(
        takeLast(1),
        tap(() => this.setChangesInvencionDatosGeneralesFragment({ hasChangesSectoresAplicacion: false })),
        tap((invencionSectoresAplicacionSaved) => {
          this.sectoresAplicacion$.next(
            invencionSectoresAplicacionSaved.map(invencionSectorAplicacionSaved => {
              this.copyInvencionSectorAplicacionRelatedAttributes(
                this.getSourceInvencionSectorAplicacion(invencionSectoresAplicacionToSave, invencionSectorAplicacionSaved),
                invencionSectorAplicacionSaved
              );
              return new StatusWrapper<IInvencionSectorAplicacion>(invencionSectorAplicacionSaved);
            }));
        }),
        switchMap(() => of(invencion))
      );
  }

  private getSourceInvencionSectorAplicacion(
    invencionSectoresAplicacionToSave: IInvencionSectorAplicacion[],
    invencionSectorAplicacionSaved: IInvencionSectorAplicacion
  ): IInvencionSectorAplicacion | undefined {
    return invencionSectoresAplicacionToSave.find(
      invencionSectorAplicacionToSave =>
        invencionSectorAplicacionToSave.sectorAplicacion.id === invencionSectorAplicacionSaved.sectorAplicacion.id
    );
  }

  private copyInvencionSectorAplicacionRelatedAttributes(
    source: IInvencionSectorAplicacion | undefined,
    target: IInvencionSectorAplicacion
  ): void {
    if (source) {
      target.invencion = source.invencion;
      target.sectorAplicacion = source.sectorAplicacion;
    }
  }

  private saveOrUpdateAreaConocimiento(invencion: IInvencion): Observable<IInvencion> {
    const values = this.areasConocimiento$.value.map(wrapper => {
      wrapper.value.invencion = invencion;
      return wrapper.value;
    }
    );

    return this.invencionService.updateAreasConocimiento(invencion.id, values)
      .pipe(
        takeLast(1),
        tap(() => this.setChangesInvencionDatosGeneralesFragment({ hasChangesAreasConocimiento: false })),
        map((invencionAreasConocimiento) => {
          const newAreasConocimientoListado: IInvencionAreaConocimientoListado[] = [];
          invencionAreasConocimiento.forEach(invencionAreaConocimiento => {
            const areaConocimiento = this.areasConocimiento$.value.find(areaConocimiento =>
              areaConocimiento.value.areaConocimiento.id === invencionAreaConocimiento.areaConocimiento.id
            );
            if (areaConocimiento) {
              newAreasConocimientoListado.push(
                {
                  id: invencionAreaConocimiento.id,
                  invencion: invencionAreaConocimiento.invencion,
                  areaConocimiento: areaConocimiento.value.areaConocimiento,
                  nivelSeleccionado: areaConocimiento.value.nivelSeleccionado,
                  niveles: areaConocimiento.value.niveles,
                  nivelesTexto: areaConocimiento.value.nivelesTexto
                }
              );
            }
          });
          this.areasConocimiento$.next(
            newAreasConocimientoListado.map(value => new StatusWrapper<IInvencionAreaConocimientoListado>(value)));
        }),
        switchMap(() => of(invencion))
      );
  }

  private saveDocumento(invencion: IInvencion): Observable<IInvencion> {
    const values = this.documentos$.value.map(wrapper => {
      wrapper.value.invencionId = invencion.id;
      return wrapper.value;
    }
    );

    return from(values)
      .pipe(
        mergeMap(
          (invencionDocumento) => this.invencionDocumentoService.create(invencionDocumento)
            .pipe(
              catchError(() => of(void 0))
            )
        ),
        takeLast(1),
        tap(() => this.setChangesInvencionDatosGeneralesFragment({ hasChangesSectoresAplicacion: false })),
        switchMap(() => of(invencion))
      );
  }

  private saveOrUpdatePalabrasClave(invencion: IInvencion): Observable<IInvencion> {
    const palabrasClave = this.getFormGroup().controls.palabrasClave.value ?? [];
    const invencionPalabrasClave: IInvencionPalabraClave[] = palabrasClave.map(palabraClave => ({
      invencion,
      palabraClave
    } as IInvencionPalabraClave));
    return this.palabraClaveService.update(palabrasClave).pipe(
      mergeMap(() => this.invencionService.updatePalabrasClave(invencion.id, invencionPalabrasClave)),
      map(() => invencion)
    );
  }

  addSectorAplicacion(sectorAplicacion: ISectorAplicacion): void {
    if (sectorAplicacion) {
      const invencionSectorAplicacion = {
        sectorAplicacion
      } as IInvencionSectorAplicacion;
      const wrapped = new StatusWrapper<IInvencionSectorAplicacion>(invencionSectorAplicacion);
      wrapped.setCreated();
      const current = this.sectoresAplicacion$.value;
      current.push(wrapped);
      this.sectoresAplicacion$.next(current);
      this.checkComplete();
      this.setChangesInvencionDatosGeneralesFragment({ hasChangesSectoresAplicacion: true });
    }
  }

  deleteSectorAplicacion(wrapper: StatusWrapper<IInvencionSectorAplicacion>): void {
    const current = this.sectoresAplicacion$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      current.splice(index, 1);
      this.sectoresAplicacion$.next(current);
      if (!current.length) {
        this.checkComplete();
      }
      this.setChangesInvencionDatosGeneralesFragment({ hasChangesSectoresAplicacion: true });
    }
  }

  addAreaConocimiento(invencionAreasConocimientoListado: IInvencionAreaConocimientoListado[]): void {
    const wrappedList = invencionAreasConocimientoListado.map(invencionAreaConocimientoListado => {
      invencionAreaConocimientoListado.invencion = this.invencion;
      invencionAreaConocimientoListado.areaConocimiento = invencionAreaConocimientoListado.nivelSeleccionado;
      const wrapped = new StatusWrapper<IInvencionAreaConocimientoListado>(invencionAreaConocimientoListado);
      wrapped.setCreated();
      return wrapped;
    })

    const current = this.areasConocimiento$.value;
    current.push(...wrappedList);
    this.areasConocimiento$.next(current);
    this.checkComplete();
    this.setChangesInvencionDatosGeneralesFragment({ hasChangesAreasConocimiento: true });
  }

  deleteAreaConocimiento(wrapper: StatusWrapper<IInvencionAreaConocimientoListado>): void {
    const current = this.areasConocimiento$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      current.splice(index, 1);
      this.areasConocimiento$.next(current);
      if (!current.length) {
        this.checkComplete();
      }
      this.setChangesInvencionDatosGeneralesFragment({ hasChangesAreasConocimiento: true });
    }
  }

  addDocumento(invencionDocumento: IInvencionDocumento): void {
    const wrapped = new StatusWrapper<IInvencionDocumento>(invencionDocumento);
    wrapped.setCreated();
    const current = this.documentos$.value;
    current.push(wrapped);
    this.documentos$.next(current);
    this.checkComplete();
    this.setChangesInvencionDatosGeneralesFragment({ hasChangesDocumentos: true });
  }

  deleteDocumento(wrapper: StatusWrapper<IInvencionDocumento>): void {
    const current = this.documentos$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      current.splice(index, 1);
      this.documentos$.next(current);
      if (!current.length) {
        this.checkComplete();
      }
      this.setChangesInvencionDatosGeneralesFragment({ hasChangesSectoresAplicacion: true });
    }
  }

  private setChangesInvencionDatosGeneralesFragment(status: Partial<IInvencionDatosGeneralesFragmentStatus> = {}): void {
    this.fragmentStatus = { ...this.fragmentStatus, ...status };
    this.setChanges(this.hasChangesInvecionDatosGeneralesFragment());
  }

  private hasChangesInvecionDatosGeneralesFragment(): boolean {
    return Object.values(this.fragmentStatus).includes(true);
  }

  private hasChangesInvecionDatosGeneralesFragmentPart(key: keyof IInvencionDatosGeneralesFragmentStatus): boolean {
    return this.fragmentStatus[key];
  }

  private checkComplete(): void {
    const isComplete = this.hasInvencionRequiredElemenmts();
    this.setComplete(isComplete);
    this.setErrors(!isComplete);
  }

  private hasInvencionRequiredElemenmts(): boolean {
    return this.isEdit() ? this.hasInvencionEditingRequiredElements() : this.hasInvencionCreationRequiredElements();
  }

  private hasInvencionCreationRequiredElements(): boolean {
    return this.sectoresAplicacion$.value.length > 0 && this.areasConocimiento$.value.length > 0 && this.documentos$.value.length > 0;
  }

  private hasInvencionEditingRequiredElements(): boolean {
    return this.sectoresAplicacion$.value.length > 0 && this.areasConocimiento$.value.length > 0;
  }

}
