import { IAcreditacion } from '@core/models/prc/acreditacion';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAcreditacionResponse } from './acreditacion-response';

class AcreditacionResponseConverter extends SgiBaseConverter<IAcreditacionResponse, IAcreditacion>{
  toTarget(value: IAcreditacionResponse): IAcreditacion {
    if (!value) {
      return value as unknown as IAcreditacion;
    }
    return {
      id: value.id,
      documento: value.documentoRef ?
        { documentoRef: value.documentoRef } as IDocumento : null,
      produccionCientifica: value.produccionCientificaId ?
        { id: value.produccionCientificaId } as IProduccionCientifica : null,
      url: value.url
    };
  }
  fromTarget(value: IAcreditacion): IAcreditacionResponse {
    if (!value) {
      return value as unknown as IAcreditacionResponse;
    }
    return {
      id: value.id,
      documentoRef: value.documento?.documentoRef,
      produccionCientificaId: value.produccionCientifica?.id,
      url: value.url
    };
  }
}

export const ACREDITACION_RESPONSE_CONVERTER = new AcreditacionResponseConverter();
