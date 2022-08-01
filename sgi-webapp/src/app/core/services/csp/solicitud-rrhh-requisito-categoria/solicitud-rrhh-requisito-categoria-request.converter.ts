import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { ISolicitudRrhhRequisitoCategoria } from '@core/models/csp/solicitud-rrhh-requisito-categoria';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhRequisitoCategoriaRequest } from './solicitud-rrhh-requisito-categoria-request';

class SolicitudRrhhRequisitoCategoriaRequestConverter
  extends SgiBaseConverter<ISolicitudRrhhRequisitoCategoriaRequest, ISolicitudRrhhRequisitoCategoria> {
  toTarget(value: ISolicitudRrhhRequisitoCategoriaRequest): ISolicitudRrhhRequisitoCategoria {
    if (!value) {
      return value as unknown as ISolicitudRrhhRequisitoCategoria;
    }
    return {
      id: null,
      solicitudRrhhId: value.solicitudRrhhId,
      documento: value.documentoRef ? { documentoRef: value.documentoRef } as IDocumento : null,
      requisitoIpCategoria: value.requisitoIpCategoriaProfesionalId
        ? { id: value.requisitoIpCategoriaProfesionalId } as IRequisitoIPCategoriaProfesional : null
    };
  }

  fromTarget(value: ISolicitudRrhhRequisitoCategoria): ISolicitudRrhhRequisitoCategoriaRequest {
    if (!value) {
      return value as unknown as ISolicitudRrhhRequisitoCategoriaRequest;
    }
    return {
      solicitudRrhhId: value.solicitudRrhhId,
      documentoRef: value.documento?.documentoRef,
      requisitoIpCategoriaProfesionalId: value.requisitoIpCategoria?.id
    };
  }
}

export const SOLICITUD_RRHH_REQUISITO_CATEGORIA_REQUEST_CONVERTER = new SolicitudRrhhRequisitoCategoriaRequestConverter();
