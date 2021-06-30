import { ISolicitudProyectoSocioPeriodoPagoBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-periodo-pago-backend';
import { ISolicitudProyectoSocioPeriodoPago } from '@core/models/csp/solicitud-proyecto-socio-periodo-pago';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudProyectoSocioPeriodoPagoConverter
  extends SgiBaseConverter<ISolicitudProyectoSocioPeriodoPagoBackend, ISolicitudProyectoSocioPeriodoPago> {

  toTarget(value: ISolicitudProyectoSocioPeriodoPagoBackend): ISolicitudProyectoSocioPeriodoPago {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocioPeriodoPago;
    }
    return {
      id: value.id,
      solicitudProyectoSocioId: value.solicitudProyectoSocioId,
      numPeriodo: value.numPeriodo,
      importe: value.importe,
      mes: value.mes
    };
  }

  fromTarget(value: ISolicitudProyectoSocioPeriodoPago): ISolicitudProyectoSocioPeriodoPagoBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocioPeriodoPagoBackend;
    }
    return {
      id: value.id,
      solicitudProyectoSocioId: value.solicitudProyectoSocioId,
      numPeriodo: value.numPeriodo,
      importe: value.importe,
      mes: value.mes
    };
  }
}

export const SOLICITUD_PROYECTO_SOCIO_PERIODO_PAGO_CONVERTER = new SolicitudProyectoSocioPeriodoPagoConverter();
