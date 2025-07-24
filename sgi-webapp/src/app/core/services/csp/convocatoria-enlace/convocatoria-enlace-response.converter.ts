import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoriaEnlaceResponse } from '@core/services/csp/convocatoria-enlace/convocatoria-enlace-response';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { TIPO_ENLACE_RESPONSE_CONVERTER } from '@core/services/csp/tipo-enlace/tipo-enlace-response.converter';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaEnlaceResponseConverter extends SgiBaseConverter<IConvocatoriaEnlaceResponse, IConvocatoriaEnlace> {

  toTarget(value: IConvocatoriaEnlaceResponse): IConvocatoriaEnlace {
    if (!value) {
      return value as unknown as IConvocatoriaEnlace;
    }
    return {
      id: value.id,
      tipoEnlace: TIPO_ENLACE_RESPONSE_CONVERTER.toTarget(value.tipoEnlace),
      activo: value.activo,
      convocatoriaId: value.convocatoriaId,
      url: value.url,
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
    };
  }

  fromTarget(value: IConvocatoriaEnlace): IConvocatoriaEnlaceResponse {
    if (!value) {
      return value as unknown as IConvocatoriaEnlaceResponse;
    }
    return {
      id: value.id,
      tipoEnlace: TIPO_ENLACE_RESPONSE_CONVERTER.fromTarget(value.tipoEnlace),
      activo: value.activo,
      convocatoriaId: value.convocatoriaId,
      url: value.url,
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
    };
  }
}

export const CONVOCATORIA_ENLACE_RESPONSE_CONVERTER = new ConvocatoriaEnlaceResponseConverter();
