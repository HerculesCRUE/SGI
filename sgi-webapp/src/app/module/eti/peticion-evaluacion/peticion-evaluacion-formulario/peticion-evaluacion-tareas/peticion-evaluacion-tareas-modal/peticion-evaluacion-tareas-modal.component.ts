import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { IMemoria } from '@core/models/eti/memoria';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { ITareaWithIsEliminable } from '@core/models/eti/tarea-with-is-eliminable';
import { IPersona } from '@core/models/sgp/persona';
import { FormacionEspecificaService } from '@core/services/eti/formacion-especifica.service';
import { TareaService } from '@core/services/eti/tarea.service';
import { TipoTareaService } from '@core/services/eti/tipo-tarea.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { I18nValidators } from '@core/validators/i18n-validator';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const TITLE_NEW_ENTITY = marker('title.new.entity');
const TAREA_KEY = marker('eti.peticion-evaluacion.tarea');
const BTN_ADD = marker('btn.add');
const BTN_OK = marker('btn.ok');
const TAREA_EQUIPO_TRABAJO_KEY = marker('eti.peticion-evaluacion.tarea.persona');
const TAREA_MEMORIA_KEY = marker('eti.memoria');
const TAREA_FORMACION_ESPECIFICA_KEY = marker('eti.peticion-evaluacion.tarea.formacion-especifica');
const TAREA_EXPERIENCIA_KEY = marker('eti.peticion-evaluacion.tarea.experiencia');
const TAREA_ANIO_KEY = marker('eti.peticion-evaluacion.tarea.anio');
const TAREA_ORGANISMO_KEY = marker('eti.peticion-evaluacion.tarea.organismo');
const SGP_NOT_FOUND = marker("error.sgp.not-found");

export interface PeticionEvaluacionTareasModalComponentData {
  tarea: ITareaWithIsEliminable;
  equiposTrabajo: IEquipoTrabajo[];
  memorias: IMemoriaPeticionEvaluacion[];
}

@Component({
  selector: 'sgi-peticion-evaluacion-tareas-modal',
  templateUrl: './peticion-evaluacion-tareas-modal.component.html',
  styleUrls: ['./peticion-evaluacion-tareas-modal.component.scss']
})

