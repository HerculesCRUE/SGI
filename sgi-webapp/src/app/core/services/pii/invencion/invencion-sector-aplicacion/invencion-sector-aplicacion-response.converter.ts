import { IInvencion } from "@core/models/pii/invencion";
import { IInvencionSectorAplicacion } from "@core/models/pii/invencion-sector-aplicacion";
import { SgiBaseConverter } from "@sgi/framework/core";
import { SECTOR_APLICACION_RESPONSE_CONVERTER } from "../../sector-aplicacion/sector-aplicacion-response.converter";
import { IInvencionSectorAplicacionResponse } from "./invencion-sector-aplicacion-response";

class IInvencionSectorAplicacionResponseConverter extends SgiBaseConverter<IInvencionSectorAplicacionResponse, IInvencionSectorAplicacion> {

  toTarget(value: IInvencionSectorAplicacionResponse): IInvencionSectorAplicacion {
    if (!value) {
      return value as unknown as IInvencionSectorAplicacion;
    }

    return {
      id: value.id,
      invencion: { id: value.invencionId } as IInvencion,
      sectorAplicacion: value.sectorAplicacion ? SECTOR_APLICACION_RESPONSE_CONVERTER.toTarget(value.sectorAplicacion) : null,
    };
  }

  fromTarget(value: IInvencionSectorAplicacion): IInvencionSectorAplicacionResponse {
    if (!value) {
      return value as unknown as IInvencionSectorAplicacionResponse;
    }

    return {
      id: value.id,
      invencionId: value.invencion?.id,
      sectorAplicacion: value.sectorAplicacion ? SECTOR_APLICACION_RESPONSE_CONVERTER.fromTarget(value.sectorAplicacion) : null
    };
  }
}

export const INVENCION_SECTORAPLICACION_RESPONSE_CONVERTER = new IInvencionSectorAplicacionResponseConverter();