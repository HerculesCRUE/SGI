import { ITipoAmbitoGeografico, ITipoOrigenFuenteFinanciacion } from './tipos-configuracion';

export interface IFuenteFinanciacion {
  id: number;
  nombre: string;
  descripcion: string;
  fondoEstructural: boolean;
  tipoAmbitoGeografico: ITipoAmbitoGeografico;
  tipoOrigenFuenteFinanciacion: ITipoOrigenFuenteFinanciacion;
  activo: boolean;
}
