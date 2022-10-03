import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRequerimientoJustificacionRequest } from './requerimiento-justificacion-request';

class RequerimientoJustificacionRequestConverter extends SgiBaseConverter<IRequerimientoJustificacionRequest, IRequerimientoJustificacion>{

  toTarget(value: IRequerimientoJustificacionRequest): IRequerimientoJustificacion {
    throw new Error('Method not implemented.');
  }

  fromTarget(value: IRequerimientoJustificacion): IRequerimientoJustificacionRequest {
    if (!value) {
      return value as unknown as IRequerimientoJustificacionRequest;
    }
    return {
      anticipoJustificado: value.anticipoJustificado,
      defectoAnticipo: value.defectoAnticipo,
      defectoSubvencion: value.defectoSubvencion,
      fechaFinAlegacion: LuxonUtils.toBackend(value.fechaFinAlegacion),
      fechaNotificacion: LuxonUtils.toBackend(value.fechaNotificacion),
      importeAceptado: value.importeAceptado,
      importeAceptadoCd: value.importeAceptadoCd,
      importeAceptadoCi: value.importeAceptadoCi,
      importeRechazado: value.importeRechazado,
      importeRechazadoCd: value.importeRechazadoCd,
      importeRechazadoCi: value.importeRechazadoCi,
      importeReintegrar: value.importeReintegrar,
      importeReintegrarCd: value.importeReintegrarCd,
      importeReintegrarCi: value.importeReintegrarCi,
      interesesReintegrar: value.interesesReintegrar,
      observaciones: value.observaciones,
      proyectoPeriodoJustificacionId: value.proyectoPeriodoJustificacion?.id,
      proyectoProyectoSgeId: value.proyectoProyectoSge?.id,
      recursoEstimado: value.recursoEstimado,
      requerimientoPrevioId: value.requerimientoPrevio?.id,
      subvencionJustificada: value.subvencionJustificada,
      tipoRequerimientoId: value.tipoRequerimiento?.id
    };
  }
}

export const REQUERIMIENTO_JUSTIFICACION_REQUEST_CONVERTER = new RequerimientoJustificacionRequestConverter();
