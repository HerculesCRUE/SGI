import { IInforme } from '@core/models/eti/informe';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInformeResponse } from './informe-response';
import { MEMORIA_RESPONSE_CONVERTER } from './memoria-response.converter';
import { INFORME_DOCUMENTO_RESPONSE_CONVERTER } from './informe-documento-response.converter';

class InformeResponseConverter extends SgiBaseConverter<IInformeResponse, IInforme> {
  toTarget(value: IInformeResponse): IInforme {
    if (!value) {
      return value as unknown as IInforme;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.toTarget(value.memoria),
      documentos: INFORME_DOCUMENTO_RESPONSE_CONVERTER.toTargetArray(value.informeDocumentos),
      version: value.version,
      tipoEvaluacion: value.tipoEvaluacion
    };
  }

  fromTarget(value: IInforme): IInformeResponse {
    if (!value) {
      return value as unknown as IInformeResponse;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.fromTarget(value.memoria),
      informeDocumentos: INFORME_DOCUMENTO_RESPONSE_CONVERTER.fromTargetArray(value.documentos),
      version: value.version,
      tipoEvaluacion: value.tipoEvaluacion
    };
  }
}


export const INFORME_RESPONSE_CONVERTER = new InformeResponseConverter();
