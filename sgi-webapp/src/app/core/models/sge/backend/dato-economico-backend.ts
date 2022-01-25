export interface IDatoEconomicoBackend {
  id: string;
  proyectoId: string;
  partidaPresupuestaria: string;
  codigoEconomico: any;
  anualidad: string;
  tipo: string;
  fechaDevengo: string;
  clasificacionSGE: {
    id: string,
    nombre: string
  };
  columnas: {
    [name: string]: string | number;
  };
}
