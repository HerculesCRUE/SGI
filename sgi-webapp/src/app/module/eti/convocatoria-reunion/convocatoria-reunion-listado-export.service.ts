import { Injectable } from '@angular/core';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IPersona } from '@core/models/sgp/persona';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { concat, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { ConvocatoriaReunionGeneralListadoExportService } from './convocatoria-reunion-general-listado-export.service';
import { ConvocatoriaReunionMemoriasListadoExportService } from './convocatoria-reunion-memorias-listado-export.service';

export interface IConvocatoriaReunionMemoriaReportData {
  referencia: string;
  solicitante: IPersona;
  evaluador1: IPersona;
  evaluador2: IPersona;
}

export interface IConvocatoriaReunionReportData extends IConvocatoriaReunion {
  memorias: IConvocatoriaReunionMemoriaReportData[];
}

export interface IConvocatoriaReunionReportOptions extends IReportOptions {
  showMemorias: boolean;
}

@Injectable()
export class ConvocatoriaReunionListadoExportService extends
  AbstractTableExportService<IConvocatoriaReunionReportData, IConvocatoriaReunionReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    private authService: SgiAuthService,
    private readonly convocatoriaReunionService: ConvocatoriaReunionService,
    private readonly convocatoriaReunionGeneralListadoExportService: ConvocatoriaReunionGeneralListadoExportService,
    private readonly convocatoriaReunionMemoriasListadoExportService: ConvocatoriaReunionMemoriasListadoExportService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getRows(peticiones: IConvocatoriaReunionReportData[], reportConfig: IReportConfig<IConvocatoriaReunionReportOptions>):
    Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    peticiones.forEach((peticion, index) => {
      requestsRow.push(this.getRowsInner(peticiones, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    peticiones: IConvocatoriaReunionReportData[],
    index: number,
    reportConfig: IReportConfig<IConvocatoriaReunionReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {

        row.elements.push(...this.convocatoriaReunionGeneralListadoExportService.fillRows(peticiones, index, reportConfig));

        if (reportConfig.reportOptions?.showMemorias) {
          row.elements.push(...this.convocatoriaReunionMemoriasListadoExportService.fillRows(peticiones, index, reportConfig));
        }

        return row;
      }), catchError(err => {
        this.logger.error(err);
        return of(err);
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<IConvocatoriaReunionReportOptions>): Observable<IConvocatoriaReunionReportData[]> {

    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    return this.convocatoriaReunionService.findAll(findOptions).pipe(
      map((convocatorias) => {
        return convocatorias.items.map((peticion) => peticion as IConvocatoriaReunionReportData);
      }),
      switchMap((convocatoriasReportData) => {
        const requestsPeticion: Observable<IConvocatoriaReunionReportData>[] = [];

        convocatoriasReportData.forEach(convocatoria => {
          requestsPeticion.push(this.getDataReportInner(convocatoria, reportConfig.reportOptions));
        });
        return zip(...requestsPeticion);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(peticionData: IConvocatoriaReunionReportData, reportOptions: IConvocatoriaReunionReportOptions):
    Observable<IConvocatoriaReunionReportData> {
    return concat(
      this.getDataReportListadoGeneral(peticionData),
      this.getDataReportMemorias(peticionData, reportOptions)
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    peticionData: IConvocatoriaReunionReportData
  ): Observable<IConvocatoriaReunionReportData> {
    return this.convocatoriaReunionGeneralListadoExportService.getData(peticionData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  private getDataReportMemorias(
    convocatoriaData: IConvocatoriaReunionReportData,
    reportOptions: IConvocatoriaReunionReportOptions
  ): Observable<IConvocatoriaReunionReportData> {
    if (reportOptions?.showMemorias) {
      return this.convocatoriaReunionMemoriasListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  protected getColumns(resultados: IConvocatoriaReunionReportData[], reportConfig: IReportConfig<IConvocatoriaReunionReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.convocatoriaReunionGeneralListadoExportService.fillColumns(resultados, reportConfig));

    if (reportConfig.reportOptions?.showMemorias) {
      columns.push(... this.convocatoriaReunionMemoriasListadoExportService.fillColumns(resultados, reportConfig));
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
