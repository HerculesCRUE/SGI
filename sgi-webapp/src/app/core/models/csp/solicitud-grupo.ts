import { IGrupo } from './grupo';
import { ISolicitud } from './solicitud';

export interface ISolicitudGrupo {
  /** Id */
  id: number;
  /** Grupo */
  grupo: IGrupo;
  /** Solicitud Id */
  solicitud: ISolicitud;
}
