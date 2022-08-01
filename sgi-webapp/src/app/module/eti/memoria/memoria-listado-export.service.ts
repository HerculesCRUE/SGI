import { Injectable } from '@angular/core';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IPersona } from '@core/models/sgp/persona';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { concat, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { MemoriaEvaluacionesListadoExportService } from './memoria-evaluaciones-listado-export.service';
import { MemoriaGeneralListadoExportService } from './memoria-general-listado-export.service';

export interface IMemoriaReportData extends IMemoriaPeticionEvaluacion {
  evaluaciones: IEvaluacion[];
  responsable: IPersona;
}

export interface IMemoriaReportOptions extends IReportOptions {
  showEvaluaciones: boolean;
}

@Injectable()
export class MemoriaListadoExportService extends
  AbstractTableExportService<IMemoriaReportData, IMemoriaReportOptions> {

  private _isInvestigador: boolean;
  public set isInvestigador(value: boolean) {
    this._isInvestigador = value;
  }

  constructor(
    protected readonly logger: NGXLogger,
    private readonly memoriaService: MemoriaService,
    private readonly memoriaGeneralListadoExportService: MemoriaGeneralListadoExportService,
    private readonly memoriaEvaluacionesListadoExportService: MemoriaEvaluacionesListadoExportService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getRows(memorias: IMemoriaReportData[], reportConfig: IReportConfig<IMemoriaReportOptions>):
    Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    memorias.forEach((memoria, index) => {
      requestsRow.push(this.getRowsInner(memorias, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    memorias: IMemoriaReportData[],
    index: number,
    reportConfig: IReportConfig<IMemoriaReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {

        row.elements.push(...this.memoriaGeneralListadoExportService.fillRows(memorias, index, reportConfig));

        if (reportConfig.reportOptions?.showEvaluaciones) {
          row.elements.push(...this.memoriaEvaluacionesListadoExportService.fillRows(memorias, index, reportConfig));
        }

        return row;
      }), catchError(err => {
        this.logger.error(err);
        return of(err);
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<IMemoriaReportOptions>): Observable<IMemoriaReportData[]> {
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    let memorias$: Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>>;

    if (this._isInvestigador) {
      memorias$ = this.memoriaService.findAllMemoriasEvaluacionByPersonaRef(findOptions);
    } else {
      memorias$ = this.memoriaService.findAll(findOptions) as unknown as Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>>;
    }
    return memorias$.pipe(
      map((memorias) => {
        return memorias.items.map((memoria) => memoria as unknown as IMemoriaReportData);
      }),
      switchMap((memoriasReportData) => {
        const requestsMemoria: Observable<IMemoriaReportData>[] = [];

        memoriasReportData.forEach(peticion => {
          requestsMemoria.push(this.getDataReportInner(peticion, reportConfig.reportOptions));
        });
        return zip(...requestsMemoria);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(peticionData: IMemoriaReportData, reportOptions: IMemoriaReportOptions):
    Observable<IMemoriaReportData> {
    return concat(
      this.getDataReportListadoGeneral(peticionData),
      this.getDataReportEvaluaciones(peticionData, reportOptions),
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    memoriaData: IMemoriaReportData
  ): Observable<IMemoriaReportData> {
    return this.memoriaGeneralListadoExportService.getData(memoriaData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  private getDataReportEvaluaciones(
    memoriaData: IMemoriaReportData,
    reportOptions: IMemoriaReportOptions
  ): Observable<IMemoriaReportData> {
    if (reportOptions?.showEvaluaciones) {
      return this.memoriaEvaluacionesListadoExportService.getData(memoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(memoriaData);
    }
  }

  protected getColumns(resultados: IMemoriaReportData[], reportConfig: IReportConfig<IMemoriaReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.memoriaGeneralListadoExportService.fillColumns(resultados, reportConfig));

    if (reportConfig.reportOptions?.showEvaluaciones) {
      columns.push(... this.memoriaEvaluacionesListadoExportService.fillColumns(resultados, reportConfig));
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
