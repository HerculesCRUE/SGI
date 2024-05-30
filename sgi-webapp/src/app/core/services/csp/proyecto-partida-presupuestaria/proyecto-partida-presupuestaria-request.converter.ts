import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoPartidaPresupuestariaRequest } from './proyecto-partida-presupuestaria-request';

class ProyectoPartidaPresupuestariaRequestConverter
  extends SgiBaseConverter<IProyectoPartidaPresupuestariaRequest, IProyectoPartida> {
  toTarget(value: IProyectoPartidaPresupuestariaRequest): IProyectoPartida {
    if (!value) {
      return value as unknown as IProyectoPartida;
    }
    return {
      id: undefined,
      proyectoId: value.proyectoId,
      convocatoriaPartidaId: value.convocatoriaPartidaId,
      descripcion: value.descripcion,
      codigo: value.codigo,
      partidaSge: value.partidaRef ? { id: value.partidaRef } as IPartidaPresupuestariaSge : null,
      tipoPartida: value.tipoPartida
    };
  }

  fromTarget(value: IProyectoPartida): IProyectoPartidaPresupuestariaRequest {
    if (!value) {
      return value as unknown as IProyectoPartidaPresupuestariaRequest;
    }
    return {
      proyectoId: value.proyectoId,
      convocatoriaPartidaId: value.convocatoriaPartidaId,
      descripcion: value.descripcion,
      codigo: value.codigo,
      partidaRef: value.partidaSge?.id,
      tipoPartida: value.tipoPartida
    };
  }
}

export const PROYECTO_PARTIDA_PRESUPUESTARIA_REQUEST_CONVERTER = new ProyectoPartidaPresupuestariaRequestConverter();
