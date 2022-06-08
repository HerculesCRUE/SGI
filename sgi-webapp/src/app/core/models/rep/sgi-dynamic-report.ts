import { FieldOrientation } from './field-orientation.enum';
import { OutputReport as OutputReport } from './output-report.enum';
import { ISgiColumnReport } from './sgi-column-report';
import { ISgiFilterReport } from './sgi-filter-report';
import { ISgiGroupReport } from './sgi-group.report';
import { ISgiRowReport } from './sgi-row.report';

export interface ISgiDynamicReport {
  /**
   * Tamaño máximo de ancho (solo aplica a pdf), Máximos PORTRAIT=470,
   * LANDSCAPE=630
   */
  customWidth?: number;

  /**
   * Ancho mínimo de columna, sino lo informamos cogerá el tamaño por
   * defecto o la proporción entre el ancho máximo de la página y el nº de
   * elementos (si es PDF)
   */
  columnMinWidth?: number;

  /**
   *  Disposición de los campos en el informe: horizontal o vertical
   */
  fieldOrientation?: FieldOrientation;

  /**
   * Indica si se mostrarán las cabeceras de los subreport si no contiene elementos
   * Tiene relación con el campo ISgiColumnReport.visibleIfSubReportEmpty
   * Solo aplicable en pdf con disposición vertical;
   */
  hideBlocksIfNoData?: boolean;

  /**
   * Tipo de exportación: PDF, EXCEL, HTML, etc
   */
  outputType: OutputReport;

  /**
   * Título del informe
   */
  title: string;

  /**
   * Filtros de consulta que han generado los datos del informe
   */
  filters?: ISgiFilterReport[];

  /**
   * Si la orientación de las filas es vertical, permite identificar el título de cabecera de cada registro
   */
  groupBy?: ISgiGroupReport;

  /**
   * Columnas del informe
   */
  columns: ISgiColumnReport[];

  /**
   * Filas del informe
   */
  rows: ISgiRowReport[];
}

