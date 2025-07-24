import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { IProyectoSocioPeriodoJustificacionResponse } from '@core/services/csp/proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoSocioPeriodoJustificacionResponseConverter extends
  SgiBaseConverter<IProyectoSocioPeriodoJustificacionResponse, IProyectoSocioPeriodoJustificacion> {

  toTarget(value: IProyectoSocioPeriodoJustificacionResponse): IProyectoSocioPeriodoJustificacion {
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
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      documentacionRecibida: value.documentacionRecibida,
      fechaRecepcion: LuxonUtils.fromBackend(value.fechaRecepcion),
      importeJustificado: value.importeJustificado
    };
  }

  fromTarget(value: IProyectoSocioPeriodoJustificacion): IProyectoSocioPeriodoJustificacionResponse {
    if (!value) {
      return value as unknown as IProyectoSocioPeriodoJustificacionResponse;
    }
    return {
      id: value.id,
      proyectoSocioId: value.proyectoSocioId,
      numPeriodo: value.numPeriodo,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicioPresentacion: LuxonUtils.toBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.toBackend(value.fechaFinPresentacion),
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      documentacionRecibida: value.documentacionRecibida,
      fechaRecepcion: LuxonUtils.toBackend(value.fechaRecepcion),
      importeJustificado: value.importeJustificado
    };
  }
}

export const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER = new ProyectoSocioPeriodoJustificacionResponseConverter();
