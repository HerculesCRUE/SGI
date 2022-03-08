import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { IEjecucionPresupuestariaReportOptions } from '../../../common/ejecucion-presupuestaria-report-options';
import { IColumnDefinition } from '../../desglose-economico.fragment';
import { IDesglose } from '../../facturas-justificantes.fragment';

const ANUALIDAD_KEY = marker('sge.dato-economico.anualidad');
const PROYECTO_KEY = marker('sge.dato-economico.proyecto');
const CLASIFICACION_SGE_KEY = marker('sge.dato-economico.clasificacion-sge');
const CONCEPTO_GASTO_KEY = marker('sge.dato-economico.concepto-gasto');
const APLICACION_PRESUPUESTARIA_KEY = marker('sge.dato-economico.partida-presupuestaria');
const CODIGO_ECONOMICO_KEY = marker('sge.dato-economico.codigo-economico');
const FECHA_DEVENGO_KEY = marker('sge.dato-economico.fecha-devengo');

@Injectable()
export class PersonalContratadoExportService
  extends AbstractTableExportService<IDatoEconomico, IEjecucionPresupuestariaReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
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
      row.elements.push(item.proyecto?.titulo ?? 'Sin clasificar');
      row.elements.push(item.conceptoGasto?.nombre ?? 'Sin clasificar');
      row.elements.push(item.clasificacionSGE?.nombre ?? 'Sin clasificar');
      row.elements.push(item.partidaPresupuestaria);

      const codigoEconomico = (item.codigoEconomico?.id ?? '') + (item.codigoEconomico?.nombre ? ' - ' + item.codigoEconomico.nombre : '');
      row.elements.push(codigoEconomico);

      row.elements.push(LuxonUtils.toBackend(item.fechaDevengo));

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
        title: this.translate.instant(PROYECTO_KEY),
        name: 'proyecto',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(CONCEPTO_GASTO_KEY),
        name: 'conceptoGasto',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(CLASIFICACION_SGE_KEY),
        name: 'clasificacionSGE',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(APLICACION_PRESUPUESTARIA_KEY),
        name: 'aplicacionPresupuestaria',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(CODIGO_ECONOMICO_KEY),
        name: 'codigoEconomico',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(FECHA_DEVENGO_KEY),
        name: 'fechaDevengo',
        type: ColumnType.DATE
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
