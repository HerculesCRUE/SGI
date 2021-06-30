export interface IFuenteFinanciacionResponse {
  id: number;
  nombre: string;
  descripcion: string;
  fondoEstructural: boolean;
  tipoAmbitoGeografico: {
    id: number;
    nombre: string;
  };
  tipoOrigenFuenteFinanciacion: {
    id: number;
    nombre: string;
  };
  activo: boolean;
}
