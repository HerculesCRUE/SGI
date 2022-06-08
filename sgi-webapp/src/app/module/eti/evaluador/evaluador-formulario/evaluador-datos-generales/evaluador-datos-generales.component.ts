import { Component, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { CargoComite } from '@core/models/eti/cargo-comite';
import { IEvaluador } from '@core/models/eti/evaluador';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { CargoComiteService } from '@core/services/eti/cargo-comite.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { EvaluadorActionService } from '../../evaluador.action.service';
import { EvaluadorDatosGeneralesFragment } from './evaluador-datos-generales.fragment';

const MSG_ERROR_INIT_ = marker('error.load');
const EVALUDADOR_COMITE_KEY = marker('label.eti.comite');
const EVALUADOR_FECHA_ALTA_KEY = marker('eti.evaluador.fecha-alta');
const EVALUADOR_FECHA_BAJA_KEY = marker('eti.evaluador.fecha-baja');
const EVALUADOR_CARGO_COMITE_KEY = marker('eti.evaluador.cargo-comite');
const EVALUADOR_RESUMEN_KEY = marker('eti.evaluador.resumen');
const EVALUADOR_PERSONA_KEY = marker('title.eti.search.user');

@Component({
  selector: 'sgi-evaluador-datos-generales',
  templateUrl: './evaluador-datos-generales.component.html',
  styleUrls: ['./evaluador-datos-generales.component.scss']
})
export class EvaluadorDatosGeneralesComponent extends FormFragmentComponent<IEvaluador> implements OnInit {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;

  cargosComite$: Observable<CargoComite[]>;
  evaluador: IEvaluador;

  msgParamComiteEntity = {};
  msgParamFechaAltaEntity = {};
  msgParamFechaBajaEntity = {};
  msgParamCargoComiteEntity = {};
  msgParamResumenEntity = {};
  msgParamPersonaEntity = {};

  datosGeneralesFragment: EvaluadorDatosGeneralesFragment;

  isEditForm: boolean;

  get tipoColectivoEvaluador() {
    return TipoColectivo.EVALUADOR_ETICA;
  }

  constructor(
    private cargoComiteService: CargoComiteService,
    actionService: EvaluadorActionService,
    private translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.datosGeneralesFragment = this.fragment as EvaluadorDatosGeneralesFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.isEditForm = this.datosGeneralesFragment.isEdit();

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '3';
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();

    this.cargosComite$ = this.cargoComiteService.findAll().pipe(
      map(response => response.items)
    );
  }

  private setupI18N(): void {
    this.translate.get(
      EVALUDADOR_COMITE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComiteEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EVALUADOR_CARGO_COMITE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCargoComiteEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EVALUADOR_FECHA_ALTA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaAltaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EVALUADOR_FECHA_BAJA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaBajaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      EVALUADOR_RESUMEN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamResumenEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      EVALUADOR_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamPersonaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });
  }

}
