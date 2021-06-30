import { IProyectoSocioPeriodoJustificacionBackend } from '@core/models/csp/backend/proyecto-socio-periodo-justificacion-backend';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoSocioPeriodoJustificacionConverter extends
  SgiBaseConverter<IProyectoSocioPeriodoJustificacionBackend, IProyectoSocioPeriodoJustificacion> {

  toTarget(value: IProyectoSocioPeriodoJustificacionBackend): IProyectoSocioPeriodoJustificacion {
    if (!value) {
      return value as unknown as IProyectoSocioPeriodoJustificacion;
    }
    return {
      id: value.id,
      proyectoSocioId: value.proyectoSocioId,
      numPeriodo: value.numPeriodo,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaInicioPresentacion: LuxonUtils.fromBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.fromBackend(value.fechaFinPresentacion),
      observaciones: value.observaciones,
      documentacionRecibida: value.documentacionRecibida,
      fechaRecepcion: LuxonUtils.fromBackend(value.fechaRecepcion),
      importeJustificado: value.importeJustificado
    };
  }

  fromTarget(value: IProyectoSocioPeriodoJustificacion): IProyectoSocioPeriodoJustificacionBackend {
    if (!value) {
      return value as unknown as IProyectoSocioPeriodoJustificacionBackend;
    }
    return {
      id: value.id,
      proyectoSocioId: value.proyectoSocioId,
      numPeriodo: value.numPeriodo,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicioPresentacion: LuxonUtils.toBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.toBackend(value.fechaFinPresentacion),
      observaciones: value.observaciones,
      documentacionRecibida: value.documentacionRecibida,
      fechaRecepcion: LuxonUtils.toBackend(value.fechaRecepcion),
      importeJustificado: value.importeJustificado
    };
  }
}

export const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER = new ProyectoSocioPeriodoJustificacionConverter();
