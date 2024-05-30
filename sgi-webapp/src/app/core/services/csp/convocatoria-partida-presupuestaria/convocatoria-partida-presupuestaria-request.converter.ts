import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConvocatoriaPartidaPresupuestariaRequest } from './convocatoria-partida-presupuestaria-request';

class ConvocatoriaPartidaPresupuestariaRequestConverter
  extends SgiBaseConverter<IConvocatoriaPartidaPresupuestariaRequest, IConvocatoriaPartidaPresupuestaria> {
  toTarget(value: IConvocatoriaPartidaPresupuestariaRequest): IConvocatoriaPartidaPresupuestaria {
    if (!value) {
      return value as unknown as IConvocatoriaPartidaPresupuestaria;
    }
    return {
      id: undefined,
      convocatoriaId: value.convocatoriaId,
      descripcion: value.descripcion,
      codigo: value.codigo,
      partidaSge: value.partidaRef ? { id: value.partidaRef } as IPartidaPresupuestariaSge : null,
      tipoPartida: value.tipoPartida
    };
  }

  fromTarget(value: IConvocatoriaPartidaPresupuestaria): IConvocatoriaPartidaPresupuestariaRequest {
    if (!value) {
      return value as unknown as IConvocatoriaPartidaPresupuestariaRequest;
    }
    return {
      convocatoriaId: value.convocatoriaId,
      descripcion: value.descripcion,
      codigo: value.codigo,
      partidaRef: value.partidaSge?.id,
      tipoPartida: value.tipoPartida
    };
  }
}

export const CONVOCATORIA_PARTIDA_PRESUPUESTARIA_REQUEST_CONVERTER = new ConvocatoriaPartidaPresupuestariaRequestConverter();
