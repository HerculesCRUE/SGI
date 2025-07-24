import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ESTADO_GASTO_PROYECTO_REQUEST_CONVERTER } from '../estado-gasto-proyecto/estado-gasto-proyecto-request.converter';
import { IGastoProyectoRequest } from './gasto-proyecto-request';

class GastoProyectoRequestConverter extends SgiBaseConverter<IGastoProyectoRequest, IGastoProyecto> {
  toTarget(value: IGastoProyectoRequest): IGastoProyecto {
    if (!value) {
      return value as unknown as IGastoProyecto;
    }
    return {
      id: undefined,
      proyectoId: value.proyectoId,
      gastoRef: value.gastoRef,
      conceptoGasto: {
        id: value.conceptoGastoId
      } as IConceptoGasto,
      estado: ESTADO_GASTO_PROYECTO_REQUEST_CONVERTER.toTarget(value.estado),
      fechaCongreso: LuxonUtils.fromBackend(value.fechaCongreso),
      importeInscripcion: value.importeInscripcion,
      observaciones: value.observaciones ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.observaciones) : []
    };
  }
  fromTarget(value: IGastoProyecto): IGastoProyectoRequest {
    if (!value) {
      return value as unknown as IGastoProyectoRequest;
    }
    return {
      proyectoId: value.proyectoId,
      gastoRef: value.gastoRef,
      conceptoGastoId: value.conceptoGasto?.id,
      estado: ESTADO_GASTO_PROYECTO_REQUEST_CONVERTER.fromTarget(value.estado),
      fechaCongreso: LuxonUtils.toBackend(value.fechaCongreso),
      importeInscripcion: value.importeInscripcion,
      observaciones: value.observaciones ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.observaciones) : []
    };
  }
}

export const GASTO_PROYECTO_REQUEST_CONVERTER = new GastoProyectoRequestConverter();
