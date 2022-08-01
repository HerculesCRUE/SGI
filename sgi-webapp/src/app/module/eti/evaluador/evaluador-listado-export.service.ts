import { Injectable } from '@angular/core';
import { IConflictoInteres } from '@core/models/eti/conflicto-interes';
import { IEvaluador } from '@core/models/eti/evaluador';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { concat, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { EvaluadorConflictosInteresListadoExportService } from './evaluador-conflictos-interes-listado-export.service';
import { EvaluadorGeneralListadoExportService } from './evaluador-general-listado-export.service';

export interface IEvaluadorReportData extends IEvaluador {
  conflictos: IConflictoInteres[];
}

export interface IEvaluadorReportOptions extends IReportOptions {
  showConflictos: boolean;
}

@Injectable()
export class EvaluadorListadoExportService extends
  AbstractTableExportService<IEvaluadorReportData, IEvaluadorReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    private readonly evaluadorService: EvaluadorService,
    private readonly evaluadorGeneralListadoExportService: EvaluadorGeneralListadoExportService,
    private readonly evaluadorConflictosInteresListadoExportService: EvaluadorConflictosInteresListadoExportService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getRows(evaluadores: IEvaluadorReportData[], reportConfig: IReportConfig<IEvaluadorReportOptions>):
    Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    evaluadores.forEach((memoria, index) => {
      requestsRow.push(this.getRowsInner(evaluadores, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    memorias: IEvaluadorReportData[],
    index: number,
    reportConfig: IReportConfig<IEvaluadorReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {

        row.elements.push(...this.evaluadorGeneralListadoExportService.fillRows(memorias, index, reportConfig));

        if (reportConfig.reportOptions?.showConflictos) {
          row.elements.push(...this.evaluadorConflictosInteresListadoExportService.fillRows(memorias, index, reportConfig));
        }

        return row;
      }), catchError(err => {
        this.logger.error(err);
        return of(err);
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<IEvaluadorReportOptions>): Observable<IEvaluadorReportData[]> {
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    let evaluadores$: Observable<SgiRestListResult<IEvaluador>>;

    evaluadores$ = this.evaluadorService.findAll(findOptions);

    return evaluadores$.pipe(
      map((evaluadores) => {
        return evaluadores.items.map((evaluador) => evaluador as IEvaluadorReportData);
      }),
      switchMap((evaluadoresReportData) => {
        const requestsEvaluador: Observable<IEvaluadorReportData>[] = [];

        evaluadoresReportData.forEach(peticion => {
          requestsEvaluador.push(this.getDataReportInner(peticion, reportConfig.reportOptions));
        });
        return zip(...requestsEvaluador);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(evaluadorData: IEvaluadorReportData, reportOptions: IEvaluadorReportOptions):
    Observable<IEvaluadorReportData> {
    return concat(
      this.getDataReportListadoGeneral(evaluadorData),
      this.getDataReportConflictos(evaluadorData, reportOptions),
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    evaluadorData: IEvaluadorReportData
  ): Observable<IEvaluadorReportData> {
    return this.evaluadorGeneralListadoExportService.getData(evaluadorData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  private getDataReportConflictos(
    evaluadorData: IEvaluadorReportData,
    reportOptions: IEvaluadorReportOptions
  ): Observable<IEvaluadorReportData> {
    if (reportOptions?.showConflictos) {
      return this.evaluadorConflictosInteresListadoExportService.getData(evaluadorData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(evaluadorData);
    }
  }

  protected getColumns(resultados: IEvaluadorReportData[], reportConfig: IReportConfig<IEvaluadorReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.evaluadorGeneralListadoExportService.fillColumns(resultados, reportConfig));

    if (reportConfig.reportOptions?.showConflictos) {
      columns.push(... this.evaluadorConflictosInteresListadoExportService.fillColumns(resultados, reportConfig));
    }
    return of(columns);
  }

  protected getGroupBy(): ISgiGroupReport {
    const groupBy: ISgiGroupReport = {
      name: 'titulo',
      visible: true
    };
    return groupBy;
  }
}
