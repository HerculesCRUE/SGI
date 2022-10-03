import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IAlegacionRequerimiento } from '@core/models/csp/alegacion-requerimiento';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { FormFragment } from '@core/services/action-service';
import { AlegacionRequerimientoService } from '@core/services/csp/alegacion-requerimiento/alegacion-requerimiento.service';
import { IncidenciaDocumentacionRequerimientoService } from '@core/services/csp/incidencia-documentacion-requerimiento/incidencia-documentacion-requerimiento.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class SeguimientoJustificacionRequerimientoRespuestaAlegacionFragment extends FormFragment<IAlegacionRequerimiento> {

  private alegacionRequerimiento: IAlegacionRequerimiento = {} as IAlegacionRequerimiento;
  currentRequerimientoJustificacion: IRequerimientoJustificacion;
  requerimientoJustificacionFormGroup: FormGroup;

  constructor(
    id: number,
    private readonly incidenciasDocumentacion$: BehaviorSubject<StatusWrapper<IIncidenciaDocumentacionRequerimiento>[]>,
    private readonly canEdit: boolean,
    private readonly alegacionRequerimientoService: AlegacionRequerimientoService,
    private readonly requerimientoJustificacionService: RequerimientoJustificacionService,
    private readonly incidenciaDocumentacionRequerimientoService: IncidenciaDocumentacionRequerimientoService
  ) {
    super(id, true);
    this.setComplete(true);
    this.requerimientoJustificacionFormGroup = this.buildRequerimientoJustificacionFormGroup();
    this.subscribeToIncidenciasDocumentacion();
  }

  private buildRequerimientoJustificacionFormGroup(): FormGroup {
    return new FormGroup({
      numRequerimiento: new FormControl({ value: '', disabled: true }),
      tipoRequerimiento: new FormControl({ value: '', disabled: true }),
    });
  }

  private subscribeToIncidenciasDocumentacion(): void {
    this.subscriptions.push(
      this.incidenciasDocumentacion$
        .pipe(
          tap(() => {
            if (this.hasChanges()) {
              this.setChanges(this.hasFragmentIncidenciasDocumentacionChangesPending());
            }
          })
        )
        .subscribe()
    );
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      id: new FormControl({ value: '', disabled: true }),
      fechaAlegacion: new FormControl(null),
      importeAlegado: new FormControl(null),
      importeAlegadoCd: new FormControl(null),
      importeAlegadoCi: new FormControl(null),
      importeReintegrado: new FormControl(null),
      importeReintegradoCd: new FormControl(null),
      importeReintegradoCi: new FormControl(null),
      interesesReintegrados: new FormControl(null),
      fechaReintegro: new FormControl(null),
      justificanteReintegro: new FormControl(null, Validators.maxLength(250)),
      observaciones: new FormControl(null, Validators.maxLength(2000)),
    });

    if (this.isEdit() && !this.canEdit) {
      form.disable();
    }

    return form;
  }

  onCurrentRequerimientoJustificacionChanges(currentRequerimiento: IRequerimientoJustificacion): void {
    this.currentRequerimientoJustificacion = currentRequerimiento;
    this.requerimientoJustificacionFormGroup.patchValue({
      numRequerimiento: currentRequerimiento?.numRequerimiento,
      tipoRequerimiento: currentRequerimiento?.tipoRequerimiento?.nombre
    });
  }

  protected buildPatch(value: IAlegacionRequerimiento): { [key: string]: any; } {
    if (!value) {
      return {};
    }
    this.alegacionRequerimiento = value;
    return {
      id: this.alegacionRequerimiento.id,
      fechaAlegacion: this.alegacionRequerimiento.fechaAlegacion,
      importeAlegado: this.alegacionRequerimiento.importeAlegado,
      importeAlegadoCd: this.alegacionRequerimiento.importeAlegadoCd,
      importeAlegadoCi: this.alegacionRequerimiento.importeAlegadoCi,
      importeReintegrado: this.alegacionRequerimiento.importeReintegrado,
      importeReintegradoCd: this.alegacionRequerimiento.importeReintegradoCd,
      importeReintegradoCi: this.alegacionRequerimiento.importeReintegradoCi,
      interesesReintegrados: this.alegacionRequerimiento.interesesReintegrados,
      fechaReintegro: this.alegacionRequerimiento.fechaReintegro,
      justificanteReintegro: this.alegacionRequerimiento.justificanteReintegro,
      observaciones: this.alegacionRequerimiento.observaciones,
    };
  }

  getValue(): IAlegacionRequerimiento {
    const form = this.getFormGroup().value;
    const alegacionRequerimiento = this.alegacionRequerimiento;
    alegacionRequerimiento.fechaAlegacion = form.fechaAlegacion;
    alegacionRequerimiento.importeAlegado = form.importeAlegado;
    alegacionRequerimiento.importeAlegadoCd = form.importeAlegadoCd;
    alegacionRequerimiento.importeAlegadoCi = form.importeAlegadoCi;
    alegacionRequerimiento.importeReintegrado = form.importeReintegrado;
    alegacionRequerimiento.importeReintegradoCd = form.importeReintegradoCd;
    alegacionRequerimiento.importeReintegradoCi = form.importeReintegradoCi;
    alegacionRequerimiento.interesesReintegrados = form.interesesReintegrados;
    alegacionRequerimiento.fechaReintegro = form.fechaReintegro;
    alegacionRequerimiento.justificanteReintegro = form.justificanteReintegro;
    alegacionRequerimiento.observaciones = form.observaciones;

    return alegacionRequerimiento;
  }

  protected initializer(key: string | number): Observable<IAlegacionRequerimiento> {
    const requerimientoJustificacionId = key as number;
    return this.requerimientoJustificacionService.findAlegacion(requerimientoJustificacionId);
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

  private hasFragmentIncidenciasDocumentacionChangesPending() {
    return this.incidenciasDocumentacion$.value.some((value) => value.edited);
  }

  saveOrUpdate(): Observable<string | number | void> {
    this.alegacionRequerimiento = this.getValue();
    this.alegacionRequerimiento.requerimientoJustificacion = this.currentRequerimientoJustificacion;

    return this.alegacionRequerimiento.id ? this.update(this.alegacionRequerimiento) : this.create(this.alegacionRequerimiento);
  }

  private create(alegacionRequerimiento: IAlegacionRequerimiento): Observable<void> {
    let cascade = of(void 0);
    if (this.formGroupStatus$.value.changes) {
      cascade = cascade.pipe(
        switchMap(() => this.createRequerimientoJustificacion(alegacionRequerimiento))
      );
    }

    if (this.hasFragmentIncidenciasDocumentacionChangesPending()) {
      cascade = cascade.pipe(
        mergeMap(() => this.updateIncidenciasDocumentacion())
      );
    }

    return cascade;
  }

  private update(alegacionRequerimiento: IAlegacionRequerimiento): Observable<void> {
    let cascade = of(void 0);
    if (this.formGroupStatus$.value.changes) {
      cascade = cascade.pipe(
        switchMap(() => this.updateRequerimientoJustificacion(alegacionRequerimiento))
      );
    } else {
      cascade = cascade.pipe(
        switchMap(() => of(alegacionRequerimiento))
      );
    }

    if (this.hasFragmentIncidenciasDocumentacionChangesPending()) {
      cascade = cascade.pipe(
        mergeMap(() => this.updateIncidenciasDocumentacion())
      );
    }

    return cascade;
  }

  private createRequerimientoJustificacion(
    alegacionRequerimiento: IAlegacionRequerimiento): Observable<IAlegacionRequerimiento> {
    return this.alegacionRequerimientoService.create(alegacionRequerimiento)
      .pipe(
        tap(alegacionRequerimientoCreated => this.alegacionRequerimiento = alegacionRequerimientoCreated)
      );
  }

  private updateRequerimientoJustificacion(
    alegacionRequerimiento: IAlegacionRequerimiento): Observable<IAlegacionRequerimiento> {
    return this.alegacionRequerimientoService.update(this.alegacionRequerimiento.id, alegacionRequerimiento)
      .pipe(
        tap(alegacionRequerimientoUpdated => this.alegacionRequerimiento = alegacionRequerimientoUpdated)
      );
  }

  private updateIncidenciasDocumentacion(): Observable<void> {
    const current = this.incidenciasDocumentacion$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      map(wrapper => {
        wrapper.value.requerimientoJustificacion = this.currentRequerimientoJustificacion;
        return wrapper;
      }),
      mergeMap(wrapper => {
        return this.incidenciaDocumentacionRequerimientoService.updateAlegacion(wrapper.value.id, wrapper.value)
          .pipe(
            map(incidenciaDocumentacionResponse => this.refreshIncidenciasDocumentacion(incidenciaDocumentacionResponse, wrapper, current)),
            catchError(() => of(void 0))
          );
      }),
      takeLast(1),
      tap(() => {
        this.setChanges(this.hasFragmentIncidenciasDocumentacionChangesPending());
      }),
    );
  }

  private refreshIncidenciasDocumentacion(
    incidenciaDocumentacionResponse: IIncidenciaDocumentacionRequerimiento,
    wrapper: StatusWrapper<IIncidenciaDocumentacionRequerimiento>,
    current: StatusWrapper<IIncidenciaDocumentacionRequerimiento>[]
  ): void {
    current[current.findIndex(c => c === wrapper)] =
      new StatusWrapper<IIncidenciaDocumentacionRequerimiento>(incidenciaDocumentacionResponse);
    this.incidenciasDocumentacion$.next(current);
  }
}
