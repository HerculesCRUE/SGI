import { IAnualidadResumen } from '@core/models/csp/anualidad-resumen';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAnualidadResumenRequest } from './anualidad-resumen-request';
import { IAnualidadResumenResponse } from './anualidad-resumen-response';

class AnualidadResumenRequetConverter extends SgiBaseConverter<IAnualidadResumenRequest, IAnualidadResumen>{
  toTarget(value: IAnualidadResumenRequest): IAnualidadResumen {
    if (!value) {
      return value as unknown as IAnualidadResumen;
    }
    return {
      tipo: value.tipo,
      codigoPartidaPresupuestaria: value.codigoPartidaPresupuestaria,
      importeConcedido: value.importeConcedido,
      importePresupuesto: value.importePresupuesto
    };
  }
  fromTarget(value: IAnualidadResumen): IAnualidadResumenRequest {
    if (!value) {
      return value as unknown as IAnualidadResumenRequest;
    }
    return {
      tipo: value.tipo,
      codigoPartidaPresupuestaria: value.codigoPartidaPresupuestaria,
      importeConcedido: value.importeConcedido,
      importePresupuesto: value.importePresupuesto
    };
  }
}

export const ANUALIDAD_RESUMEN_REQUEST_CONVERTER = new AnualidadResumenRequetConverter();
