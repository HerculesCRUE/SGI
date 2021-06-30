import { IEntidadFinanciadora } from './entidad-financiadora';

export interface IConvocatoriaEntidadFinanciadora extends IEntidadFinanciadora {
  /** Id de Convocatoria */
  convocatoriaId: number;
}
