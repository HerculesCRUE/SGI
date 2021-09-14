import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInvencionGastoRequest } from './invencion-gasto-request';

class InvencionGastoRequestConverter extends SgiBaseConverter<IInvencionGastoRequest, IInvencionGasto> {

  toTarget(value: IInvencionGastoRequest): IInvencionGasto {
    if (!value) {
      return value as unknown as IInvencionGasto;
    }

    return {
      id: undefined,
      invencion: { id: value.invencionId } as IInvencion,
      solicitudProteccion: { id: value.solicitudProteccionId } as ISolicitudProteccion,
      gasto: { id: value.gastoRef } as IDatoEconomico,
      importePendienteDeducir: value.importePendienteDeducir,
      estado: value.estado
    };
  }

  fromTarget(value: IInvencionGasto): IInvencionGastoRequest {
    if (!value) {
      return value as unknown as IInvencionGastoRequest;
    }

    return {
      invencionId: value.invencion?.id,
      solicitudProteccionId: value.solicitudProteccion?.id,
      gastoRef: value.gasto.id,
      importePendienteDeducir: value.importePendienteDeducir,
      estado: value.estado
    };
  }
}

export const INVENCION_GASTO_REQUEST_CONVERTER = new InvencionGastoRequestConverter();
