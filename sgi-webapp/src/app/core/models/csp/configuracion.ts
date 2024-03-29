export interface IConfiguracion {
  id: number;
  /** Expresión regular para validar el formato del código de las partidas presupuestarias */
  formatoPartidaPresupuestaria: string;
  /** Plantilla informativa del formato del código de las partidas presupuestarias */
  plantillaFormatoPartidaPresupuestaria: string;
  /** Determina cuándo la validación de gastos está activa en la app */
  validacionGastos: boolean;
  /** Expresión regular para validar el formato del código de los identificadores de justificación */
  formatoIdentificadorJustificacion: string;
  /** Plantilla informativa del formato del código de los identificadores de justificación */
  plantillaFormatoIdentificadorJustificacion: string;
  /** Dedicacion minima de un miembro de un grupo de investigacion */
  dedicacionMinimaGrupo: number;
  /** Expresión regular para validar el formato del código interno de proyecto */
  formatoCodigoInternoProyecto: string;
  /** Plantilla informativa del formato del código interno de proyecto */
  plantillaFormatoCodigoInternoProyecto: string;
}