export class PeticionEvaluacionTareasModalComponent
  extends DialogFormComponent<PeticionEvaluacionTareasModalComponentData> implements OnInit {

  formaciones$ = this.formacionService.findAll().pipe(map(response => response.items));
  personas$: Subject<IPersona[]> = new BehaviorSubject<IPersona[]>([]);
  tipoTareas$ = this.tipoTareaService.findAll().pipe(map(response => response.items));

  tareaLibre = false;
  formacionLibre = false;
  detalleFormacion = false;

  title: string;
  textoAceptar: string;

  msgParamEquipoTrabajoEntity = {};
  msgParamMemoriaEntity = {};
  msgParamTareaEntity = {};
  msgParamFormacionEntity = {};
  msgParamFormacionEspecificaEntity = {};
  msgParamAnioEntity = {};
  msgParamOrganismoEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<PeticionEvaluacionTareasModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PeticionEvaluacionTareasModalComponentData,
    protected readonly tareaService: TareaService,
    private readonly formacionService: FormacionEspecificaService,
    protected readonly personaService: PersonaService,
    private readonly tipoTareaService: TipoTareaService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data.tarea?.memoria);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.onChangeMemoria(this.data.tarea?.memoria);
    this.loadDatosUsuario(this.data.equiposTrabajo);
  }

  private setupI18N(): void {
    if (this.data.tarea?.memoria) {
      this.translate.get(
        TAREA_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.translate.get(
        BTN_OK,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.textoAceptar = value);

    } else {
      this.translate.get(
        TAREA_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);

      this.translate.get(
        BTN_ADD,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.textoAceptar = value);
    }

    this.translate.get(
      TAREA_EQUIPO_TRABAJO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEquipoTrabajoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TAREA_MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMemoriaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TAREA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTareaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TAREA_EXPERIENCIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFormacionEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TAREA_FORMACION_ESPECIFICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFormacionEspecificaEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TAREA_ANIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAnioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TAREA_ORGANISMO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamOrganismoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  /**
   * Devuelve el titulo de una memoria
   * @param memoria memoria
   * returns título de la memoria
   */
  displayerMemoria(memoria: IMemoria): string {
    return memoria?.numReferencia;
  }

  /**
   * Evento click de selector de memorias
   * @param memoria memoria
   * muestra y oculta los campos oportunos
   */
  private onChangeMemoria(memoria: IMemoria): void {
    this.tareaLibre = !!memoria?.comite?.tareaNombreLibre;
    this.formacionLibre = !!memoria?.comite?.tareaExperienciaLibre;
    this.detalleFormacion = !!memoria?.comite?.tareaExperienciaDetalle;
    if (this.tareaLibre) {
      this.formGroup.controls.tipoTarea.disable();
      this.formGroup.controls.nombre.enable();
    }
    else {
      this.formGroup.controls.tipoTarea.enable();
      this.formGroup.controls.nombre.disable();
    }
    if (this.formacionLibre) {
      this.formGroup.controls.formacionEspecifica.disable();
      this.formGroup.controls.formacion.enable();
    }
    else {
      this.formGroup.controls.formacionEspecifica.enable();
      this.formGroup.controls.formacion.disable();
    }
    if (this.detalleFormacion) {
      this.formGroup.controls.anio.enable();
      this.formGroup.controls.organismo.enable();
    }
    else {
      this.formGroup.controls.anio.disable();
      this.formGroup.controls.organismo.disable();
    }
  }

  /**
   * Devuelve la persona del equipo de trabajo
   * @param persona la entidad IPersona
   * returns persona del equipo de trabajo
   */
  displayerPersonaEquipoTrabajo = (persona: IPersona): string => {
    if (persona?.nombre) {
      return `${persona?.nombre} ${persona?.apellidos} (${this.getEmailPrincipal(persona)})`;
    } else if (persona?.id) {
      return this.translate.instant(SGP_NOT_FOUND, { ids: persona.id, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
    } else {
      return null;
    }
  }

  private getEmailPrincipal({ emails }: IPersona): string {
    if (!emails) {
      return '';
    }
    const emailDataPrincipal = emails.find(emailData => emailData.principal);
    return emailDataPrincipal?.email ?? '';
  }

  /**
   * Devuelve los datos de persona de los equipos de trabajo
   * @param equiposTrabajo listado de equipos de trabajo
   * returns los equipos de trabajo con los datos de persona
   */
  private loadDatosUsuario(equiposTrabajo: IEquipoTrabajo[]) {
    const personaIds = new Set<string>();

    if (equiposTrabajo && equiposTrabajo.length > 0) {
      equiposTrabajo.forEach((equipoTrabajo: IEquipoTrabajo) => {
        personaIds.add(equipoTrabajo?.persona?.id);
      });

      const idsArray = Array.from(personaIds);

      this.subscriptions.push(
        this.personaService.findAllByIdIn(idsArray).pipe(
          map((result) => {
            const personasMap = new Map<string, IPersona>();
            result.items.forEach((persona) => {
              personasMap.set(persona.id, persona);
            });

            // Asegurar que todos los ids estén presentes
            const personasCompletas = idsArray.map((id) => {
              return personasMap.get(id) || { id } as IPersona;
            });

            return personasCompletas;
          })
        ).subscribe((personasCompletas) => {
          this.personas$.next(personasCompletas);
        })
      );
    }
  }

  protected getValue(): PeticionEvaluacionTareasModalComponentData {
    this.data.tarea.equipoTrabajo = {} as IEquipoTrabajo;
    this.data.equiposTrabajo.filter(equipo => equipo.persona.id === this.formGroup.controls.equipoTrabajo.value.id)
      .forEach(equipoTrabajo => this.data.tarea.equipoTrabajo = equipoTrabajo);
    this.data.tarea.equipoTrabajo.persona = this.formGroup.controls.equipoTrabajo.value;

    this.data.tarea.memoria = this.formGroup.controls.memoria.value;
    this.data.tarea.nombre = this.tareaLibre ? this.formGroup.controls.nombre.value : [];
    this.data.tarea.tipoTarea = this.tareaLibre ? null : this.formGroup.controls.tipoTarea.value;
    this.data.tarea.formacion = this.formacionLibre ? this.formGroup.controls.formacion.value : [];
    this.data.tarea.formacionEspecifica = this.formacionLibre ? null : this.formGroup.controls.formacionEspecifica.value;
    this.data.tarea.organismo = this.detalleFormacion ? this.formGroup.controls.organismo.value : [];
    this.data.tarea.anio = this.detalleFormacion ? this.formGroup.controls.anio.value : null;

    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.data.tarea?.nombre ?? [], [Validators.required]),
      tipoTarea: new FormControl(this.data.tarea?.tipoTarea, [Validators.required]),
      organismo: new FormControl(this.data.tarea?.organismo ?? [], [I18nValidators.required, I18nValidators.maxLength(250)]),
      anio: new FormControl(this.data.tarea?.anio, [Validators.required]),
      formacionEspecifica: new FormControl(this.data.tarea?.formacionEspecifica, [Validators.required]),
      formacion: new FormControl(this.data.tarea?.formacion ?? [], [I18nValidators.required]),
      memoria: new FormControl(this.data.tarea?.memoria, [Validators.required]),
      equipoTrabajo: new FormControl(this.data.tarea?.equipoTrabajo == null ? '' :
        this.data.tarea?.equipoTrabajo.persona, [Validators.required])
    });

    formGroup.controls.memoria.valueChanges.subscribe((memoria) => {
      this.onChangeMemoria(memoria);
    });

    return formGroup;
  }

}
