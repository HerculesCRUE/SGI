import { Injectable } from '@angular/core';
import { IInvencion } from '@core/models/pii/invencion';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IPais } from '@core/models/sgo/pais';
import { IPersona } from '@core/models/sgp/persona';
import { IVinculacion } from '@core/models/sgp/vinculacion';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { InvencionEquipoInventorListadoExportService } from './invencion-equipo-inventor-listado-export.service';
import { InvencionGeneralListadoExportService } from './invencion-general-listado-export.service';
import { InvencionSolicitudesProteccionListadoExportService } from './invencion-solicitudes-proteccion-listado-export.service';

export interface IInventorMiembro {
  persona: IPersona;
  vinculacion: IVinculacion;
}
export interface IInvencionReportData extends IInvencion {
  solicitudesDeProteccion?: ISolicitudProteccionReport[];
  equipoInventor?: IInventorMiembro[];
}

export interface IInvencionReportOptions extends IReportOptions {
  showSolicitudesDeProteccion: boolean;
  showEquipoInventor: boolean;
}

export interface ISolicitudProteccionReport extends ISolicitudProteccion {
  pais?: IPais;
}

@Injectable()
export class InvencionListadoExportService extends AbstractTableExportService<IInvencionReportData, IInvencionReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    private authService: SgiAuthService,
    private readonly invencionService: InvencionService,
    private readonly invencionGeneralListadoExportService: InvencionGeneralListadoExportService,
    private readonly invencionEquipoInventorListadoExportService: InvencionEquipoInventorListadoExportService,
    private readonly invencionSolicitudesProteccionListadoExportService: InvencionSolicitudesProteccionListadoExportService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getRows(invenciones: IInvencionReportData[], reportConfig: IReportConfig<IInvencionReportOptions>):
    Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    invenciones.forEach((invencion, index) => {
      requestsRow.push(this.getRowsInner(invenciones, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    invenciones: IInvencionReportData[],
    index: number,
    reportConfig: IReportConfig<IInvencionReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {

        row.elements.push(...this.invencionGeneralListadoExportService.fillRows(invenciones, index, reportConfig));

        if (reportConfig.reportOptions?.showEquipoInventor) {
          row.elements.push(...this.invencionEquipoInventorListadoExportService.fillRows(invenciones, index, reportConfig));
        }

        if (reportConfig.reportOptions?.showSolicitudesDeProteccion) {
          row.elements.push(...this.invencionSolicitudesProteccionListadoExportService.fillRows(invenciones, index, reportConfig));
        }
        return row;
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<IInvencionReportOptions>): Observable<IInvencionReportData[]> {
    let observable$: Observable<SgiRestListResult<IInvencion>> = null;
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    if (this.authService.hasAuthorityForAnyUO('PII-INV-R')) {
      observable$ = this.invencionService.findTodos(findOptions);
    } else {
      observable$ = this.invencionService.findAll(findOptions);
    }

    return observable$.pipe(
      map((invenciones) => {
        return invenciones.items.map((invencion) => invencion as IInvencionReportData);
      }),
      switchMap((invencionesReportData) => {
        const requestsInvencion: Observable<IInvencionReportData>[] = [];

        invencionesReportData.forEach(invencion => {
          requestsInvencion.push(this.getDataReportInner(invencion, reportConfig.reportOptions));
        });
        return zip(...requestsInvencion);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(invencionData: IInvencionReportData, reportOptions: IInvencionReportOptions):
    Observable<IInvencionReportData> {
    return merge(
      this.getDataReportListadoGeneral(invencionData),
      this.getDataReportEquipoInventor(invencionData, reportOptions),
      this.getDataReportSolicitudesDeProteccion(invencionData, reportOptions),
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    invencionData: IInvencionReportData
  ): Observable<IInvencionReportData> {
    return this.invencionGeneralListadoExportService.getData(invencionData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  private getDataReportSolicitudesDeProteccion(
    invencionData: IInvencionReportData,
    reportOptions: IInvencionReportOptions
  ): Observable<IInvencionReportData> {
    if (reportOptions?.showSolicitudesDeProteccion) {
      return this.invencionSolicitudesProteccionListadoExportService.getData(invencionData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(invencionData);
    }
  }

  private getDataReportEquipoInventor(
    invencionData: IInvencionReportData,
    reportOptions: IInvencionReportOptions
  ): Observable<IInvencionReportData> {
    if (reportOptions?.showEquipoInventor) {
      return this.invencionEquipoInventorListadoExportService.getData(invencionData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(invencionData);
    }
  }

  protected getColumns(resultados: IInvencionReportData[], reportConfig: IReportConfig<IInvencionReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.invencionGeneralListadoExportService.fillColumns(resultados, reportConfig));

    if (reportConfig.reportOptions?.showEquipoInventor) {
      columns.push(... this.invencionEquipoInventorListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showSolicitudesDeProteccion) {
      columns.push(... this.invencionSolicitudesProteccionListadoExportService.fillColumns(resultados, reportConfig));
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
