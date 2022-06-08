import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IComentario } from '@core/models/eti/comentario';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IMemoria } from '@core/models/eti/memoria';
import { FormFragment } from '@core/services/action-service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

export class SeguimientoEvaluacionFragment extends FormFragment<IMemoria> {

  private memoria: IMemoria;
  evaluacion$: BehaviorSubject<IEvaluacion> = new BehaviorSubject<IEvaluacion>(null);
  evaluacion: IEvaluacion;
  comentarios$: BehaviorSubject<StatusWrapper<IComentario>[]> = new BehaviorSubject<StatusWrapper<IComentario>[]>([]);

  constructor(
    private fb: FormBuilder,
    key: number,
    protected readonly snackBarService: SnackBarService,
    private service: EvaluacionService,
    private personaService: PersonaService) {
    super(key);
    this.memoria = {} as IMemoria;
  }

  protected buildFormGroup(): FormGroup {
    return this.fb.group({
      comite: [{ value: '', disabled: true }],
      fechaEvaluacion: [{ value: null, disabled: true }],
      referenciaMemoria: [{ value: '', disabled: true }],
      solicitante: [{ value: '', disabled: true }],
      version: [{ value: '', disabled: true }],
      dictamen: [null, [Validators.required, new NullIdValidador().isValid()]]
    });
  }

  protected initializer(key: number): Observable<IMemoria> {
    return this.service.findById(key).pipe(
      map((evaluacion) => {
        this.memoria = evaluacion.memoria as IMemoria;
        this.evaluacion$.next(evaluacion);
        this.evaluacion = evaluacion;
        return this.memoria;
      }),
      switchMap((memoria) => {
        if (memoria.peticionEvaluacion?.solicitante?.id) {
          return this.personaService.findById(memoria.peticionEvaluacion.solicitante.id).pipe(
            map((persona) => {
              memoria.peticionEvaluacion.solicitante = persona;
              return memoria;
            }),
            catchError((e) => of(memoria)),
          );
        }
        else {
          return of(memoria);
        }
      })
    );
  }

  buildPatch(value: IMemoria): { [key: string]: any } {
    return {
      comite: value.comite.comite,
      fechaEvaluacion: value.fechaEnvioSecretaria,
      referenciaMemoria: value.numReferencia,
      version: value.version,
      solicitante: `${value?.peticionEvaluacion?.solicitante?.nombre} ${value?.peticionEvaluacion?.solicitante?.apellidos}`,
      dictamen: this.evaluacion.dictamen
    };
  }

  getValue(): IMemoria {
    return this.memoria;
  }

  saveOrUpdate(): Observable<number> {
    this.evaluacion = this.getValueFormDictamen();

    const obs = this.isEdit() ? this.service.update(this.evaluacion.id, this.evaluacion) : this.service.create(this.evaluacion);
    return obs.pipe(
      map((value) => {
        this.evaluacion = value;
        return this.evaluacion.id;
      })
    );
  }

  getValueFormDictamen(): IEvaluacion {
    const form = this.getFormGroup().value;
    this.evaluacion.dictamen = form.dictamen;
    return this.evaluacion;
  }

  setComentarios(comentarios: BehaviorSubject<StatusWrapper<IComentario>[]>) {
    this.comentarios$ = comentarios;
  }
}
