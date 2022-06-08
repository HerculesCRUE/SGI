export interface ISgiColumnHorizontalOptionsReport {
  /**
   * Permite definir el ancho de la columna cuando se trata de disposición
   * horizontal de columnas en función de un porcentaje sobre el ancho máximo
   * posible.
   * 
   * reportDto.customWidth = 630
   * customHorizontalWidth = 20
   * 
   * Ancho de columna = 630*0.20 = 126
   * 
   * Si no lo informamos cogerá el valor por defecto customWidth/nº de columnas
   * por lo que podría darse el caso de que no cojan todas las columnas, en ese
   * caso, lo ideal, sería darle un "porcentaje" a todas las columnas, es decir,
   * informar este campo para cada columna sin que sobrepase la suma de todos
   * ellos en un 100%
   * 
   */
  customWidth?: number;

  /**
   * Alineación de la celda
   */
  elementAlignmentType?: ElementAlignmentType;
}

export enum ElementAlignmentType {
  RIGHT = 'RIGHT', LEFT = 'LEFT',
  JUSTIFY = 'JUSTIFY', BOTTOM = 'BOTTOM',
  MIDDLE = 'MIDDLE', TOP = 'TOP', CENTER = 'CENTER'

}



