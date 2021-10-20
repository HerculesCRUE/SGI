import { ISolicitudProyectoPresupuestoBackend } from '@core/models/csp/backend/solicitud-proyecto-presupuesto-backend';
import { ISolicitudProyectoEntidad } from '@core/models/csp/solicitud-proyecto-entidad';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudProyectoPresupuestoConverter extends SgiBaseConverter<ISolicitudProyectoPresupuestoBackend, ISolicitudProyectoPresupuesto> {

  toTarget(value: ISolicitudProyectoPresupuestoBackend): ISolicitudProyectoPresupuesto {
    if (!value) {
      return value as unknown as ISolicitudProyectoPresupuesto;
    }
    return {
      id: value.id,
      solicitudProyectoId: value.solicitudProyectoId,
      conceptoGasto: value.conceptoGasto,
      solicitudProyectoEntidad: { id: value.solicitudProyectoEntidadId } as ISolicitudProyectoEntidad,
      anualidad: value.anualidad,
      importeSolicitado: value.importeSolicitado,
      importePresupuestado: value.importePresupuestado,
      observaciones: value.observaciones
    };
  }

  fromTarget(value: ISolicitudProyectoPresupuesto): ISolicitudProyectoPresupuestoBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoPresupuestoBackend;
    }
    return {
      id: value.id,
      solicitudProyectoId: value.solicitudProyectoId,
      solicitudProyectoEntidadId: value.solicitudProyectoEntidad?.id,
      conceptoGasto: value.conceptoGasto,
      anualidad: value.anualidad,
      importeSolicitado: value.importeSolicitado,
      importePresupuestado: value.importePresupuestado,
      observaciones: value.observaciones
    };
  }
}

export const SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER = new SolicitudProyectoPresupuestoConverter();
