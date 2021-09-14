export interface IDatoEconomico {
  id: string;
  proyectoId: string;
  partidaPresupuestaria: string;
  codigoEconomico: any;
  anualidad: string;
  tipo: string;
  columnas: {
    [name: string]: string | number;
  };
}
