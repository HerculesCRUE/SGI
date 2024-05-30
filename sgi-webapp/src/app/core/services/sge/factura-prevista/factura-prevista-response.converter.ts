import { IFacturaPrevista } from '@core/models/sge/factura-prevista';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IFacturaPrevistaResponse } from './factura-prevista-response';

class FacturaPrevistaResponseConverter extends SgiBaseConverter<IFacturaPrevistaResponse, IFacturaPrevista> {

  toTarget(value: IFacturaPrevistaResponse): IFacturaPrevista {
    return value ? {
      id: value.id,
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

  fromTarget(value: IFacturaPrevista): IFacturaPrevistaResponse {
    return value ? {
      id: value.id,
      proyectoIdSGI: value.proyectoIdSGI,
      proyectoSgeId: value.proyectoSgeId,
      numeroPrevision: value.numeroPrevision,
      fechaEmision: LuxonUtils.toBackend(value.fechaEmision),
      importeBase: value.importeBase,
      porcentajeIVA: value.porcentajeIVA,
      comentario: value.comentario,
      tipoFacturacion: value.tipoFacturacion
    } : value as unknown as IFacturaPrevistaResponse;
  }

}

export const FACTURA_PREVISTA_RESPONSE_CONVERTER = new FacturaPrevistaResponseConverter();
