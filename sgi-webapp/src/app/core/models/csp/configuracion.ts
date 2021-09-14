export interface IConfiguracion {
  id: number;
  /** Expresión regular para validar el formato del código de las partidas presupuestarias */
  formatoPartidaPresupuestaria: string;
  /** Plantilla informativa del formato del código de las partidas presupuestarias */
  plantillaFormatoPartidaPresupuestaria: string;
}
