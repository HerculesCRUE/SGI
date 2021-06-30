import { IConvocatoriaAreaTematicaBackend } from '@core/models/csp/backend/convocatoria-area-tematica-backend';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaAreaTematicaConverter extends SgiBaseConverter<IConvocatoriaAreaTematicaBackend, IConvocatoriaAreaTematica> {

  toTarget(value: IConvocatoriaAreaTematicaBackend): IConvocatoriaAreaTematica {
    if (!value) {
      return value as unknown as IConvocatoriaAreaTematica;
    }
    return {
      id: value.id,
      areaTematica: value.areaTematica,
      convocatoriaId: value.convocatoriaId,
      observaciones: value.observaciones
    };
  }

  fromTarget(value: IConvocatoriaAreaTematica): IConvocatoriaAreaTematicaBackend {
    if (!value) {
      return value as unknown as IConvocatoriaAreaTematicaBackend;
    }
    return {
      id: value.id,
      areaTematica: value.areaTematica,
      convocatoriaId: value.convocatoriaId,
      observaciones: value.observaciones
    };
  }
}

export const CONVOCATORIA_AREA_TEMATICA_CONVERTER = new ConvocatoriaAreaTematicaConverter();
