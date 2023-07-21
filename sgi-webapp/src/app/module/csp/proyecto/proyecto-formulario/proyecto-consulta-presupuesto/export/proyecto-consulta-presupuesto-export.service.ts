import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';

const ANUALIDAD_KEY = marker('csp.proyecto-consulta-presupuesto.label.anualidad');
const CONCEPTO_GASTO_KEY = marker('csp.proyecto-consulta-presupuesto.label.concepto-gasto');
const APLICACION_PRESUPUESTARIA_KEY = marker('csp.proyecto-consulta-presupuesto.label.aplicacion-presupuestaria');
const CODIGO_ECONOMICO_KEY = marker('csp.proyecto-consulta-presupuesto.label.codigo-economico');
const CODIGO_ECONOMICO_DESCRIPCION_KEY = marker('csp.proyecto-consulta-presupuesto.label.codigo-economico-descripcion');
const IMPORTE_PRESUPUESTO_KEY = marker('csp.proyecto-consulta-presupuesto.label.importe-presupuesto');
const IMPORTE_CONCEDIDO_KEY = marker('csp.proyecto-consulta-presupuesto.label.importe-concedido');

export interface IColumnDefinition {
  id: string;
  name: string;
  compute: boolean;
}

export interface IConsultaPresupuestoReportOptions extends IReportOptions {
  data: IAnualidadGasto[];
  columns: IColumnDefinition[];
}

export interface IConsultaPresupuestoExportData extends IBaseExportModalData {
  data: IAnualidadGasto[];
  columns: IColumnDefinition[];
}

@Injectable()
export class ProyectoConsultaPresupuestoExportService
  extends AbstractTableExportService<IAnualidadGasto, IConsultaPresupuestoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getRows(data: IAnualidadGasto[], reportConfig:
    IReportConfig<IConsultaPresupuestoReportOptions>): Observable<ISgiRowReport[]> {
    const rows: ISgiRowReport[] = [];

    data.forEach((item: IAnualidadGasto) => {
      const row: ISgiRowReport = {
        elements: []
      };
      row.elements.push(item.proyectoAnualidad?.anio ?? '');
      row.elements.push(item.conceptoGasto?.nombre ?? 'Sin clasificar');
      row.elements.push(item.proyectoPartida?.codigo ?? 'Sin clasificar');
      row.elements.push(item.codigoEconomico?.id ?? '');
      row.elements.push(item.codigoEconomico?.nombre ?? '');
      row.elements.push(item.importePresupuesto);
      row.elements.push(item.importeConcedido);

      rows.push(row);
    });

    return of(rows);
  }

  protected getDataReport(
    reportConfig: IReportConfig<IConsultaPresupuestoReportOptions>
  ): Observable<IAnualidadGasto[]> {
    return of(reportConfig.reportOptions.data);
  }

  protected getColumns(
    resultados: IAnualidadGasto[],
    reportConfig: IReportConfig<IConsultaPresupuestoReportOptions>
  ): Observable<ISgiColumnReport[]> {

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
        title: this.translate.instant(CONCEPTO_GASTO_KEY),
        name: 'conceptoGasto',
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
        title: this.translate.instant(CODIGO_ECONOMICO_DESCRIPCION_KEY),
        name: 'codigoEconomicoDescripcion',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(IMPORTE_PRESUPUESTO_KEY),
        name: 'importePresupuesto',
        type: ColumnType.NUMBER
      },
      {
        title: this.translate.instant(IMPORTE_CONCEDIDO_KEY),
        name: 'importeConcedido',
        type: ColumnType.NUMBER
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
