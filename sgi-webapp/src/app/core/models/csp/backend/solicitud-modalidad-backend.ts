import { IPrograma } from '../programa';

export interface ISolicitudModalidadBackend {
  /** Id */
  id: number;
  /** Id de Solicitud */
  solicitudId: number;
  /** EntidadRef */
  entidadRef: string;
  /** Programa */
  programa: IPrograma;
}
