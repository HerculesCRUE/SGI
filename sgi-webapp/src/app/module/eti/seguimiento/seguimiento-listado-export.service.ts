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
import { SeguimientoEvaluacionesAnterioresListadoExportService } from './seguimiento-evaluaciones-anteriores-listado-export.service';
import { SeguimientoGeneralListadoExportService } from './seguimiento-general-listado-export.service';


export enum RolPersona {
  GESTOR = 1,
  EVALUADOR = 2
}

export interface ISeguimientoReportData extends IEvaluacion {
  evaluacionesAnteriores: IEvaluacionWithNumComentario[];
  solicitante: IPersona;
}

export interface ISeguimientoReportOptions extends IReportOptions {
  showEvaluacionesAnteriores: boolean;
}

@Injectable()
export class SeguimientoListadoExportService extends
  AbstractTableExportService<ISeguimientoReportData, ISeguimientoReportOptions> {

  private _rolPersona: RolPersona;

  public set rolPersona(value: RolPersona) {
    this._rolPersona = value;
    this.seguimientoEvaluacionesAnterioresListadoExportService.tipoComentario = this._rolPersona;
  }

  constructor(
    protected readonly logger: NGXLogger,
    private readonly evaluadorService: EvaluadorService,
    private readonly evaluacionService: EvaluacionService,
    private readonly seguimientoGeneralListadoExportService: SeguimientoGeneralListadoExportService,
    private readonly seguimientoEvaluacionesAnterioresListadoExportService: SeguimientoEvaluacionesAnterioresListadoExportService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getRows(seguimientos: ISeguimientoReportData[], reportConfig: IReportConfig<ISeguimientoReportOptions>):
    Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    seguimientos.forEach((seguimiento, index) => {
      requestsRow.push(this.getRowsInner(seguimientos, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    evaluaciones: ISeguimientoReportData[],
    index: number,
    reportConfig: IReportConfig<ISeguimientoReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {

        row.elements.push(...this.seguimientoGeneralListadoExportService.fillRows(evaluaciones, index, reportConfig));

        if (reportConfig.reportOptions?.showEvaluacionesAnteriores) {
          row.elements.push(...this.seguimientoEvaluacionesAnterioresListadoExportService.fillRows(evaluaciones, index, reportConfig));
        }

        return row;
      }), catchError(err => {
        this.logger.error(err);
        return of(err);
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<ISeguimientoReportOptions>): Observable<ISeguimientoReportData[]> {

    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    const gestorOrInvestigadorRequest$ = this._rolPersona === RolPersona.GESTOR ?
      this.evaluacionService.findSeguimientoMemoria(findOptions) :
      this.evaluadorService.getSeguimientos(findOptions);

    return gestorOrInvestigadorRequest$.pipe(
      map((evaluaciones) => {
        return evaluaciones.items.map((evaluacion) => evaluacion as unknown as ISeguimientoReportData);
      }),
      switchMap((evaluacionReportData) => {
        const requestsEvaluacion: Observable<ISeguimientoReportData>[] = [];

        evaluacionReportData.forEach(evaluacion => {
          requestsEvaluacion.push(this.getDataReportInner(evaluacion, reportConfig.reportOptions));
        });
        return zip(...requestsEvaluacion);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(evaluacionData: ISeguimientoReportData, reportOptions: ISeguimientoReportOptions):
    Observable<ISeguimientoReportData> {
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
    evaluacionData: ISeguimientoReportData
  ): Observable<ISeguimientoReportData> {
    return this.seguimientoGeneralListadoExportService.getData(evaluacionData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  private getDataReportEvaluacionesAnteriores(
    evaluacionData: ISeguimientoReportData,
    reportOptions: ISeguimientoReportOptions
  ): Observable<ISeguimientoReportData> {
    if (reportOptions?.showEvaluacionesAnteriores) {
      return this.seguimientoEvaluacionesAnterioresListadoExportService.getData(evaluacionData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(evaluacionData);
    }
  }

  protected getColumns(resultados: ISeguimientoReportData[], reportConfig: IReportConfig<ISeguimientoReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.seguimientoGeneralListadoExportService.fillColumns(resultados, reportConfig));

    if (reportConfig.reportOptions?.showEvaluacionesAnteriores) {
      columns.push(... this.seguimientoEvaluacionesAnterioresListadoExportService.fillColumns(resultados, reportConfig));
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
