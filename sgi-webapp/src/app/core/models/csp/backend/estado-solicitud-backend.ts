import { Estado } from '../estado-solicitud';

export interface IEstadoSolicitudBackend {
  /** Id */
  id: number;
  /** ID de la solicitud */
  solicitudId: number;
  /** Estado */
  estado: Estado;
  /** Fecha estado */
  fechaEstado: string;
  /** Comentario */
  comentario: string;
}
