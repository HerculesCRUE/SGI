import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_FINANCIACION_MAP, IPeticionEvaluacion, TIPO_VALOR_SOCIAL_MAP } from '@core/models/eti/peticion-evaluacion';
import { ITipoActividad } from '@core/models/eti/tipo-actividad';
import { ITipoInvestigacionTutelada } from '@core/models/eti/tipo-investigacion-tutelada';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TipoActividadService } from '@core/services/eti/tipo-actividad.service';
import { TipoInvestigacionTuteladaService } from '@core/services/eti/tipo-investigacion-tutelada.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { PeticionEvaluacionActionService } from '../../peticion-evaluacion.action.service';
import { PeticionEvaluacionDatosGeneralesFragment } from './peticion-evaluacion-datos-generales.fragment';

const PETICION_EVALUACION_CODIGO_KEY = marker('eti.peticion-evaluacion.codigo');
const PETICION_EVALUACION_TITULO_KEY = marker('eti.peticion-evaluacion.titulo');
const PETICION_EVALUACION_FINANCIACION_KEY = marker('eti.peticion-evaluacion.organo-financiador');
const PETICION_EVALUACION_FECHA_INICIO_KEY = marker('eti.peticion-evaluacion.fecha-inicio');
const PETICION_EVALUACION_FECHA_FIN_KEY = marker('eti.peticion-evaluacion.fecha-fin');
const PETICION_EVALUACION_RESUMEN_KEY = marker('eti.peticion-evaluacion.resumen');
const PETICION_EVALUACION_VALOR_SOCIAL_KEY = marker('eti.peticion-evaluacion.valor-social');
const PETICION_EVALUACION_OBJETIVO_CIENTIFICO_KEY = marker('eti.peticion-evaluacion.objetivo-cientifico');
const PETICION_EVALUACION_DISENIO_METODOLOGICO_KEY = marker('eti.peticion-evaluacion.disenio-metodologico');
const PETICION_EVALUACION_TIPO_ACTIVIDAD_KEY = marker('eti.peticion-evaluacion.tipo-actividad');
const PETICION_EVALUACION_TIPO_INVESTIGACION_TUTELADA_KEY = marker('eti.peticion-evaluacion.tipo-investigacion-tutelada');
const PETICION_EVALUACION_EXISTE_FINANCIACION_KEY = marker('eti.peticion-evaluacion.existe-financiacion');
const PETICION_EVALUACION_ESTADO_FINANCIACION_KEY = marker('eti.peticion-evaluacion.estado-financiacion');
const PETICION_EVALUACION_IMPORTE_FINANCIACION_KEY = marker('eti.peticion-evaluacion.importe-financiacion');
const PETICION_EVALUACION_OTRO_VALOR_SOCIAL_KEY = marker('eti.peticion-evaluacion.otro-valor-social');
const PETICION_EVALUACION_TIENE_FONDOS_PROPIOS_KEY = marker('eti.peticion-evaluacion.tiene-fondos-propios');
const PETICION_EVALUACION_TUTOR_KEY = marker('eti.peticion-evaluacion.tutor');

@Component({
  selector: 'sgi-peticion-evaluacion-datos-generales',
  templateUrl: './peticion-evaluacion-datos-generales.component.html',
  styleUrls: ['./peticion-evaluacion-datos-generales.component.scss']
})

export class PeticionEvaluacionDatosGeneralesComponent extends FormFragmentComponent<IPeticionEvaluacion> implements OnInit, OnDestroy {
  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  private suscripciones: Subscription[] = [];
  tipoInvestigacionTuteladas$: Observable<ITipoInvestigacionTutelada[]>;
  tipoActividades$: Observable<ITipoActividad[]>;

  isInvestigacionTutelada$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  peticionEvaluacionFragment: PeticionEvaluacionDatosGeneralesFragment;

  msgParamFinanciacionEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamFechaFinEntity = {};
  msgParamResumenEntity = {};
  msgParamValorSocialEntity = {};
  msgParamObjetivoCientificoEntity = {};
  msgParamDisenioMetodologicoEntity = {};
  msgParamTituloEntity = {};
  msgParamCodigoEntity = {};
  msgParamTipoActividadEntity = {};
  msgParamTipoInvestigacionTuteladaEntity = {};
  msgParamExisteFinanciacionEntity = {};
  msgParamEstadoFinanciacionEntity = {};
  msgParamImporteFinanciacionEntity = {};
  msgParamOtroValorSocialEntity = {};
  msgParamTieneFondosPropiosEntity = {};
  msgParamTutorEntity = {};

  get ESTADO_FINANCIACION_MAP() {
    return ESTADO_FINANCIACION_MAP;
  }

  get TIPO_VALOR_SOCIAL_MAP() {
    return TIPO_VALOR_SOCIAL_MAP;
  }

  get tipoColectivoTutor() {
    return TipoColectivo.TUTOR_CSP;
  }

  constructor(
    private readonly tipoActividadService: TipoActividadService,
    private readonly tipoInvestigacionTuteladaService: TipoInvestigacionTuteladaService,
    private actionService: PeticionEvaluacionActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '4';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.peticionEvaluacionFragment = this.fragment as PeticionEvaluacionDatosGeneralesFragment;
    this.isInvestigacionTutelada$ = (this.fragment as PeticionEvaluacionDatosGeneralesFragment).isTipoInvestigacionTutelada$;

    this.tipoActividades$ = this.tipoActividadService.findAll().pipe(
      map(response => response.items));

    this.tipoInvestigacionTuteladas$ = this.tipoInvestigacionTuteladaService.findAll().pipe(
      map(response => response.items));
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.actionService.initializeEquiposInvestigador();

    this.formGroup.controls.tipoActividad.valueChanges.subscribe(value => {
      this.selectTipoActividad(value);
    });
  }

  private setupI18N(): void {
    this.translate.get(
      PETICION_EVALUACION_CODIGO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PETICION_EVALUACION_TITULO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PETICION_EVALUACION_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFinanciacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PETICION_EVALUACION_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PETICION_EVALUACION_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PETICION_EVALUACION_RESUMEN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamResumenEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PETICION_EVALUACION_VALOR_SOCIAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamValorSocialEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PETICION_EVALUACION_OBJETIVO_CIENTIFICO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamObjetivoCientificoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      PETICION_EVALUACION_DISENIO_METODOLOGICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDisenioMetodologicoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PETICION_EVALUACION_TIPO_ACTIVIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoActividadEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PETICION_EVALUACION_TIPO_INVESTIGACION_TUTELADA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoInvestigacionTuteladaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PETICION_EVALUACION_EXISTE_FINANCIACION_KEY
    ).subscribe((value) => this.msgParamExisteFinanciacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PETICION_EVALUACION_ESTADO_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEstadoFinanciacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PETICION_EVALUACION_IMPORTE_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamImporteFinanciacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PETICION_EVALUACION_OTRO_VALOR_SOCIAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamOtroValorSocialEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PETICION_EVALUACION_TIENE_FONDOS_PROPIOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTieneFondosPropiosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PETICION_EVALUACION_TUTOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTutorEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

  }

  selectTipoActividad(tipoActividad: ITipoActividad): void {
    if (tipoActividad?.id === 3) {
      this.isInvestigacionTutelada$.next(true);
      this.formGroup.controls.tipoInvestigacionTutelada.setValidators([Validators.required]);
      this.formGroup.controls.tutor.setValidators([Validators.required]);
    } else {
      this.isInvestigacionTutelada$.next(false);
      this.formGroup.controls.tipoInvestigacionTutelada.clearValidators();
      this.formGroup.controls.tutor.clearValidators();
    }
    this.formGroup.controls.tipoInvestigacionTutelada.updateValueAndValidity();
    this.formGroup.controls.tutor.updateValueAndValidity();
  }

  ngOnDestroy(): void {
    this.suscripciones?.forEach(x => x.unsubscribe());
  }

}
