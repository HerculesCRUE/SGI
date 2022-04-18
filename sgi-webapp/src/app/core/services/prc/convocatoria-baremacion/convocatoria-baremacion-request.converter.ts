import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConvocatoriaBaremacionRequest } from './convocatoria-baremacion-request';

class ConvocatoriaBaremacionRequestConverter extends SgiBaseConverter<IConvocatoriaBaremacionRequest, IConvocatoriaBaremacion>{
  toTarget(value: IConvocatoriaBaremacionRequest): IConvocatoriaBaremacion {
    if (!value) {
      return value as unknown as IConvocatoriaBaremacion;
    }
    return {
      id: undefined,
      activo: undefined,
      anio: value.anio,
      aniosBaremables: value.aniosBaremables,
      fechaFinEjecucion: undefined,
      fechaInicioEjecucion: undefined,
      importeTotal: value.importeTotal,
      nombre: value.nombre,
      partidaPresupuestaria: value.partidaPresupuestaria,
      puntoCostesIndirectos: undefined,
      puntoProduccion: undefined,
      puntoSexenio: undefined,
      ultimoAnio: value.ultimoAnio,
    };
  }
  fromTarget(value: IConvocatoriaBaremacion): IConvocatoriaBaremacionRequest {
    if (!value) {
      return value as unknown as IConvocatoriaBaremacionRequest;
    }
    return {
      anio: value.anio,
      aniosBaremables: value.aniosBaremables,
      importeTotal: value.importeTotal,
      nombre: value.nombre,
      partidaPresupuestaria: value.partidaPresupuestaria,
      ultimoAnio: value.ultimoAnio
    };
  }
}

export const CONVOCATORIA_BAREMACION_REQUEST_CONVERTER = new ConvocatoriaBaremacionRequestConverter();
