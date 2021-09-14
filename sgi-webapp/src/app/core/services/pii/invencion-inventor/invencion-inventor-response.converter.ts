
import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInvencionInventorResponse } from './invencion-inventor-response';


class InvencionInventorResponseConverter extends SgiBaseConverter<IInvencionInventorResponse, IInvencionInventor> {

  toTarget(value: IInvencionInventorResponse): IInvencionInventor {

    if (!value) {
      return value as unknown as IInvencionInventor;
    }

    return {
      id: value?.id,
      invencion: { id: value.invencionId } as IInvencion,
      inventor: { id: value.inventorRef } as IPersona,
      participacion: value.participacion,
      repartoUniversidad: value.repartoUniversidad,
      activo: true
    };
  }

  fromTarget(value: IInvencionInventor): IInvencionInventorResponse {

    if (!value) {
      return value as unknown as IInvencionInventorResponse;
    }

    return {
      id: value?.id,
      invencionId: value.invencion?.id,
      inventorRef: value.inventor?.id,
      participacion: value.participacion,
      repartoUniversidad: value.repartoUniversidad,
      activo: value.activo
    };
  }
}

export const INVENCION_INVENTOR_RESPONSE_CONVERTER = new InvencionInventorResponseConverter();
