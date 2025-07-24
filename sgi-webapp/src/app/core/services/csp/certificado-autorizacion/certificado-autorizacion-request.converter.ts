import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IAutorizacion } from '@core/models/csp/autorizacion';
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
      documentoRef: value.documentoRef ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.documentoRef) : [],
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      visible: value.visible
    };
  }

  fromTarget(value: ICertificadoAutorizacion): ICertificadoAutorizacionRequest {
    if (!value) {
      return value as unknown as ICertificadoAutorizacionRequest;
    }
    return {
      autorizacionId: value.autorizacion?.id,
      documentoRef: value.documentoRef ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.documentoRef) : [],
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
      visible: value.visible
    };
  }
}

export const CERTIFICADO_AUTORIZACION_REQUEST_CONVERTER = new CertificadoAutorizacionRequestConverter();
