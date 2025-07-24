import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IModeloEjecucionResponse } from './modelo-ejecucion-response';

class ModeloEjecucionResponseConverter extends SgiBaseConverter<IModeloEjecucionResponse, IModeloEjecucion> {
  toTarget(value: IModeloEjecucionResponse): IModeloEjecucion {
    if (!value) {
      return value as unknown as IModeloEjecucion;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      externo: value.externo,
      contrato: value.contrato,
      solicitudSinConvocatoria: value.solicitudSinConvocatoria,
      activo: value.activo
    };
  }
  fromTarget(value: IModeloEjecucion): IModeloEjecucionResponse {
    if (!value) {
      return value as unknown as IModeloEjecucionResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      externo: value.externo,
      contrato: value.contrato,
      solicitudSinConvocatoria: value.solicitudSinConvocatoria,
      activo: value.activo
    };
  }
}

export const MODELO_EJECUCION_RESPONSE_CONVERTER = new ModeloEjecucionResponseConverter();
