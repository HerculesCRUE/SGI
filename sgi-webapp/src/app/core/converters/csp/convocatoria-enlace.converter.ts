import { IConvocatoriaEnlaceBackend } from '@core/models/csp/backend/convocatoria-enlace-backend';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaEnlaceConverter extends SgiBaseConverter<IConvocatoriaEnlaceBackend, IConvocatoriaEnlace> {

  toTarget(value: IConvocatoriaEnlaceBackend): IConvocatoriaEnlace {
    if (!value) {
      return value as unknown as IConvocatoriaEnlace;
    }
    return {
      id: value.id,
      tipoEnlace: value.tipoEnlace,
      activo: value.activo,
      convocatoriaId: value.convocatoriaId,
      url: value.url,
      descripcion: value.descripcion
    };
  }

  fromTarget(value: IConvocatoriaEnlace): IConvocatoriaEnlaceBackend {
    if (!value) {
      return value as unknown as IConvocatoriaEnlaceBackend;
    }
    return {
      id: value.id,
      tipoEnlace: value.tipoEnlace,
      activo: value.activo,
      convocatoriaId: value.convocatoriaId,
      url: value.url,
      descripcion: value.descripcion
    };
  }
}

export const CONVOCATORIA_ENLACE_CONVERTER = new ConvocatoriaEnlaceConverter();
