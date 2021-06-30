import { IDocumentoBackend } from '@core/models/sgdoc/backend/documento-backend';
import { IDocumento } from '@core/models/sgdoc/documento';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class DocumentoConverter extends SgiBaseConverter<IDocumentoBackend, IDocumento> {
  toTarget(value: IDocumentoBackend): IDocumento {
    if (!value) {
      return value as unknown as IDocumento;
    }
    return {
      documentoRef: value.documentoRef,
      nombre: value.nombre,
      version: value.version,
      archivo: value.archivo,
      fechaCreacion: LuxonUtils.fromBackend(value.fechaCreacion),
      tipo: value.tipo,
      autorRef: value.autorRef
    };
  }

  fromTarget(value: IDocumento): IDocumentoBackend {
    if (!value) {
      return value as unknown as IDocumentoBackend;
    }
    return {
      documentoRef: value.documentoRef,
      nombre: value.nombre,
      version: value.version,
      archivo: value.archivo,
      fechaCreacion: LuxonUtils.toBackend(value.fechaCreacion),
      tipo: value.tipo,
      autorRef: value.autorRef
    };
  }
}

export const DOCUMENTO_CONVERTER = new DocumentoConverter();
