import { ITipoDocumento } from '@core/models/eer/tipo-documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoDocumentoResponse } from './tipo-documento-response';

class TipoDocumentoResponseConverter
  extends SgiBaseConverter<ITipoDocumentoResponse, ITipoDocumento> {
  toTarget(value: ITipoDocumentoResponse): ITipoDocumento {
    if (!value) {
      return value as unknown as ITipoDocumento;
    }
    return {
      id: value.id,
      descripcion: value.descripcion,
      nombre: value.nombre,
      activo: value.activo
    };
  }

  fromTarget(value: ITipoDocumento): ITipoDocumentoResponse {
    throw new Error('Method not implemented');
  }
}

export const TIPO_DOCUMENTO_RESPONSE_CONVERTER = new TipoDocumentoResponseConverter();
