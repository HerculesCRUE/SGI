import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IProyecto } from '@core/models/csp/proyecto';
import { IInvencion } from '@core/models/pii/invencion';
import { IRelacion } from '@core/models/rel/relacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRelacionResponse } from './relacion-response';

class RelacionResponseConverter extends SgiBaseConverter<IRelacionResponse, IRelacion>{
  toTarget(value: IRelacionResponse): IRelacion {
    if (!value) {
      return value as unknown as IRelacion;
    }
    return {
      id: value.id,
      tipoEntidadDestino: value.tipoEntidadDestino,
      tipoEntidadOrigen: value.tipoEntidadOrigen,
      entidadDestino: { id: +value.entidadDestinoRef } as IInvencion | IProyecto | IConvocatoria,
      entidadOrigen: { id: +value.entidadOrigenRef } as IInvencion | IProyecto | IConvocatoria,
      observaciones: value.observaciones
    };
  }
  fromTarget(value: IRelacion): IRelacionResponse {
    if (!value) {
      return value as unknown as IRelacionResponse;
    }
    return {
      id: value.id,
      tipoEntidadDestino: value.tipoEntidadDestino,
      tipoEntidadOrigen: value.tipoEntidadOrigen,
      entidadDestinoRef: value.entidadDestino?.id?.toString(),
      entidadOrigenRef: value.entidadOrigen?.id?.toString(),
      observaciones: value.observaciones
    };
  }
}

export const RELACION_RESPONSE_CONVERTER = new RelacionResponseConverter();