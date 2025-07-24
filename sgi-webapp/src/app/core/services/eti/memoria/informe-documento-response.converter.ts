import { Language } from '@core/i18n/language';
import { IInformeDocumento } from '@core/models/eti/informe-documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInformeDocumentoResponse } from './informe-documento-response';

class InformeDocumentoResponseConverter extends SgiBaseConverter<IInformeDocumentoResponse, IInformeDocumento> {
  toTarget(value: IInformeDocumentoResponse): IInformeDocumento {
    if (!value) {
      return value as unknown as IInformeDocumento;
    }
    return {
      documentoRef: value.documentoRef,
      informeId: value.informeId,
      lang: Language.fromCode(value.lang)
    };
  }

  fromTarget(value: IInformeDocumento): IInformeDocumentoResponse {
    if (!value) {
      return value as unknown as IInformeDocumentoResponse;
    }
    return {
      documentoRef: value.documentoRef,
      informeId: value.informeId,
      lang: value.lang.code
    };
  }
}


export const INFORME_DOCUMENTO_RESPONSE_CONVERTER = new InformeDocumentoResponseConverter();
