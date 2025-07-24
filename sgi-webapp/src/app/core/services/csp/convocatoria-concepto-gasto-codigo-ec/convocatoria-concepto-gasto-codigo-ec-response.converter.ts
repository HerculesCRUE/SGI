import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoriaConceptoGastoCodigoEcResponse } from '@core/services/csp/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec-response';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaConceptoGastoCodigoEcResponseConverter extends
  SgiBaseConverter<IConvocatoriaConceptoGastoCodigoEcResponse, IConvocatoriaConceptoGastoCodigoEc> {

  toTarget(value: IConvocatoriaConceptoGastoCodigoEcResponse): IConvocatoriaConceptoGastoCodigoEc {
    if (!value) {
      return value as unknown as IConvocatoriaConceptoGastoCodigoEc;
    }
    return {
      id: value.id,
      convocatoriaConceptoGastoId: value.convocatoriaConceptoGastoId,
      codigoEconomico: { id: value.codigoEconomicoRef } as ICodigoEconomicoGasto,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
    };
  }

  fromTarget(value: IConvocatoriaConceptoGastoCodigoEc): IConvocatoriaConceptoGastoCodigoEcResponse {
    if (!value) {
      return value as unknown as IConvocatoriaConceptoGastoCodigoEcResponse;
    }
    return {
      id: value.id,
      convocatoriaConceptoGastoId: value.convocatoriaConceptoGastoId,
      codigoEconomicoRef: value.codigoEconomico.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
    };
  }
}

export const CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER = new ConvocatoriaConceptoGastoCodigoEcResponseConverter();
