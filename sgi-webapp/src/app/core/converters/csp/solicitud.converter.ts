import { ISolicitudBackend } from '@core/models/csp/backend/solicitud-backend';
import { ISolicitud } from '@core/models/csp/solicitud';
import { IPersona } from '@core/models/sgp/persona';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ESTADO_SOLICITUD_CONVERTER } from './estado-solicitud.converter';

class SolicitudConverter extends SgiBaseConverter<ISolicitudBackend, ISolicitud> {

  toTarget(value: ISolicitudBackend): ISolicitud {
    if (!value) {
      return value as unknown as ISolicitud;
    }
    return {
      id: value.id,
      titulo: value.titulo,
      activo: value.activo,
      codigoExterno: value.codigoExterno,
      codigoRegistroInterno: value.codigoRegistroInterno,
      estado: ESTADO_SOLICITUD_CONVERTER.toTarget(value.estado),
      convocatoriaId: value.convocatoriaId,
      convocatoriaExterna: value.convocatoriaExterna,
      creador: { id: value.creadorRef } as IPersona,
      solicitante: { id: value.solicitanteRef } as IPersona,
      formularioSolicitud: value.formularioSolicitud,
      unidadGestion: { id: +value.unidadGestionRef } as IUnidadGestion,
      observaciones: value.observaciones
    };
  }

  fromTarget(value: ISolicitud): ISolicitudBackend {
    if (!value) {
      return value as unknown as ISolicitudBackend;
    }
    return {
      id: value.id,
      titulo: value.titulo,
      activo: value.activo,
      codigoExterno: value.codigoExterno,
      codigoRegistroInterno: value.codigoRegistroInterno,
      estado: ESTADO_SOLICITUD_CONVERTER.fromTarget(value.estado),
      convocatoriaId: value.convocatoriaId,
      convocatoriaExterna: value.convocatoriaExterna,
      creadorRef: value.creador?.id,
      solicitanteRef: value.solicitante?.id,
      formularioSolicitud: value.formularioSolicitud,
      unidadGestionRef: String(value.unidadGestion?.id),
      observaciones: value.observaciones
    };
  }
}

export const SOLICITUD_CONVERTER = new SolicitudConverter();
