import { IFacturaPrevistaEmitida } from '@core/models/sge/factura-prevista-emitida';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IFacturaPrevistaEmitidaResponse } from './factura-prevista-emitida-response';

class FacturaPrevistaEmitidaResponseConverter extends SgiBaseConverter<IFacturaPrevistaEmitidaResponse, IFacturaPrevistaEmitida>{

  toTarget(value: IFacturaPrevistaEmitidaResponse): IFacturaPrevistaEmitida {
    return value ? {
      id: value.id,
      numeroFactura: value.numeroFactura,
      numeroPrevision: value.numeroPrevision,
      proyectoIdSGI: value.proyectoIdSGI

    } : value as unknown as IFacturaPrevistaEmitida;
  }

  fromTarget(value: IFacturaPrevistaEmitida): IFacturaPrevistaEmitidaResponse {
    return value ? {
      id: value.id,
      numeroFactura: value.numeroFactura,
      numeroPrevision: value.numeroPrevision,
      proyectoIdSGI: value.proyectoIdSGI
    } : value as unknown as IFacturaPrevistaEmitidaResponse;
  }

}

export const FACTURA_PREVISTA_EMITIDA_RESPONSE_CONVERTER = new FacturaPrevistaEmitidaResponseConverter();
