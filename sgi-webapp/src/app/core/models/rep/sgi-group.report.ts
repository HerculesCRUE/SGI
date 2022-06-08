
export interface ISgiGroupReport {
  /**
   * Nombre del campo que se va a mostrar en la cabecera del registro
   * , necesario cuando la disposición de registros es en vertical
   */
  name: string;
  /**
   * Indica si se visualiza o no el campo en la cabecera
   */
  visible: boolean;

  /**
   * Campos adicionales de agrupacion que se mostrarán en la cabecera 
   * (solo válido para disposición vertical)
   */
  additionalGroupNames?: string[];
}

