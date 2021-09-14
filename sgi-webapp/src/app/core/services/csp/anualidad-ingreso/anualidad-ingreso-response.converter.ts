import { IAnualidadIngreso } from '@core/models/csp/anualidad-ingreso';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { ICodigoEconomicoIngreso } from '@core/models/sge/codigo-economico-ingreso';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAnualidadIngresoResponse } from './anualidad-ingreso-response';

class AnualidadIngresoResponseConverter extends SgiBaseConverter<IAnualidadIngresoResponse, IAnualidadIngreso>{
  toTarget(value: IAnualidadIngresoResponse): IAnualidadIngreso {
    if (!value) {
      return value as unknown as IAnualidadIngreso;
    }
    return {
      id: value.id,
      proyectoAnualidad: { id: value.proyectoAnualidadId } as IProyectoAnualidad,
      codigoEconomico: { id: value.codigoEconomicoRef } as ICodigoEconomicoIngreso,
      proyectoPartida: {
        id: value.proyectoPartida?.id,
        codigo: value.proyectoPartida?.codigo
      } as IProyectoPartida,
      importeConcedido: value.importeConcedido,
      proyectoSgeRef: value.proyectoSgeRef
    };
  }
  fromTarget(value: IAnualidadIngreso): IAnualidadIngresoResponse {
    if (!value) {
      return value as unknown as IAnualidadIngresoResponse;
    }
    return {
      id: value.id,
      proyectoAnualidadId: value.proyectoAnualidad?.id,
      codigoEconomicoRef: value.codigoEconomico?.id,
      proyectoPartida: {
        id: value.proyectoPartida?.id,
        codigo: value.proyectoPartida?.codigo
      },
      importeConcedido: value.importeConcedido,
      proyectoSgeRef: value.proyectoSgeRef
    };
  }
}

export const ANUALIDAD_INGRESO_RESPONSE_CONVERTER = new AnualidadIngresoResponseConverter();
