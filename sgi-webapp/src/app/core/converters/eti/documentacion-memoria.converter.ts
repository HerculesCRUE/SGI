import { IDocumentacionMemoriaBackend } from '@core/models/eti/backend/documentacion-memoria-backend';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { MEMORIA_CONVERTER } from './memoria.converter';

class DocumentacionMemoriaConverter extends SgiBaseConverter<IDocumentacionMemoriaBackend, IDocumentacionMemoria> {
  toTarget(value: IDocumentacionMemoriaBackend): IDocumentacionMemoria {
    if (!value) {
      return value as unknown as IDocumentacionMemoria;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.toTarget(value.memoria),
      tipoDocumento: value.tipoDocumento,
      nombre: value.nombre,
      documento: { documentoRef: value.documentoRef } as IDocumento,
    };
  }

  fromTarget(value: IDocumentacionMemoria): IDocumentacionMemoriaBackend {
    if (!value) {
      return value as unknown as IDocumentacionMemoriaBackend;
    }
    return {
      id: value.id,
      memoria: MEMORIA_CONVERTER.fromTarget(value.memoria),
      tipoDocumento: value.tipoDocumento,
      nombre: value.nombre,
      documentoRef: value.documento.documentoRef,
    };
  }
}

export const DOCUMENTACION_MEMORIA_CONVERTER = new DocumentacionMemoriaConverter();
