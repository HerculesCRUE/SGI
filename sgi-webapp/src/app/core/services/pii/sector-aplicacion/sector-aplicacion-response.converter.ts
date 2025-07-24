import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISectorAplicacionResponse } from './sector-aplicacion-response';

class SectorAplicacionResponseConverter extends SgiBaseConverter<ISectorAplicacionResponse, ISectorAplicacion> {
  toTarget(value: ISectorAplicacionResponse): ISectorAplicacion {
    if (!value) {
      return value as unknown as ISectorAplicacion;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ISectorAplicacion): ISectorAplicacionResponse {
    if (!value) {
      return value as unknown as ISectorAplicacionResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
}

export const SECTOR_APLICACION_RESPONSE_CONVERTER = new SectorAplicacionResponseConverter();
