import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoAmbitoGeograficoResponse } from './tipo-ambito-geografico-response';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipos-configuracion';
import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';

class TipoAmbitoGeograficoResponseConverter extends SgiBaseConverter<ITipoAmbitoGeograficoResponse, ITipoAmbitoGeografico> {
  toTarget(value: ITipoAmbitoGeograficoResponse): ITipoAmbitoGeografico {
    if (!value) {
      return value as unknown as ITipoAmbitoGeografico;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ITipoAmbitoGeografico): ITipoAmbitoGeograficoResponse {
    if (!value) {
      return value as unknown as ITipoAmbitoGeograficoResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
}

export const TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER = new TipoAmbitoGeograficoResponseConverter();
