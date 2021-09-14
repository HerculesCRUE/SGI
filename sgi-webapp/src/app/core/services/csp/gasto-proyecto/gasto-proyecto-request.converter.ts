import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IEstadoGastoProyecto } from '@core/models/csp/estado-gasto-proyecto';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGastoProyectoRequest } from './gasto-proyecto-request';

class GastoProyectoRequestConverter extends SgiBaseConverter<IGastoProyectoRequest, IGastoProyecto>{
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
      estado: {
        id: value.estadoId,
      } as IEstadoGastoProyecto,
      fechaCongreso: LuxonUtils.fromBackend(value.fechaCongreso),
      importeInscripcion: value.importeInscripcion,
      observaciones: value.observaciones
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
      estadoId: value.estado?.id,
      fechaCongreso: LuxonUtils.toBackend(value.fechaCongreso),
      importeInscripcion: value.importeInscripcion,
      observaciones: value.observaciones
    };
  }
}

export const GASTO_PROYECTO_REQUEST_CONVERTER = new GastoProyectoRequestConverter();
