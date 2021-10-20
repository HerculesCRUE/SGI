import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInvencionIngresoRequest } from './invencion-ingreso-request';

class InvencionIngresoRequestConverter extends SgiBaseConverter<IInvencionIngresoRequest, IInvencionIngreso> {

  toTarget(value: IInvencionIngresoRequest): IInvencionIngreso {
    if (!value) {
      return value as unknown as IInvencionIngreso;
    }

    return {
      id: undefined,
      invencion: { id: value.invencionId } as IInvencion,
      ingreso: { id: value.ingresoRef } as IDatoEconomico,
      importePendienteRepartir: value.importePendienteRepartir,
      estado: value.estado
    };
  }

  fromTarget(value: IInvencionIngreso): IInvencionIngresoRequest {
    if (!value) {
      return value as unknown as IInvencionIngresoRequest;
    }

    return {
      invencionId: value.invencion?.id,
      ingresoRef: value.ingreso?.id,
      importePendienteRepartir: value.importePendienteRepartir,
      estado: value.estado
    };
  }
}

export const INVENCION_INGRESO_REQUEST_CONVERTER = new InvencionIngresoRequestConverter();
