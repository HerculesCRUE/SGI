import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoPartidaPresupuestariaResponse } from './proyecto-partida-presupuestaria-response';

class ProyectoPartidaPresupuestariaResponseConverter
  extends SgiBaseConverter<IProyectoPartidaPresupuestariaResponse, IProyectoPartida> {
  toTarget(value: IProyectoPartidaPresupuestariaResponse): IProyectoPartida {
    if (!value) {
      return value as unknown as IProyectoPartida;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      convocatoriaPartidaId: value.convocatoriaPartidaId,
      descripcion: value.descripcion,
      codigo: value.codigo,
      partidaSge: value.partidaRef ? { id: value.partidaRef } as IPartidaPresupuestariaSge : null,
      tipoPartida: value.tipoPartida
    };
  }

  fromTarget(value: IProyectoPartida): IProyectoPartidaPresupuestariaResponse {
    if (!value) {
      return value as unknown as IProyectoPartidaPresupuestariaResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      convocatoriaPartidaId: value.convocatoriaPartidaId,
      descripcion: value.descripcion,
      codigo: value.codigo,
      partidaRef: value.partidaSge?.id,
      tipoPartida: value.tipoPartida
    };
  }
}

export const PROYECTO_PARTIDA_PRESUPUESTARIA_RESPONSE_CONVERTER = new ProyectoPartidaPresupuestariaResponseConverter();
