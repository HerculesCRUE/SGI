import { FormBuilder, FormGroup } from '@angular/forms';
import { IMemoria } from '@core/models/eti/memoria';
import { FormFragment } from '@core/services/action-service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

export class EvaluacionDatosMemoriaFragment extends FormFragment<IMemoria> {

  private memoria: IMemoria;

  constructor(
    private fb: FormBuilder,
    key: number,
    private service: EvaluacionService,
    private personaService: PersonaService
  ) {
    super(key);
    this.memoria = {} as IMemoria;
  }

  protected buildFormGroup(): FormGroup {
    const fb = this.fb.group({
      comite: [{ value: '', disabled: true }],
      fechaEvaluacion: [{ value: null, disabled: true }],
      referenciaMemoria: [{ value: '', disabled: true }],
      solicitante: [{ value: '', disabled: true }],
      version: [{ value: '', disabled: true }]
    });
    return fb;
  }

  protected initializer(key: number): Observable<IMemoria> {
    return this.service.findById(key).pipe(
      map((evaluacion) => {
        this.memoria = evaluacion.memoria;
        this.memoria.version = evaluacion.version;
        return this.memoria;
      }),
      switchMap((memoria) => {
        if (memoria.peticionEvaluacion?.solicitante?.id) {
          return this.personaService.findById(memoria.peticionEvaluacion?.solicitante.id)
            .pipe(
              map((persona) => {
                memoria.peticionEvaluacion.solicitante = persona;
                return memoria;
              }),
              catchError(() => {
                return of(memoria);
              })
            );
        }
        else {
          return of(memoria);
        }
      })
    );
  }

  buildPatch(value: IMemoria): { [key: string]: any } {
    const patch = {
      comite: value.comite.comite,
      fechaEvaluacion: value.fechaEnvioSecretaria,
      referenciaMemoria: value.numReferencia,
      version: value.version,
      solicitante: `${value?.peticionEvaluacion.solicitante?.nombre} ${value?.peticionEvaluacion.solicitante?.apellidos}`
    };
    return patch;
  }

  getValue(): IMemoria {
    return this.memoria;
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }

  handleErrors(errors: Error[]): void {
    this.clearProblems();
    errors.forEach(this.processError);
  }

}
