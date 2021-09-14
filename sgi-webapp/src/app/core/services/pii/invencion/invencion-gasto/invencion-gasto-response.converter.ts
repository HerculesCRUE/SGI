import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInvencionGastoResponse } from './invencion-gasto-response';

class InvencionGastoResponseConverter extends SgiBaseConverter<IInvencionGastoResponse, IInvencionGasto>{
  toTarget(value: IInvencionGastoResponse): IInvencionGasto {
    if (!value) {
      return value as unknown as IInvencionGasto;
    }
    return {
      id: value.id,
      invencion: { id: +value.invencionId } as IInvencion,
      solicitudProteccion: value.solicitudProteccionId !== null ? { id: +value.solicitudProteccionId } as ISolicitudProteccion : null,
      gasto: { id: value.gastoRef } as IDatoEconomico,
      importePendienteDeducir: value.importePendienteDeducir,
      estado: value.estado
    };
  }

  fromTarget(value: IInvencionGasto): IInvencionGastoResponse {
    if (!value) {
      return value as unknown as IInvencionGastoResponse;
    }
    return {
      id: value.id,
      invencionId: value.invencion?.id,
      solicitudProteccionId: value.solicitudProteccion?.id,
      gastoRef: value.gasto?.id,
      importePendienteDeducir: value.importePendienteDeducir,
      estado: value.estado
    };
  }
}

export const INVENCION_GASTO_RESPONSE_CONVERTER = new InvencionGastoResponseConverter();
