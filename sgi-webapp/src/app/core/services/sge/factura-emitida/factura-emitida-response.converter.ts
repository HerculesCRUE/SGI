import { IFacturaEmitida } from '@core/models/sge/factura-emitida';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IFacturaEmitidaResponse } from './factura-emitida-response';

class FacturaEmitidaResponseConverter extends SgiBaseConverter<IFacturaEmitidaResponse, IFacturaEmitida> {

  toTarget(value: IFacturaEmitidaResponse): IFacturaEmitida {
    return value ? {
      id: value.id,
      proyectoId: value.proyectoId,
      anualidad: value.anualidad,
      numeroFactura: value.numeroFactura,
      columnas: value.columnas
    } : value as unknown as IFacturaEmitida;
  }

  fromTarget(value: IFacturaEmitida): IFacturaEmitidaResponse {
    return value ? {
      id: value.id,
      proyectoId: value.proyectoId,
      anualidad: value.anualidad,
      numeroFactura: value.numeroFactura,
      columnas: value.columnas
    } : value as unknown as IFacturaEmitidaResponse;
  }

}

export const FACTURA_EMITIDA_RESPONSE_CONVERTER = new FacturaEmitidaResponseConverter();
