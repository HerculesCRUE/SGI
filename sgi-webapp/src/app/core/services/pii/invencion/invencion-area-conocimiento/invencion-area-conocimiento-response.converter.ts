import { IInvencion } from "@core/models/pii/invencion";
import { IInvencionAreaConocimiento } from "@core/models/pii/invencion-area-conocimiento";
import { IAreaConocimiento } from "@core/models/sgo/area-conocimiento";
import { SgiBaseConverter } from "@sgi/framework/core";
import { IInvencionAreaConocimientoResponse } from "./invencion-area-conocimiento-response";

class IInvencionAreaConocimientoResponseConverter extends SgiBaseConverter<IInvencionAreaConocimientoResponse, IInvencionAreaConocimiento> {

  toTarget(value: IInvencionAreaConocimientoResponse): IInvencionAreaConocimiento {
    if (!value) {
      return value as unknown as IInvencionAreaConocimiento;
    }

    return {
      id: value.id,
      invencion: { id: value.invencionId } as IInvencion,
      areaConocimiento: { id: value.areaConocimientoRef } as IAreaConocimiento,
    };
  }

  fromTarget(value: IInvencionAreaConocimiento): IInvencionAreaConocimientoResponse {
    if (!value) {
      return value as unknown as IInvencionAreaConocimientoResponse;
    }

    return {
      id: value.id,
      invencionId: value.invencion?.id,
      areaConocimientoRef: value.areaConocimiento?.id
    };
  }
}

export const INVENCION_AREACONOCIMIENTO_RESPONSE_CONVERTER = new IInvencionAreaConocimientoResponseConverter();