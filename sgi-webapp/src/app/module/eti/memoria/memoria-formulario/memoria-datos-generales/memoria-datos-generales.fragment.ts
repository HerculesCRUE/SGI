import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { COMITE, IComite } from '@core/models/eti/comite';
import { IEstadoMemoria } from '@core/models/eti/estado-memoria';
import { IMemoria } from '@core/models/eti/memoria';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { ESTADO_MEMORIA } from '@core/models/eti/tipo-estado-memoria';
import { ITipoMemoria, TIPO_MEMORIA } from '@core/models/eti/tipo-memoria';
import { IPersona } from '@core/models/sgp/persona';
import { FormFragment } from '@core/services/action-service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

export class MemoriaDatosGeneralesFragment extends FormFragment<IMemoria>  {
  private memoria: IMemoria;
  public readonly: boolean;
  public showTitulo = false;
  public showMemoriaOriginal = false;
  public personasResponsable$: BehaviorSubject<IPersona[]> = new BehaviorSubject<IPersona[]>([]);
  public mostrarCodOrgano = false;
  public showInfoRatificacion = false;
  public showComentarioSubsanacion = false;
  private estadoMemoria: IEstadoMemoria;

  public idPeticionEvaluacion: number;
  private isInvestigador: boolean;

  constructor(
    private fb: FormBuilder,
    readonly: boolean,
    key: number,
    private service: MemoriaService,
    private personaService: PersonaService,
    private readonly peticionEvaluacionService: PeticionEvaluacionService,
    private readonly moduloInv: boolean) {
    super(key);
    this.memoria = {} as IMemoria;
    this.readonly = readonly;
    this.isInvestigador = moduloInv;
  }

  public loadResponsable(idPeticionEvaluacion: number): void {
    this.idPeticionEvaluacion = idPeticionEvaluacion;
    this.subscriptions.push(
      this.peticionEvaluacionService.findEquipoInvestigador(idPeticionEvaluacion).pipe(
        map((response) => {
          const equiposTrabajo = response.items;
          if (response.items) {
            const personaIdsEquiposTrabajo = new Set<string>(equiposTrabajo.map((equipoTrabajo) => equipoTrabajo.persona.id));
            if (personaIdsEquiposTrabajo.size > 0) {
              this.personaService.findAllByIdIn([...personaIdsEquiposTrabajo]).pipe(
                map(
                  responsePersonas => {
                    return responsePersonas.items;
                  }
                )
              ).subscribe((persona) => {
                this.personasResponsable$.next(persona);
              });
            }
          }
        })
      ).subscribe());
  }

  protected buildFormGroup(): FormGroup {
    return this.fb.group({
      numReferencia: [
        { value: '', disabled: true }
      ],
      comite: [
        { value: this.isEdit() ? this.memoria.comite : null, disabled: (this.isEdit() || this.readonly) },
        Validators.required
      ],
      tipoMemoria: [
        { value: this.isEdit() ? this.memoria.tipoMemoria : null, disabled: (this.isEdit() || this.readonly) },
        Validators.required
      ],
      titulo: [
        { value: this.isEdit() ? this.memoria.titulo : '', disabled: this.readonly }, Validators.maxLength(2000)
      ],
      personaResponsable: [
        { value: null, disabled: this.readonly }
      ],
      memoriaOriginal: [
        { value: this.isEdit() ? this.memoria.memoriaOriginal : null, disabled: this.isEdit() },
        Validators.required
      ],
      comentarioSubsanacion: [
        { value: '', disabled: true }
      ]
    });
  }

  buildPatch(value: IMemoria): { [key: string]: any } {
    this.memoria = value;
    this.onComiteChange(value.comite);
    this.onTipoMemoriaChange(value.tipoMemoria);

    return {
      numReferencia: value.numReferencia,
      comite: value.comite,
      tipoMemoria: value.tipoMemoria,
      titulo: value.titulo,
      personaResponsable: value.responsable?.id ? value.responsable : null,
      memoriaOriginal: value.memoriaOriginal,
      comentarioSubsanacion: this.estadoMemoria?.comentario ?? ''
    };
  }

