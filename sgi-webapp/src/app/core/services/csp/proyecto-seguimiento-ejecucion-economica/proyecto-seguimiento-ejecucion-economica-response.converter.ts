import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoSeguimientoEjecucionEconomica } from '@core/models/csp/proyecto-seguimiento-ejecucion-economica';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoSeguimientoEjecucionEconomicaResponse } from './proyecto-seguimiento-ejecucion-economica-response';

class ProyectoSeguimientoEjecucionEconomicaResponseConverter
  extends SgiBaseConverter<IProyectoSeguimientoEjecucionEconomicaResponse, IProyectoSeguimientoEjecucionEconomica> {
  toTarget(value: IProyectoSeguimientoEjecucionEconomicaResponse): IProyectoSeguimientoEjecucionEconomica {
    if (!value) {
      return value as unknown as IProyectoSeguimientoEjecucionEconomica;
    }
    return {
      id: value.id,
      codigoExterno: value.codigoExterno,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFinDefinitiva: LuxonUtils.fromBackend(value.fechaFinDefinitiva),
      importeConcedido: value.importeConcedido,
      importeConcedidoCostesIndirectos: value.importeConcedidoCostesIndirectos,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      proyectoId: value.proyectoId,
      proyectoSgeRef: value.proyectoSgeRef,
      tituloConvocatoria: value.tituloConvocatoria ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.tituloConvocatoria) : []
    };
  }
  fromTarget(value: IProyectoSeguimientoEjecucionEconomica): IProyectoSeguimientoEjecucionEconomicaResponse {
    if (!value) {
      return value as unknown as IProyectoSeguimientoEjecucionEconomicaResponse;
    }
    return {
      id: value.id,
      codigoExterno: value.codigoExterno,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFinDefinitiva: LuxonUtils.toBackend(value.fechaFinDefinitiva),
      importeConcedido: value.importeConcedido,
      importeConcedidoCostesIndirectos: value.importeConcedidoCostesIndirectos,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      proyectoId: value.proyectoId,
      proyectoSgeRef: value.proyectoSgeRef,
      tituloConvocatoria: value.tituloConvocatoria ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.tituloConvocatoria) : []
    };
  }
}

export const PROYECTO_SEGUIMIENTO_EJECUCION_ECONOMICA_RESPONSE_CONVERTER = new ProyectoSeguimientoEjecucionEconomicaResponseConverter();
