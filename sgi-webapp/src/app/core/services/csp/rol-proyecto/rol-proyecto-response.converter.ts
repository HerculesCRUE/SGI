
import { EQUIPO_MAP, IRolProyecto } from '@core/models/csp/rol-proyecto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRolProyectoResponse } from './rol-proyecto-response';
import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';


class RolProyectoResponseConverter extends SgiBaseConverter<IRolProyectoResponse, IRolProyecto> {
  toTarget(value: IRolProyectoResponse): IRolProyecto {
    if (!value) {
      return value as unknown as IRolProyecto;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      abreviatura: value.abreviatura ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.abreviatura) : [],
      baremablePRC: value.baremablePRC,
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      equipo: value.equipo,
      orden: value.orden,
      rolPrincipal: value.rolPrincipal,
      activo: value.activo
    };
  }
  fromTarget(value: IRolProyecto): IRolProyectoResponse {
    if (!value) {
      return value as unknown as IRolProyectoResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      abreviatura: value.abreviatura ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.abreviatura) : [],
      baremablePRC: value.baremablePRC,
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      equipo: value.equipo,
      orden: value.orden,
      rolPrincipal: value.rolPrincipal,
      activo: value.activo
    };
  }
}

export const ROL_PROYECTO_RESPONSE_CONVERTER = new RolProyectoResponseConverter();
