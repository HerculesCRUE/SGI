import { ISolicitudProyectoPresupuestoBackend } from '@core/models/csp/backend/solicitud-proyecto-presupuesto-backend';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { IEmpresa } from '@core/models/sgemp/empresa';
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
      empresa: { id: value.entidadRef } as IEmpresa,
      anualidad: value.anualidad,
      importeSolicitado: value.importeSolicitado,
      importePresupuestado: value.importePresupuestado,
      observaciones: value.observaciones,
      financiacionAjena: value.financiacionAjena
    };
  }

  fromTarget(value: ISolicitudProyectoPresupuesto): ISolicitudProyectoPresupuestoBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoPresupuestoBackend;
    }
    return {
      id: value.id,
      solicitudProyectoId: value.solicitudProyectoId,
      conceptoGasto: value.conceptoGasto,
      entidadRef: value.empresa?.id,
      anualidad: value.anualidad,
      importeSolicitado: value.importeSolicitado,
      importePresupuestado: value.importePresupuestado,
      observaciones: value.observaciones,
      financiacionAjena: value.financiacionAjena ? true : false
    };
  }
}

export const SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER = new SolicitudProyectoPresupuestoConverter();
