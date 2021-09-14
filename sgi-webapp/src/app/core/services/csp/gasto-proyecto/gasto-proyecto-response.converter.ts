import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGastoProyectoResponse } from './gasto-proyecto-response';

class GastoProyectoResponseConverter extends SgiBaseConverter<IGastoProyectoResponse, IGastoProyecto>{
  toTarget(value: IGastoProyectoResponse): IGastoProyecto {
    if (!value) {
      return value as unknown as IGastoProyecto;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      gastoRef: value.gastoRef,
      conceptoGasto: {
        id: value.conceptoGasto?.id,
        nombre: value.conceptoGasto?.nombre,
        descripcion: value.conceptoGasto?.descripcion
      } as IConceptoGasto,
      estado: {
        id: value.estado?.id,
        estado: value.estado?.estado,
        fechaEstado: LuxonUtils.fromBackend(value.estado?.fechaEstado),
        comentario: value.estado?.comentario,
        gastoProyectoId: value.id
      },
      fechaCongreso: LuxonUtils.fromBackend(value.fechaCongreso),
      importeInscripcion: value.importeInscripcion,
      observaciones: value.observaciones
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
      conceptoGasto: {
        id: value.conceptoGasto.id,
        nombre: value.conceptoGasto.nombre,
        descripcion: value.conceptoGasto.descripcion
      },
      estado: {
        id: value.estado.id,
        estado: value.estado.estado,
        fechaEstado: LuxonUtils.toBackend(value.estado.fechaEstado),
        comentario: value.estado.comentario
      },
      fechaCongreso: LuxonUtils.toBackend(value.fechaCongreso),
      importeInscripcion: value.importeInscripcion,
      observaciones: value.observaciones
    };
  }
}

export const GASTO_PROYECTO_RESPONSE_CONVERTER = new GastoProyectoResponseConverter();
