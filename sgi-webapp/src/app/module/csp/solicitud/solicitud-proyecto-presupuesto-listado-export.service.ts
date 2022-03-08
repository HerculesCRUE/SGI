import { DecimalPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, zip } from 'rxjs';
import { map, switchMap, takeLast } from 'rxjs/operators';
import { ISolicitudListadoData } from './solicitud-listado/solicitud-listado.component';

const SOLICITUD_PROYECTO_PRESUPUESTO_KEY = marker('csp.solicitud-proyecto-presupuesto.titulo');
const SOLICITUD_PROYECTO_PRESUPUESTO_ANUALIDAD_KEY = marker('csp.solicitud-proyecto-presupuesto.anualidad');
const SOLICITUD_PROYECTO_PRESUPUESTO_CONCEPTO_GASTO_KEY = marker('csp.solicitud-proyecto-presupuesto.concepto-gasto');
const SOLICITUD_PROYECTO_PRESUPUESTO_IMPORTE_PRESUPUESTADO_KEY = marker('csp.solicitud-proyecto-presupuesto.importe-presupuestado');
const SOLICITUD_PROYECTO_PRESUPUESTO_IMPORTE_SOLICITADO_KEY = marker('csp.solicitud-proyecto-presupuesto.importe-solicitado');

export interface ISolicitudPrespuestoReportData extends ISolicitudListadoData {
  solicitudesPresupuesto?: ISolicitudProyectoPresupuesto[];
}

export interface ISolicitudPresupuestoReportOptions extends IReportOptions {
  solicitudId: number;
}

