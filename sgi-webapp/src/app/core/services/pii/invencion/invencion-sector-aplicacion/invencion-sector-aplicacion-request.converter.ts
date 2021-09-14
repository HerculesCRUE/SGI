import { IInvencion } from "@core/models/pii/invencion";
import { IInvencionSectorAplicacion } from "@core/models/pii/invencion-sector-aplicacion";
import { ISectorAplicacion } from "@core/models/pii/sector-aplicacion";
import { SgiBaseConverter } from "@sgi/framework/core";
import { IInvencionSectorAplicacionRequest } from "./invencion-sector-aplicacion-request";

class IInvencionSectorAplicacionRequestConverter extends SgiBaseConverter<IInvencionSectorAplicacionRequest, IInvencionSectorAplicacion> {

  toTarget(value: IInvencionSectorAplicacionRequest): IInvencionSectorAplicacion {
    if (!value) {
      return value as unknown as IInvencionSectorAplicacion;
    }

    return {
      id: undefined,
      invencion: { id: value.invencionId } as IInvencion,
      sectorAplicacion: { id: value.sectorAplicacionId } as ISectorAplicacion,
    };
  }

  fromTarget(value: IInvencionSectorAplicacion): IInvencionSectorAplicacionRequest {
    if (!value) {
      return value as unknown as IInvencionSectorAplicacionRequest;
    }

    return {
      invencionId: value.invencion?.id,
      sectorAplicacionId: value.sectorAplicacion.id
    };
  }
}

export const INVENCION_SECTORAPLICACION_REQUEST_CONVERTER = new IInvencionSectorAplicacionRequestConverter();
