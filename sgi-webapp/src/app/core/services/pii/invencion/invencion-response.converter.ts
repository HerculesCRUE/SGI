import { IInvencion } from "@core/models/pii/invencion";
import { ITipoProteccion } from "@core/models/pii/tipo-proteccion";
import { LuxonUtils } from "@core/utils/luxon-utils";
import { SgiBaseConverter } from "@sgi/framework/core";
import { IInvencionResponse } from "./invencion-response";

class InvencionResponseConverter extends SgiBaseConverter<IInvencionResponse, IInvencion>{
  toTarget(value: IInvencionResponse): IInvencion {
    if (!value) {
      return value as unknown as IInvencion;
    }
    return {
      id: value.id,
      fechaComunicacion: LuxonUtils.fromBackend(value.fechaComunicacion),
      titulo: value.titulo,
      descripcion: value.descripcion,
      comentarios: value.comentarios,
      tipoProteccion: {
        id: value.tipoProteccion.id,
        nombre: value.tipoProteccion.nombre
      } as ITipoProteccion,
      activo: value.activo
    };
  }
  fromTarget(value: IInvencion): IInvencionResponse {
    if (!value) {
      return value as unknown as IInvencionResponse;
    }
    return {
      id: value.id,
      fechaComunicacion: LuxonUtils.toBackend(value.fechaComunicacion),
      titulo: value.titulo,
      descripcion: value.descripcion,
      comentarios: value.comentarios,
      tipoProteccion: {
        id: value.tipoProteccion.id,
        nombre: value.tipoProteccion.nombre
      },
      activo: value.activo
    };
  }
}

export const INVENCION_RESPONSE_CONVERTER = new InvencionResponseConverter();