import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ILineaInvestigacion } from '@core/models/csp/linea-investigacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ILineaInvestigacionResponse } from './linea-investigacion-response';

class LineaInvestigacionResponseConverter extends SgiBaseConverter<ILineaInvestigacionResponse, ILineaInvestigacion>{
  toTarget(value: ILineaInvestigacionResponse): ILineaInvestigacion {
    if (!value) {
      return value as unknown as ILineaInvestigacion;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ILineaInvestigacion): ILineaInvestigacionResponse {
    if (!value) {
      return value as unknown as ILineaInvestigacionResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
}

export const LINEA_INVESTIGACION_RESPONSE_CONVERTER = new LineaInvestigacionResponseConverter();
