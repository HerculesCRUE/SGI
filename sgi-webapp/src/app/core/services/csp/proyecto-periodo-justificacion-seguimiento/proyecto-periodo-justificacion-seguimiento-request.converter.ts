import { IProyectoPeriodoJustificacionSeguimiento } from '@core/models/csp/proyecto-periodo-justificacion-seguimiento';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoPeriodoJustificacionSeguimientoRequest } from './proyecto-periodo-justificacion-seguimiento-request';

class ProyectoPeriodoJustificacionSeguimientoRequestConverter
  extends SgiBaseConverter<IProyectoPeriodoJustificacionSeguimientoRequest, IProyectoPeriodoJustificacionSeguimiento>{

  toTarget(value: IProyectoPeriodoJustificacionSeguimientoRequest): IProyectoPeriodoJustificacionSeguimiento {
    throw new Error('Method not implemented.');
  }

  fromTarget(value: IProyectoPeriodoJustificacionSeguimiento): IProyectoPeriodoJustificacionSeguimientoRequest {
    if (!value) {
      return value as unknown as IProyectoPeriodoJustificacionSeguimientoRequest;
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
      proyectoAnualidadId: value.proyectoAnualidad?.id,
      proyectoPeriodoJustificacionId: value.proyectoPeriodoJustificacion?.id
    };
  }
}

export const PROYECTO_PERIODO_JUSTIFICACION_SEGUIMIENTO_REQUEST_CONVERTER = new ProyectoPeriodoJustificacionSeguimientoRequestConverter();
