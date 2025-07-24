import { TipoDatoEconomico } from "../dato-economico";

export interface IDatoEconomicoBackend {
  id: string;
  proyectoId: string;
  partidaPresupuestaria: string;
  codigoEconomico: any;
  anualidad: string;
  tipo: TipoDatoEconomico;
  fechaDevengo: string;
  clasificacionSGE: {
    id: string,
    nombre: string
  };
  columnas: {
    [name: string]: string | number;
  };
}
