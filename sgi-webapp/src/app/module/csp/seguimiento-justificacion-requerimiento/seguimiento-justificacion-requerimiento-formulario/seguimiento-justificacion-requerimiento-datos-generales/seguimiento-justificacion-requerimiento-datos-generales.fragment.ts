import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { FormFragment } from '@core/services/action-service';
import { IncidenciaDocumentacionRequerimientoService } from '@core/services/csp/incidencia-documentacion-requerimiento/incidencia-documentacion-requerimiento.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, startWith, takeLast, tap } from 'rxjs/operators';

export class SeguimientoJustificacionRequerimientoDatosGeneralesFragment extends FormFragment<IRequerimientoJustificacion> {
  private requerimientoJustificacionServerData: IRequerimientoJustificacion;
  private incidenciasDocumentacionToDelete: StatusWrapper<IIncidenciaDocumentacionRequerimiento>[] = [];

  constructor(
    private readonly logger: NGXLogger,
    private readonly requerimientoJustificacion: IRequerimientoJustificacion,
    private readonly incidenciasDocumentacion$: BehaviorSubject<StatusWrapper<IIncidenciaDocumentacionRequerimiento>[]>,
    private readonly canEdit: boolean,
    readonly proyectoSgeRef: string,
    private readonly requerimientoJustificacionService: RequerimientoJustificacionService,
    private readonly incidenciaDocumentacionRequerimientoService: IncidenciaDocumentacionRequerimientoService
  ) {
    super(requerimientoJustificacion?.id, true);
    this.setComplete(true);
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      id: new FormControl({ value: '', disabled: true }),
      proyectoProyectoSge: new FormControl(null, [Validators.required]),
      proyectoPeriodoJustificacion: new FormControl(null),
      numRequerimiento: new FormControl({ value: '', disabled: true }),
      tipoRequerimiento: new FormControl(null, [Validators.required]),
      requerimientoPrevio: new FormControl(null),
      fechaNotificacion: new FormControl(null),
      fechaFinAlegacion: new FormControl(null),
      importeAceptado: new FormControl(null),
      importeAceptadoCd: new FormControl(null),
      importeAceptadoCi: new FormControl(null),
      importeRechazado: new FormControl(null),
      importeRechazadoCd: new FormControl(null),
      importeRechazadoCi: new FormControl(null),
      importeReintegrar: new FormControl(null),
      importeReintegrarCd: new FormControl(null),
      importeReintegrarCi: new FormControl(null),
      interesesReintegrar: new FormControl(null),
      observaciones: new FormControl('', [Validators.maxLength(2000)]),
      subvencionJustificada: new FormControl(null),
      defectoSubvencion: new FormControl(null),
      anticipoJustificado: new FormControl(null),
      defectoAnticipo: new FormControl(null),
      recursoEstimado: new FormControl(null),
    });

    if (this.isEdit() && !this.canEdit) {
      form.disable();
    }

    return form;
  }

  getCurrentRequerimientoJustificacion$(): Observable<IRequerimientoJustificacion> {
    return this.getFormGroup().valueChanges.pipe(
      startWith(this.requerimientoJustificacion),
      map(() => this.getValue())
    );
  }

  protected buildPatch(value: IRequerimientoJustificacion): { [key: string]: any; } {
    return {
      id: value.id,
      proyectoProyectoSge: value.proyectoProyectoSge,
      proyectoPeriodoJustificacion: value.proyectoPeriodoJustificacion,
      numRequerimiento: value.numRequerimiento,
      tipoRequerimiento: value.tipoRequerimiento,
      requerimientoPrevio: value.requerimientoPrevio,
      fechaNotificacion: value.fechaNotificacion,
      fechaFinAlegacion: value.fechaFinAlegacion,
      importeAceptado: value.importeAceptado,
      importeAceptadoCd: value.importeAceptadoCd,
      importeAceptadoCi: value.importeAceptadoCi,
      importeRechazado: value.importeRechazado,
      importeRechazadoCd: value.importeRechazadoCd,
      importeRechazadoCi: value.importeRechazadoCi,
      importeReintegrar: value.importeReintegrar,
      importeReintegrarCd: value.importeReintegrarCd,
      importeReintegrarCi: value.importeReintegrarCi,
      interesesReintegrar: value.interesesReintegrar,
      observaciones: value.observaciones,
      subvencionJustificada: value.subvencionJustificada,
      defectoSubvencion: value.defectoSubvencion,
      anticipoJustificado: value.anticipoJustificado,
      defectoAnticipo: value.defectoAnticipo,
      recursoEstimado: value.recursoEstimado
    };
  }

  getValue(): IRequerimientoJustificacion {
    const form = this.getFormGroup().value;
    const requerimientoJustificacion = this.requerimientoJustificacionServerData ?? {} as IRequerimientoJustificacion;
    requerimientoJustificacion.anticipoJustificado = form.anticipoJustificado;
    requerimientoJustificacion.defectoAnticipo = form.defectoAnticipo;
    requerimientoJustificacion.defectoSubvencion = form.defectoSubvencion;
    requerimientoJustificacion.fechaFinAlegacion = form.fechaFinAlegacion;
    requerimientoJustificacion.fechaNotificacion = form.fechaNotificacion;
    requerimientoJustificacion.importeAceptado = form.importeAceptado;
    requerimientoJustificacion.importeAceptadoCd = form.importeAceptadoCd;
    requerimientoJustificacion.importeAceptadoCi = form.importeAceptadoCi;
    requerimientoJustificacion.importeRechazado = form.importeRechazado;
    requerimientoJustificacion.importeRechazadoCd = form.importeRechazadoCd;
    requerimientoJustificacion.importeRechazadoCi = form.importeRechazadoCi;
    requerimientoJustificacion.importeReintegrar = form.importeReintegrar;
    requerimientoJustificacion.importeReintegrarCd = form.importeReintegrarCd;
    requerimientoJustificacion.importeReintegrarCi = form.importeReintegrarCi;
    requerimientoJustificacion.interesesReintegrar = form.interesesReintegrar;
    requerimientoJustificacion.observaciones = form.observaciones;
    requerimientoJustificacion.proyectoPeriodoJustificacion = form.proyectoPeriodoJustificacion;
    requerimientoJustificacion.proyectoProyectoSge = form.proyectoProyectoSge;
    requerimientoJustificacion.recursoEstimado = form.recursoEstimado;
    requerimientoJustificacion.requerimientoPrevio = form.requerimientoPrevio;
    requerimientoJustificacion.subvencionJustificada = form.subvencionJustificada;
    requerimientoJustificacion.tipoRequerimiento = form.tipoRequerimiento;

    return requerimientoJustificacion;
  }

  protected initializer(key: string | number): Observable<IRequerimientoJustificacion> {
    return of(this.requerimientoJustificacion)
      .pipe(
        tap((requerimientoJustificacion) => {
          this.requerimientoJustificacionServerData = requerimientoJustificacion;
        }),
        map((requerimientoJustificacion) => requerimientoJustificacion)
      );
  }

  addIncidenciaDocumentacion(incidenciaDocumentacionRequerimiento: IIncidenciaDocumentacionRequerimiento): void {
    const wrapped = new StatusWrapper<IIncidenciaDocumentacionRequerimiento>(incidenciaDocumentacionRequerimiento);
    wrapped.setCreated();
    const current = this.incidenciasDocumentacion$.value;
    current.push(wrapped);
    this.incidenciasDocumentacion$.next(current);
    this.setChanges(true);
  }

  updateIncidenciaDocumentacion(
    updatedIncidenciaDocumentacionRequerimiento: IIncidenciaDocumentacionRequerimiento,
    index: number
  ) {
    if (index >= 0) {
      const current = this.incidenciasDocumentacion$.value;
      const wrapper = current[index];
      if (!wrapper.created) {
        wrapper.setEdited();
      }
      this.incidenciasDocumentacion$.next(current);
      this.setChanges(true);
    }
  }

  deleteIncidenciaDocumentacion(wrapper: StatusWrapper<IIncidenciaDocumentacionRequerimiento>): void {
    const current = this.incidenciasDocumentacion$.value;
    const index = current.findIndex(value => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.incidenciasDocumentacionToDelete.push(current[index]);
      }
      current.splice(index, 1);
      this.incidenciasDocumentacion$.next(current);
      this.setChanges(this.hasFragmentIncidenciasDocumentacionChangesPending());
    }
  }

  private hasFragmentIncidenciasDocumentacionChangesPending() {
    return this.incidenciasDocumentacionToDelete.length > 0 ||
      this.incidenciasDocumentacion$.value.some((value) => value.created || value.edited);
  }

  saveOrUpdate(): Observable<string | number | void> {
    const invencion = this.getValue();
    const observable$ = this.isEdit() ? this.update(invencion) : this.create(invencion);

    return observable$.pipe(
      map((result: IRequerimientoJustificacion) => {
        this.requerimientoJustificacionServerData = result;
        return result?.id;
      })
    );
  }

  private create(requerimientoJustificacion: IRequerimientoJustificacion): Observable<IRequerimientoJustificacion> {
    let cascade = of(void 0);
    if (this.formGroupStatus$.value.changes) {
      cascade = cascade.pipe(
        mergeMap(() => this.createRequerimientoJustificacion(requerimientoJustificacion))
      );
    }

    if (this.hasFragmentIncidenciasDocumentacionChangesPending()) {
      cascade = cascade.pipe(
        mergeMap((createdRequerimientoJustificacion: IRequerimientoJustificacion) =>
          this.saveOrUpdateIncidenciasDocumentacion(createdRequerimientoJustificacion)
        )
      );
    }

    return cascade;
  }

  private update(requerimientoJustificacion: IRequerimientoJustificacion): Observable<IRequerimientoJustificacion> {
    let cascade = of(void 0);
    if (this.formGroupStatus$.value.changes) {
      cascade = cascade.pipe(
        mergeMap(() => this.updateRequerimientoJustificacion(requerimientoJustificacion))
      );
    } else {
      cascade = cascade.pipe(
        mergeMap(() => of(requerimientoJustificacion))
      );
    }

    if (this.hasFragmentIncidenciasDocumentacionChangesPending()) {
      cascade = cascade.pipe(
        mergeMap((updatedRequerimientoJustificacion: IRequerimientoJustificacion) =>
          this.saveOrUpdateIncidenciasDocumentacion(updatedRequerimientoJustificacion)
        )
      );
    }

    return cascade;
  }

  private createRequerimientoJustificacion(
    requerimientoJustificacion: IRequerimientoJustificacion): Observable<IRequerimientoJustificacion> {
    return this.requerimientoJustificacionService.create(requerimientoJustificacion);
  }

  private updateRequerimientoJustificacion(
    invencion: IRequerimientoJustificacion): Observable<IRequerimientoJustificacion> {
    return this.requerimientoJustificacionService.update(Number(this.getKey()), invencion).pipe(
      tap((result: IRequerimientoJustificacion) => {
        this.getFormGroup().controls.numRequerimiento.setValue(result.numRequerimiento);
      })
    );
  }

  private saveOrUpdateIncidenciasDocumentacion(requerimientoJustificacion: IRequerimientoJustificacion):
    Observable<IRequerimientoJustificacion> {
    return merge(
      this.deleteIncidenciasDocumentacion(),
      this.updateIncidenciasDocumentacion(requerimientoJustificacion),
      this.createIncidenciasDocumentacion(requerimientoJustificacion)
    ).pipe(
      takeLast(1),
      tap(() => {
        this.setChanges(this.hasFragmentIncidenciasDocumentacionChangesPending());
      }),
      map(() => requerimientoJustificacion)
    );
  }

  private deleteIncidenciasDocumentacion(): Observable<void> {
    if (this.incidenciasDocumentacionToDelete.length === 0) {
      return of(void 0);
    }

    return from(this.incidenciasDocumentacionToDelete).pipe(
      mergeMap(wrapped => this.deleteIncidenciaDocumentacionById(wrapped))
    );
  }

  private deleteIncidenciaDocumentacionById(wrapped: StatusWrapper<IIncidenciaDocumentacionRequerimiento>): Observable<void> {
    return this.incidenciaDocumentacionRequerimientoService.deleteById(wrapped.value.id).pipe(
      tap(() =>
        this.incidenciasDocumentacionToDelete = this.incidenciasDocumentacionToDelete.filter(entidadEliminada =>
          entidadEliminada.value.id !== wrapped.value.id
        ),
        catchError(() => of(void 0))
      )
    );
  }

  private updateIncidenciasDocumentacion(requerimientoJustificacion: IRequerimientoJustificacion): Observable<void> {
    const current = this.incidenciasDocumentacion$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      map(wrapper => {
        wrapper.value.requerimientoJustificacion = requerimientoJustificacion;
        return wrapper;
      }),
      mergeMap((wrapper => {
        return this.incidenciaDocumentacionRequerimientoService.update(wrapper.value.id, wrapper.value).pipe(
          map((incidenciaDocumentacionResponse) => this.refreshIncidenciasDocumentacion(incidenciaDocumentacionResponse, wrapper, current)),
          catchError(() => of(void 0))
        );
      }))
    );
  }

  private createIncidenciasDocumentacion(requerimientoJustificacion: IRequerimientoJustificacion): Observable<void> {
    const current = this.incidenciasDocumentacion$.value;
    return from(current.filter(wrapper => wrapper.created)).pipe(
      map(wrapper => {
        wrapper.value.requerimientoJustificacion = requerimientoJustificacion;
        return wrapper;
      }),
      mergeMap((wrapper => {
        return this.incidenciaDocumentacionRequerimientoService.create(wrapper.value).pipe(
          map((incidenciaDocumentacionResponse) => this.refreshIncidenciasDocumentacion(incidenciaDocumentacionResponse, wrapper, current)),
          catchError(() => of(void 0))
        );
      }))
    );
  }

  private refreshIncidenciasDocumentacion(
    incidenciaDocumentacionResponse: IIncidenciaDocumentacionRequerimiento,
    wrapper: StatusWrapper<IIncidenciaDocumentacionRequerimiento>,
    current: StatusWrapper<IIncidenciaDocumentacionRequerimiento>[]
  ): void {
    let wrapperToUpdate = current[current.findIndex(c => c === wrapper)];
    if (!this.incidienciaDocumentacionHasChangesPending(incidenciaDocumentacionResponse, wrapper.value)) {
      wrapperToUpdate = new StatusWrapper<IIncidenciaDocumentacionRequerimiento>(incidenciaDocumentacionResponse);
    } else {
      if (wrapperToUpdate.created) {
        wrapperToUpdate.value.id = incidenciaDocumentacionResponse.id;
      }
      wrapperToUpdate.setEdited();
    }
    this.incidenciasDocumentacion$.next(current);
  }

  incidienciaDocumentacionHasChangesPending(
    serverData: IIncidenciaDocumentacionRequerimiento,
    appData: IIncidenciaDocumentacionRequerimiento): boolean {
    return serverData.alegacion !== appData.alegacion;
  }
}
