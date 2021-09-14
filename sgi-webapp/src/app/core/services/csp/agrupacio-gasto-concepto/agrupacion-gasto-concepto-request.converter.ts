import { IAgrupacionGastoConcepto } from '@core/models/csp/agrupacion-gasto-concepto';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAgrupacionGastoConceptoRequest } from './agrupacion-gasto-concepto-request';

class AgrupacionGastoConceptoRequestConverter
  extends SgiBaseConverter<IAgrupacionGastoConceptoRequest, IAgrupacionGastoConcepto> {
  toTarget(value: IAgrupacionGastoConceptoRequest): IAgrupacionGastoConcepto {
    if (!value) {
      return value as unknown as IAgrupacionGastoConcepto;
    }
    return {
      id: undefined,
      agrupacionId: value.agrupacionId,
      conceptoGasto: { id: value.conceptoGastoId } as IConceptoGasto
    };
  }

  fromTarget(value: IAgrupacionGastoConcepto): IAgrupacionGastoConceptoRequest {
    if (!value) {
      return value as unknown as IAgrupacionGastoConceptoRequest;
    }
    return {
      agrupacionId: value.agrupacionId,
      conceptoGastoId: value.conceptoGasto.id
    };
  }
}

export const AGRUPACION_GASTO_CONCEPTO_REQUEST_CONVERTER = new AgrupacionGastoConceptoRequestConverter();
