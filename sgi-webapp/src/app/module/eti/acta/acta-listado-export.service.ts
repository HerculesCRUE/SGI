import { Injectable } from '@angular/core';
import { IActaWithNumEvaluaciones } from '@core/models/eti/acta-with-num-evaluaciones';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ActaService } from '@core/services/eti/acta.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { MemoriaListado } from './acta-formulario/acta-memorias/acta-memorias.fragment';
import { ActaGeneralListadoExportService } from './acta-general-listado-export.service';
import { ActaMemoriaListadoExportService } from './acta-memoria-listado-export.service';

export interface IActaReportData extends IActaWithNumEvaluaciones {
  convocatoriaReunion: IConvocatoriaReunion;
  memorias: MemoriaListado[];
}

export interface IActaReportOptions extends IReportOptions {
  showMemorias: boolean;
}

@Injectable()
export class ActaListadoExportService extends AbstractTableExportService<IActaReportData, IActaReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly translate: TranslateService,
    private readonly actaService: ActaService,
    protected reportService: ReportService,
    private readonly actaGeneralListadoExportService: ActaGeneralListadoExportService,
    private readonly actaMemoriaListadoExportService: ActaMemoriaListadoExportService
  ) {
    super(reportService);
  }

  protected getRows(actas: IActaReportData[], reportConfig: IReportConfig<IActaReportOptions>)
    : Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];
    actas.forEach((acta, index) => {
      requestsRow.push(this.getRowsInner(actas, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    actas: IActaReportData[],
    index: number,
    reportConfig: IReportConfig<IActaReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {
        row.elements.push(...this.actaGeneralListadoExportService.fillRows(actas, index, reportConfig));
        if (reportConfig.reportOptions?.showMemorias) {
          row.elements.push(...this.actaMemoriaListadoExportService.fillRows(actas, index, reportConfig));
        }
        return row;
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<IActaReportOptions>): Observable<IActaReportData[]> {
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;
    findOptions.sort = new RSQLSgiRestSort('convocatoriaReunion.comite.comite', SgiRestSortDirection.ASC).and('numero', SgiRestSortDirection.ASC);

    return this.actaService.findActivasWithEvaluaciones(findOptions).pipe(
      map((actas) => {
        return actas.items.map((acta) => {
          return acta as IActaReportData;
        });
      }),
      switchMap((actasReportData) => {
        const requestsActa: Observable<IActaReportData>[] = [];

        actasReportData.forEach(acta => {
          requestsActa.push(this.getDataReportInner(acta, reportConfig.reportOptions));
        });
        return zip(...requestsActa);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(actaData: IActaReportData, reportOptions: IActaReportOptions)
    : Observable<IActaReportData> {
    return merge(
      this.getDataReportListadoGeneral(actaData),
      this.getDataReportMemorias(actaData, reportOptions)
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    actaData: IActaReportData
  ): Observable<IActaReportData> {
    return this.actaGeneralListadoExportService.getData(actaData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  private getDataReportMemorias(
    actaData: IActaReportData,
    reportOptions: IActaReportOptions
  ): Observable<IActaReportData> {
    if (reportOptions?.showMemorias) {
      return this.actaMemoriaListadoExportService.getData(actaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(actaData);
    }
  }

  protected getColumns(resultados: IActaReportData[], reportConfig: IReportConfig<IActaReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.actaGeneralListadoExportService.fillColumns(resultados, reportConfig));
    if (reportConfig.reportOptions?.showMemorias) {
      columns.push(... this.actaMemoriaListadoExportService.fillColumns(resultados, reportConfig));
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
