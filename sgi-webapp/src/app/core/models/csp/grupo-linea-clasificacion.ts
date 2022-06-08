import { IClasificacion } from '../sgo/clasificacion';

export interface IGrupoLineaClasificacion {
  id: number;
  grupoLineaInvestigacionId: number;
  clasificacion: IClasificacion;
}
