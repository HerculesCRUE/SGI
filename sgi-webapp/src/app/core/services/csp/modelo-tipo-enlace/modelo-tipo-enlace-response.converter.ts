import { IModeloTipoEnlace } from '@core/models/csp/modelo-tipo-enlace';
import { MODELO_EJECUCION_RESPONSE_CONVERTER } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response.converter';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_ENLACE_RESPONSE_CONVERTER } from '../tipo-enlace/tipo-enlace-response.converter';
import { IModeloTipoEnlaceResponse } from './modelo-tipo-enlace-response';

class ModeloTipoEnlaceResponseConverter extends SgiBaseConverter<IModeloTipoEnlaceResponse, IModeloTipoEnlace> {
  toTarget(value: IModeloTipoEnlaceResponse): IModeloTipoEnlace {
    if (!value) {
      return value as unknown as IModeloTipoEnlace;
    }
    return {
      id: value.id,
      modeloEjecucion: MODELO_EJECUCION_RESPONSE_CONVERTER.toTarget(value.modeloEjecucion),
      tipoEnlace: TIPO_ENLACE_RESPONSE_CONVERTER.toTarget(value.tipoEnlace),
      activo: value.activo
    };
  }
  fromTarget(value: IModeloTipoEnlace): IModeloTipoEnlaceResponse {
    if (!value) {
      return value as unknown as IModeloTipoEnlaceResponse;
    }
    return {
      id: value.id,
      modeloEjecucion: MODELO_EJECUCION_RESPONSE_CONVERTER.fromTarget(value.modeloEjecucion),
      tipoEnlace: TIPO_ENLACE_RESPONSE_CONVERTER.fromTarget(value.tipoEnlace),
      activo: value.activo
    };
  }
}

export const MODELO_TIPO_ENLACE_RESPONSE_CONVERTER = new ModeloTipoEnlaceResponseConverter();
