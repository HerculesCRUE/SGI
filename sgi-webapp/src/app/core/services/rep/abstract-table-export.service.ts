import { Directive } from '@angular/core';
import { FieldOrientationType } from '@core/models/rep/field-orientation.enum';
import { OutputReportType } from '@core/models/rep/output-report.enum';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiDynamicReport } from '@core/models/rep/sgi-dynamic-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ReportService } from '@core/services/rep/report.service';
import { triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';


const REPORT_NAME = 'report';

export interface IReportConfig<T> {
  /**
   * Título del informe
   */
  title: string;

  /**
   * Tipo de exportación: PDF, EXCEL, HTML, CSV, etc
   */
  outputType?: OutputReportType;

  /**
   * Opciones de configuración del informe
   */
  reportOptions?: T;
}

export interface IReportOptions {
  /**
   * Filtros de consulta para la obtención de datos del informe
   */
  findOptions?: SgiRestFindOptions;

  /**
   * Indica la disposición de los elementos que son relaciones: tabla o lista de columnas por cada registro de la relación
   */
  relationsOrientationTable?: boolean;

  /**
   * Ancho mínimo de columna, sino lo informamos cogerá el tamaño por
   * defecto o la proporción entre el ancho máximo de la página y el nº de
   * elementos (si es PDF)
   */
  columnMinWidth?: number;
}

export interface IExportService<T> {
  export(reportConfig: IReportConfig<T>): Observable<void>;
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class AbstractTableExportService<T, R extends IReportOptions> implements IExportService<R> {

  constructor(
    protected reportService: ReportService
  ) { }

  public export(reportConfig: IReportConfig<R>): Observable<void> {

    const report: ISgiDynamicReport = {
      outputReportType: reportConfig.outputType,
      fieldOrientationType: this.getFieldOrientationTypeByExportType(reportConfig.outputType),
      columnMinWidth: reportConfig.reportOptions?.columnMinWidth,
      title: reportConfig.title,
      filters: [],
      groupBy: this.getGroupBy(),
      columns: [],
      rows: []
    };

    return this.getDataReport(reportConfig).pipe(
      switchMap((resultados) => {
        return this.getColumns(resultados, reportConfig).pipe(
          map(columnsTranslate => {
            report.columns = columnsTranslate;
            return resultados;
          })
        );
      }),
      switchMap((resultados) => {
        return this.getRows(resultados, reportConfig).pipe(
          map(rowsTranslate => {
            report.rows = rowsTranslate;
            return report;
          })
        );
      }),
      switchMap((reportFill) => {
        return this.reportService.downloadDynamicReport(report);
      }),
      map((data: Blob) => {
        triggerDownloadToUser(data, REPORT_NAME);
        return (void 0);
      })
    );
  }

  protected abstract getGroupBy(): ISgiGroupReport;

  protected abstract getDataReport(reportConfig: IReportConfig<R>): Observable<T[]>;

  protected abstract getRows(resultados: T[], reportConfig: IReportConfig<R>): Observable<ISgiRowReport[]>;

  protected abstract getColumns(resultados: T[], reportConfig: IReportConfig<R>): Observable<ISgiColumnReport[]>;

  protected getFieldOrientationTypeByExportType(exportType: OutputReportType): FieldOrientationType {
    let fieldOrientationType: FieldOrientationType = FieldOrientationType.HORIZONTAL;
    if (exportType === OutputReportType.PDF) {
      fieldOrientationType = FieldOrientationType.VERTICAL;
    }
    return fieldOrientationType;
  }
}
