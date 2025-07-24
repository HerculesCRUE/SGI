import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IFormacionEspecifica } from '@core/models/eti/formacion-especifica';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IFormacionEspecificaResponse } from './formacion-especifica-response';

class FormacionEspecificaResponseConverter extends SgiBaseConverter<IFormacionEspecificaResponse, IFormacionEspecifica> {
  toTarget(value: IFormacionEspecificaResponse): IFormacionEspecifica {
    if (!value) {
      return value as unknown as IFormacionEspecifica;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }

  fromTarget(value: IFormacionEspecifica): IFormacionEspecificaResponse {
    if (!value) {
      return value as unknown as IFormacionEspecificaResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
}

export const FORMACION_ESPECIFICA_RESPONSE_CONVERTER = new FormacionEspecificaResponseConverter();
