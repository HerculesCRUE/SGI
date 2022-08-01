import { Injectable } from '@angular/core';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IEvaluacionWithNumComentario } from '@core/models/eti/evaluacion-with-num-comentario';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IPersona } from '@core/models/sgp/persona';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { NGXLogger } from 'ngx-logger';
import { concat, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { EvaluacionEvaluacionesAnterioresListadoExportService } from './evaluacion-evaluaciones-anteriores-listado-export.service';
import { EvaluacionGeneralListadoExportService } from './evaluacion-general-listado-export.service';

export enum TipoComentario {
  GESTOR = 1,
  EVALUADOR = 2
}

export interface IEvaluacionReportData extends IEvaluacion {
  evaluacionesAnteriores: IEvaluacionWithNumComentario[];
  solicitante: IPersona;
}

export interface IEvaluacionReportOptions extends IReportOptions {
  showEvaluacionesAnteriores: boolean;
}

@Injectable()
export class EvaluacionListadoExportService extends
  AbstractTableExportService<IEvaluacionReportData, IEvaluacionReportOptions> {

  private _tipoComentario: TipoComentario;

  public set tipoComentario(value: TipoComentario) {
    this._tipoComentario = value;
    this.evaluacionEvaluacionesAnterioresListadoExportService.tipoComentario = this._tipoComentario;
  }

  constructor(
    protected readonly logger: NGXLogger,
    private readonly evaluacionService: EvaluacionService,
    private readonly evaluadorService: EvaluadorService,
    private readonly evaluacionGeneralListadoExportService: EvaluacionGeneralListadoExportService,
    private readonly evaluacionEvaluacionesAnterioresListadoExportService: EvaluacionEvaluacionesAnterioresListadoExportService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getRows(evaluaciones: IEvaluacionReportData[], reportConfig: IReportConfig<IEvaluacionReportOptions>):
    Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    evaluaciones.forEach((evaluacion, index) => {
      requestsRow.push(this.getRowsInner(evaluaciones, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    evaluaciones: IEvaluacionReportData[],
    index: number,
    reportConfig: IReportConfig<IEvaluacionReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {

        row.elements.push(...this.evaluacionGeneralListadoExportService.fillRows(evaluaciones, index, reportConfig));

        if (reportConfig.reportOptions?.showEvaluacionesAnteriores) {
          row.elements.push(...this.evaluacionEvaluacionesAnterioresListadoExportService.fillRows(evaluaciones, index, reportConfig));
        }

        return row;
      }), catchError(err => {
        this.logger.error(err);
        return of(err);
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<IEvaluacionReportOptions>): Observable<IEvaluacionReportData[]> {

    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    const gestorOrInvestigadorRequest$ = this._tipoComentario == TipoComentario.GESTOR ?
      this.evaluacionService.findAllByMemoriaAndRetrospectivaEnEvaluacion(findOptions) :
      this.evaluadorService.getEvaluaciones(findOptions);

    return gestorOrInvestigadorRequest$.pipe(
      map((evaluaciones) => {
        return evaluaciones.items.map((evaluacion) => evaluacion as unknown as IEvaluacionReportData);
      }),
      switchMap((evaluacionReportData) => {
        const requestsEvaluacion: Observable<IEvaluacionReportData>[] = [];

        evaluacionReportData.forEach(evaluacion => {
          requestsEvaluacion.push(this.getDataReportInner(evaluacion, reportConfig.reportOptions));
        });
        return zip(...requestsEvaluacion);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(evaluacionData: IEvaluacionReportData, reportOptions: IEvaluacionReportOptions):
    Observable<IEvaluacionReportData> {
    return concat(
      this.getDataReportListadoGeneral(evaluacionData),
      this.getDataReportEvaluacionesAnteriores(evaluacionData, reportOptions)
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    evaluacionData: IEvaluacionReportData
  ): Observable<IEvaluacionReportData> {
    return this.evaluacionGeneralListadoExportService.getData(evaluacionData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  private getDataReportEvaluacionesAnteriores(
    evaluacionData: IEvaluacionReportData,
    reportOptions: IEvaluacionReportOptions
  ): Observable<IEvaluacionReportData> {
    if (reportOptions?.showEvaluacionesAnteriores) {
      return this.evaluacionEvaluacionesAnterioresListadoExportService.getData(evaluacionData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(evaluacionData);
    }
  }

  protected getColumns(resultados: IEvaluacionReportData[], reportConfig: IReportConfig<IEvaluacionReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.evaluacionGeneralListadoExportService.fillColumns(resultados, reportConfig));

    if (reportConfig.reportOptions?.showEvaluacionesAnteriores) {
      columns.push(... this.evaluacionEvaluacionesAnterioresListadoExportService.fillColumns(resultados, reportConfig));
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
