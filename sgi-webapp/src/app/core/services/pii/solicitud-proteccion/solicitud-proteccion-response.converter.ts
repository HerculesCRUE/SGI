import { IInvencion } from '@core/models/pii/invencion';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { ITipoCaducidad } from '@core/models/pii/tipo-caducidad';
import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPais } from '@core/models/sgo/pais';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudProteccionResponse } from './solicitud-proteccion-response';

export class SolicitudProteccionResponseConverter extends SgiBaseConverter<ISolicitudProteccionResponse, ISolicitudProteccion> {

  toTarget(value: ISolicitudProteccionResponse): ISolicitudProteccion {
    return value ? {
      id: value.id,
      fechaCaducidad: LuxonUtils.fromBackend(value.fechaCaducidad),
      fechaConcesion: LuxonUtils.fromBackend(value.fechaConcesion),
      fechaFinPriorPresFasNacRec: LuxonUtils.fromBackend(value.fechaFinPriorPresFasNacRec),
      fechaPrioridadSolicitud: LuxonUtils.fromBackend(value.fechaPrioridadSolicitud),
      fechaPublicacion: LuxonUtils.fromBackend(value.fechaPublicacion),
      agentePropiedad: { id: value.agentePropiedadRef } as IEmpresa,
      comentarios: value.comentarios,
      estado: value.estado,
      invencion: value.invencion as IInvencion,
      numeroConcesion: value.numeroConcesion,
      numeroPublicacion: value.numeroPublicacion,
      numeroRegistro: value.numeroRegistro,
      numeroSolicitud: value.numeroSolicitud,
      paisProteccion: { id: value.paisProteccionRef } as IPais,
      tipoCaducidad: value.tipoCaducidad as ITipoCaducidad,
      titulo: value.titulo,
      viaProteccion: value.viaProteccion as IViaProteccion
    } : (value as unknown as ISolicitudProteccion);
  }

  fromTarget(value: ISolicitudProteccion): ISolicitudProteccionResponse {
    return value ? {
      id: value.id,
      fechaCaducidad: LuxonUtils.toBackend(value.fechaCaducidad),
      fechaConcesion: LuxonUtils.toBackend(value.fechaConcesion),
      fechaFinPriorPresFasNacRec: LuxonUtils.toBackend(value.fechaFinPriorPresFasNacRec),
      fechaPrioridadSolicitud: LuxonUtils.toBackend(value.fechaPrioridadSolicitud),
      fechaPublicacion: LuxonUtils.toBackend(value.fechaPublicacion),
      agentePropiedadRef: value.agentePropiedad.id,
      comentarios: value.comentarios,
      estado: value.estado,
      invencion: value.invencion,
      numeroConcesion: value.numeroConcesion,
      numeroPublicacion: value.numeroPublicacion,
      numeroRegistro: value.numeroRegistro,
      numeroSolicitud: value.numeroSolicitud,
      paisProteccionRef: value.paisProteccion.id,
      tipoCaducidad: value.tipoCaducidad,
      titulo: value.titulo,
      viaProteccion: value.viaProteccion
    } : (value as unknown as ISolicitudProteccionResponse);
  }
}

export const SOLICITUD_PROTECCION_RESPONSE_CONVERTER = new SolicitudProteccionResponseConverter();
