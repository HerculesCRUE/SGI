import { IPersona } from '../sgp/persona';

export interface ISolicitudProyectoResponsableEconomico {
  id: number;
  solicitudProyectoId: number;
  persona: IPersona;
  mesInicio: number;
  mesFin: number;
}
