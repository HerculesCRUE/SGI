import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConvocatoriaBaremacionResponse } from './convocatoria-baremacion-response';

class ConvocatoriaBaremacionResponseConverter extends SgiBaseConverter<IConvocatoriaBaremacionResponse, IConvocatoriaBaremacion>{
  toTarget(value: IConvocatoriaBaremacionResponse): IConvocatoriaBaremacion {
    if (!value) {
      return value as unknown as IConvocatoriaBaremacion;
    }
    return {
      id: value.id,
      activo: value.activo,
      anio: value.anio,
      aniosBaremables: value.aniosBaremables,
      fechaFinEjecucion: LuxonUtils.fromBackend(value.fechaFinEjecucion),
      fechaInicioEjecucion: LuxonUtils.fromBackend(value.fechaInicioEjecucion),
      importeTotal: value.importeTotal,
      nombre: value.nombre,
      partidaPresupuestaria: value.partidaPresupuestaria,
      puntoCostesIndirectos: value.puntoCostesIndirectos,
      puntoProduccion: value.puntoProduccion,
      puntoSexenio: value.puntoSexenio,
      ultimoAnio: value.ultimoAnio,
    };
  }
  fromTarget(value: IConvocatoriaBaremacion): IConvocatoriaBaremacionResponse {
    if (!value) {
      return value as unknown as IConvocatoriaBaremacionResponse;
    }
    return {
      id: value.id,
      activo: value.activo,
      anio: value.anio,
      aniosBaremables: value.aniosBaremables,
      fechaFinEjecucion: LuxonUtils.toBackend(value.fechaFinEjecucion),
      fechaInicioEjecucion: LuxonUtils.toBackend(value.fechaInicioEjecucion),
      importeTotal: value.importeTotal,
      nombre: value.nombre,
      partidaPresupuestaria: value.partidaPresupuestaria,
      puntoCostesIndirectos: value.puntoCostesIndirectos,
      puntoProduccion: value.puntoProduccion,
      puntoSexenio: value.puntoSexenio,
      ultimoAnio: value.ultimoAnio
    };
  }
}

export const CONVOCATORIA_BAREMACION_RESPONSE_CONVERTER = new ConvocatoriaBaremacionResponseConverter();
