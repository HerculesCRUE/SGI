import { ITramoReparto } from "@core/models/pii/tramo-reparto";
import { SgiBaseConverter } from "@sgi/framework/core";
import { ITramoRepartoRequest } from "./tramo-reparto-request";

class TramoRepartoRequestConverter extends SgiBaseConverter<ITramoRepartoRequest, ITramoReparto>{
  toTarget(value: ITramoRepartoRequest): ITramoReparto {
    if (!value) {
      return value as unknown as ITramoReparto;
    }
    return {
      id: undefined,
      desde: value.desde,
      hasta: value.hasta,
      porcentajeUniversidad: value.porcentajeUniversidad,
      porcentajeInventores: value.porcentajeInventores,
      activo: true
    };
  }
  fromTarget(value: ITramoReparto): ITramoRepartoRequest {
    if (!value) {
      return value as unknown as ITramoRepartoRequest;
    }
    return {
      desde: value.desde,
      hasta: value.hasta,
      porcentajeUniversidad: value.porcentajeUniversidad,
      porcentajeInventores: value.porcentajeInventores,
    };
  }
}

export const TRAMO_REPARTO_REQUEST_CONVERTER = new TramoRepartoRequestConverter();