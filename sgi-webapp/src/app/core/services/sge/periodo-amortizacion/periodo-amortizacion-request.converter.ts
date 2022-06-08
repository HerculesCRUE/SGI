
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IProyectoPeriodoAmortizacion } from '@core/models/csp/proyecto-periodo-amortizacion';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IPeriodoAmortizacionRequest } from './periodo-amortizacion-request';

class PeriodoAmortizacionRequestConverter extends SgiBaseConverter<IPeriodoAmortizacionRequest, IProyectoPeriodoAmortizacion>{
  toTarget(value: IPeriodoAmortizacionRequest): IProyectoPeriodoAmortizacion {
    if (!value) {
      return value as unknown as IProyectoPeriodoAmortizacion;
    }
    return {
      id: +value.id,
      proyectoSGE: {
        id: value.proyectoId
      } as IProyectoSge,
      proyectoAnualidad: {
        anio: +value.anualidad
      } as IProyectoAnualidad,
      proyectoEntidadFinanciadora: {
        empresa: {
          id: value.empresaRef
        },
        tipoFinanciacion: {
          id: +value.tipoFinanciacion.id,
          nombre: value.tipoFinanciacion.nombre
        },
        fuenteFinanciacion: {
          id: +value.fuenteFinanciacion.id,
          nombre: value.fuenteFinanciacion.nombre
        }
      } as IProyectoEntidadFinanciadora,
      fechaLimiteAmortizacion: LuxonUtils.fromBackend(value.fecha),
      importe: value.importe
    };
  }
  fromTarget(value: IProyectoPeriodoAmortizacion): IPeriodoAmortizacionRequest {
    if (!value) {
      return value as unknown as IPeriodoAmortizacionRequest;
    }
    return {
      id: value.id.toString(),
      proyectoId: value.proyectoSGE.id,
      anualidad: value.proyectoAnualidad?.anio?.toString() ?? '0',
      empresaRef: value.proyectoEntidadFinanciadora.empresa.id,
      tipoFinanciacion: {
        id: value.proyectoEntidadFinanciadora?.tipoFinanciacion?.id.toString(),
        nombre: value.proyectoEntidadFinanciadora?.tipoFinanciacion?.nombre
      },
      fuenteFinanciacion: {
        id: value.proyectoEntidadFinanciadora?.fuenteFinanciacion?.id.toString(),
        nombre: value.proyectoEntidadFinanciadora?.fuenteFinanciacion?.nombre
      },
      fecha: LuxonUtils.toBackend(value.fechaLimiteAmortizacion),
      importe: value.importe
    };
  }
}

export const PERIODO_AMORTIZACION_REQUEST_CONVERTER = new PeriodoAmortizacionRequestConverter();
