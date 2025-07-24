import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoFacturacionRequest } from './tipo-facturacion-request';

class TipoFacturacionRequestConverter extends SgiBaseConverter<ITipoFacturacionRequest, ITipoFacturacion> {

  toTarget(value: ITipoFacturacionRequest): ITipoFacturacion {
    return !value ? value as unknown as ITipoFacturacion :
      {
        id: undefined,
        nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
        incluirEnComunicado: value.incluirEnComunicado,
        activo: true
      };
  }

  fromTarget(value: ITipoFacturacion): ITipoFacturacionRequest {
    return !value ? value as unknown as ITipoFacturacionRequest :
      {
        nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
        incluirEnComunicado: value.incluirEnComunicado
      };
  }

}

export const TIPO_FACTURACION_REQUEST_CONVERTER = new TipoFacturacionRequestConverter();
