import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IResultadoInformePatentibilidad } from '@core/models/pii/resultado-informe-patentabilidad';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IResultadoInformePatentibilidadRequest } from './resultado-informe-patentabilidad-request';

class ResultadoInformePatentibilidadRequestConverter extends SgiBaseConverter<IResultadoInformePatentibilidadRequest, IResultadoInformePatentibilidad> {
  toTarget(value: IResultadoInformePatentibilidadRequest): IResultadoInformePatentibilidad {
    if (!value) {
      return value as unknown as IResultadoInformePatentibilidad;
    }
    return {
      id: undefined,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.descripcion) : [],
      activo: true
    };
  }
  fromTarget(value: IResultadoInformePatentibilidad): IResultadoInformePatentibilidadRequest {
    if (!value) {
      return value as unknown as IResultadoInformePatentibilidadRequest;
    }
    return {
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.descripcion) : [],
    };
  }
}

export const RESULTADO_INFORME_PATENTABILIDAD_REQUEST_CONVERTER = new ResultadoInformePatentibilidadRequestConverter();
