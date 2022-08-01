import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { ISolicitudRrhhRequisitoNivelAcademico } from '@core/models/csp/solicitud-rrhh-requisito-nivel-academico';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhRequisitoNivelAcademicoResponse } from './solicitud-rrhh-requisito-nivel-academico-response';

class SolicitudRrhhRequisitoNivelAcademicoResponseConverter
  extends SgiBaseConverter<ISolicitudRrhhRequisitoNivelAcademicoResponse, ISolicitudRrhhRequisitoNivelAcademico> {
  toTarget(value: ISolicitudRrhhRequisitoNivelAcademicoResponse): ISolicitudRrhhRequisitoNivelAcademico {
    if (!value) {
      return value as unknown as ISolicitudRrhhRequisitoNivelAcademico;
    }
    return {

      id: value.id,
      solicitudRrhhId: value.solicitudRrhhId,
      documento: value.documentoRef ? { documentoRef: value.documentoRef } as IDocumento : null,
      requisitoIpNivelAcademico: value.requisitoIpNivelAcademicoId
        ? { id: value.requisitoIpNivelAcademicoId } as IRequisitoIPNivelAcademico : null
    };
  }

  fromTarget(value: ISolicitudRrhhRequisitoNivelAcademico): ISolicitudRrhhRequisitoNivelAcademicoResponse {
    if (!value) {
      return value as unknown as ISolicitudRrhhRequisitoNivelAcademicoResponse;
    }
    return {
      id: value.id,
      solicitudRrhhId: value.solicitudRrhhId,
      documentoRef: value.documento?.documentoRef,
      requisitoIpNivelAcademicoId: value.requisitoIpNivelAcademico?.id
    };
  }
}

export const SOLICITUD_RRHH_REQUISITO_NIVEL_ACADEMICO_RESPONSE_CONVERTER = new SolicitudRrhhRequisitoNivelAcademicoResponseConverter();
