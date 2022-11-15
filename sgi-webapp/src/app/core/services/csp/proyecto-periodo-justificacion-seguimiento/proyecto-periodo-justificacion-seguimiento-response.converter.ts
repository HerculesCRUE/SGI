import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { IProyectoPeriodoJustificacionSeguimiento } from '@core/models/csp/proyecto-periodo-justificacion-seguimiento';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoPeriodoJustificacionSeguimientoResponse } from './proyecto-periodo-justificacion-seguimiento-response';

class ProyectoPeriodoJustificacionSeguimientoResponseConverter
  extends SgiBaseConverter<IProyectoPeriodoJustificacionSeguimientoResponse, IProyectoPeriodoJustificacionSeguimiento>{
  toTarget(value: IProyectoPeriodoJustificacionSeguimientoResponse): IProyectoPeriodoJustificacionSeguimiento {
    if (!value) {
      return value as unknown as IProyectoPeriodoJustificacionSeguimiento;
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
      proyectoAnualidad: {
        id: value.proyectoAnualidadId
      } as IProyectoAnualidad,
      proyectoPeriodoJustificacion: {
        id: value.proyectoPeriodoJustificacionId
      } as IProyectoPeriodoJustificacion
    };
  }
  fromTarget(value: IProyectoPeriodoJustificacionSeguimiento): IProyectoPeriodoJustificacionSeguimientoResponse {
    if (!value) {
      return value as unknown as IProyectoPeriodoJustificacionSeguimientoResponse;
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
      proyectoAnualidadId: value.proyectoAnualidad?.id,
      proyectoPeriodoJustificacionId: value.proyectoPeriodoJustificacion?.id
    };
  }
}

export const PROYECTO_PERIODO_JUSTIFICACION_SEGUIMIENTO_RESPONSE_CONVERTER = new ProyectoPeriodoJustificacionSeguimientoResponseConverter();
