import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAreaTematicaResponse } from './area-tematica-response';

class AreaTematicaResponseConverter
  extends SgiBaseConverter<IAreaTematicaResponse, IAreaTematica> {
  toTarget(value: IAreaTematicaResponse): IAreaTematica {
    if (!value) {
      return value as unknown as IAreaTematica;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      padre: AREA_TEMATICA_RESPONSE_CONVERTER.toTarget(value.padre),
      activo: value.activo
    };
  }

  fromTarget(value: IAreaTematica): IAreaTematicaResponse {
    if (!value) {
      return value as unknown as IAreaTematicaResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      padre: AREA_TEMATICA_RESPONSE_CONVERTER.fromTarget(value.padre),
      activo: value.activo
    };
  }
}

export const AREA_TEMATICA_RESPONSE_CONVERTER = new AreaTematicaResponseConverter();
