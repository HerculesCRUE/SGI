import { Injectable } from '@angular/core';
import { IAlegacionRequerimiento } from '@core/models/csp/alegacion-requerimiento';
import { IGastoRequerimientoJustificacion } from '@core/models/csp/gasto-requerimiento-justificacion';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoSeguimientoJustificacion } from '@core/models/csp/proyecto-seguimiento-justificacion';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IGastoJustificado } from '@core/models/sge/gasto-justificado';
import { IGastoJustificadoDetalle } from '@core/models/sge/gasto-justificado-detalle';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SeguimientoJustificacionService } from '@core/services/sge/seguimiento-justificacion/seguimiento-justificacion.service';
import { RSQLSgiRestFilter, SgiRestFilterOperator } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { concat, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { SeguimientoGastosJustificadosResumenListadoGeneralExportService } from './seguimiento-gastos-justificados-listado-general-export.service';

export interface IRequerimientoJustificacionAlegacion extends IRequerimientoJustificacion {
  alegacionRequerimiento: IAlegacionRequerimiento;
}
export interface IGastoRequerimiento extends IGastoRequerimientoJustificacion {
  requerimientosJustificacionAlegacion: IRequerimientoJustificacionAlegacion;
}

export interface IProyectoResponsablesReportData {
  proyectoSgiId: number;
  responsables: IResponsableReportData[];
}

export interface IResponsableReportData {
  nombre: string;
  apellidos: string;
  email: string;
}

export interface IProyectoEntidadesFinanciadorasReportData {
  proyectoSgiId: number;
  entidadesFinanciadoras: IEntidadFinanciadoraReportData[];
}

export interface IEntidadFinanciadoraReportData {
  nombre: string;
  numIdentificacion: string;
}

export interface IGastoJustificadoReportData extends IGastoJustificado {
  detalle: IGastoJustificadoDetalle;
  proyectoSgi: IProyecto;
  responsablesIds: string;
  responsables: IResponsableReportData[];
  tituloConvocatoria: string;
  entidadesFinanciadoras: IEntidadFinanciadoraReportData[];
  importeConcedido: number;
  importeConcedidoCD: number;
  importeConcedidoCI: number;
  fechaUltimaJustificacion: DateTime;
  proyectoSeguimientoJustificacion: IProyectoSeguimientoJustificacion;
  requerimientos: IGastoRequerimiento[];
}

// tslint:disable-next-line: no-empty-interface
export interface IGastosJustificadosReportOptions extends IReportOptions {
}

@Injectable()
export class SeguimientoGastosJustificadosResumenListadoExportService
  extends AbstractTableExportService<IGastoJustificadoReportData, IGastosJustificadosReportOptions> {

  // tslint:disable-next-line: variable-name
  private _proyectoSgeRef: string;
  public set proyectoSgeRef(value: string) {
    this._proyectoSgeRef = value;
  }

  constructor(
    protected readonly logger: NGXLogger,
    protected reportService: ReportService,
    private readonly seguimientoJustificacionService: SeguimientoJustificacionService,
    private readonly seguimientoGastosJustificadosResumenListadoGeneralExportService:
      SeguimientoGastosJustificadosResumenListadoGeneralExportService
  ) {
    super(reportService);
  }

  protected getRows(
    gastos: IGastoJustificadoReportData[],
    reportConfig: IReportConfig<IGastosJustificadosReportOptions>
  ): Observable<ISgiRowReport[]> {
    if (!!!gastos || gastos.length === 0) {
      return this.getRowsInner([], 0, reportConfig).pipe(map(gasto => [gasto]));
    }

    const requestsRow: Observable<ISgiRowReport>[] = [];
    gastos.forEach((_, index) => {
      requestsRow.push(this.getRowsInner(gastos, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    gastos: IGastoJustificadoReportData[],
    index: number,
    reportConfig: IReportConfig<IGastosJustificadosReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {
        row.elements.push(...this.seguimientoGastosJustificadosResumenListadoGeneralExportService.fillRows(gastos, index, reportConfig));

        return row;
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<IGastosJustificadosReportOptions>): Observable<IGastoJustificadoReportData[]> {

    const options = {
      filter: new RSQLSgiRestFilter(
        'proyectoId', SgiRestFilterOperator.EQUALS, this._proyectoSgeRef)
    };

    return this.seguimientoJustificacionService.findAll(options).pipe(
      map((gastos) => {
        return gastos.items.map((pr) => pr as IGastoJustificadoReportData);
      }),
      switchMap(data => this.seguimientoGastosJustificadosResumenListadoGeneralExportService.fillDataList(data)),
      switchMap((gastosJustificadosReportData) => {
        if (gastosJustificadosReportData.length === 0) {
          return of(gastosJustificadosReportData);
        }

        const requestsGastos: Observable<IGastoJustificadoReportData>[] = [];

        gastosJustificadosReportData.forEach(gastoJustificado => {
          requestsGastos.push(this.getDataReportInner(gastoJustificado, reportConfig.reportOptions, reportConfig.outputType));
        });
        return zip(...requestsGastos);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(
    gastoJustificadoData: IGastoJustificadoReportData,
    reportOptions: IGastosJustificadosReportOptions,
    output: OutputReport
  ): Observable<IGastoJustificadoReportData> {
    return concat(
      this.getDataReportListadoGeneral(gastoJustificadoData),

    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    gastoJustificadoData: IGastoJustificadoReportData
  ): Observable<IGastoJustificadoReportData> {
    return this.seguimientoGastosJustificadosResumenListadoGeneralExportService.getData(gastoJustificadoData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  protected getColumns(resultados: IGastoJustificadoReportData[], reportConfig: IReportConfig<IGastosJustificadosReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.seguimientoGastosJustificadosResumenListadoGeneralExportService.fillColumns(resultados, reportConfig));


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
