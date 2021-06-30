import { IInformeBackend } from '@core/models/eti/backend/informe-backend';
import { IInforme } from '@core/models/eti/informe';
import { SgiBaseConverter } from '@sgi/framework/core';
import { MEMORIA_CONVERTER } from './memoria.converter';

class InformeConverter extends SgiBaseConverter<IInformeBackend, IInforme> {
  toTarget(value: IInformeBackend): IInforme {
    if (!value) {
      return value as unknown as IInforme;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.toTarget(value.memoria),
      documentoRef: value.documentoRef,
      version: value.version,
      tipoEvaluacion: value.tipoEvaluacion
    };
  }

  fromTarget(value: IInforme): IInformeBackend {
    if (!value) {
      return value as unknown as IInformeBackend;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.fromTarget(value.memoria),
      documentoRef: value.documentoRef,
      version: value.version,
      tipoEvaluacion: value.tipoEvaluacion
    };
  }
}


export const INFORME_CONVERTER = new InformeConverter();
