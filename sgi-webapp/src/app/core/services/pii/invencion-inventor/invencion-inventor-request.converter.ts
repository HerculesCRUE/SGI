import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInvencionInventorRequest } from './invencion-inventor-request';

class InvencionInventorRequestConverter extends SgiBaseConverter<IInvencionInventorRequest, IInvencionInventor> {

  toTarget(value: IInvencionInventorRequest): IInvencionInventor {

    if (!value) {
      return value as unknown as IInvencionInventor;
    }

    return {
      id: value?.id,
      invencion: { id: value.invencionId } as IInvencion,
      inventor: { id: value.inventorRef } as IPersona,
      participacion: value.participacion,
      repartoUniversidad: value.repartoUniversidad,
      activo: value.activo
    };
  }

  fromTarget(value: IInvencionInventor): IInvencionInventorRequest {

    if (!value) {
      return value as unknown as IInvencionInventorRequest;
    }

    return {
      id: value.id,
      invencionId: value.invencion?.id,
      inventorRef: value.inventor?.id,
      participacion: value.participacion,
      repartoUniversidad: value.repartoUniversidad,
      activo: value.activo
    };
  }

}

export const INVENCION_INVENTOR_REQUEST_CONVERTER = new InvencionInventorRequestConverter();
