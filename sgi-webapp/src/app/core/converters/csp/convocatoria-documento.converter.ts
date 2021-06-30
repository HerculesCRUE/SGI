import { IConvocatoriaDocumentoBackend } from '@core/models/csp/backend/convocatoria-documento-backend';
import { IConvocatoriaDocumento } from '@core/models/csp/convocatoria-documento';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaDocumentoConverter extends SgiBaseConverter<IConvocatoriaDocumentoBackend, IConvocatoriaDocumento> {

  toTarget(value: IConvocatoriaDocumentoBackend): IConvocatoriaDocumento {
    if (!value) {
      return value as unknown as IConvocatoriaDocumento;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoFase: value.tipoFase,
      tipoDocumento: value.tipoDocumento,
      publico: value.publico,
      observaciones: value.observaciones
    };
  }

  fromTarget(value: IConvocatoriaDocumento): IConvocatoriaDocumentoBackend {
    if (!value) {
      return value as unknown as IConvocatoriaDocumentoBackend;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoFase: value.tipoFase,
      tipoDocumento: value.tipoDocumento,
      publico: value.publico,
      observaciones: value.observaciones
    };
  }
}

export const CONVOCATORIA_DOCUMENTO_CONVERTER = new ConvocatoriaDocumentoConverter();
