import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IProyectoPeriodoAmortizacion } from '@core/models/csp/proyecto-periodo-amortizacion';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoPeriodoAmortizacionRequest } from './proyecto-periodo-amortizacion-request';

class ProyectoPeriodoAmortizacionRequestConverter extends
  SgiBaseConverter<IProyectoPeriodoAmortizacionRequest, IProyectoPeriodoAmortizacion> {

  toTarget(value: IProyectoPeriodoAmortizacionRequest): IProyectoPeriodoAmortizacion {
    if (!value) {
      return value as unknown as IProyectoPeriodoAmortizacion;
    }
    return {
      id: undefined,
      proyectoSGE: {
        id: value.proyectoSGERef
      } as IProyectoSge,
      proyectoAnualidad: {
        id: value.proyectoAnualidadId
      } as IProyectoAnualidad,
      proyectoEntidadFinanciadora: {
        id: value.proyectoEntidadFinanciadoraId,
      } as IProyectoEntidadFinanciadora,
      fechaLimiteAmortizacion: LuxonUtils.fromBackend(value.fechaLimiteAmortizacion),
      importe: value.importe
    };
  }

  fromTarget(value: IProyectoPeriodoAmortizacion): IProyectoPeriodoAmortizacionRequest {
    if (!value) {
      return value as unknown as IProyectoPeriodoAmortizacionRequest;
    }
    return {
      proyectoSGERef: value.proyectoSGE?.id,
      proyectoAnualidadId: value.proyectoAnualidad?.id,
      proyectoEntidadFinanciadoraId: value.proyectoEntidadFinanciadora?.id,
      fechaLimiteAmortizacion: LuxonUtils.toBackend(value.fechaLimiteAmortizacion),
      importe: value.importe
    };
  }
}

export const PROYECTO_PERIODO_AMORTIZACION_REQUEST_CONVERTER = new ProyectoPeriodoAmortizacionRequestConverter();
