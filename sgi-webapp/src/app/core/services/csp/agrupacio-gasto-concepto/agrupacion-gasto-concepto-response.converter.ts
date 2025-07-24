import { IAgrupacionGastoConcepto } from '@core/models/csp/agrupacion-gasto-concepto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONCEPTO_GASTO_RESPONSE_CONVERTER } from '../concepto-gasto/concepto-gasto-response.converter';
import { IAgrupacionGastoConceptoResponse } from './agrupacion-gasto-concepto-response';

class AgrupacionGastoConceptoResponseConverter
  extends SgiBaseConverter<IAgrupacionGastoConceptoResponse, IAgrupacionGastoConcepto> {
  toTarget(value: IAgrupacionGastoConceptoResponse): IAgrupacionGastoConcepto {
    if (!value) {
      return value as unknown as IAgrupacionGastoConcepto;
    }
    return {
      id: value.id,
      agrupacionId: value.agrupacionId,
      conceptoGasto: value.conceptoGasto ? CONCEPTO_GASTO_RESPONSE_CONVERTER.toTarget(value.conceptoGasto) : null
    };
  }

  fromTarget(value: IAgrupacionGastoConcepto): IAgrupacionGastoConceptoResponse {
    if (!value) {
      return value as unknown as IAgrupacionGastoConceptoResponse;
    }
    return {
      id: value.id,
      agrupacionId: value.agrupacionId,
      conceptoGasto: value.conceptoGasto ? CONCEPTO_GASTO_RESPONSE_CONVERTER.fromTarget(value.conceptoGasto) : null
    };
  }
}

export const AGRUPACION_GASTO_CONCEPTO_RESPONSE_CONVERTER = new AgrupacionGastoConceptoResponseConverter();
