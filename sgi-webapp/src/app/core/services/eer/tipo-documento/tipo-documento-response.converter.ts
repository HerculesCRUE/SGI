import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
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
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }

  fromTarget(value: ITipoDocumento): ITipoDocumentoResponse {
    throw new Error('Method not implemented');
  }

  getTipoDocumento(value: ITipoDocumentoResponse): ITipoDocumento {
    if (!value) {
      return undefined;
    }
    return value.padre ? this.toTarget(value.padre) : this.toTarget(value);
  }

  getSubtipoDocumento(value: ITipoDocumentoResponse): ITipoDocumento {
    if (!value) {
      return undefined;
    }
    return value.padre ? this.toTarget(value) : undefined;
  }
}

export const TIPO_DOCUMENTO_RESPONSE_CONVERTER = new TipoDocumentoResponseConverter();
