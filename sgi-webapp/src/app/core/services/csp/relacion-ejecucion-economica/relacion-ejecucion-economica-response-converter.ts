import { IRelacionEjecucionEconomica as IRelacionEjecucionEconomica } from '@core/models/csp/relacion-ejecucion-economica';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRelacionEjecucionEconomicaResponse } from './relacion-ejecucion-economica-response';

class RelacionEjecucionEconomicaResponseConverter
  extends SgiBaseConverter<IRelacionEjecucionEconomicaResponse, IRelacionEjecucionEconomica> {
  toTarget(value: IRelacionEjecucionEconomicaResponse): IRelacionEjecucionEconomica {
    if (!value) {
      return value as unknown as IRelacionEjecucionEconomica;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      proyectoSge: value.proyectoSgeRef ? { id: value.proyectoSgeRef } as IProyectoSge : null,
      tipoEntidad: value.tipoEntidad
    };
  }

  fromTarget(value: IRelacionEjecucionEconomica): IRelacionEjecucionEconomicaResponse {
    if (!value) {
      return value as unknown as IRelacionEjecucionEconomicaResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      proyectoSgeRef: value.proyectoSge?.id,
      tipoEntidad: value.tipoEntidad
    };
  }
}

export const RELACION_EJECUCION_ECONOMICA_RESPONSE_CONVERTER = new RelacionEjecucionEconomicaResponseConverter();
