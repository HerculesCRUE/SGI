import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IInvencionDocumento } from '@core/models/pii/invencion-documento';
import { IDocumento } from '@core/models/sgdoc/documento';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInvencionDocumentoRequest } from './invencion-documento-request';

export class InvencionDocumentoRequestConverter extends SgiBaseConverter<IInvencionDocumentoRequest, IInvencionDocumento> {

  toTarget(value: IInvencionDocumentoRequest): IInvencionDocumento {

    return value ? {
      id: undefined,
      fechaAnadido: LuxonUtils.fromBackend(value.fechaAnadido),
      documento: { documentoRef: value.documentoRef } as IDocumento,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      invencionId: value.invencionId
    } : (value as unknown as IInvencionDocumento);
  }

  fromTarget(value: IInvencionDocumento): IInvencionDocumentoRequest {
    return value ? {
      documentoRef: value.documento.documentoRef,
      fechaAnadido: LuxonUtils.toBackend(value.fechaAnadido),
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
      invencionId: value.invencionId
    } as IInvencionDocumentoRequest : value as unknown as IInvencionDocumentoRequest;
  }

}

export const INVENCION_DOCUMENTO_REQUEST_CONVERTER = new InvencionDocumentoRequestConverter();
