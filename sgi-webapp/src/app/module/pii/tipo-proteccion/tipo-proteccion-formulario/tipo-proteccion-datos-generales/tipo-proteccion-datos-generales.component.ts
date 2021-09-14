import { Component, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { TIPO_PROPIEDAD_MAP } from '@core/enums/tipo-propiedad';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';
import { TipoProteccionActionService } from '../../tipo-proteccion.action.service';

const TIPO_PROTECCION_NOMBRE_KEY = marker('pii.tipo-proteccion.nombre');
const TIPO_PROTECCION_DESCRIPCION_KEY = marker('pii.tipo-proteccion.descripcion');
const TIPO_PROTECCION_TIPO_PROPIEDAD_KEY = marker('pii.tipo-proteccion.tipo-propiedad');

@Component({
  selector: 'sgi-tipo-proteccion-datos-generales',
  templateUrl: './tipo-proteccion-datos-generales.component.html',
  styleUrls: ['./tipo-proteccion-datos-generales.component.scss']
})
export class TipoProteccionDatosGeneralesComponent extends FormFragmentComponent<ITipoProteccion> implements OnInit {

  FormGroupUtil = FormGroupUtil;
  msgParamNombreEntity = {};
  msgParamDescripcionEntity = {};
  msgParamTipoPropiedadEntity = {};
  key: number;

  get TIPO_PROPIEDAD_MAP() {
    return TIPO_PROPIEDAD_MAP;
  }

  constructor(
    readonly actionService: TipoProteccionActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.key = this.fragment.getKey() as number;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_PROTECCION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TIPO_PROTECCION_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TIPO_PROTECCION_TIPO_PROPIEDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamTipoPropiedadEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

}
