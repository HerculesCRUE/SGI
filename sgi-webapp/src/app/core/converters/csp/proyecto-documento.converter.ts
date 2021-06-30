import { IProyectoDocumentoBackend } from '@core/models/csp/backend/proyecto-documento-backend';
import { IProyectoDocumento } from '@core/models/csp/proyecto-documento';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoDocumentoConverter extends SgiBaseConverter<IProyectoDocumentoBackend, IProyectoDocumento> {

  toTarget(value: IProyectoDocumentoBackend): IProyectoDocumento {
    if (!value) {
      return value as unknown as IProyectoDocumento;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoFase: value.tipoFase,
      tipoDocumento: value.tipoDocumento,
      comentario: value.comentario,
      visible: value.visible
    };
  }

  fromTarget(value: IProyectoDocumento): IProyectoDocumentoBackend {
    if (!value) {
      return value as unknown as IProyectoDocumentoBackend;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoFase: value.tipoFase,
      tipoDocumento: value.tipoDocumento,
      comentario: value.comentario,
      visible: value.visible
    };
  }
}

export const PROYECTO_DOCUMENTO_CONVERTER = new ProyectoDocumentoConverter();
