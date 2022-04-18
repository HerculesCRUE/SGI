import { ITipoHito } from '@core/models/csp/tipos-configuracion';

export interface ISolicitudHitoResponse {
  /** Id */
  id: number;
  /** Fecha inicio  */
  fecha: string;
  /** Tipo de hito */
  tipoHito: ITipoHito;
  /** Comentario */
  comentario: string;
  /** Id de Solicitud */
  solicitudId: number;
  aviso: {
    comunicadoRef: string;
    tareaProgramadaRef: string;
    incluirIpsSolicitud: boolean;
  };
}
