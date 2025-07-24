import { I18N_FIELD_REQUEST_CONVERTER } from "@core/i18n/i18n-field.converter";
import { IProyecto } from "@core/models/csp/proyecto";
import { IInvencion } from "@core/models/pii/invencion";
import { ITipoProteccion } from "@core/models/pii/tipo-proteccion";
import { LuxonUtils } from "@core/utils/luxon-utils";
import { SgiBaseConverter } from "@sgi/framework/core";
import { TIPO_PROTECCION_REQUEST_CONVERTER } from "../tipo-proteccion/tipo-proteccion-request.converter";
import { IInvencionRequest } from "./invencion-request";

class InvencionRequestConverter extends SgiBaseConverter<IInvencionRequest, IInvencion> {

  toTarget(value: IInvencionRequest): IInvencion {
    if (!value) {
      return value as unknown as IInvencion;
    }
    return {
      id: undefined,
      titulo: value.titulo ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.titulo) : [],
      fechaComunicacion: LuxonUtils.fromBackend(value.fechaComunicacion),
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.descripcion) : [],
      proyecto: value.proyectoRef !== null ? { id: +value.proyectoRef } as IProyecto : null,
      tipoProteccion: { id: value.tipoProteccionId } as ITipoProteccion,
      comentarios: value.comentarios ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.comentarios) : [],
      activo: true
    };
  }

  fromTarget(value: IInvencion): IInvencionRequest {
    if (!value) {
      return value as unknown as IInvencionRequest;
    }
    return {
      titulo: value.titulo ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.titulo) : [],
      fechaComunicacion: LuxonUtils.toBackend(value.fechaComunicacion),
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.descripcion) : [],
      proyectoRef: value.proyecto?.id?.toString(),
      tipoProteccionId: value.tipoProteccion ? TIPO_PROTECCION_REQUEST_CONVERTER.fromTarget(value.tipoProteccion).id : null,
      comentarios: value.comentarios ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.comentarios) : []
    };
  }
}

export const INVENCION_REQUEST_CONVERTER = new InvencionRequestConverter();
