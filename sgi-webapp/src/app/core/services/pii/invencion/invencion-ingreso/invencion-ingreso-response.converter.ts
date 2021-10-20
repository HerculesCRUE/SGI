import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInvencionIngresoResponse } from './invencion-ingreso-response';

class InvencionIngresoResponseConverter extends SgiBaseConverter<IInvencionIngresoResponse, IInvencionIngreso>{
  toTarget(value: IInvencionIngresoResponse): IInvencionIngreso {
    if (!value) {
      return value as unknown as IInvencionIngreso;
    }
    return {
      id: value.id,
      invencion: { id: +value.invencionId } as IInvencion,
      ingreso: { id: value.ingresoRef } as IDatoEconomico,
      importePendienteRepartir: value.importePendienteRepartir,
      estado: value.estado
    };
  }

  fromTarget(value: IInvencionIngreso): IInvencionIngresoResponse {
    if (!value) {
      return value as unknown as IInvencionIngresoResponse;
    }
    return {
      id: value.id,
      invencionId: value.invencion?.id,
      ingresoRef: value.ingreso?.id,
      importePendienteRepartir: value.importePendienteRepartir,
      estado: value.estado
    };
  }
}

export const INVENCION_INGRESO_RESPONSE_CONVERTER = new InvencionIngresoResponseConverter();
