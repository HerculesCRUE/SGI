import { FieldOrientation } from './field-orientation.enum';
import { ISgiColumnHorizontalOptionsReport } from './sgi-column-horizontal-options-report';

export interface ISgiColumnReport {
  title: string;

  /**
   * Cabeceras adicionales en el título solo aplicables si el report es pdf con
   * orientación vertical
   */
  additionalTitle?: string[];

  name: string;

  type: ColumnType;

  /**
   * Indica si es visible cuando el subreport no contiene elementos
   */
  visibleIfSubReportEmpty?: boolean;

  /**
   * Indica si es visible el campo en el report (se suele utilizar para visualizar
   * dicho campo en la cabecera additionalGroupNames sin mostrarlo en el listado)
   */
  visible?: boolean;

  /**
   * Opciones de columnas para disposición horizontal
   */
  horizontalOptions?: ISgiColumnHorizontalOptionsReport;

  format?: string;

  fieldOrientation?: FieldOrientation;

  columns?: ISgiColumnReport[];
}

export enum ColumnType {
  STRING = 'STRING',
  DATE = 'DATE',
  NUMBER = 'NUMBER',
  BOOLEAN = 'BOOLEAN',
  FORMULA = 'FORMULA',
  SUBREPORT = 'SUBREPORT'
}



