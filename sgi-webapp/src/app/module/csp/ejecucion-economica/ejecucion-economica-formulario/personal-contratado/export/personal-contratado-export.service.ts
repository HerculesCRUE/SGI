import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { AbstractTableExportService, IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { IEjecucionPresupuestariaReportOptions } from '../../../common/ejecucion-presupuestaria-report-options';
import { IColumnDefinition, IRowConfig } from '../../desglose-economico.fragment';
import { IDesglose } from '../../facturas-justificantes.fragment';

const ANUALIDAD_KEY = marker('sge.dato-economico.anualidad');
const PROYECTO_KEY = marker('sge.dato-economico.proyecto');
const CLASIFICACION_SGE_KEY = marker('sge.dato-economico.clasificacion-sge');
const CONCEPTO_GASTO_KEY = marker('sge.dato-economico.concepto-gasto');
const APLICACION_PRESUPUESTARIA_KEY = marker('sge.dato-economico.partida-presupuestaria');
const CODIGO_ECONOMICO_KEY = marker('sge.dato-economico.codigo-economico');
const FECHA_DEVENGO_KEY = marker('sge.dato-economico.fecha-devengo');
const CLASIFICADO_AUTOMATICAMENTE_KEY = marker('sge.dato-economico.clasificado-automaticamente');
const MSG_TRUE = marker('label.si');
const MSG_FALSE = marker('label.no');

@Injectable()
export class PersonalContratadoExportService
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

      if (reportConfig.reportOptions.rowConfig.anualidadShow) {
        row.elements.push(item.anualidad);
      }

      if (reportConfig.reportOptions.rowConfig.proyectoShow) {
        row.elements.push(item.proyecto?.titulo ?? 'Sin clasificar');
      }

      row.elements.push(item.conceptoGasto?.nombre ?? 'Sin clasificar');

      if (reportConfig.reportOptions.rowConfig.clasificadoAutomaticamenteShow) {
        row.elements.push(this.getI18nBooleanYesNo(item.clasificadoAutomaticamente));
      }

      if (reportConfig.reportOptions.rowConfig.clasificacionSgeShow) {
        row.elements.push(item.clasificacionSGE?.nombre ?? 'Sin clasificar');
      }

      if (reportConfig.reportOptions.rowConfig.aplicacionPresupuestariaShow) {
        row.elements.push(item.partidaPresupuestaria);
      }

      const codigoEconomico = (item.codigoEconomico?.id ?? '') + (item.codigoEconomico?.nombre ? ' - ' + item.codigoEconomico.nombre : '');
      row.elements.push(codigoEconomico);

      row.elements.push(LuxonUtils.toBackend(item.fechaDevengo));

      reportConfig.reportOptions.columns.forEach((column, _) => {
        const value = item.columnas[column.id] ?? (column?.compute ? 0 : '');
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

    return of(
      this.toReportColumns(
        reportConfig.reportOptions.columns,
        reportConfig.reportOptions.rowConfig
      )
    );
  }

  private toReportColumns(columns: IColumnDefinition[], rowConfig: IRowConfig): ISgiColumnReport[] {

    const columnsReport: ISgiColumnReport[] = [];

    if (rowConfig.anualidadShow) {
      columnsReport.push({
        title: this.translate.instant(ANUALIDAD_KEY),
        name: 'anualidad',
        type: ColumnType.STRING,
      });
    }

    if (rowConfig.proyectoShow) {
      columnsReport.push({
        title: this.translate.instant(PROYECTO_KEY),
        name: 'proyecto',
        type: ColumnType.STRING
      });
    }

    columnsReport.push({
      title: this.translate.instant(CONCEPTO_GASTO_KEY),
      name: 'conceptoGasto',
      type: ColumnType.STRING
    });

    if (rowConfig.clasificadoAutomaticamenteShow) {
      columnsReport.push({
        title: this.translate.instant(CLASIFICADO_AUTOMATICAMENTE_KEY),
        name: 'clasificadoAutomaticamente',
        type: ColumnType.STRING
      });
    }

    if (rowConfig.clasificacionSgeShow) {
      columnsReport.push({
        title: this.translate.instant(CLASIFICACION_SGE_KEY),
        name: 'clasificacionSGE',
        type: ColumnType.STRING
      });
    }

    if (rowConfig.aplicacionPresupuestariaShow) {
      columnsReport.push({
        title: this.translate.instant(APLICACION_PRESUPUESTARIA_KEY),
        name: 'aplicacionPresupuestaria',
        type: ColumnType.STRING
      });
    }

    columnsReport.push({
      title: this.translate.instant(CODIGO_ECONOMICO_KEY),
      name: 'codigoEconomico',
      type: ColumnType.STRING
    });

    columnsReport.push({
      title: this.translate.instant(FECHA_DEVENGO_KEY),
      name: 'fechaDevengo',
      type: ColumnType.DATE
    });

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

  protected getI18nBooleanYesNo(field: boolean): string {
    return field ? this.translate.instant(MSG_TRUE) : this.translate.instant(MSG_FALSE);
  }

}
