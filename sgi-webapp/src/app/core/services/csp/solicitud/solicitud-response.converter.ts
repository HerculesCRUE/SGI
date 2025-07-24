import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ISolicitud } from '@core/models/csp/solicitud';
import { IModeloEjecucion, ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { IPersona } from '@core/models/sgp/persona';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { ISolicitudResponse } from '@core/services/csp/solicitud/solicitud-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ESTADO_SOLICITUD_RESPONSE_CONVERTER } from '../estado-solicitud/estado-solicitud-response.converter';

class SolicitudResponseConverter extends SgiBaseConverter<ISolicitudResponse, ISolicitud> {

  toTarget(value: ISolicitudResponse): ISolicitud {
    if (!value) {
      return value as unknown as ISolicitud;
    }
    return {
      id: value.id,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.titulo) : [],
      activo: value.activo,
      codigoExterno: value.codigoExterno,
      codigoRegistroInterno: value.codigoRegistroInterno,
      estado: ESTADO_SOLICITUD_RESPONSE_CONVERTER.toTarget(value.estado),
      convocatoriaId: value.convocatoriaId,
      convocatoriaExterna: value.convocatoriaExterna,
      creador: { id: value.creadorRef } as IPersona,
      solicitante: value.solicitanteRef ? { id: value.solicitanteRef } as IPersona : null,
      formularioSolicitud: value.formularioSolicitud,
      tipoSolicitudGrupo: value.tipoSolicitudGrupo,
      unidadGestion: { id: +value.unidadGestionRef } as IUnidadGestion,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      anio: value.anio,
      modeloEjecucion: value.modeloEjecucionId ? { id: value.modeloEjecucionId } as IModeloEjecucion : null,
      origenSolicitud: value.origenSolicitud,
      tipoFinalidad: value.tipoFinalidadId ? { id: value.tipoFinalidadId } as ITipoFinalidad : null
    };
  }

  fromTarget(value: ISolicitud): ISolicitudResponse {
    if (!value) {
      return value as unknown as ISolicitudResponse;
    }
    return {
      id: value.id,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.titulo) : [],
      activo: value.activo,
      codigoExterno: value.codigoExterno,
      codigoRegistroInterno: value.codigoRegistroInterno,
      estado: ESTADO_SOLICITUD_RESPONSE_CONVERTER.fromTarget(value.estado),
      convocatoriaId: value.convocatoriaId,
      convocatoriaExterna: value.convocatoriaExterna,
      creadorRef: value.creador?.id,
      solicitanteRef: value.solicitante?.id,
      formularioSolicitud: value.formularioSolicitud,
      tipoSolicitudGrupo: value.tipoSolicitudGrupo,
      unidadGestionRef: String(value.unidadGestion?.id),
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      anio: value.anio,
      modeloEjecucionId: value.modeloEjecucion?.id,
      origenSolicitud: value.origenSolicitud,
      tipoFinalidadId: value.tipoFinalidad?.id
    };
  }
}

export const SOLICITUD_RESPONSE_CONVERTER = new SolicitudResponseConverter();
