import { IAnualidadIngreso } from '@core/models/csp/anualidad-ingreso';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAnualidadIngresoRequest } from './anualidad-ingreso-request';

class AnualidadIngresoRequestConverter extends SgiBaseConverter<IAnualidadIngresoRequest, IAnualidadIngreso>{
  toTarget(value: IAnualidadIngresoRequest): IAnualidadIngreso {
    if (!value) {
      return value as unknown as IAnualidadIngreso;
    }
    return {
      id: undefined,
      proyectoAnualidad: { id: value.proyectoAnualidadId } as IProyectoAnualidad,
      codigoEconomicoRef: value.codigoEconomicoRef,
      proyectoPartida: {
        id: value.proyectoPartidaId
      } as IProyectoPartida,
      importeConcedido: value.importeConcedido,
      proyectoSgeRef: value.proyectoSgeRef
    };
  }
  fromTarget(value: IAnualidadIngreso): IAnualidadIngresoRequest {
    if (!value) {
      return value as unknown as IAnualidadIngresoRequest;
    }
    return {
      proyectoAnualidadId: value.proyectoAnualidad?.id,
      codigoEconomicoRef: value.codigoEconomicoRef,
      proyectoPartidaId: value.proyectoPartida.id,
      importeConcedido: value.importeConcedido,
      proyectoSgeRef: value.proyectoSgeRef
    };
  }
}

export const ANUALIDAD_INGRESO_REQUEST_CONVERTER = new AnualidadIngresoRequestConverter();
