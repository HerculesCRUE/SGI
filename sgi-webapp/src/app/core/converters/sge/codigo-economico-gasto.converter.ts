import { ICodigoEconomicoGastoBackend } from '@core/models/sge/backend/codigo-economico-gasto-backend';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class CodigoEconomicoGastoConverter extends SgiBaseConverter<ICodigoEconomicoGastoBackend, ICodigoEconomicoGasto> {
  toTarget(value: ICodigoEconomicoGastoBackend): ICodigoEconomicoGasto {
    if (!value) {
      return value as unknown as ICodigoEconomicoGasto;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin)
    };
  }

  fromTarget(value: ICodigoEconomicoGasto): ICodigoEconomicoGastoBackend {
    if (!value) {
      return value as unknown as ICodigoEconomicoGastoBackend;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin)
    };
  }
}

export const CODIGO_ECONOMICO_GASTO_CONVERTER = new CodigoEconomicoGastoConverter();
