import { IAnualidadResumen } from '@core/models/csp/anualidad-resumen';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAnualidadResumenResponse } from './anualidad-resumen-response';

class AnualidadResumenResponseConverter extends SgiBaseConverter<IAnualidadResumenResponse, IAnualidadResumen>{
  toTarget(value: IAnualidadResumenResponse): IAnualidadResumen {
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
  fromTarget(value: IAnualidadResumen): IAnualidadResumenResponse {
    if (!value) {
      return value as unknown as IAnualidadResumenResponse;
    }
    return {
      tipo: value.tipo,
      codigoPartidaPresupuestaria: value.codigoPartidaPresupuestaria,
      importeConcedido: value.importeConcedido,
      importePresupuesto: value.importePresupuesto
    };
  }
}

export const ANUALIDAD_RESUMEN_RESPONSE_CONVERTER = new AnualidadResumenResponseConverter();
