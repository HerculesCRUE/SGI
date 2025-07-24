import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_HITO_RESPONSE_CONVERTER } from '../tipo-hito/tipo-hito-response.converter';
import { IModeloTipoHitoResponse } from './modelo-tipo-hito-response';
import { MODELO_EJECUCION_RESPONSE_CONVERTER } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response.converter';

class ModeloTipoHitoResponseConverter extends SgiBaseConverter<IModeloTipoHitoResponse, IModeloTipoHito> {
  toTarget(value: IModeloTipoHitoResponse): IModeloTipoHito {
    if (!value) {
      return value as unknown as IModeloTipoHito;
    }
    return {
      id: value.id,
      modeloEjecucion: value.modeloEjecucion ? MODELO_EJECUCION_RESPONSE_CONVERTER.toTarget(value.modeloEjecucion) : null,
      tipoHito: value.tipoHito ? TIPO_HITO_RESPONSE_CONVERTER.toTarget(value.tipoHito) : null,
      convocatoria: value.convocatoria,
      proyecto: value.proyecto,
      solicitud: value.solicitud,
      activo: value.activo
    };
  }
  fromTarget(value: IModeloTipoHito): IModeloTipoHitoResponse {
    if (!value) {
      return value as unknown as IModeloTipoHitoResponse;
    }
    return {
      id: value.id,
      modeloEjecucion: value.modeloEjecucion ? MODELO_EJECUCION_RESPONSE_CONVERTER.fromTarget(value.modeloEjecucion) : null,
      tipoHito: value.tipoHito ? TIPO_HITO_RESPONSE_CONVERTER.fromTarget(value.tipoHito) : null,
      convocatoria: value.convocatoria,
      proyecto: value.proyecto,
      solicitud: value.solicitud,
      activo: value.activo
    };
  }
}

export const MODELO_TIPO_HITO_RESPONSE_CONVERTER = new ModeloTipoHitoResponseConverter();
