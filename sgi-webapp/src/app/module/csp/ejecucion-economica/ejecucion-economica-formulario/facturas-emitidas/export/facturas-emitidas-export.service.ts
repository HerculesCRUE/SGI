import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { AbstractTableExportService, IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { IEjecucionPresupuestariaReportOptions } from '../../../common/ejecucion-presupuestaria-report-options';
import { IColumnDefinition } from '../../desglose-economico.fragment';
import { IDesglose } from '../../facturas-justificantes.fragment';

const ANUALIDAD_KEY = marker('sge.dato-economico.anualidad');
const FACTURA_KEY = marker('sge.dato-economico.factura');


@Injectable({
  providedIn: 'root'
})
export class FacturasEmitidasExportService
  extends AbstractTableExportService<IDatoEconomico, IEjecucionPresupuestariaReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getRows(data: IDatoEconomico[], reportConfig:
    IReportConfig<IEjecucionPresupuestariaReportOptions>): Observable<ISgiRowReport[]> {
    const rows: ISgiRowReport[] = [];

    data.forEach((item: IDesglose) => {
      const row: ISgiRowReport = {
        elements: []
      };
      row.elements.push(item.anualidad);
      row.elements.push(item.id);

      reportConfig.reportOptions.columns.forEach((column, index) => {
        const value = item.columnas[column.id] ?? 0;
        row.elements.push(value);
      });
      rows.push(row);
    });

    return of(rows);
  }

  protected getDataReport(
    reportConfig: IReportConfig<IEjecucionPresupuestariaReportOptions>
  ): Observable<IDatoEconomico[]> {
    return of(reportConfig.reportOptions.data);
  }

  protected getColumns(resultados: IDatoEconomico[], reportConfig: IReportConfig<IEjecucionPresupuestariaReportOptions>):
    Observable<ISgiColumnReport[]> {

    return of(this.toReportColumns(reportConfig.reportOptions.columns));
  }

  private toReportColumns(columns: IColumnDefinition[]): ISgiColumnReport[] {

    const columnsReport: ISgiColumnReport[] = [
      {
        title: this.translate.instant(ANUALIDAD_KEY),
        name: 'anualidad',
        type: ColumnType.STRING,
      },
      {
        title: this.translate.instant(FACTURA_KEY),
        name: 'factura',
        type: ColumnType.STRING
      }
    ];

    columns.forEach(column => {
      columnsReport.push({
        name: column.id,
        title: column.name,
        type: column.compute ? ColumnType.NUMBER : ColumnType.STRING
      });
    });

    return columnsReport;
  }

  protected getGroupBy(): ISgiGroupReport {
    const groupBy: ISgiGroupReport = {
      name: 'anualidad',
      visible: true
    };
    return groupBy;
  }
}
