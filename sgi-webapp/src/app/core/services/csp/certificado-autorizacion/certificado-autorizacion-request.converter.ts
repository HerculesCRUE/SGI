import { IAutorizacion } from '@core/models/csp/autorizacion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { ICertificadoAutorizacion } from '@core/models/csp/certificado-autorizacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ICertificadoAutorizacionRequest } from './certificado-autorizacion-request';

class CertificadoAutorizacionRequestConverter
  extends SgiBaseConverter<ICertificadoAutorizacionRequest, ICertificadoAutorizacion> {
  toTarget(value: ICertificadoAutorizacionRequest): ICertificadoAutorizacion {
    if (!value) {
      return value as unknown as ICertificadoAutorizacion;
    }
    return {
      id: undefined,
      autorizacion: { id: value.autorizacionId } as IAutorizacion,
      documento: { documentoRef: value.documentoRef } as IDocumento,
      nombre: value.nombre,
      visible: value.visible
    };
  }

  fromTarget(value: ICertificadoAutorizacion): ICertificadoAutorizacionRequest {
    if (!value) {
      return value as unknown as ICertificadoAutorizacionRequest;
    }
    return {
      autorizacionId: value.autorizacion?.id,
      documentoRef: value.documento?.documentoRef,
      nombre: value.nombre,
      visible: value.visible
    };
  }
}

export const CERTIFICADO_AUTORIZACION_REQUEST_CONVERTER = new CertificadoAutorizacionRequestConverter();
