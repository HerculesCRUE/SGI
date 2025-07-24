import { IModeloTipoDocumento } from '@core/models/csp/modelo-tipo-documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { MODELO_TIPO_FASE_RESPONSE_CONVERTER } from '../modelo-tipo-fase/modelo-tipo-fase-response.converter';
import { MODELO_EJECUCION_RESPONSE_CONVERTER } from '../modelo-ejecucion/modelo-ejecucion-response.converter';
import { IModeloTipoDocumentoResponse } from './modelo-tipo-documento-response';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from '../tipo-documento/tipo-documento-response.converter';

class ModeloTipoDocumentoResponseConverter extends SgiBaseConverter<IModeloTipoDocumentoResponse, IModeloTipoDocumento> {
  toTarget(value: IModeloTipoDocumentoResponse): IModeloTipoDocumento {
    if (!value) {
      return value as unknown as IModeloTipoDocumento;
    }
    return {
      id: value.id,
      modeloEjecucion: value.modeloEjecucion ? MODELO_EJECUCION_RESPONSE_CONVERTER.toTarget(value.modeloEjecucion) : null,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.toTarget(value.tipoDocumento) : null,
      modeloTipoFase: value.modeloTipoFase ? MODELO_TIPO_FASE_RESPONSE_CONVERTER.toTarget(value.modeloTipoFase) : null,
      activo: value.activo
    };
  }
  fromTarget(value: IModeloTipoDocumento): IModeloTipoDocumentoResponse {
    if (!value) {
      return value as unknown as IModeloTipoDocumentoResponse;
    }
    return {
      id: value.id,
      modeloEjecucion: value.modeloEjecucion ? MODELO_EJECUCION_RESPONSE_CONVERTER.fromTarget(value.modeloEjecucion) : null,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.fromTarget(value.tipoDocumento) : null,
      modeloTipoFase: value.modeloTipoFase ? MODELO_TIPO_FASE_RESPONSE_CONVERTER.fromTarget(value.modeloTipoFase) : null,
      activo: value.activo
    };
  }
}

export const MODELO_TIPO_DOCUMENTO_RESPONSE_CONVERTER = new ModeloTipoDocumentoResponseConverter();
