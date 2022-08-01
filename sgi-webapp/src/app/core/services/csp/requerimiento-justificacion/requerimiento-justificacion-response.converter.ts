import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { ITipoRequerimiento } from '@core/models/csp/tipo-requerimiento';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRequerimientoJustificacionResponse } from './requerimiento-justificacion-response';

class RequerimientoJustificacionResponseConverter extends
  SgiBaseConverter<IRequerimientoJustificacionResponse, IRequerimientoJustificacion>{
  toTarget(value: IRequerimientoJustificacionResponse): IRequerimientoJustificacion {
    if (!value) {
      return value as unknown as IRequerimientoJustificacion;
    }
    return {
      id: value.id,
      anticipoJustificado: value.anticipoJustificado,
      defectoAnticipo: value.defectoAnticipo,
      defectoSubvencion: value.defectoSubvencion,
      fechaFinAlegacion: LuxonUtils.fromBackend(value.fechaFinAlegacion),
      fechaNotificacion: LuxonUtils.fromBackend(value.fechaNotificacion),
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
      numRequerimiento: value.numRequerimiento,
      observaciones: value.observaciones,
      proyectoPeriodoJustificacion: value.proyectoPeriodoJustificacionId ?
        { id: value.proyectoPeriodoJustificacionId } as IProyectoPeriodoJustificacion : null,
      proyectoProyectoSge: value.proyectoProyectoSgeId ? { id: value.proyectoProyectoSgeId } as IProyectoProyectoSge : null,
      recursoEstimado: value.recursoEstimado,
      requerimientoPrevio: value.requerimientoPrevioId ? { id: value.requerimientoPrevioId } as IRequerimientoJustificacion : null,
      subvencionJustificada: value.subvencionJustificada,
      tipoRequerimiento: {
        id: value.tipoRequerimiento.id,
        nombre: value.tipoRequerimiento.nombre
      } as ITipoRequerimiento
    };
  }
  fromTarget(value: IRequerimientoJustificacion): IRequerimientoJustificacionResponse {
    if (!value) {
      return value as unknown as IRequerimientoJustificacionResponse;
    }
    return {
      id: value.id,
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
      numRequerimiento: value.numRequerimiento,
      observaciones: value.observaciones,
      proyectoPeriodoJustificacionId: value.proyectoPeriodoJustificacion?.id,
      proyectoProyectoSgeId: value.proyectoProyectoSge?.id,
      recursoEstimado: value.recursoEstimado,
      requerimientoPrevioId: value.requerimientoPrevio?.id,
      subvencionJustificada: value.subvencionJustificada,
      tipoRequerimiento: {
        id: value.tipoRequerimiento.id,
        nombre: value.tipoRequerimiento.nombre
      } as ITipoRequerimiento
    };
  }
}

export const REQUERIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER = new RequerimientoJustificacionResponseConverter();
