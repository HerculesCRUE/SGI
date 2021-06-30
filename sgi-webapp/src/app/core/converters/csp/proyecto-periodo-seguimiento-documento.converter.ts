import { IProyectoPeriodoSeguimientoDocumentoBackend } from '@core/models/csp/backend/proyecto-periodo-seguimiento-documento-backend';
import { IProyectoPeriodoSeguimientoDocumento } from '@core/models/csp/proyecto-periodo-seguimiento-documento';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoPeriodoSeguimientoDocumentoConverter extends
  SgiBaseConverter<IProyectoPeriodoSeguimientoDocumentoBackend, IProyectoPeriodoSeguimientoDocumento> {

  toTarget(value: IProyectoPeriodoSeguimientoDocumentoBackend): IProyectoPeriodoSeguimientoDocumento {
    if (!value) {
      return value as unknown as IProyectoPeriodoSeguimientoDocumento;
    }
    return {
      id: value.id,
      proyectoPeriodoSeguimientoId: value.proyectoPeriodoSeguimientoId,
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento,
      visible: value.visible,
      comentario: value.comentario
    };
  }

  fromTarget(value: IProyectoPeriodoSeguimientoDocumento): IProyectoPeriodoSeguimientoDocumentoBackend {
    if (!value) {
      return value as unknown as IProyectoPeriodoSeguimientoDocumentoBackend;
    }
    return {
      id: value.id,
      proyectoPeriodoSeguimientoId: value.proyectoPeriodoSeguimientoId,
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento,
      visible: value.visible,
      comentario: value.comentario
    };
  }
}

export const PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_CONVERTER = new ProyectoPeriodoSeguimientoDocumentoConverter();
