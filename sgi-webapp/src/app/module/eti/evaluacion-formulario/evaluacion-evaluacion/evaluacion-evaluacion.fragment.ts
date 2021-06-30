import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IComentario } from '@core/models/eti/comentario';
import { DICTAMEN, IDictamen } from '@core/models/eti/dictamen';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IMemoria } from '@core/models/eti/memoria';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { FormFragment } from '@core/services/action-service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

export class EvaluacionEvaluacionFragment extends FormFragment<IMemoria> {

  private memoria: IMemoria;
  evaluacion$: BehaviorSubject<IEvaluacion> = new BehaviorSubject<IEvaluacion>(null);
  evaluacion: IEvaluacion;
  comentarios$: BehaviorSubject<StatusWrapper<IComentario>[]> = new BehaviorSubject<StatusWrapper<IComentario>[]>([]);

  mostrarComentario$ = new BehaviorSubject<boolean>(false);

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
    const form = this.fb.group({
      comite: [{ value: '', disabled: true }],
      fechaEvaluacion: [{ value: null, disabled: true }],
      referenciaMemoria: [{ value: '', disabled: true }],
      solicitante: [{ value: '', disabled: true }],
      version: [{ value: '', disabled: true }],
      dictamen: [null, [Validators.required]],
      comentario: ['', [Validators.maxLength(2000)]]
    });

    this.subscriptions.push(form.controls.dictamen.valueChanges.subscribe(
      (value: IDictamen) => this.checkComentario(value)));

    return form;
  }

  protected initializer(key: number): Observable<IMemoria> {
    return this.service.findById(key).pipe(
      map((evaluacion) => {
        this.memoria = evaluacion.memoria;
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
      solicitante: `${value.peticionEvaluacion?.solicitante?.nombre} ${value.peticionEvaluacion?.solicitante?.apellidos}`,
      dictamen: this.evaluacion.dictamen,
      comentario: this.evaluacion.comentario
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
    this.evaluacion.comentario = form.comentario;
    return this.evaluacion;
  }

  setComentarios(comentarios: BehaviorSubject<StatusWrapper<IComentario>[]>) {
    this.comentarios$ = comentarios;
  }

  private checkComentario(dictamen: IDictamen) {
    if (this.evaluacion?.tipoEvaluacion?.id === TIPO_EVALUACION.MEMORIA && dictamen?.id === DICTAMEN.NO_PROCEDE_EVALUAR) {
      this.mostrarComentario$.next(true);
    } else {
      this.mostrarComentario$.next(false);
    }
  }

}
