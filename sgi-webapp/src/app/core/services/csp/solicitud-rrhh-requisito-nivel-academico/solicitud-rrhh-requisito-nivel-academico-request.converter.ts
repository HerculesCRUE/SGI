import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { ISolicitudRrhhRequisitoNivelAcademico } from '@core/models/csp/solicitud-rrhh-requisito-nivel-academico';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhRequisitoNivelAcademicoRequest } from './solicitud-rrhh-requisito-nivel-academico-request';

class SolicitudRrhhRequisitoNivelAcademicoRequestConverter
  extends SgiBaseConverter<ISolicitudRrhhRequisitoNivelAcademicoRequest, ISolicitudRrhhRequisitoNivelAcademico> {
  toTarget(value: ISolicitudRrhhRequisitoNivelAcademicoRequest): ISolicitudRrhhRequisitoNivelAcademico {
    if (!value) {
      return value as unknown as ISolicitudRrhhRequisitoNivelAcademico;
    }
    return {
      id: null,
      solicitudRrhhId: value.solicitudRrhhId,
      documento: value.documentoRef ? { documentoRef: value.documentoRef } as IDocumento : null,
      requisitoIpNivelAcademico: value.requisitoIpNivelAcademicoId
        ? { id: value.requisitoIpNivelAcademicoId } as IRequisitoIPNivelAcademico : null
    };
  }

  fromTarget(value: ISolicitudRrhhRequisitoNivelAcademico): ISolicitudRrhhRequisitoNivelAcademicoRequest {
    if (!value) {
      return value as unknown as ISolicitudRrhhRequisitoNivelAcademicoRequest;
    }
    return {
      solicitudRrhhId: value.solicitudRrhhId,
      documentoRef: value.documento?.documentoRef,
      requisitoIpNivelAcademicoId: value.requisitoIpNivelAcademico?.id
    };
  }
}

export const SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_REQUEST_CONVERTER = new SolicitudRrhhRequisitoNivelAcademicoRequestConverter();
