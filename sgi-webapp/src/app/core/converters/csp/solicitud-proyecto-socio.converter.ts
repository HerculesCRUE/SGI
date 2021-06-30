import { ISolicitudProyectoSocioBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-backend';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudProyectoSocioConverter extends SgiBaseConverter<ISolicitudProyectoSocioBackend, ISolicitudProyectoSocio> {

  toTarget(value: ISolicitudProyectoSocioBackend): ISolicitudProyectoSocio {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocio;
    }
    return {
      empresa: { id: value.empresaRef } as IEmpresa,
      id: value.id,
      importeSolicitado: value.importeSolicitado,
      importePresupuestado: value.importePresupuestado,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      numInvestigadores: value.numInvestigadores,
      rolSocio: value.rolSocio,
      solicitudProyectoId: value.solicitudProyectoId
    };
  }

  fromTarget(value: ISolicitudProyectoSocio): ISolicitudProyectoSocioBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocioBackend;
    }
    return {
      empresaRef: value.empresa.id,
      id: value.id,
      importeSolicitado: value.importeSolicitado,
      importePresupuestado: value.importePresupuestado,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      numInvestigadores: value.numInvestigadores,
      rolSocio: value.rolSocio,
      solicitudProyectoId: value.solicitudProyectoId
    };
  }
}

export const SOLICITUD_PROYECTO_SOCIO_CONVERTER = new SolicitudProyectoSocioConverter();
