import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { IConceptoGasto } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAnualidadGastoRequest } from './anualidad-gasto-request';

class AnualidadGastoRequestConverter extends SgiBaseConverter<IAnualidadGastoRequest, IAnualidadGasto>{
  toTarget(value: IAnualidadGastoRequest): IAnualidadGasto {
    if (!value) {
      return value as unknown as IAnualidadGasto;
    }
    return {
      id: undefined,
      proyectoAnualidad: { id: value.proyectoAnualidadId } as IProyectoAnualidad,
      conceptoGasto: { id: value.conceptoGastoId } as IConceptoGasto,
      codigoEconomicoRef: value.codigoEconomicoRef,
      importePresupuesto: value.importePresupuesto,
      importeConcedido: value.importeConcedido,
      proyectoPartida: { id: value.proyectoPartidaId } as IProyectoPartida,
      proyectoSgeRef: value.proyectoSgeRef
    };
  }
  fromTarget(value: IAnualidadGasto): IAnualidadGastoRequest {
    if (!value) {
      return value as unknown as IAnualidadGastoRequest;
    }
    return {
      proyectoAnualidadId: value.proyectoAnualidad?.id,
      conceptoGastoId: value.conceptoGasto?.id,
      codigoEconomicoRef: value.codigoEconomicoRef,
      importePresupuesto: value.importePresupuesto,
      importeConcedido: value.importeConcedido,
      proyectoPartidaId: value.proyectoPartida.id,
      proyectoSgeRef: value.proyectoSgeRef
    };
  }
}

export const ANUALIDAD_GASTO_REQUEST_CONVERTER = new AnualidadGastoRequestConverter();
