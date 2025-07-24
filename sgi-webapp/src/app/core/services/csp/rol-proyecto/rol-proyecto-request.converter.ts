
import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRolProyectoRequest } from './rol-proyecto-request';


class RolProyectoRequestConverter extends SgiBaseConverter<IRolProyectoRequest, IRolProyecto> {
  toTarget(value: IRolProyectoRequest): IRolProyecto {
    if (!value) {
      return value as unknown as IRolProyecto;
    }
    return {
      id: undefined,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      abreviatura: value.abreviatura ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.abreviatura) : [],
      baremablePRC: value.baremablePRC,
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.descripcion) : [],
      equipo: value.equipo,
      orden: value.orden,
      rolPrincipal: value.rolPrincipal,
      activo: value.activo
    };
  }
  fromTarget(value: IRolProyecto): IRolProyectoRequest {
    if (!value) {
      return value as unknown as IRolProyectoRequest;
    }
    return {
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
      abreviatura: value.abreviatura ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.abreviatura) : [],
      baremablePRC: value.baremablePRC,
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.descripcion) : [],
      equipo: value.equipo,
      orden: value.orden,
      rolPrincipal: value.rolPrincipal,
      activo: value.activo
    };
  }
}

export const ROL_PROYECTO_REQUEST_CONVERTER = new RolProyectoRequestConverter();
