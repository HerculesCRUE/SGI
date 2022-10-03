import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoSeguimientoJustificacion } from '@core/models/csp/proyecto-seguimiento-justificacion';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoSeguimientoJustificacionResponse } from './proyecto-seguimiento-justificacion-response';

class ProyectoSeguimientoJustificacionResponseConverter
  extends SgiBaseConverter<IProyectoSeguimientoJustificacionResponse, IProyectoSeguimientoJustificacion>{
  toTarget(value: IProyectoSeguimientoJustificacionResponse): IProyectoSeguimientoJustificacion {
    if (!value) {
      return value as unknown as IProyectoSeguimientoJustificacion;
    }
    return {
      id: value.id,
      fechaReintegro: LuxonUtils.fromBackend(value.fechaReintegro),
      importeAceptado: value.importeAceptado,
      importeAceptadoCD: value.importeAceptadoCD,
      importeAceptadoCI: value.importeAceptadoCI,
      importeAlegado: value.importeAlegado,
      importeAlegadoCD: value.importeAlegadoCD,
      importeAlegadoCI: value.importeAlegadoCI,
      importeJustificado: value.importeJustificado,
      importeJustificadoCD: value.importeJustificadoCD,
      importeJustificadoCI: value.importeJustificadoCI,
      importeNoEjecutado: value.importeNoEjecutado,
      importeNoEjecutadoCD: value.importeNoEjecutadoCD,
      importeNoEjecutadoCI: value.importeNoEjecutadoCI,
      importeRechazado: value.importeRechazado,
      importeRechazadoCD: value.importeRechazadoCD,
      importeRechazadoCI: value.importeRechazadoCI,
      importeReintegrado: value.importeReintegrado,
      importeReintegradoCD: value.importeReintegradoCD,
      importeReintegradoCI: value.importeReintegradoCI,
      importeReintegrar: value.importeReintegrar,
      importeReintegrarCD: value.importeReintegrarCD,
      importeReintegrarCI: value.importeReintegrarCI,
      interesesReintegrados: value.interesesReintegrados,
      interesesReintegrar: value.interesesReintegrar,
      justificanteReintegro: value.justificanteReintegro,
      proyectoProyectoSge: {
        id: value.proyectoProyectoSge?.id,
        proyecto: {
          id: value.proyectoProyectoSge?.proyectoId
        } as IProyecto,
        proyectoSge: {
          id: value.proyectoProyectoSge?.proyectoSgeRef
        } as IProyectoSge
      }
    };
  }
  fromTarget(value: IProyectoSeguimientoJustificacion): IProyectoSeguimientoJustificacionResponse {
    if (!value) {
      return value as unknown as IProyectoSeguimientoJustificacionResponse;
    }
    return {
      id: value.id,
      fechaReintegro: LuxonUtils.toBackend(value.fechaReintegro),
      importeAceptado: value.importeAceptado,
      importeAceptadoCD: value.importeAceptadoCD,
      importeAceptadoCI: value.importeAceptadoCI,
      importeAlegado: value.importeAlegado,
      importeAlegadoCD: value.importeAlegadoCD,
      importeAlegadoCI: value.importeAlegadoCI,
      importeJustificado: value.importeJustificado,
      importeJustificadoCD: value.importeJustificadoCD,
      importeJustificadoCI: value.importeJustificadoCI,
      importeNoEjecutado: value.importeNoEjecutado,
      importeNoEjecutadoCD: value.importeNoEjecutadoCD,
      importeNoEjecutadoCI: value.importeNoEjecutadoCI,
      importeRechazado: value.importeRechazado,
      importeRechazadoCD: value.importeRechazadoCD,
      importeRechazadoCI: value.importeRechazadoCI,
      importeReintegrado: value.importeReintegrado,
      importeReintegradoCD: value.importeReintegradoCD,
      importeReintegradoCI: value.importeReintegradoCI,
      importeReintegrar: value.importeReintegrar,
      importeReintegrarCD: value.importeReintegrarCD,
      importeReintegrarCI: value.importeReintegrarCI,
      interesesReintegrados: value.interesesReintegrados,
      interesesReintegrar: value.interesesReintegrar,
      justificanteReintegro: value.justificanteReintegro,
      proyectoProyectoSge: {
        id: value.proyectoProyectoSge?.id,
        proyectoId: value.proyectoProyectoSge?.proyecto.id,
        proyectoSgeRef: value.proyectoProyectoSge?.proyectoSge.id
      }
    };
  }
}

export const PROYECTO_SEGUIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER = new ProyectoSeguimientoJustificacionResponseConverter();
