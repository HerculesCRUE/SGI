import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConvocatoriaPartidaPresupuestariaResponse } from './convocatoria-partida-presupuestaria-response';

class ConvocatoriaPartidaPresupuestariaResponseConverter
  extends SgiBaseConverter<IConvocatoriaPartidaPresupuestariaResponse, IConvocatoriaPartidaPresupuestaria> {
  toTarget(value: IConvocatoriaPartidaPresupuestariaResponse): IConvocatoriaPartidaPresupuestaria {
    if (!value) {
      return value as unknown as IConvocatoriaPartidaPresupuestaria;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      codigo: value.codigo,
      partidaSge: value.partidaRef ? { id: value.partidaRef } as IPartidaPresupuestariaSge : null,
      tipoPartida: value.tipoPartida
    };
  }

  fromTarget(value: IConvocatoriaPartidaPresupuestaria): IConvocatoriaPartidaPresupuestariaResponse {
    if (!value) {
      return value as unknown as IConvocatoriaPartidaPresupuestariaResponse;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      codigo: value.codigo,
      partidaRef: value.partidaSge?.id,
      tipoPartida: value.tipoPartida
    };
  }
}

export const CONVOCATORIA_PARTIDA_PRESUPUESTARIA_RESPONSE_CONVERTER = new ConvocatoriaPartidaPresupuestariaResponseConverter();
