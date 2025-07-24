import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONCEPTO_GASTO_RESPONSE_CONVERTER } from '../concepto-gasto/concepto-gasto-response.converter';
import { IConvocatoriaConceptoGastoResponse } from './convocatoria-concepto-gasto-response';

class ConvocatoriaConceptoGastoResponseConverter extends SgiBaseConverter<IConvocatoriaConceptoGastoResponse, IConvocatoriaConceptoGasto> {

  toTarget(value: IConvocatoriaConceptoGastoResponse): IConvocatoriaConceptoGasto {
    if (!value) {
      return value as unknown as IConvocatoriaConceptoGasto;
    }
    return {
      id: value.id,
      conceptoGasto: value.conceptoGasto ? CONCEPTO_GASTO_RESPONSE_CONVERTER.toTarget(value.conceptoGasto) : null,
      convocatoriaId: value.convocatoriaId,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      importeMaximo: value.importeMaximo,
      porcentajeMaximo: value.porcentajeMaximo,
      permitido: value.permitido,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
    };
  }

  fromTarget(value: IConvocatoriaConceptoGasto): IConvocatoriaConceptoGastoResponse {
    if (!value) {
      return value as unknown as IConvocatoriaConceptoGastoResponse;
    }
    return {
      id: value.id,
      conceptoGasto: value.conceptoGasto ? CONCEPTO_GASTO_RESPONSE_CONVERTER.fromTarget(value.conceptoGasto) : null,
      convocatoriaId: value.convocatoriaId,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      importeMaximo: value.importeMaximo,
      porcentajeMaximo: value.porcentajeMaximo,
      permitido: value.permitido,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
    };
  }
}

export const CONVOCATORIA_CONCEPTO_GASTO_RESPONSE_CONVERTER = new ConvocatoriaConceptoGastoResponseConverter();
