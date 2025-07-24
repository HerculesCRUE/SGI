import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoFaseResponse } from './tipo-fase-response';

class TipoFaseResponseConverter extends SgiBaseConverter<ITipoFaseResponse, ITipoFase> {
  toTarget(value: ITipoFaseResponse): ITipoFase {
    if (!value) {
      return value as unknown as ITipoFase;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ITipoFase): ITipoFaseResponse {
    if (!value) {
      return value as unknown as ITipoFaseResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
}

export const TIPO_FASE_RESPONSE_CONVERTER = new TipoFaseResponseConverter();
