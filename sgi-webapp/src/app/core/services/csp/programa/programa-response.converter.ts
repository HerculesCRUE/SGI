import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IPrograma } from '@core/models/csp/programa';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProgramaResponse, } from './programa-response';

class ProgramaResponseConverter extends SgiBaseConverter<IProgramaResponse, IPrograma> {
  toTarget(value: IProgramaResponse): IPrograma {
    if (!value) {
      return value as unknown as IPrograma;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      padre: value.padre ? PROGRAMA_RESPONSE_CONVERTER.toTarget(value.padre) : null,
      activo: value.activo
    };
  }
  fromTarget(value: IPrograma): IProgramaResponse {
    if (!value) {
      return value as unknown as IProgramaResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      padre: value.padre ? PROGRAMA_RESPONSE_CONVERTER.fromTarget(value.padre) : null,
      activo: value.activo
    };
  }
}

export const PROGRAMA_RESPONSE_CONVERTER = new ProgramaResponseConverter();
