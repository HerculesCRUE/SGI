import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { COMITE } from '@core/models/eti/comite';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { FormacionEspecifica } from '@core/models/eti/formacion-especifica';
import { FORMULARIO } from '@core/models/eti/formulario';
import { IMemoria } from '@core/models/eti/memoria';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { ITareaWithIsEliminable } from '@core/models/eti/tarea-with-is-eliminable';
import { TipoTarea } from '@core/models/eti/tipo-tarea';
import { IPersona } from '@core/models/sgp/persona';
import { EquipoTrabajoService } from '@core/services/eti/equipo-trabajo.service';
import { FormacionEspecificaService } from '@core/services/eti/formacion-especifica.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { TareaService } from '@core/services/eti/tarea.service';
import { TipoTareaService } from '@core/services/eti/tipo-tarea.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subject, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

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

  formaciones$: Subject<FormacionEspecifica[]> = new BehaviorSubject<FormacionEspecifica[]>([]);
  personas$: Subject<IPersona[]> = new BehaviorSubject<IPersona[]>([]);
  tipoTareas$: Subject<TipoTarea[]> = new BehaviorSubject<TipoTarea[]>([]);

  mostrarOrganismo: boolean;
  mostrarOrganismo$ = new BehaviorSubject(false);
  mostrarOrganismoSubscription: Subscription;

  mostrarOrganismoYanio: boolean;
  mostrarOrganismoYanio$ = new BehaviorSubject(false);
  mostrarOrganismoYanioSubscription: Subscription;

  tareaYformacionTexto: boolean;
  tareaYformacionTexto$ = new BehaviorSubject(false);
  tareaYformacionTextoSubscription: Subscription;

  title: string;
  textoAceptar: string;

  msgParamEquipoTrabajoEntity = {};
  msgParamMemoriaEntity = {};
  msgParamTareaEntity = {};
  msgParamFormacionEntity = {};
  msgParamAnioEntity = {};
  msgParamOrganismoEntity = {};

  textoFormacionExperiencia$ = new BehaviorSubject(null);

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<PeticionEvaluacionTareasModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PeticionEvaluacionTareasModalComponentData,
    protected readonly tareaService: TareaService,
    protected readonly formacionService: FormacionEspecificaService,
    protected readonly memoriaService: MemoriaService,
    protected readonly equipoTrabajoService: EquipoTrabajoService,
    protected readonly personaService: PersonaService,
    protected readonly tipoTareaService: TipoTareaService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data.tarea?.memoria);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.mostrarOrganismoYanioSubscription = this.mostrarOrganismoYanio$.subscribe(mostrar => {
      this.mostrarOrganismoYanio = mostrar;
    });
    this.tareaYformacionTextoSubscription = this.tareaYformacionTexto$.subscribe(mostrar => {
      this.tareaYformacionTexto = mostrar;
    });
    this.onChangeMemoria(this.data.tarea?.memoria);
    this.loadFormaciones();
    this.loadTipoTareas();
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
      if (this.data.tarea?.memoria.comite.formulario.id === FORMULARIO.M10) {
        this.translate.get(
          TAREA_EXPERIENCIA_KEY
        ).subscribe((value) => this.textoFormacionExperiencia$.next(value));
      } else {
        this.translate.get(
          TAREA_FORMACION_ESPECIFICA_KEY
        ).subscribe((value) => this.textoFormacionExperiencia$.next(value));
      }

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

      this.translate.get(
        TAREA_FORMACION_ESPECIFICA_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.textoFormacionExperiencia$.next(value));
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
      TAREA_FORMACION_ESPECIFICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFormacionEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TAREA_ANIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAnioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      TAREA_ORGANISMO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamOrganismoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });
  }

  /**
   * Recupera un listado de las formaciones escíficas que hay en el sistema.
   */
  private loadFormaciones(): void {
    this.subscriptions.push(this.formacionService.findAll().subscribe(
      (response) => {
        this.formaciones$.next(response.items);
      }
    ));
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
    if (memoria?.comite?.id === COMITE.CBE || memoria?.comite?.id === COMITE.CEEA) {
      this.mostrarOrganismoYanio$.next(true);
      this.formGroup.controls.organismo.setValidators([Validators.required, Validators.maxLength(250)]);
      this.formGroup.controls.anio.setValidators(Validators.required);
    } else {
      this.mostrarOrganismoYanio$.next(false);
      this.formGroup.controls.organismo.clearValidators();
      this.formGroup.controls.anio.clearValidators();
    }

    if (memoria?.comite?.id === COMITE.CEI || memoria?.comite?.id === COMITE.CBE) {
      this.tareaYformacionTexto$.next(true);
      this.formGroup.controls.tarea.setValidators([Validators.required, Validators.maxLength(250)]);
      this.formGroup.controls.formacion.setValidators([Validators.required, Validators.maxLength(250)]);
      this.formGroup.controls.tipoTarea.clearValidators();
      this.formGroup.controls.formacionEspecifica.clearValidators();
    } else {
      this.tareaYformacionTexto$.next(false);
      this.formGroup.controls.tipoTarea.setValidators(Validators.required);
      this.formGroup.controls.formacionEspecifica.setValidators(Validators.required);
      this.formGroup.controls.tarea.clearValidators();
      this.formGroup.controls.formacion.clearValidators();
    }

    if (memoria?.comite?.formulario.id === FORMULARIO.M10 || memoria?.comite?.formulario.id === FORMULARIO.M30) {
      this.translate.get(
        TAREA_EXPERIENCIA_KEY
      ).subscribe((value) => this.textoFormacionExperiencia$.next(value));
    } else {
      this.translate.get(
        TAREA_FORMACION_ESPECIFICA_KEY
      ).subscribe((value) => this.textoFormacionExperiencia$.next(value));
    }

    this.formGroup.controls.anio.updateValueAndValidity();
    this.formGroup.controls.formacion.updateValueAndValidity();
    this.formGroup.controls.formacionEspecifica.updateValueAndValidity();
    this.formGroup.controls.organismo.updateValueAndValidity();
    this.formGroup.controls.tarea.updateValueAndValidity();
    this.formGroup.controls.tipoTarea.updateValueAndValidity();
  }

  /**
   * Devuelve la persona del equipo de trabajo
   * @param persona la entidad IPersona
   * returns persona del equipo de trabajo
   */
  displayerPersonaEquipoTrabajo = (persona: IPersona): string => {
    return persona && persona.id ?
      `${persona?.nombre} ${persona?.apellidos} (${this.getEmailPrincipal(persona)})` : null;
  }

  private getEmailPrincipal({ emails }: IPersona): string {
    if (!emails) {
      return '';
    }
    const emailDataPrincipal = emails.find(emailData => emailData.principal);
    return emailDataPrincipal?.email ?? '';
  }

  /**
   * Recupera un listado de los tipos de tareas que hay en el sistema.
   */
  private loadTipoTareas(): void {
    this.subscriptions.push(this.tipoTareaService.findAll().subscribe(
      (response) => {
        this.tipoTareas$.next(response.items);
      }));
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

      this.subscriptions.push(this.personaService.findAllByIdIn([...personaIds]).subscribe((result) => {
        this.personas$.next(result.items);
      }));
    }
  }

  protected getValue(): PeticionEvaluacionTareasModalComponentData {
    if (this.mostrarOrganismoYanio) {
      this.data.tarea.organismo = this.formGroup.controls.organismo.value;
      this.data.tarea.anio = this.formGroup.controls.anio.value;
    } else {
      this.data.tarea.organismo = null;
      this.data.tarea.anio = null;
    }

    if (this.tareaYformacionTexto) {
      this.data.tarea.tipoTarea = null;
      this.data.tarea.formacionEspecifica = null;
      this.data.tarea.tarea = this.formGroup.controls.tarea.value;
      this.data.tarea.formacion = this.formGroup.controls.formacion.value;
    } else {
      this.data.tarea.tarea = null;
      this.data.tarea.formacion = null;
      this.data.tarea.tipoTarea = this.formGroup.controls.tipoTarea.value;
      this.data.tarea.formacionEspecifica = this.formGroup.controls.formacionEspecifica.value;
    }

    this.data.tarea.memoria = this.formGroup.controls.memoria.value;
    this.data.tarea.equipoTrabajo = {} as IEquipoTrabajo;
    this.data.equiposTrabajo.filter(equipo => equipo.persona.id === this.formGroup.controls.equipoTrabajo.value.id)
      .forEach(equipoTrabajo => this.data.tarea.equipoTrabajo = equipoTrabajo);
    this.data.tarea.equipoTrabajo.persona = this.formGroup.controls.equipoTrabajo.value;

    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tarea: new FormControl(this.data.tarea?.tarea),
      tipoTarea: new FormControl(this.data.tarea?.tipoTarea),
      organismo: new FormControl(this.data.tarea?.organismo),
      anio: new FormControl(this.data.tarea?.anio),
      formacionEspecifica: new FormControl(this.data.tarea?.formacionEspecifica),
      formacion: new FormControl(this.data.tarea?.formacion),
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
