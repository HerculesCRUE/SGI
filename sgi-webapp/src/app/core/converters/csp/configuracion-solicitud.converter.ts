import { IConfiguracionSolicitudBackend } from '@core/models/csp/backend/configuracion-solicitud-backend';
import { IConfiguracionSolicitud } from '@core/models/csp/configuracion-solicitud';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONVOCATORIA_FASE_CONVERTER } from './convocatoria-fase.converter';

class ConfiguracionSolicitudConverter extends SgiBaseConverter<IConfiguracionSolicitudBackend, IConfiguracionSolicitud> {

  toTarget(value: IConfiguracionSolicitudBackend): IConfiguracionSolicitud {
    if (!value) {
      return value as unknown as IConfiguracionSolicitud;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      tramitacionSGI: value.tramitacionSGI,
      fasePresentacionSolicitudes: CONVOCATORIA_FASE_CONVERTER.toTarget(value.fasePresentacionSolicitudes),
      importeMaximoSolicitud: value.importeMaximoSolicitud
    };
  }

  fromTarget(value: IConfiguracionSolicitud): IConfiguracionSolicitudBackend {
    if (!value) {
      return value as unknown as IConfiguracionSolicitudBackend;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      tramitacionSGI: value.tramitacionSGI,
      fasePresentacionSolicitudes: CONVOCATORIA_FASE_CONVERTER.fromTarget(value.fasePresentacionSolicitudes),
      importeMaximoSolicitud: value.importeMaximoSolicitud,
    };
  }
}

export const CONFIGURACION_SOLICITUD_CONVERTER = new ConfiguracionSolicitudConverter();
