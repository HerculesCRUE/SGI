import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONCEPTO_GASTO_RESPONSE_CONVERTER } from '../concepto-gasto/concepto-gasto-response.converter';
import { ESTADO_GASTO_PROYECTO_RESPONSE_CONVERTER } from '../estado-gasto-proyecto/estado-gasto-proyecto-response.converter';
import { IGastoProyectoResponse } from './gasto-proyecto-response';

class GastoProyectoResponseConverter extends SgiBaseConverter<IGastoProyectoResponse, IGastoProyecto> {
  toTarget(value: IGastoProyectoResponse): IGastoProyecto {
    if (!value) {
      return value as unknown as IGastoProyecto;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      gastoRef: value.gastoRef,
      conceptoGasto: value.conceptoGasto ? CONCEPTO_GASTO_RESPONSE_CONVERTER.toTarget(value.conceptoGasto) : null,
      estado: ESTADO_GASTO_PROYECTO_RESPONSE_CONVERTER.toTarget({
        ...value.estado,
        gastoProyectoId: value.id
      }),
      fechaCongreso: LuxonUtils.fromBackend(value.fechaCongreso),
      importeInscripcion: value.importeInscripcion,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : []
    };
  }
  fromTarget(value: IGastoProyecto): IGastoProyectoResponse {
    if (!value) {
      return value as unknown as IGastoProyectoResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      gastoRef: value.gastoRef,
      conceptoGasto: value.conceptoGasto ? CONCEPTO_GASTO_RESPONSE_CONVERTER.fromTarget(value.conceptoGasto) : null,
      estado: ESTADO_GASTO_PROYECTO_RESPONSE_CONVERTER.fromTarget(value.estado),
      fechaCongreso: LuxonUtils.toBackend(value.fechaCongreso),
      importeInscripcion: value.importeInscripcion,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : []
    };
  }
}

export const GASTO_PROYECTO_RESPONSE_CONVERTER = new GastoProyectoResponseConverter();
