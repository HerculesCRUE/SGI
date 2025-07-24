import { IModeloTipoFinalidad } from '@core/models/csp/modelo-tipo-finalidad';
import { SgiBaseConverter } from '@sgi/framework/core';
import { MODELO_EJECUCION_RESPONSE_CONVERTER } from '../modelo-ejecucion/modelo-ejecucion-response.converter';
import { TIPO_FINALIDAD_RESPONSE_CONVERTER } from '../tipo-finalidad/tipo-finalidad-response.converter';
import { IModeloTipoFinalidadResponse } from './modelo-tipo-finalidad-response';

class ModeloTipoFinalidadResponseConverter extends SgiBaseConverter<IModeloTipoFinalidadResponse, IModeloTipoFinalidad> {
  toTarget(value: IModeloTipoFinalidadResponse): IModeloTipoFinalidad {
    if (!value) {
      return value as unknown as IModeloTipoFinalidad;
    }
    return {
      id: value.id,
      modeloEjecucion: value.modeloEjecucion ? MODELO_EJECUCION_RESPONSE_CONVERTER.toTarget(value.modeloEjecucion) : null,
      tipoFinalidad: value.tipoFinalidad ? TIPO_FINALIDAD_RESPONSE_CONVERTER.toTarget(value.tipoFinalidad) : null,
      activo: value.activo
    };
  }
  fromTarget(value: IModeloTipoFinalidad): IModeloTipoFinalidadResponse {
    if (!value) {
      return value as unknown as IModeloTipoFinalidadResponse;
    }
    return {
      id: value.id,
      modeloEjecucion: value.modeloEjecucion ? MODELO_EJECUCION_RESPONSE_CONVERTER.fromTarget(value.modeloEjecucion) : null,
      tipoFinalidad: value.tipoFinalidad ? TIPO_FINALIDAD_RESPONSE_CONVERTER.fromTarget(value.tipoFinalidad) : null,
      activo: value.activo
    };
  }
}

export const MODELO_TIPO_FINALIDAD_RESPONSE_CONVERTER = new ModeloTipoFinalidadResponseConverter();
