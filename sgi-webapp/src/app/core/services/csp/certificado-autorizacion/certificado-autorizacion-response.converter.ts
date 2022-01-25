import { IAutorizacion } from '@core/models/csp/autorizacion';
import { ICertificadoAutorizacion } from '@core/models/csp/certificado-autorizacion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ICertificadoAutorizacionResponse } from './certificado-autorizacion-response';

class CertificadoAutorizacionResponseConverter
  extends SgiBaseConverter<ICertificadoAutorizacionResponse, ICertificadoAutorizacion> {
  toTarget(value: ICertificadoAutorizacionResponse): ICertificadoAutorizacion {
    if (!value) {
      return value as unknown as ICertificadoAutorizacion;
    }
    return {
      id: value.id,
      autorizacion: { id: value.autorizacionId } as IAutorizacion,
      documento: { documentoRef: value.documentoRef } as IDocumento,
      nombre: value.nombre,
      visible: value.visible
    };
  }

  fromTarget(value: ICertificadoAutorizacion): ICertificadoAutorizacionResponse {
    if (!value) {
      return value as unknown as ICertificadoAutorizacionResponse;
    }
    return {
      id: value.id,
      autorizacionId: value.autorizacion?.id,
      documentoRef: value.documento.documentoRef,
      nombre: value.nombre,
      visible: value.visible
    };
  }
}

export const CERTIFICADO_AUTORIZACION_RESPONSE_CONVERTER = new CertificadoAutorizacionResponseConverter();
