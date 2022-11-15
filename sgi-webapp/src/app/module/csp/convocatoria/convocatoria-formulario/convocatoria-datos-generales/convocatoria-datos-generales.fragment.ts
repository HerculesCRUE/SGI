import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { Estado, IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { IConvocatoriaEntidadGestora } from '@core/models/csp/convocatoria-entidad-gestora';
import { IConvocatoriaPalabraClave } from '@core/models/csp/convocatoria-palabra-clave';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FormFragment } from '@core/services/action-service';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { ConvocatoriaAreaTematicaService } from '@core/services/csp/convocatoria-area-tematica.service';
import { ConvocatoriaEntidadGestoraService } from '@core/services/csp/convocatoria-entidad-gestora.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, from, merge, Observable, of, Subject } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface AreaTematicaData {
  padre: IAreaTematica;
  convocatoriaAreaTematica: StatusWrapper<IConvocatoriaAreaTematica>;
}

export class ConvocatoriaDatosGeneralesFragment extends FormFragment<IConvocatoria> {
  readonly areasTematicas$ = new BehaviorSubject<AreaTematicaData[]>([]);
  private convocatoriaAreaTematicaEliminadas: StatusWrapper<IConvocatoriaAreaTematica>[] = [];

  private convocatoria: IConvocatoria;
  private convocatoriaEntidadGestora: IConvocatoriaEntidadGestora;

  public showAddAreaTematica: boolean;
  public hasProyectoVinculado$: Subject<boolean> = new BehaviorSubject<boolean>(false);

