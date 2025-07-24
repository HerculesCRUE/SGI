import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoAmbitoGeograficoRequest } from './tipo-ambito-geografico-request';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipos-configuracion';
import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';

class TipoAmbitoGeograficoRequestConverter extends SgiBaseConverter<ITipoAmbitoGeograficoRequest, ITipoAmbitoGeografico> {
  toTarget(value: ITipoAmbitoGeograficoRequest): ITipoAmbitoGeografico {
    if (!value) {
      return value as unknown as ITipoAmbitoGeografico;
    }
    return {
      id: undefined,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      activo: true
    };
  }
  fromTarget(value: ITipoAmbitoGeografico): ITipoAmbitoGeograficoRequest {
    if (!value) {
      return value as unknown as ITipoAmbitoGeograficoRequest;
    }
    return {
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
    };
  }
}

export const TIPO_AMBITO_GEOGRAFICO_REQUEST_CONVERTER = new TipoAmbitoGeograficoRequestConverter();
