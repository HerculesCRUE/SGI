import { IAgrupacionGastoConcepto } from '@core/models/csp/agrupacion-gasto-concepto';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { SgiBaseConverter } from '@sgi/framework/core';
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
      conceptoGasto: {
        id: value.conceptoGasto.id,
        nombre: value.conceptoGasto.nombre
      } as IConceptoGasto,
    };
  }

  fromTarget(value: IAgrupacionGastoConcepto): IAgrupacionGastoConceptoResponse {
    if (!value) {
      return value as unknown as IAgrupacionGastoConceptoResponse;
    }
    return {
      id: value.id,
      agrupacionId: value.agrupacionId,
      conceptoGasto: {
        id: value.conceptoGasto.id,
        nombre: value.conceptoGasto.nombre
      },
    };
  }
}

export const AGRUPACION_GASTO_CONCEPTO_RESPONSE_CONVERTER = new AgrupacionGastoConceptoResponseConverter();
