import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { COMITE, IComite } from '@core/models/eti/comite';
import { IMemoria } from '@core/models/eti/memoria';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
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
  public showCodOrganoCompetente = false;
  public showTitulo = false;
  public showMemoriaOriginal = false;
  public personasResponsable$: BehaviorSubject<IPersona[]> = new BehaviorSubject<IPersona[]>([]);
  public mostrarCodOrgano = false;

  private idPeticionEvaluacion: number;

  constructor(
    private fb: FormBuilder, readonly: boolean, key: number, private service: MemoriaService,
    private personaService: PersonaService,
    private readonly peticionEvaluacionService: PeticionEvaluacionService) {
    super(key);
    this.memoria = {} as IMemoria;
    this.readonly = readonly;
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
        { value: this.isEdit() ? this.memoria.titulo : '', disabled: this.readonly }
      ],
      personaResponsable: [
        { value: null, disabled: this.readonly }
      ],
      codOrganoCompetente: [
        { value: this.isEdit() ? this.memoria.codOrganoCompetente : '', disabled: this.readonly },
        Validators.maxLength(250)
      ],
      memoriaOriginal: [
        { value: this.isEdit() ? this.memoria.memoriaOriginal : null, disabled: this.isEdit() },
        Validators.required
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
      personaResponsable: value.responsable,
      codOrganoCompetente: value.codOrganoCompetente,
      memoriaOriginal: value.memoriaOriginal
    };
  }

  getValue(): IMemoria {
    const form = this.getFormGroup().controls;
    this.memoria.comite = form.comite.value;
    this.memoria.tipoMemoria = form.tipoMemoria.value;

    if (this.showTitulo && form.titulo.value) {
      this.memoria.titulo = form.titulo.value;
    }
    this.memoria.responsable = form.personaResponsable.value;
    if (this.memoria.comite.id === COMITE.CEEA) {
      this.memoria.codOrganoCompetente = form.codOrganoCompetente.value;
    } else {
      this.memoria.codOrganoCompetente = null;
    }
    return this.memoria;
  }

  public onComiteChange(comite: IComite) {
    this.showCodOrganoCompetente = comite.id === COMITE.CEEA;
    this.showTitulo = comite.id === COMITE.CEEA;

    this.getFormGroup().controls.tipoMemoria.markAsTouched();
    this.getFormGroup().controls.codOrganoCompetente?.reset();
  }

  public onTipoMemoriaChange(tipoMemoria: ITipoMemoria) {
    this.showMemoriaOriginal = tipoMemoria?.id === TIPO_MEMORIA.MODIFICACION;
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
        tap((memoria) => {
          this.loadResponsable(memoria.peticionEvaluacion.id);
        })
      );
    }
  }

}
