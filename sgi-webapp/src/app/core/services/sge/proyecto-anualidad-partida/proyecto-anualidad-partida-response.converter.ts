import { IProyectoAnualidadPartida } from '@core/models/sge/proyecto-anualidad-partida';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoAnualidadPartidaResponse } from './proyecto-anualidad-partida-response';

class ProyectoAnualidadPartidaResponseConverter extends SgiBaseConverter<IProyectoAnualidadPartidaResponse, IProyectoAnualidadPartida>{
  toTarget(value: IProyectoAnualidadPartidaResponse): IProyectoAnualidadPartida {
    if (!value) {
      return value as unknown as IProyectoAnualidadPartida;
    }
    return {
      proyecto: {
        id: value.proyectoId
      } as IProyectoSge,
      anualidad: value.anualidad,
      tipoDatoEconomico: value.tipo,
      partidaPresupuestaria: value.partidaPresupuestaria,
      importe: value.importe
    };
  }
  fromTarget(value: IProyectoAnualidadPartida): IProyectoAnualidadPartidaResponse {
    if (!value) {
      return value as unknown as IProyectoAnualidadPartidaResponse;
    }
    return {
      proyectoId: value.proyecto.id,
      anualidad: value.anualidad,
      tipo: value.tipoDatoEconomico,
      partidaPresupuestaria: value.partidaPresupuestaria,
      importe: value.importe
    };
  }
}

export const PROYECTO_ANUALIDAD_PARTIDA_RESPONSE_CONVERTER = new ProyectoAnualidadPartidaResponseConverter();
