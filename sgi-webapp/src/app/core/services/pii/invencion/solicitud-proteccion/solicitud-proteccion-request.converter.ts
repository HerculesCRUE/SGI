import { IInvencion } from '@core/models/pii/invencion';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { ITipoCaducidad } from '@core/models/pii/tipo-caducidad';
import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPais } from '@core/models/sgo/pais';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudProteccionRequest } from './solicitud-proteccion-request';

export class SolicitudProteccionRequestConverter extends SgiBaseConverter<ISolicitudProteccionRequest, ISolicitudProteccion> {

  toTarget(value: ISolicitudProteccionRequest): ISolicitudProteccion {
    return value ? {
      id: undefined,
      fechaCaducidad: LuxonUtils.fromBackend(value.fechaCaducidad),
      fechaConcesion: LuxonUtils.fromBackend(value.fechaConcesion),
      fechaFinPriorPresFasNacRec: LuxonUtils.fromBackend(value.fechaFinPriorPresFasNacRec),
      fechaPrioridadSolicitud: LuxonUtils.fromBackend(value.fechaPrioridadSolicitud),
      fechaPublicacion: LuxonUtils.fromBackend(value.fechaPublicacion),
      agentePropiedad: { id: value.agentePropiedadRef } as IEmpresa,
      comentarios: value.comentarios,
      estado: value.estado,
      invencion: { id: value.invencionId } as IInvencion,
      numeroConcesion: value.numeroConcesion,
      numeroPublicacion: value.numeroPublicacion,
      numeroRegistro: value.numeroRegistro,
      numeroSolicitud: value.numeroSolicitud,
      paisProteccion: { id: value.paisProteccionRef } as IPais,
      tipoCaducidad: { id: value.tipoCaducidadId } as ITipoCaducidad,
      titulo: value.titulo,
      viaProteccion: { id: value.viaProteccionId } as IViaProteccion,
    } as ISolicitudProteccion : (value as unknown as ISolicitudProteccion);
  }

  fromTarget(value: ISolicitudProteccion): ISolicitudProteccionRequest {
    return value ? {
      fechaCaducidad: LuxonUtils.toBackend(value.fechaCaducidad),
      fechaConcesion: LuxonUtils.toBackend(value.fechaConcesion),
      fechaFinPriorPresFasNacRec: LuxonUtils.toBackend(value.fechaFinPriorPresFasNacRec),
      fechaPrioridadSolicitud: LuxonUtils.toBackend(value.fechaPrioridadSolicitud),
      fechaPublicacion: LuxonUtils.toBackend(value.fechaPublicacion),
      agentePropiedadRef: value.agentePropiedad?.id,
      comentarios: value.comentarios,
      estado: value.estado,
      invencionId: value.invencion.id,
      numeroConcesion: value.numeroConcesion,
      numeroPublicacion: value.numeroPublicacion,
      numeroRegistro: value.numeroRegistro,
      numeroSolicitud: value.numeroSolicitud,
      paisProteccionRef: value.paisProteccion?.id,
      tipoCaducidadId: value.tipoCaducidad?.id,
      titulo: value.titulo,
      viaProteccionId: value.viaProteccion.id,
    } : (value as unknown as ISolicitudProteccionRequest);
  }
}

export const SOLICITUD_PROTECCION_REQUEST_CONVERTER = new SolicitudProteccionRequestConverter();
