import { IInvencion } from "@core/models/pii/invencion";
import { IInvencionAreaConocimiento } from "@core/models/pii/invencion-area-conocimiento";
import { IAreaConocimiento } from "@core/models/sgo/area-conocimiento";
import { SgiBaseConverter } from "@sgi/framework/core";
import { IInvencionAreaConocimientoRequest } from "./invencion-area-conocimiento-request";

class IInvencionAreaConocimientoRequestConverter extends SgiBaseConverter<IInvencionAreaConocimientoRequest, IInvencionAreaConocimiento> {

  toTarget(value: IInvencionAreaConocimientoRequest): IInvencionAreaConocimiento {
    if (!value) {
      return value as unknown as IInvencionAreaConocimiento;
    }

    return {
      id: undefined,
      invencion: { id: value.invencionId } as IInvencion,
      areaConocimiento: { id: value.areaConocimientoRef } as IAreaConocimiento,
    };
  }

  fromTarget(value: IInvencionAreaConocimiento): IInvencionAreaConocimientoRequest {
    if (!value) {
      return value as unknown as IInvencionAreaConocimientoRequest;
    }

    return {
      invencionId: value.invencion?.id,
      areaConocimientoRef: value.areaConocimiento?.id
    };
  }
}

export const INVENCION_AREACONOCIMIENTO_REQUEST_CONVERTER = new IInvencionAreaConocimientoRequestConverter();