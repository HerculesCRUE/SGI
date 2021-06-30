import { IConvocatoriaConceptoGastoCodigoEcBackend } from '@core/models/csp/backend/convocatoria-concepto-gasto-codigo-ec-backend';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaConceptoGastoCodigoEcConverter extends
  SgiBaseConverter<IConvocatoriaConceptoGastoCodigoEcBackend, IConvocatoriaConceptoGastoCodigoEc> {

  toTarget(value: IConvocatoriaConceptoGastoCodigoEcBackend): IConvocatoriaConceptoGastoCodigoEc {
    if (!value) {
      return value as unknown as IConvocatoriaConceptoGastoCodigoEc;
    }
    return {
      id: value.id,
      convocatoriaConceptoGastoId: value.convocatoriaConceptoGastoId,
      codigoEconomicoRef: value.codigoEconomicoRef,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      observaciones: value.observaciones
    };
  }

  fromTarget(value: IConvocatoriaConceptoGastoCodigoEc): IConvocatoriaConceptoGastoCodigoEcBackend {
    if (!value) {
      return value as unknown as IConvocatoriaConceptoGastoCodigoEcBackend;
    }
    return {
      id: value.id,
      convocatoriaConceptoGastoId: value.convocatoriaConceptoGastoId,
      codigoEconomicoRef: value.codigoEconomicoRef,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      observaciones: value.observaciones
    };
  }
}

export const CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_CONVERTER = new ConvocatoriaConceptoGastoCodigoEcConverter();
