import { I18N_FIELD_REQUEST_CONVERTER, I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ISolicitudProyectoEntidad } from '@core/models/csp/solicitud-proyecto-entidad';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { CONCEPTO_GASTO_RESPONSE_CONVERTER } from '@core/services/csp/concepto-gasto/concepto-gasto-response.converter';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudProyectoPresupuestoResponse } from './solicitud-proyecto-presupuesto-response';

class SolicitudProyectoPresupuestoConverter extends SgiBaseConverter<ISolicitudProyectoPresupuestoResponse, ISolicitudProyectoPresupuesto> {

  toTarget(value: ISolicitudProyectoPresupuestoResponse): ISolicitudProyectoPresupuesto {
    if (!value) {
      return value as unknown as ISolicitudProyectoPresupuesto;
    }
    return {
      id: value.id,
      solicitudProyectoId: value.solicitudProyectoId,
      conceptoGasto: value.conceptoGasto ? CONCEPTO_GASTO_RESPONSE_CONVERTER.toTarget(value.conceptoGasto) : null,
      solicitudProyectoEntidad: { id: value.solicitudProyectoEntidadId } as ISolicitudProyectoEntidad,
      anualidad: value.anualidad,
      importeSolicitado: value.importeSolicitado,
      importePresupuestado: value.importePresupuestado,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
    };
  }

  fromTarget(value: ISolicitudProyectoPresupuesto): ISolicitudProyectoPresupuestoResponse {
    if (!value) {
      return value as unknown as ISolicitudProyectoPresupuestoResponse;
    }
    return {
      id: value.id,
      solicitudProyectoId: value.solicitudProyectoId,
      solicitudProyectoEntidadId: value.solicitudProyectoEntidad?.id,
      conceptoGasto: value.conceptoGasto ? CONCEPTO_GASTO_RESPONSE_CONVERTER.fromTarget(value.conceptoGasto) : null,
      anualidad: value.anualidad,
      importeSolicitado: value.importeSolicitado,
      importePresupuestado: value.importePresupuestado,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
    };
  }
}

export const SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER = new SolicitudProyectoPresupuestoConverter();
