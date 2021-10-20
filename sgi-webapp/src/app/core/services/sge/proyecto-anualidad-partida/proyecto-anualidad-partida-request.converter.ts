
import { IProyectoAnualidadPartida } from '@core/models/sge/proyecto-anualidad-partida';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoAnualidadPartidaRequest } from './proyecto-anualidad-partida-request';

class ProyectoAnualidadPartidaRequestConverter extends SgiBaseConverter<IProyectoAnualidadPartidaRequest, IProyectoAnualidadPartida>{
  toTarget(value: IProyectoAnualidadPartidaRequest): IProyectoAnualidadPartida {
    if (!value) {
      return value as unknown as IProyectoAnualidadPartida;
    }
    return {
      proyecto: {
        id: value.proyectoId
      } as IProyectoSge,
      anualidad: value.anualidad,
      tipoDatoEconomico: value.tipoDatoEconomico,
      partidaPresupuestaria: value.partidaPresupuestaria,
      importe: value.importe
    };
  }
  fromTarget(value: IProyectoAnualidadPartida): IProyectoAnualidadPartidaRequest {
    if (!value) {
      return value as unknown as IProyectoAnualidadPartidaRequest;
    }
    return {
      proyectoId: value.proyecto.id,
      anualidad: value.anualidad,
      tipoDatoEconomico: value.tipoDatoEconomico,
      partidaPresupuestaria: value.partidaPresupuestaria,
      importe: value.importe
    };
  }
}

export const PROYECTO_ANUALIDAD_PARTIDA_REQUEST_CONVERTER = new ProyectoAnualidadPartidaRequestConverter();