  getValue(): IMemoria {
    const form = this.getFormGroup().controls;
    this.memoria.comite = form.comite.value;
    this.memoria.tipoMemoria = form.tipoMemoria.value;

    if (this.showTitulo) {
      this.memoria.titulo = form.titulo.value;
    }
    this.memoria.responsable = form.personaResponsable.value;
    return this.memoria;
  }

  public onComiteChange(comite: IComite) {
    this.showTitulo = comite.id === COMITE.CEEA;

    this.getFormGroup().controls.tipoMemoria.markAsTouched();
  }

  public onTipoMemoriaChange(tipoMemoria: ITipoMemoria) {
    this.showMemoriaOriginal = tipoMemoria?.id === TIPO_MEMORIA.MODIFICACION;
    if (this.isInvestigador) {
      this.checkShowInfoRatificacion(tipoMemoria);
    } else {
      this.showInfoRatificacion = false;
    }

    if (this.showMemoriaOriginal) {
      this.getFormGroup().controls.memoriaOriginal.setValidators(Validators.required);
    }
    else {
      this.getFormGroup().controls.memoriaOriginal.clearValidators();
    }
    this.getFormGroup().controls.memoriaOriginal.updateValueAndValidity();
  }

  saveOrUpdate(): Observable<number> {
    const datosGenerales = this.getValue();
    const peticionEvaluacionData = datosGenerales.peticionEvaluacion;
    datosGenerales.peticionEvaluacion = {} as IPeticionEvaluacion;
    datosGenerales.peticionEvaluacion.id = this.idPeticionEvaluacion;
    const obs = this.isEdit()
      ? this.service.update(this.getKey() as number, datosGenerales)
      : datosGenerales.tipoMemoria.id === TIPO_MEMORIA.MODIFICACION
        ? this.service.createMemoriaModificada(datosGenerales, this.getFormGroup().controls.memoriaOriginal.value.id)
        : this.service.create(datosGenerales);
    return obs.pipe(
      map((value) => {
        value.responsable = datosGenerales.responsable;
        // Se reestablecen los datos originales de la petición de evalución.
        value.peticionEvaluacion = peticionEvaluacionData;
        this.memoria = value;
        return this.memoria.id;
      })
    );
  }

  protected initializer(key: number): Observable<IMemoria> {
    if (this.getKey()) {
      return this.service.findById(key).pipe(
        switchMap((memoria) => {
          if (memoria.responsable && memoria.responsable.id) {
            return this.personaService.findById(memoria.responsable.id).pipe(
              map((persona) => {
                memoria.responsable = persona;
                return memoria;
              }),
              catchError(() => of(memoria)),
            );
          } else {
            return of(memoria);
          }
        }),
        switchMap(memoria => {
          if (memoria.estadoActual.id === ESTADO_MEMORIA.SUBSANACION) {
            return this.service.getEstadoActual(memoria.id).pipe(
              tap(estadoMemoria => {
                this.estadoMemoria = estadoMemoria;
                this.showComentarioSubsanacion = true;
              }),
              map(_ => memoria)
            )
          }

          return of(memoria);
        }),
        tap((memoria) => {
          this.loadResponsable(memoria.peticionEvaluacion.id);
        })
      );
    }
  }

  private checkShowInfoRatificacion(tipoMemoria: ITipoMemoria) {
    if (tipoMemoria?.id === TIPO_MEMORIA.RATIFICACION && this.memoria.estadoActual) {
      const estado = this.memoria.estadoActual.id as ESTADO_MEMORIA;
      if (estado === ESTADO_MEMORIA.COMPLETADA || estado === ESTADO_MEMORIA.EN_ELABORACION
        || estado === ESTADO_MEMORIA.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS
        || estado === ESTADO_MEMORIA.PENDIENTE_CORRECCIONES) {
        this.showInfoRatificacion = true;
      }
    } else if (tipoMemoria?.id === TIPO_MEMORIA.RATIFICACION && !this.memoria.estadoActual) {
      this.showInfoRatificacion = true;
    } else if (tipoMemoria?.id !== TIPO_MEMORIA.RATIFICACION) {
      this.showInfoRatificacion = false;
    }
  }

}
