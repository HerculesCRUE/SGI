import { Injectable } from '@angular/core';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IPais } from '@core/models/sgo/pais';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { concat, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { SolicitudProteccionGeneralListadoExportService } from './solicitud-proteccion-general-listado-export.service';

export interface ISolicitudProteccionReportOptions extends IReportOptions {
  invencionId: number;
}

export interface ISolicitudProteccionReportData extends ISolicitudProteccion {
  pais: IPais;
}

@Injectable()
export class SolicitudProteccionListadoExportService extends AbstractTableExportService<ISolicitudProteccionReportData,
IReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    private readonly invencionService: InvencionService,
    private readonly solicitudProteccionGeneralListadoExportService: SolicitudProteccionGeneralListadoExportService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getRows(solicitudes: ISolicitudProteccionReportData[], reportConfig: IReportConfig<ISolicitudProteccionReportOptions>):
    Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    solicitudes.forEach((solicitud, index) => {
      requestsRow.push(this.getRowsInner(solicitudes, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    solicitudes: ISolicitudProteccionReportData[],
    index: number,
    reportConfig: IReportConfig<ISolicitudProteccionReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {

        row.elements.push(...this.solicitudProteccionGeneralListadoExportService.fillRows(solicitudes, index, reportConfig));

        return row;
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<ISolicitudProteccionReportOptions>): Observable<ISolicitudProteccionReportData[]> {
    let observable$: Observable<SgiRestListResult<ISolicitudProteccion>> = null;
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    observable$ = this.invencionService.findAllSolicitudesProteccion(reportConfig.reportOptions.invencionId, findOptions);


    return observable$.pipe(
      map((invenciones) => {
        return invenciones.items.map((solicitud) => solicitud as ISolicitudProteccionReportData);
      }),
      switchMap((invencionesReportData) => {
        const requestsInvencion: Observable<ISolicitudProteccionReportData>[] = [];

        invencionesReportData.forEach(invencion => {
          requestsInvencion.push(this.getDataReportInner(invencion, reportConfig.reportOptions));
        });
        return zip(...requestsInvencion);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(invencionData: ISolicitudProteccionReportData, reportOptions: IReportOptions):
    Observable<ISolicitudProteccionReportData> {
    return concat(
      this.getDataReportListadoGeneral(invencionData),
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    solicitudProteccionData: ISolicitudProteccionReportData
  ): Observable<ISolicitudProteccionReportData> {
    return this.solicitudProteccionGeneralListadoExportService.getData(solicitudProteccionData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  protected getColumns(resultados: ISolicitudProteccionReportData[], reportConfig: IReportConfig<ISolicitudProteccionReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.solicitudProteccionGeneralListadoExportService.fillColumns(resultados, reportConfig));

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
