import { IDocumentacionConvocatoriaReunionBackend } from '@core/models/eti/backend/documentacion-convocatoria-reunion-backend';
import { IDocumentacionConvocatoriaReunion } from '@core/models/eti/documentacion-convocatoria-reunion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONVOCATORIA_REUNION_CONVERTER } from './convocatoria-reunion.converter';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';

class DocumentacionConvocatoriaReunionConverter extends SgiBaseConverter<IDocumentacionConvocatoriaReunionBackend, IDocumentacionConvocatoriaReunion> {
  toTarget(value: IDocumentacionConvocatoriaReunionBackend): IDocumentacionConvocatoriaReunion {
    if (!value) {
      return value as unknown as IDocumentacionConvocatoriaReunion;
    }
    return {
      id: value.id,
      convocatoriaReunion: { id: value.convocatoriaReunionId } as IConvocatoriaReunion,
      nombre: value.nombre,
      documento: { documentoRef: value.documentoRef } as IDocumento,
    };
  }

  fromTarget(value: IDocumentacionConvocatoriaReunion): IDocumentacionConvocatoriaReunionBackend {
    if (!value) {
      return value as unknown as IDocumentacionConvocatoriaReunionBackend;
    }
    return {
      id: value.id,
      convocatoriaReunionId: value.convocatoriaReunion?.id,
      nombre: value.nombre,
      documentoRef: value.documento.documentoRef,
    };
  }
}

export const DOCUMENTACION_CONVOCATORIA_REUNION_CONVERTER = new DocumentacionConvocatoriaReunionConverter();
