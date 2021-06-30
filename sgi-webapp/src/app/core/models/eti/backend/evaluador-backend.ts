import { CargoComite } from '../cargo-comite';
import { IComite } from '../comite';

export interface IEvaluadorBackend {
  /** Id */
  id: number;
  /** Comité */
  comite: IComite;
  /** Cargo comité */
  cargoComite: CargoComite;
  /** Resumen */
  resumen: string;
  /** Fecha Alta. */
  fechaAlta: string;
  /** Fecha Baja. */
  fechaBaja: string;
  /** Referencia persona */
  personaRef: string;
  /** Activo */
  activo: boolean;
}
