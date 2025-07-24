import { IProgramaResponse } from '../programa/programa-response';

export interface ISolicitudModalidadResponse {
  /** Id */
  id: number;
  /** Id de Solicitud */
  solicitudId: number;
  /** EntidadRef */
  entidadRef: string;
  /** Programa */
  programa: IProgramaResponse;
  /** Programa de la convocatoria */
  programaConvocatoriaId: number;
}
