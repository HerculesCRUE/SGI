import { IConvocatoriaAreaTematicaBackend } from '@core/models/csp/backend/convocatoria-area-tematica-backend';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { AREA_TEMATICA_RESPONSE_CONVERTER } from '@core/services/csp/area-tematica/area-tematica-response.converter';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaAreaTematicaConverter extends SgiBaseConverter<IConvocatoriaAreaTematicaBackend, IConvocatoriaAreaTematica> {

  toTarget(value: IConvocatoriaAreaTematicaBackend): IConvocatoriaAreaTematica {
    if (!value) {
      return value as unknown as IConvocatoriaAreaTematica;
    }
    return {
      id: value.id,
      areaTematica: AREA_TEMATICA_RESPONSE_CONVERTER.toTarget(value.areaTematica),
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
      areaTematica: AREA_TEMATICA_RESPONSE_CONVERTER.fromTarget(value.areaTematica),
      convocatoriaId: value.convocatoriaId,
      observaciones: value.observaciones
    };
  }
}

export const CONVOCATORIA_AREA_TEMATICA_CONVERTER = new ConvocatoriaAreaTematicaConverter();
