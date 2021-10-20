import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { IConvocatoriaFaseBackend } from './convocatoria-fase-backend';

export interface IConfiguracionSolicitudBackend {
  /** Id */
  id: number;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Tramitacion SGI */
  tramitacionSGI: boolean;
  /** Convocatoria Fase */
  fasePresentacionSolicitudes: IConvocatoriaFaseBackend;
  /** Importe Máximo Solicitud */
  importeMaximoSolicitud: number;
}
