import { IConvocatoriaPartidaPresupuestariaBackend } from '@core/models/csp/backend/convocatoria-partida-backend';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaPartidaConverter extends SgiBaseConverter<IConvocatoriaPartidaPresupuestariaBackend, IConvocatoriaPartidaPresupuestaria> {

  toTarget(value: IConvocatoriaPartidaPresupuestariaBackend): IConvocatoriaPartidaPresupuestaria {
    if (!value) {
      return value as unknown as IConvocatoriaPartidaPresupuestaria;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      codigo: value.codigo,
      descripcion: value.descripcion,
      tipoPartida: value.tipoPartida
    };
  }

  fromTarget(value: IConvocatoriaPartidaPresupuestaria): IConvocatoriaPartidaPresupuestariaBackend {
    if (!value) {
      return value as unknown as IConvocatoriaPartidaPresupuestariaBackend;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      codigo: value.codigo,
      descripcion: value.descripcion,
      tipoPartida: value.tipoPartida
    };
  }
}

export const CONVOCATORIA_PARTIDA_PRESUPUESTARIA_CONVERTER = new ConvocatoriaPartidaConverter();
