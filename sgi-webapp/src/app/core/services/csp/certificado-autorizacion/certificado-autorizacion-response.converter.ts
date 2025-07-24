import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { ICertificadoAutorizacion } from '@core/models/csp/certificado-autorizacion';
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
      documentoRef: value.documentoRef ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.documentoRef) : [],
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
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
      documentoRef: value.documentoRef ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.documentoRef) : [],
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      visible: value.visible
    };
  }
}

export const CERTIFICADO_AUTORIZACION_RESPONSE_CONVERTER = new CertificadoAutorizacionResponseConverter();
