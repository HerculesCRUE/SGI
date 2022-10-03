import { IProyectoSeguimientoJustificacion } from '@core/models/csp/proyecto-seguimiento-justificacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoSeguimientoJustificacionRequest } from './proyecto-seguimiento-justificacion-request';

class ProyectoSeguimientoJustificacionRequestConverter
  extends SgiBaseConverter<IProyectoSeguimientoJustificacionRequest, IProyectoSeguimientoJustificacion>{

  toTarget(value: IProyectoSeguimientoJustificacionRequest): IProyectoSeguimientoJustificacion {
    throw new Error('Method not implemented.');
  }

  fromTarget(value: IProyectoSeguimientoJustificacion): IProyectoSeguimientoJustificacionRequest {
    if (!value) {
      return value as unknown as IProyectoSeguimientoJustificacionRequest;
    }
    return {
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
      proyectoProyectoSgeId: value.proyectoProyectoSge?.id,
    };
  }
}

export const PROYECTO_SEGUIMIENTO_JUSTIFICACION_REQUEST_CONVERTER = new ProyectoSeguimientoJustificacionRequestConverter();
