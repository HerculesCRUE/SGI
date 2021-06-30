import { IConvocatoriaHitoBackend } from '@core/models/csp/backend/convocatoria-hito-backend';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaHitoConverter extends SgiBaseConverter<IConvocatoriaHitoBackend, IConvocatoriaHito> {

  toTarget(value: IConvocatoriaHitoBackend): IConvocatoriaHito {
    if (!value) {
      return value as unknown as IConvocatoriaHito;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.fromBackend(value.fecha),
      tipoHito: value.tipoHito,
      comentario: value.comentario,
      convocatoriaId: value.convocatoriaId,
      generaAviso: value.generaAviso
    };
  }

  fromTarget(value: IConvocatoriaHito): IConvocatoriaHitoBackend {
    if (!value) {
      return value as unknown as IConvocatoriaHitoBackend;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.toBackend(value.fecha),
      tipoHito: value.tipoHito,
      comentario: value.comentario,
      convocatoriaId: value.convocatoriaId,
      generaAviso: value.generaAviso
    };
  }
}

export const CONVOCATORIA_HITO_CONVERTER = new ConvocatoriaHitoConverter();
