import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ROL_SOCIO_RESPONSE_CONVERTER } from '../rol-socio/rol-socio-response.converter';
import { ISolicitudProyectoSocioResponse } from './solicitud-proyecto-socio-response';

class SolicitudProyectoSocioConverter extends SgiBaseConverter<ISolicitudProyectoSocioResponse, ISolicitudProyectoSocio> {

  toTarget(value: ISolicitudProyectoSocioResponse): ISolicitudProyectoSocio {
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
      rolSocio: ROL_SOCIO_RESPONSE_CONVERTER.toTarget(value.rolSocio),
      solicitudProyectoId: value.solicitudProyectoId
    };
  }

  fromTarget(value: ISolicitudProyectoSocio): ISolicitudProyectoSocioResponse {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocioResponse;
    }
    return {
      empresaRef: value.empresa.id,
      id: value.id,
      importeSolicitado: value.importeSolicitado,
      importePresupuestado: value.importePresupuestado,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      numInvestigadores: value.numInvestigadores,
      rolSocio: ROL_SOCIO_RESPONSE_CONVERTER.fromTarget(value.rolSocio),
      solicitudProyectoId: value.solicitudProyectoId
    };
  }
}

export const SOLICITUD_PROYECTO_SOCIO_RESPONSE_CONVERTER = new SolicitudProyectoSocioConverter();
