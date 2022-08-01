import { IConvocatoriaFaseResponse } from '@core/services/csp/convocatoria-fase/convocatoria-fase-response';

export interface IConfiguracionSolicitudBackend {
  /** Id */
  id: number;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Tramitacion SGI */
  tramitacionSGI: boolean;
  /** Convocatoria Fase */
  fasePresentacionSolicitudes: IConvocatoriaFaseResponse;
  /** Importe Máximo Solicitud */
  importeMaximoSolicitud: number;
}
