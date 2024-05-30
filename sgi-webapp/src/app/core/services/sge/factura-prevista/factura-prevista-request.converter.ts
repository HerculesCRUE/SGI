import { IFacturaPrevista } from '@core/models/sge/factura-prevista';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IFacturaPrevistaRequest } from './factura-prevista-request';
import { LuxonUtils } from '@core/utils/luxon-utils';

class FacturaPrevistaRequestConverter extends SgiBaseConverter<IFacturaPrevistaRequest, IFacturaPrevista> {

  toTarget(value: IFacturaPrevistaRequest): IFacturaPrevista {
    return value ? {
      id: null,
      proyectoIdSGI: value.proyectoIdSGI,
      proyectoSgeId: value.proyectoSgeId,
      numeroPrevision: value.numeroPrevision,
      fechaEmision: LuxonUtils.fromBackend(value.fechaEmision),
      importeBase: value.importeBase,
      porcentajeIVA: value.porcentajeIVA,
      comentario: value.comentario,
      tipoFacturacion: value.tipoFacturacion
    } : value as unknown as IFacturaPrevista;
  }

  fromTarget(value: IFacturaPrevista): IFacturaPrevistaRequest {
    return value ? {
      proyectoIdSGI: value.proyectoIdSGI,
      proyectoSgeId: value.proyectoSgeId,
      numeroPrevision: value.numeroPrevision,
      fechaEmision: LuxonUtils.toBackend(value.fechaEmision),
      importeBase: value.importeBase,
      porcentajeIVA: value.porcentajeIVA,
      comentario: value.comentario,
      tipoFacturacion: value.tipoFacturacion
    } : value as unknown as IFacturaPrevistaRequest;
  }

}

export const FACTURA_PREVISTA_REQUEST_CONVERTER = new FacturaPrevistaRequestConverter();
