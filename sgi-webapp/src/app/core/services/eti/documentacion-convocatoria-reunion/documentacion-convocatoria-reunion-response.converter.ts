import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IDocumentacionConvocatoriaReunion } from '@core/models/eti/documentacion-convocatoria-reunion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { IDocumentacionConvocatoriaReunionResponse } from '@core/services/eti/documentacion-convocatoria-reunion/documentacion-convocatoria-reunion-response';
import { SgiBaseConverter } from '@sgi/framework/core';

class DocumentacionConvocatoriaReunionResponseConverter extends SgiBaseConverter<IDocumentacionConvocatoriaReunionResponse, IDocumentacionConvocatoriaReunion> {
  toTarget(value: IDocumentacionConvocatoriaReunionResponse): IDocumentacionConvocatoriaReunion {
    if (!value) {
      return value as unknown as IDocumentacionConvocatoriaReunion;
    }
    return {
      id: value.id,
      convocatoriaReunion: { id: value.convocatoriaReunionId } as IConvocatoriaReunion,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      documento: { documentoRef: value.documentoRef } as IDocumento,
    };
  }

  fromTarget(value: IDocumentacionConvocatoriaReunion): IDocumentacionConvocatoriaReunionResponse {
    if (!value) {
      return value as unknown as IDocumentacionConvocatoriaReunionResponse;
    }
    return {
      id: value.id,
      convocatoriaReunionId: value.convocatoriaReunion?.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      documentoRef: value.documento.documentoRef,
    };
  }
}

export const DOCUMENTACION_CONVOCATORIA_REUNION_RESPONSE_CONVERTER = new DocumentacionConvocatoriaReunionResponseConverter();
