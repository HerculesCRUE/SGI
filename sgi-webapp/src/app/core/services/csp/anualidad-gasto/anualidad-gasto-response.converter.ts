import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { IConceptoGasto } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAnualidadGastoResponse } from './anualidad-gasto-response';

class AnualidadGastoResponseConverter extends SgiBaseConverter<IAnualidadGastoResponse, IAnualidadGasto>{
  toTarget(value: IAnualidadGastoResponse): IAnualidadGasto {
    if (!value) {
      return value as unknown as IAnualidadGasto;
    }
    return {
      id: value.id,
      proyectoAnualidad: { id: value.proyectoAnualidadId } as IProyectoAnualidad,
      conceptoGasto: {
        id: value.conceptoGasto?.id,
        nombre: value.conceptoGasto?.nombre
      } as IConceptoGasto,
      codigoEconomicoRef: value.codigoEconomicoRef,
      importePresupuesto: value.importePresupuesto,
      proyectoPartida: {
        id: value.proyectoPartida.id,
        codigo: value.proyectoPartida?.codigo
      } as IProyectoPartida,
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
      conceptoGasto: {
        id: value.conceptoGasto?.id,
        nombre: value.conceptoGasto?.nombre
      } as IConceptoGasto,
      codigoEconomicoRef: value.codigoEconomicoRef,
      importePresupuesto: value.importePresupuesto,
      proyectoPartida: {
        id: value.proyectoPartida?.id,
        codigo: value.proyectoPartida?.codigo
      } as IProyectoPartida,
      importeConcedido: value.importeConcedido,
      proyectoSgeRef: value.proyectoSgeRef
    };
  }
}

export const ANUALIDAD_GASTO_RESPONSE_CONVERTER = new AnualidadGastoResponseConverter();
