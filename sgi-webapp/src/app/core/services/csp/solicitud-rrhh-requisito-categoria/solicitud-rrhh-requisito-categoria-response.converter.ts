import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { ISolicitudRrhhRequisitoCategoria } from '@core/models/csp/solicitud-rrhh-requisito-categoria';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhRequisitoCategoriaResponse } from './solicitud-rrhh-requisito-categoria-response';

class SolicitudRrhhRequisitoCategoriaResponseConverter
  extends SgiBaseConverter<ISolicitudRrhhRequisitoCategoriaResponse, ISolicitudRrhhRequisitoCategoria> {
  toTarget(value: ISolicitudRrhhRequisitoCategoriaResponse): ISolicitudRrhhRequisitoCategoria {
    if (!value) {
      return value as unknown as ISolicitudRrhhRequisitoCategoria;
    }
    return {

      id: value.id,
      solicitudRrhhId: value.solicitudRrhhId,
      documento: value.documentoRef ? { documentoRef: value.documentoRef } as IDocumento : null,
      requisitoIpCategoria: value.requisitoIpCategoriaProfesionalId
        ? { id: value.requisitoIpCategoriaProfesionalId } as IRequisitoIPCategoriaProfesional : null
    };
  }

  fromTarget(value: ISolicitudRrhhRequisitoCategoria): ISolicitudRrhhRequisitoCategoriaResponse {
    if (!value) {
      return value as unknown as ISolicitudRrhhRequisitoCategoriaResponse;
    }
    return {
      id: value.id,
      solicitudRrhhId: value.solicitudRrhhId,
      documentoRef: value.documento?.documentoRef,
      requisitoIpCategoriaProfesionalId: value.requisitoIpCategoria?.id
    };
  }
}

export const SOLICITUD_RRHH_REQUISITO_CATEGORIA_RESPONSE_CONVERTER = new SolicitudRrhhRequisitoCategoriaResponseConverter();
