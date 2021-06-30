import { ITipoAmbitoGeografico } from './tipo-ambito-geografico';
import { ITipoOrigenFuenteFinanciacion } from './tipo-origen-fuente-financiacion';

export interface IFuenteFinanciacion {
  id: number;
  nombre: string;
  descripcion: string;
  fondoEstructural: boolean;
  tipoAmbitoGeografico: ITipoAmbitoGeografico;
  tipoOrigenFuenteFinanciacion: ITipoOrigenFuenteFinanciacion;
  activo: boolean;
}
