import { ILineaInvestigacion } from '@core/models/csp/linea-investigacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ILineaInvestigacionRequest } from './linea-investigacion-request';
import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';

class LineaInvestigacionRequestConverter extends SgiBaseConverter<ILineaInvestigacionRequest, ILineaInvestigacion>{
  toTarget(value: ILineaInvestigacionRequest): ILineaInvestigacion {
    if (!value) {
      return value as unknown as ILineaInvestigacion;
    }
    return {
      id: undefined,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      activo: true
    };
  }
  fromTarget(value: ILineaInvestigacion): ILineaInvestigacionRequest {
    if (!value) {
      return value as unknown as ILineaInvestigacionRequest;
    }
    return {
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
    };
  }
}

export const LINEA_INVESTIGACION_REQUEST_CONVERTER = new LineaInvestigacionRequestConverter();
