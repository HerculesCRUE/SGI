import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONCEPTO_GASTO_RESPONSE_CONVERTER } from '../concepto-gasto/concepto-gasto-response.converter';
import { IAnualidadGastoResponse } from './anualidad-gasto-response';

class AnualidadGastoResponseConverter extends SgiBaseConverter<IAnualidadGastoResponse, IAnualidadGasto>{
  toTarget(value: IAnualidadGastoResponse): IAnualidadGasto {
    if (!value) {
      return value as unknown as IAnualidadGasto;
    }
    return {
      id: value.id,
      proyectoAnualidad: { id: value.proyectoAnualidadId } as IProyectoAnualidad,
      conceptoGasto: value.conceptoGasto ? CONCEPTO_GASTO_RESPONSE_CONVERTER.toTarget(value.conceptoGasto) : null,
      codigoEconomico: value.codigoEconomicoRef ? { id: value.codigoEconomicoRef } as ICodigoEconomicoGasto : null,
      importePresupuesto: value.importePresupuesto,
      proyectoPartida: value.proyectoPartida?.id ? {
        id: value.proyectoPartida?.id,
        codigo: value.proyectoPartida?.codigo,
        partidaSge: value.proyectoPartida?.partidaRef ? { id: value.proyectoPartida.partidaRef } as IPartidaPresupuestariaSge : null
      } as IProyectoPartida : null,
      importeConcedido: value.importeConcedido,
      proyectoSgeRef: value.proyectoSgeRef
    };
  }
  fromTarget(value: IAnualidadGasto): IAnualidadGastoResponse {
    if (!value) {
      return value as unknown as IAnualidadGastoResponse;
    }
    return {
      id: value.id,
      proyectoAnualidadId: value.proyectoAnualidad?.id,
      conceptoGasto: value.conceptoGasto ? CONCEPTO_GASTO_RESPONSE_CONVERTER.fromTarget(value.conceptoGasto) : null,
      codigoEconomicoRef: value.codigoEconomico?.id,
      importePresupuesto: value.importePresupuesto,
      proyectoPartida: {
        id: value.proyectoPartida?.id,
        codigo: value.proyectoPartida?.codigo,
        partidaRef: value.proyectoPartida?.partidaSge?.id
      },
      importeConcedido: value.importeConcedido,
      proyectoSgeRef: value.proyectoSgeRef
    };
  }
}

export const ANUALIDAD_GASTO_RESPONSE_CONVERTER = new AnualidadGastoResponseConverter();
