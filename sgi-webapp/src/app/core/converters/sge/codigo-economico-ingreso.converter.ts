import { ICodigoEconomicoIngresoBackend } from '@core/models/sge/backend/codigo-economico-ingreso-backend';
import { ICodigoEconomicoIngreso } from '@core/models/sge/codigo-economico-ingreso';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class CodigoEconomicoIngresoConverter extends SgiBaseConverter<ICodigoEconomicoIngresoBackend, ICodigoEconomicoIngreso> {
  toTarget(value: ICodigoEconomicoIngresoBackend): ICodigoEconomicoIngreso {
    if (!value) {
      return value as unknown as ICodigoEconomicoIngreso;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin)
    };
  }

  fromTarget(value: ICodigoEconomicoIngreso): ICodigoEconomicoIngresoBackend {
    if (!value) {
      return value as unknown as ICodigoEconomicoIngresoBackend;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin)
    };
  }
}

export const CODIGO_ECONOMICO_INGRESO_CONVERTER = new CodigoEconomicoIngresoConverter();
