import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IResultadoInformePatentibilidad } from '@core/models/pii/resultado-informe-patentabilidad';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IResultadoInformePatentibilidadResponse } from './resultado-informe-patentabilidad-response';

class ResultadoInformePatentibilidadResponseConverter
  extends SgiBaseConverter<IResultadoInformePatentibilidadResponse, IResultadoInformePatentibilidad> {
  toTarget(value: IResultadoInformePatentibilidadResponse): IResultadoInformePatentibilidad {
    if (!value) {
      return value as unknown as IResultadoInformePatentibilidad;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
  fromTarget(value: IResultadoInformePatentibilidad): IResultadoInformePatentibilidadResponse {
    if (!value) {
      return value as unknown as IResultadoInformePatentibilidadResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
}

export const RESULTADO_INFORME_PATENTABILIDAD_RESPONSE_CONVERTER = new ResultadoInformePatentibilidadResponseConverter();
