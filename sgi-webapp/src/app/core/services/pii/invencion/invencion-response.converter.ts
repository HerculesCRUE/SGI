import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyecto } from '@core/models/csp/proyecto';
import { IInvencion } from '@core/models/pii/invencion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_PROTECCION_RESPONSE_CONVERTER } from '../tipo-proteccion/tipo-proteccion-response.converter';
import { IInvencionResponse } from './invencion-response';

class InvencionResponseConverter extends SgiBaseConverter<IInvencionResponse, IInvencion> {
  toTarget(value: IInvencionResponse): IInvencion {
    if (!value) {
      return value as unknown as IInvencion;
    }
    return {
      id: value.id,
      fechaComunicacion: LuxonUtils.fromBackend(value.fechaComunicacion),
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.titulo) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      comentarios: value.comentarios ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentarios) : [],
      proyecto: value.proyectoRef !== null ? { id: +value.proyectoRef } as IProyecto : null,
      tipoProteccion: value.tipoProteccion ? TIPO_PROTECCION_RESPONSE_CONVERTER.toTarget(value.tipoProteccion) : null,
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
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.titulo) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      comentarios: value.comentarios ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentarios) : [],
      proyectoRef: value.proyecto?.id?.toString(),
      tipoProteccion: value.tipoProteccion ? TIPO_PROTECCION_RESPONSE_CONVERTER.fromTarget(value.tipoProteccion) : null,
      activo: value.activo
    };
  }
}

export const INVENCION_RESPONSE_CONVERTER = new InvencionResponseConverter();
