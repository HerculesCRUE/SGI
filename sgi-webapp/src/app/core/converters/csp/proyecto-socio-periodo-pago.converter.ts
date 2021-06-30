import { IProyectoSocioPeriodoPagoBackend } from '@core/models/csp/backend/proyecto-socio-periodo-pago-backend';
import { IProyectoSocioPeriodoPago } from '@core/models/csp/proyecto-socio-periodo-pago';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoSocioPeriodoPagoConverter extends SgiBaseConverter<IProyectoSocioPeriodoPagoBackend, IProyectoSocioPeriodoPago> {

  toTarget(value: IProyectoSocioPeriodoPagoBackend): IProyectoSocioPeriodoPago {
    if (!value) {
      return value as unknown as IProyectoSocioPeriodoPago;
    }
    return {
      id: value.id,
      proyectoSocioId: value.proyectoSocioId,
      numPeriodo: value.numPeriodo,
      importe: value.importe,
      fechaPrevistaPago: LuxonUtils.fromBackend(value.fechaPrevistaPago),
      fechaPago: LuxonUtils.fromBackend(value.fechaPago)
    };
  }

  fromTarget(value: IProyectoSocioPeriodoPago): IProyectoSocioPeriodoPagoBackend {
    if (!value) {
      return value as unknown as IProyectoSocioPeriodoPagoBackend;
    }
    return {
      id: value.id,
      proyectoSocioId: value.proyectoSocioId,
      numPeriodo: value.numPeriodo,
      importe: value.importe,
      fechaPrevistaPago: LuxonUtils.toBackend(value.fechaPrevistaPago),
      fechaPago: LuxonUtils.toBackend(value.fechaPago)
    };
  }
}

export const PROYECTO_SOCIO_PERIODO_PAGO_CONVERTER = new ProyectoSocioPeriodoPagoConverter();
