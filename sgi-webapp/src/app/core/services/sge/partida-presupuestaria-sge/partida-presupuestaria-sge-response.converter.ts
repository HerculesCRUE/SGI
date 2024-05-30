import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IPartidaPresupuestariaSgeResponse } from './partida-presupuestaria-sge-response';

class PartidaPresupuestariaSgeResponseConverter extends SgiBaseConverter<IPartidaPresupuestariaSgeResponse, IPartidaPresupuestariaSge>{

  toTarget(value: IPartidaPresupuestariaSgeResponse): IPartidaPresupuestariaSge {
    return value ? {
      id: value.id,
      codigo: value.codigo,
      descripcion: value.descripcion,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio)
    } : value as unknown as IPartidaPresupuestariaSge;
  }

  fromTarget(value: IPartidaPresupuestariaSge): IPartidaPresupuestariaSgeResponse {
    return value ? {
      id: value.id,
      codigo: value.codigo,
      descripcion: value.descripcion,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio)
    } : value as unknown as IPartidaPresupuestariaSgeResponse;
  }

}

export const PARTIDA_PRESUPUESTARIA_SGE_RESPONSE_CONVERTER = new PartidaPresupuestariaSgeResponseConverter();
