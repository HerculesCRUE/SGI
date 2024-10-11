import { IFacturaPrevistaPendiente } from '@core/models/sge/factura-prevista-pendiente';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IFacturaPrevistaPendienteResponse } from './factura-prevista-pendiente-response';

class FacturaPrevistaPendienteResponseConverter extends SgiBaseConverter<IFacturaPrevistaPendienteResponse, IFacturaPrevistaPendiente> {

  toTarget(value: IFacturaPrevistaPendienteResponse): IFacturaPrevistaPendiente {
    return value ? {
      numeroPrevision: value.numeroPrevision,
      proyectoIdSGE: value.proyectoIdSGE,
      proyectoIdSGI: value.proyectoIdSGI
    } : value as unknown as IFacturaPrevistaPendiente;
  }

  fromTarget(value: IFacturaPrevistaPendiente): IFacturaPrevistaPendienteResponse {
    return value ? {
      numeroPrevision: value.numeroPrevision,
      proyectoIdSGE: value.proyectoIdSGE,
      proyectoIdSGI: value.proyectoIdSGI
    } : value as unknown as IFacturaPrevistaPendienteResponse;
  }

}

export const FACTURA_PREVISTA_PENDIENTE_RESPONSE_CONVERTER = new FacturaPrevistaPendienteResponseConverter();
