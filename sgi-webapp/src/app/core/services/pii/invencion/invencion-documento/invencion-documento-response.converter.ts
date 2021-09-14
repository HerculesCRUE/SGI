import { IInvencionDocumento } from '@core/models/pii/invencion-documento';
import { IDocumento } from '@core/models/sgdoc/documento';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInvencionDocumentoResponse } from './invencion-documento-response';

export class InvencionDocumentoResponseConverter extends SgiBaseConverter<IInvencionDocumentoResponse, IInvencionDocumento>{

  toTarget(value: IInvencionDocumentoResponse): IInvencionDocumento {

    return value ? {
      id: value.id,
      documento: { documentoRef: value.documentoRef } as IDocumento,
      fechaAnadido: LuxonUtils.fromBackend(value.fechaAnadido),
      nombre: value.nombre,
      invencionId: value.invencionId
    } : value as unknown as IInvencionDocumento;
  }

  fromTarget(value: IInvencionDocumento): IInvencionDocumentoResponse {

    return value ? {
      id: value.id,
      documentoRef: value.documento.documentoRef,
      fechaAnadido: LuxonUtils.toBackend(value.fechaAnadido),
      nombre: value.nombre,
      invencionId: value.invencionId
    } : value as unknown as IInvencionDocumentoResponse;
  }
}
export const INVENCION_DOCUMENTO_RESPONSE_CONVERTER = new InvencionDocumentoResponseConverter();