@Injectable()
export class SolicitudProyectoPresupuestoListadoExportService extends AbstractTableExportService<ISolicitudPrespuestoReportData, ISolicitudPresupuestoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly translate: TranslateService,
    private readonly decimalPipe: DecimalPipe,
    private readonly solicitudService: SolicitudService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getRows(solicitudesPresupuesto: ISolicitudPrespuestoReportData[], reportConfig: IReportConfig<ISolicitudPresupuestoReportOptions>): Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    solicitudesPresupuesto.forEach((solicitudPresupuesto, index) => {
      requestsRow.push(this.getRowsInner(solicitudesPresupuesto, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    solicitudesPresupuestoData: ISolicitudPrespuestoReportData[],
    index: number,
    reportConfig: IReportConfig<ISolicitudPresupuestoReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {
        row.elements.push(...this.fillRows(solicitudesPresupuestoData, index, reportConfig));
        return row;
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<ISolicitudPresupuestoReportOptions>): Observable<ISolicitudPrespuestoReportData[]> {

    const solicitudId = reportConfig.reportOptions.solicitudId;

    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC),
      filter: new RSQLSgiRestFilter('id', SgiRestFilterOperator.EQUALS, solicitudId.toString())
    };

    return this.solicitudService.findAllTodos(findOptions).pipe(
      map((solicitudes) => {
        return solicitudes.items.map((solicitud) => {
          return solicitud as ISolicitudPrespuestoReportData;
        });
      }),
      switchMap((solicitudesPresupuestoReportData) => {
        if (solicitudesPresupuestoReportData.length === 0) {
          return of(solicitudesPresupuestoReportData);
        }

        const requestsSolicitud: Observable<ISolicitudPrespuestoReportData>[] = [];

        solicitudesPresupuestoReportData.forEach(solicitudPresupuestoData => {
          requestsSolicitud.push(this.getDataReportListadoGeneral(solicitudPresupuestoData));
        });
        return zip(...requestsSolicitud);
      }),
      takeLast(1)
    );
  }

  private getDataReportListadoGeneral(
    solicitudPresupuestoData: ISolicitudPrespuestoReportData
  ): Observable<ISolicitudPrespuestoReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('anualidad', SgiRestSortDirection.DESC)
    };
    return this.solicitudService.findAllSolicitudProyectoPresupuesto(solicitudPresupuestoData.id, findOptions).pipe(
      map((solicitudesPresupestoResponse) => {
        solicitudPresupuestoData.solicitudesPresupuesto = solicitudesPresupestoResponse.items;
        return solicitudPresupuestoData;
      })
    );
  }

  protected getColumns(resultados: ISolicitudPrespuestoReportData[], reportConfig: IReportConfig<ISolicitudPresupuestoReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.fillColumns(resultados, reportConfig));

    return of(columns);
  }

  public fillColumns(
    solicitudesPresupuestoData: ISolicitudPrespuestoReportData[],
    reportConfig: IReportConfig<ISolicitudPresupuestoReportOptions>
  ): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumPresupuestos = Math.max(...solicitudesPresupuestoData.map(s => s.solicitudesPresupuesto ? s.solicitudesPresupuesto?.length : 0));

    const columnTitle = {
      title: this.translate.instant(SOLICITUD_PROYECTO_PRESUPUESTO_KEY),
      name: 'titulo',
      type: ColumnType.STRING,
      format: '#'
    };
    columns.push(columnTitle);

    for (let i = 0; i < maxNumPresupuestos; i++) {
      const idPresupuesto: string = String(i + 1);

      const columnAnualidad = {
        title: this.translate.instant(SOLICITUD_PROYECTO_PRESUPUESTO_ANUALIDAD_KEY) + ' ' + idPresupuesto,
        name: 'anualidad' + idPresupuesto,
        type: ColumnType.NUMBER,
        format: '#'
      };
      columns.push(columnAnualidad);

      const columnConceptoGasto = {
        title: this.translate.instant(SOLICITUD_PROYECTO_PRESUPUESTO_CONCEPTO_GASTO_KEY) + ' ' + idPresupuesto,
        name: 'conceptoGasto' + idPresupuesto,
        type: ColumnType.STRING,
        format: '#'
      };
      columns.push(columnConceptoGasto);

      const columnImportePresupuestado = {
        title: this.translate.instant(SOLICITUD_PROYECTO_PRESUPUESTO_IMPORTE_PRESUPUESTADO_KEY) + ' ' + idPresupuesto,
        name: 'importePresupuestado' + idPresupuesto,
        type: ColumnType.STRING,
        format: '#'
      };
      columns.push(columnImportePresupuestado);

      const columnImporteSolicitado = {
        title: this.translate.instant(SOLICITUD_PROYECTO_PRESUPUESTO_IMPORTE_SOLICITADO_KEY) + ' ' + idPresupuesto,
        name: 'importeSolicitado' + idPresupuesto,
        type: ColumnType.STRING,
        format: '#'
      };
      columns.push(columnImporteSolicitado);
    }

    return columns;
  }

  public fillRows(resultados: ISolicitudPrespuestoReportData[], index: number, reportConfig: IReportConfig<ISolicitudPresupuestoReportOptions>): any[] {
    const solicitudPresupuestoData = resultados[index];
    const elementsRow: any[] = [];
    elementsRow.push(solicitudPresupuestoData.codigoRegistroInterno ?? '');
    if (solicitudPresupuestoData.solicitudesPresupuesto.length > 0) {
      solicitudPresupuestoData.solicitudesPresupuesto.forEach(solicitudPresupuesto => {
        elementsRow.push(solicitudPresupuesto?.anualidad ?? '');
        elementsRow.push(solicitudPresupuesto?.conceptoGasto ? solicitudPresupuesto?.conceptoGasto.nombre ?? '' : '');
        elementsRow.push(solicitudPresupuesto?.importePresupuestado ? this.decimalPipe.transform(solicitudPresupuesto?.importePresupuestado, '2.2-2') ?? '' : '');
        elementsRow.push(solicitudPresupuesto?.importeSolicitado ? this.decimalPipe.transform(solicitudPresupuesto?.importeSolicitado, '2.2-2') ?? '' : '');
      });
    }
    return elementsRow;
  }

  protected getGroupBy(): ISgiGroupReport {
    const groupBy: ISgiGroupReport = {
      name: 'titulo',
      visible: true
    };
    return groupBy;
  }
}
