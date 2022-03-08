import { IProyectoResumen } from '@core/models/csp/proyecto-resumen';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoResumenResponse } from './proyecto-resumen-response';

class ProyectoResumenResponseConverter extends SgiBaseConverter<IProyectoResumenResponse, IProyectoResumen>{
  toTarget(value: IProyectoResumenResponse): IProyectoResumen {
    if (!value) {
      return value as unknown as IProyectoResumen;
    }
    return {
      id: value.id,
      acronimo: value.acronimo,
      codigoExterno: value.codigoExterno,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaFinDefinitiva: LuxonUtils.fromBackend(value.fechaFinDefinitiva),
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      titulo: value.titulo
    };
  }
  fromTarget(value: IProyectoResumen): IProyectoResumenResponse {
    if (!value) {
      return value as unknown as IProyectoResumenResponse;
    }
    return {
      id: value.id,
      acronimo: value.acronimo,
      codigoExterno: value.codigoExterno,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaFinDefinitiva: LuxonUtils.toBackend(value.fechaFinDefinitiva),
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      titulo: value.titulo
    };
  }
}

export const PROYECTO_RESUMEN_RESPONSE_CONVERTER = new ProyectoResumenResponseConverter();
