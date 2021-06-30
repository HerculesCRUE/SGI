import { IProyectoSocioPeriodoJustificacionDocumentoBackend } from '@core/models/csp/backend/proyecto-socio-periodo-justificacion-documento-backend';
import { IProyectoSocioPeriodoJustificacionDocumento } from '@core/models/csp/proyecto-socio-periodo-justificacion-documento';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoSocioPeriodoJustificacionDocumentoConverter extends
  SgiBaseConverter<IProyectoSocioPeriodoJustificacionDocumentoBackend, IProyectoSocioPeriodoJustificacionDocumento> {

  toTarget(value: IProyectoSocioPeriodoJustificacionDocumentoBackend): IProyectoSocioPeriodoJustificacionDocumento {
    if (!value) {
      return value as unknown as IProyectoSocioPeriodoJustificacionDocumento;
    }
    return {
      id: value.id,
      proyectoSocioPeriodoJustificacionId: value.proyectoSocioPeriodoJustificacionId,
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento,
      comentario: value.comentario,
      visible: value.visible
    };
  }

  fromTarget(value: IProyectoSocioPeriodoJustificacionDocumento): IProyectoSocioPeriodoJustificacionDocumentoBackend {
    if (!value) {
      return value as unknown as IProyectoSocioPeriodoJustificacionDocumentoBackend;
    }
    return {
      id: value.id,
      proyectoSocioPeriodoJustificacionId: value.proyectoSocioPeriodoJustificacionId,
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento,
      comentario: value.comentario,
      visible: value.visible
    };
  }
}

export const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER = new ProyectoSocioPeriodoJustificacionDocumentoConverter();
