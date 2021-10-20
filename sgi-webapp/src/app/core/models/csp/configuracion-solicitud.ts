import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { IConvocatoriaFase } from './convocatoria-fase';

export interface IConfiguracionSolicitud {
  /** Id */
  id: number;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Tramitacion SGI */
  tramitacionSGI: boolean;
  /** Convocatoria Fase */
  fasePresentacionSolicitudes: IConvocatoriaFase;
  /** Importe Máximo Solicitud */
  importeMaximoSolicitud: number;
}