  readonly modeloEjecucion$: Subject<IModeloEjecucion> = new BehaviorSubject<IModeloEjecucion>(null);
  readonly vinculacionesModeloEjecucion$: Subject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private proyectoService: ProyectoService,
    private empresaService: EmpresaService,
    private convocatoriaEntidadGestoraService: ConvocatoriaEntidadGestoraService,
    private unidadGestionService: UnidadGestionService,
    private convocatoriaAreaTematicaService: ConvocatoriaAreaTematicaService,
    private configuracionSolicitudService: ConfiguracionSolicitudService,
    public isConvocatoriaVinculada: boolean,
    public hasEditPerm: boolean,
    private readonly palabraClaveService: PalabraClaveService
  ) {
    super(key, true);
    this.setComplete(true);
    this.convocatoria = {
      activo: true,
      estado: Estado.BORRADOR
    } as IConvocatoria;
    this.checkIfAddAreaTematicaIsAllowed();
    this.convocatoriaEntidadGestora = {} as IConvocatoriaEntidadGestora;
    this.checkHasProyectoVinculado(key);
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      estado: new FormControl({ value: null, disabled: true }),
      codigo: new FormControl(null, Validators.maxLength(50)),
      unidadGestion: new FormControl(null, Validators.required),
      fechaPublicacion: new FormControl(null),
      fechaProvisional: new FormControl(null),
      fechaConcesion: new FormControl(null),
      titulo: new FormControl('', [Validators.required, Validators.maxLength(1000)]),
      modeloEjecucion: new FormControl(null),
      finalidad: new FormControl(null),
      duracion: new FormControl('', [Validators.min(1), Validators.max(9999)]),
      excelencia: new FormControl(null),
      ambitoGeografico: new FormControl(null),
      formularioSolicitud: new FormControl(FormularioSolicitud.PROYECTO),
      clasificacionCVN: new FormControl(null),
      regimenConcurrencia: new FormControl(null),
      entidadGestora: new FormControl(null),
      objeto: new FormControl('', Validators.maxLength(2000)),
      observaciones: new FormControl('', Validators.maxLength(2000)),
      palabrasClave: new FormControl(null)
    });

    if (!this.hasEditPerm) {
      form.disable();
    } else if (this.isConvocatoriaVinculada) {
      form.controls.unidadGestion.disable();
      form.controls.modeloEjecucion.disable();
      form.controls.entidadGestora.disable();
      form.controls.duracion.disable();
      form.controls.formularioSolicitud.disable();
    }

    this.subscriptions.push(
      form.controls.modeloEjecucion.valueChanges.subscribe((value) => {
        this.modeloEjecucion$.next(value);
      })
    );

    if (!this.isConvocatoriaVinculada) {
      this.subscriptions.push(
        this.vinculacionesModeloEjecucion$.subscribe(
          value => {
            if (value || !this.hasEditPerm) {
              form.controls.unidadGestion.disable();
              form.controls.modeloEjecucion.disable();
            }
            else if (this.hasEditPerm) {
              form.controls.unidadGestion.enable();
              form.controls.modeloEjecucion.enable();
            }
          }
        )
      );
    }

    this.subscriptions.push(
      this.hasProyectoVinculado$.subscribe(
        value => {
          if (value && this.isConvocatoriaVinculada) {
            form.controls.regimenConcurrencia.disable();
            form.controls.clasificacionCVN.disable();
          } else if (this.hasEditPerm) {
            form.controls.regimenConcurrencia.enable();
            form.controls.clasificacionCVN.enable();
          }
        }
      )
    );

    this.checkEstado(form, this.convocatoria);
    return form;
  }

  buildPatch(convocatoria: IConvocatoria): { [key: string]: any } {
    this.convocatoria = convocatoria;
    const result = {
      modeloEjecucion: convocatoria.modeloEjecucion,
      unidadGestion: convocatoria.unidadGestion,
      codigo: convocatoria.codigo,
      fechaPublicacion: convocatoria.fechaPublicacion,
      fechaProvisional: convocatoria.fechaProvisional,
      fechaConcesion: convocatoria.fechaConcesion,
      titulo: convocatoria.titulo,
      objeto: convocatoria.objeto,
      observaciones: convocatoria.observaciones,
      finalidad: convocatoria.finalidad,
      regimenConcurrencia: convocatoria.regimenConcurrencia,
      formularioSolicitud: convocatoria.formularioSolicitud,
      duracion: convocatoria.duracion,
      ambitoGeografico: convocatoria.ambitoGeografico,
      clasificacionCVN: convocatoria.clasificacionCVN,
      estado: convocatoria.estado,
      excelencia: convocatoria.excelencia
    };

    this.checkEstado(this.getFormGroup(), convocatoria);
    return result;
  }

  /**
   * Añade validadores al formulario dependiendo del estado de la convocatoria
   */
  private checkEstado(formgroup: FormGroup, convocatoria: IConvocatoria): void {
    if (convocatoria.estado === Estado.BORRADOR) {
      formgroup.get('modeloEjecucion').clearValidators();
      formgroup.get('finalidad').clearValidators();
      formgroup.get('ambitoGeografico').clearValidators();
    } else {
      formgroup.get('modeloEjecucion').setValidators(Validators.required);
      formgroup.get('finalidad').setValidators(Validators.required);
      formgroup.get('ambitoGeografico').setValidators(Validators.required);
    }
  }

  isEstadoRegistrada() {
    return this.convocatoria.estado === Estado.REGISTRADA;
  }

  protected initializer(key: number): Observable<IConvocatoria> {
    return this.convocatoriaService.findById(key).pipe(
      switchMap((convocatoria) => {
        return this.unidadGestionService.findById(convocatoria.unidadGestion.id).pipe(
          map(unidadGestion => {
            convocatoria.unidadGestion = unidadGestion;
            return convocatoria;
          })
        );
      }),
      switchMap((convocatoria) => {
        return this.convocatoriaService.findAllConvocatoriaEntidadGestora(key).pipe(
          switchMap((listResult) => {
            const convocatoriasEntidadGestoras = listResult.items;
            if (convocatoriasEntidadGestoras.length > 0) {
              this.convocatoriaEntidadGestora = convocatoriasEntidadGestoras[0];
              return this.empresaService.findById(this.convocatoriaEntidadGestora.empresa.id).pipe(
                map((empresa) => {
                  this.getFormGroup().get('entidadGestora').setValue(empresa);
                  return convocatoria;
                })
              );
            }
            else {
              return of(convocatoria);
            }
          })
        );
      }),
      switchMap(convocatoria =>
        this.convocatoriaService.findPalabrasClave(key).pipe(
          map(({ items }) => items.map(convocatoriaPalabraClave => convocatoriaPalabraClave.palabraClave)),
          tap(palabrasClave => this.getFormGroup().controls.palabrasClave.setValue(palabrasClave)),
          map(() => convocatoria)
        )
      ),
      tap(() => this.loadAreasTematicas(key)),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  private checkIfAddAreaTematicaIsAllowed(): void {
    const fechaActual = DateTime.now();
    if (this.isEdit()) {
      this.configuracionSolicitudService.findByConvocatoriaId(this.getKey() as number).pipe(
        map(configuracionSolicitud => {
          if (configuracionSolicitud === null && this.hasEditPerm) {
            this.showAddAreaTematica = true;
          }
          else if (configuracionSolicitud.fasePresentacionSolicitudes === null && this.hasEditPerm) {
            this.showAddAreaTematica = true;
          }
          return configuracionSolicitud?.fasePresentacionSolicitudes?.fechaInicio ?? null;
        })
      ).subscribe(fechaInicio => {
        if ((this.convocatoria.estado === Estado.REGISTRADA || this.convocatoria.estado === Estado.BORRADOR)
          && (fechaInicio === null || fechaInicio > fechaActual) && this.hasEditPerm) {
          return this.showAddAreaTematica = true;
        }
        return this.showAddAreaTematica = false;
      });
    } else {
      this.showAddAreaTematica = true;
    }
  }

  private checkHasProyectoVinculado(key): void {
    this.subscriptions.push(this.proyectoService.findAllProyectosByConvocatoria(key).subscribe(
      (proyectos) => this.hasProyectoVinculado$.next(Boolean(!!proyectos.total))
    ));
  }

  private loadAreasTematicas(id: number): void {
    const subscription = this.convocatoriaService.findAreaTematicas(id).pipe(
      map(response => response.items),
      map(convocatoriasAreaTematicas => {
        const list: AreaTematicaData[] = [];
        convocatoriasAreaTematicas.forEach(
          convocatoriaAreaTematica => {
            const element: AreaTematicaData = {
              padre: undefined,
              convocatoriaAreaTematica: new StatusWrapper<IConvocatoriaAreaTematica>(convocatoriaAreaTematica),
            };
            list.push(this.loadAreaData(element));
          }
        );
        return list;
      })
    ).subscribe(areas => {
      this.areasTematicas$.next(areas);
    });
    this.subscriptions.push(subscription);
  }

  private getSecondLevelAreaTematica(areaTematica: IAreaTematica): IAreaTematica {
    if (areaTematica?.padre?.padre) {
      return this.getSecondLevelAreaTematica(areaTematica.padre);
    }
    return areaTematica;
  }

  private loadAreaData(data: AreaTematicaData): AreaTematicaData {
    const areaTematica = data.convocatoriaAreaTematica.value.areaTematica;
    if (areaTematica) {
      const result = this.getSecondLevelAreaTematica(areaTematica);
      const padre = result.padre ? result.padre : areaTematica;
      const element: AreaTematicaData = {
        padre,
        convocatoriaAreaTematica: data.convocatoriaAreaTematica,
      };
      return element;
    }
    return data;
  }

  getValue(): IConvocatoria {
    const form = this.getFormGroup().controls;
    this.convocatoria.unidadGestion = form.unidadGestion.value;
    this.convocatoria.modeloEjecucion = form.modeloEjecucion.value;
    this.convocatoria.codigo = form.codigo.value;
    this.convocatoria.fechaPublicacion = form.fechaPublicacion.value;
    this.convocatoria.fechaProvisional = form.fechaProvisional.value;
    this.convocatoria.fechaConcesion = form.fechaConcesion.value;
    this.convocatoria.titulo = form.titulo.value;
    this.convocatoria.objeto = form.objeto.value;
    this.convocatoria.observaciones = form.observaciones.value;
    this.convocatoria.finalidad = form.finalidad.value;
    this.convocatoria.regimenConcurrencia = form.regimenConcurrencia.value;
    this.convocatoria.duracion = form.duracion.value;
    this.convocatoria.ambitoGeografico = form.ambitoGeografico.value;
    this.convocatoria.formularioSolicitud = form.formularioSolicitud.value;
    this.convocatoria.clasificacionCVN = form.clasificacionCVN.value;
    this.convocatoria.excelencia = form.excelencia.value;

    return this.convocatoria;
  }

  saveOrUpdate(): Observable<number> {
    const convocatoria = this.getValue();
    const observable$ = this.isEdit() ? this.update(convocatoria) :
      this.create(convocatoria);
    return observable$.pipe(
      map(value => {
        this.convocatoria = value;
        return this.convocatoria.id;
      })
    );
  }

  private create(convocatoria: IConvocatoria): Observable<IConvocatoria> {
    let cascade = this.convocatoriaService.create(convocatoria).pipe(
      tap(result => this.convocatoria = result),
      switchMap((result) => this.saveOrUpdateConvocatoriaEntidadGestora(result)),
      switchMap((result) => this.saveOrUpdateAreasTematicas(result))
    );

    if (this.getFormGroup().controls.palabrasClave.dirty) {
      cascade = cascade.pipe(
        mergeMap((createdConvocatoria: IConvocatoria) => this.saveOrUpdatePalabrasClave(createdConvocatoria))
      );
    }

    return cascade;
  }

  private update(convocatoria: IConvocatoria): Observable<IConvocatoria> {
    let cascade = this.convocatoriaService.update(convocatoria.id, convocatoria).pipe(
      tap(result => this.convocatoria = result),
      switchMap((result) => this.saveOrUpdateConvocatoriaEntidadGestora(result)),
      switchMap((result) => this.saveOrUpdateAreasTematicas(result))
    );

    if (this.getFormGroup().controls.palabrasClave.dirty) {
      cascade = cascade.pipe(
        mergeMap((updatedConvocatoria: IConvocatoria) => this.saveOrUpdatePalabrasClave(updatedConvocatoria))
      );
    }

    return cascade;
  }

  private saveOrUpdatePalabrasClave(convocatoria: IConvocatoria): Observable<IConvocatoria> {
    const palabrasClave = this.getFormGroup().controls.palabrasClave.value ?? [];
    const proyectoPalabrasClave: IConvocatoriaPalabraClave[] = palabrasClave.map(palabraClave => ({
      convocatoria,
      palabraClave
    } as IConvocatoriaPalabraClave));
    return this.palabraClaveService.update(palabrasClave).pipe(
      mergeMap(() => this.convocatoriaService.updatePalabrasClave(convocatoria.id, proyectoPalabrasClave)),
      map(() => convocatoria)
    );
  }

  private saveOrUpdateConvocatoriaEntidadGestora(result: IConvocatoria): Observable<IConvocatoria> {
    let observable$: Observable<any>;
    const empresaId = this.getFormGroup().controls.entidadGestora.value?.id;
    this.convocatoriaEntidadGestora.convocatoriaId = result.id;
    if (empresaId !== this.convocatoriaEntidadGestora.empresa?.id) {
      if (!empresaId) {
        observable$ = this.deleteConvocatoriaEntidadGestora();
      }
      else {
        observable$ = this.convocatoriaEntidadGestora.id ?
          this.updateConvocatoriaEntidadGestora() : this.createConvocatoriaEntidadGestora();
      }
      return observable$.pipe(
        map(() => result)
      );
    }
    return of(result);
  }

  private createConvocatoriaEntidadGestora(): Observable<IConvocatoriaEntidadGestora> {
    this.convocatoriaEntidadGestora.empresa = this.getFormGroup().controls.entidadGestora.value;
    return this.convocatoriaEntidadGestoraService.create(this.convocatoriaEntidadGestora).pipe(
      tap(result => {
        this.convocatoriaEntidadGestora = result;
        this.convocatoriaEntidadGestora.empresa = this.getFormGroup().controls.entidadGestora.value;
      })
    );
  }

  private updateConvocatoriaEntidadGestora(): Observable<IConvocatoriaEntidadGestora> {
    this.convocatoriaEntidadGestora.empresa = this.getFormGroup().controls.entidadGestora.value;
    return this.convocatoriaEntidadGestoraService.update(
      this.convocatoriaEntidadGestora.id, this.convocatoriaEntidadGestora).pipe(
        tap(result => {
          this.convocatoriaEntidadGestora = result;
          this.convocatoriaEntidadGestora.empresa = this.getFormGroup().controls.entidadGestora.value;
        })
      );
  }

  private deleteConvocatoriaEntidadGestora(): Observable<void> {
    this.convocatoriaEntidadGestora.empresa = this.getFormGroup().controls.entidadGestora.value;
    return this.convocatoriaEntidadGestoraService.deleteById(
      this.convocatoriaEntidadGestora.id).pipe(
        tap(() => {
          this.convocatoriaEntidadGestora = {} as IConvocatoriaEntidadGestora;
          this.convocatoriaEntidadGestora.empresa = {} as IEmpresa;
        })
      );
  }

  updateListaAreasTematicas(areasTematicas: IAreaTematica[], padre: IAreaTematica): void {

    const convocatoriaAreaTematicaRecuperadas = this.convocatoriaAreaTematicaEliminadas
      .filter(areaEliminada => areasTematicas.map(a => a.id).includes(areaEliminada.value.areaTematica.id))
      .map(convocatoriaAreaTematica => {
        const areaTematicaData: AreaTematicaData = {
          padre,
          convocatoriaAreaTematica
        };
        return areaTematicaData;
      });

    const convocatoriaAreaTematicaEliminadas = this.areasTematicas$.value
      .map(areaTematica => areaTematica.convocatoriaAreaTematica)
      .filter(convocatoriaAreaTematica => !areasTematicas.map(a => a.id).includes(convocatoriaAreaTematica.value.areaTematica.id));

    // Elimina de la lista de areas eliminadas las que se han vuelto a añadir
    this.convocatoriaAreaTematicaEliminadas = this.convocatoriaAreaTematicaEliminadas
      .filter(areaEliminada =>
        !convocatoriaAreaTematicaRecuperadas
          .map(a => a.convocatoriaAreaTematica.value.areaTematica.id)
          .includes(areaEliminada.value.areaTematica.id)
      );

    // Anade las nuevas areas eliminadas a la lista de eliminadas
    this.convocatoriaAreaTematicaEliminadas = this.convocatoriaAreaTematicaEliminadas.concat(convocatoriaAreaTematicaEliminadas);

    // Anade las nuevas areas anadidas
    const convocatoriaAreaTematicaAnadidas = areasTematicas
      .filter(areaTematica =>
        !convocatoriaAreaTematicaRecuperadas
          .concat(this.areasTematicas$.value)
          .map(a => a.convocatoriaAreaTematica.value.areaTematica.id)
          .includes(areaTematica.id)
      )
      .map(element => {
        const areaTematicaData: AreaTematicaData = {
          padre,
          convocatoriaAreaTematica:
            new StatusWrapper<IConvocatoriaAreaTematica>({ areaTematica: element } as IConvocatoriaAreaTematica)
        };
        return areaTematicaData;
      });

    const convocatoriaAreaTematicaNoModificadas = this.areasTematicas$.value
      .filter(areaTematica =>
        !convocatoriaAreaTematicaEliminadas
          .map(a => a.value.areaTematica.id)
          .includes(areaTematica.convocatoriaAreaTematica.value.areaTematica.id)
      );

    this.areasTematicas$.next(
      convocatoriaAreaTematicaNoModificadas.concat(convocatoriaAreaTematicaAnadidas).concat(convocatoriaAreaTematicaRecuperadas)
    );

    if (convocatoriaAreaTematicaRecuperadas.length > 0
      || convocatoriaAreaTematicaEliminadas.length > 0
      || convocatoriaAreaTematicaAnadidas.length > 0) {
      this.setChanges(true);
    }

  }

  addConvocatoriaAreaTematica(data: AreaTematicaData) {
    const element = this.loadAreaData(data);
    const wrapper = new StatusWrapper<AreaTematicaData>(element);
    wrapper.setCreated();
    const current = this.areasTematicas$.value;
    current.push(wrapper.value);
    this.areasTematicas$.next(current);
    this.setChanges(true);
    this.setErrors(false);
  }

  saveOrUpdateAreasTematicas(result: IConvocatoria): Observable<IConvocatoria> {
    return merge(
      this.deleteConvocatoriaAreaTematicas(),
      this.createConvocatoriaAreaTematicas()
    ).pipe(
      takeLast(1),
      switchMap(() => of(result))
    );
  }

  private deleteConvocatoriaAreaTematicas(): Observable<void> {
    const deleteEntidades = this.convocatoriaAreaTematicaEliminadas?.filter((value) => value.value.id);
    if (deleteEntidades?.length === 0 || deleteEntidades == null) {
      return of(void 0);
    }
    return from(deleteEntidades).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaAreaTematicaService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.convocatoriaAreaTematicaEliminadas =
                this.convocatoriaAreaTematicaEliminadas?.filter(deletedModelo =>
                  deletedModelo.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private createConvocatoriaAreaTematicas(): Observable<void> {
    const createdAreas = this.areasTematicas$.value.filter((wrapper) => !wrapper.convocatoriaAreaTematica.value.id);
    if (createdAreas.length === 0) {
      return of(void 0);
    }
    createdAreas.forEach(
      (wrapper) => wrapper.convocatoriaAreaTematica.value.convocatoriaId = this.convocatoria.id
    );
    return from(createdAreas).pipe(
      mergeMap((data) => {
        return this.convocatoriaAreaTematicaService.create(data.convocatoriaAreaTematica.value).pipe(
          map((createdEntidad) => {
            const index = this.areasTematicas$.value.findIndex(currentEntidad =>
              currentEntidad.convocatoriaAreaTematica.value.areaTematica.id === createdEntidad.areaTematica.id);
            this.areasTematicas$.value[index].convocatoriaAreaTematica = new StatusWrapper<IConvocatoriaAreaTematica>(createdEntidad);
          })
        );
      })
    );
  }
}
