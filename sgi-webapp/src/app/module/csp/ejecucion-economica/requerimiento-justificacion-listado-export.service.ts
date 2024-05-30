import { Injectable } from '@angular/core';
import { IAlegacionRequerimiento } from '@core/models/csp/alegacion-requerimiento';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IProyectoSeguimientoEjecucionEconomica } from '@core/models/csp/proyecto-seguimiento-ejecucion-economica';
import { IProyectoSeguimientoJustificacion } from '@core/models/csp/proyecto-seguimiento-justificacion';
import { IRelacionEjecucionEconomica } from '@core/models/csp/relacion-ejecucion-economica';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RelacionEjecucionEconomicaService } from '@core/services/csp/relacion-ejecucion-economica/relacion-ejecucion-economica.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { concat, EMPTY, from, Observable, of, zip } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';
import { IProyectoPeriodoJustificacionWithTituloProyecto, IProyectoPeriodoSeguimientoWithTituloProyecto } from './ejecucion-economica-formulario/seguimiento-justificacion-resumen/seguimiento-justificacion-resumen.fragment';
import { IRelacionEjecucionEconomicaWithResponsables } from './ejecucion-economica.action.service';
import { RequerimientoJustificacionGeneralListadoExportService } from './requerimiento-justificacion-general-listado-export.service';

export interface IRequerimientoJustificacionReportOptions extends IReportOptions {
  idsProyectoSge: string[];
}

export interface IRequerimientoJustificacionInforme extends IRequerimientoJustificacion {
  alegacion: IAlegacionRequerimiento;
}

export interface IRequerimientoJustificacionReportData extends IRelacionEjecucionEconomicaWithResponsables {
  proyecto: IProyecto;
  proyectoProyectoSge: IProyectoProyectoSge;
  proyectosSGI: IProyectoSeguimientoEjecucionEconomica[];
  seguimientosJustificacion: IProyectoSeguimientoJustificacion[];
  periodosJustificacion: IProyectoPeriodoJustificacionWithTituloProyecto[];
  periodosSeguimiento: IProyectoPeriodoSeguimientoWithTituloProyecto[];
  tituloConvocatoria: string;
  entidadesFinanciadoras: IProyectoEntidadFinanciadora[];
  requerimientosJustificacion: IRequerimientoJustificacionInforme[];
}

@Injectable()
export class RequerimientoJustificacionListadoExportService
  extends AbstractTableExportService<IRequerimientoJustificacionReportData, IReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    private relacionEjecucionEconomicaService: RelacionEjecucionEconomicaService,
    private readonly requerimientoJustificacionListadoExportService: RequerimientoJustificacionGeneralListadoExportService,
    private proyectoService: ProyectoService,
    protected reportService: ReportService,
    private personaService: PersonaService,
  ) {
    super(reportService);
  }

  protected getRows(
    solicitudes: IRequerimientoJustificacionReportData[],
    reportConfig: IReportConfig<IRequerimientoJustificacionReportOptions>
  ): Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    solicitudes.forEach((_, index) => {
      requestsRow.push(this.getRowsInner(solicitudes, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    solicitudes: IRequerimientoJustificacionReportData[],
    index: number,
    reportConfig: IReportConfig<IRequerimientoJustificacionReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {

        row.elements.push(...this.requerimientoJustificacionListadoExportService.fillRows(solicitudes, index, reportConfig));

        return row;
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<IRequerimientoJustificacionReportOptions>): any {
    let observable$: Observable<SgiRestListResult<IRelacionEjecucionEconomica>> = null;
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    const filtro = new RSQLSgiRestFilter('proyectoSgeRef', SgiRestFilterOperator.IN, reportConfig.reportOptions.idsProyectoSge);

    const options: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('proyectoSgeRef', SgiRestSortDirection.ASC),
      filter: filtro
    };

    observable$ = this.relacionEjecucionEconomicaService.findRelacionesProyectos(options);

    return observable$.pipe(
      map(result => result as SgiRestListResult<IRelacionEjecucionEconomicaWithResponsables>),
      switchMap(response =>
        from(response.items).pipe(
          mergeMap(relacion => {
            return this.proyectoService.findInvestigadoresPrincipales(relacion.id).pipe(
              filter(personaRefs => !!personaRefs),
              map(investigadoresPrincipales => investigadoresPrincipales.map(investigador => investigador.persona.id)),
              switchMap(personaRefs => this.personaService.findAllByIdIn(personaRefs).pipe(
                catchError((error) => {
                  this.logger.error(error);
                  return EMPTY;
                })
              )),
              map(responsables => {
                relacion.responsables = responsables.items;
                return relacion;
              }),
            );
          }),
          toArray(),
          map(() => {
            return response;
          })

        )
      ),
      switchMap((result) => {
        const requestsRequerimiento: Observable<IRequerimientoJustificacionReportData>[] = [];
        result.items.forEach(relacion => {
          const requerimiento = {
            id: relacion.id,
            proyectoSge: relacion.proyectoSge,
            nombre: relacion.nombre,
            fechaInicio: relacion.fechaInicio,
            fechaFin: relacion.fechaFin,
            responsables: relacion.responsables
          } as IRequerimientoJustificacionReportData;
          requestsRequerimiento.push(this.getDataReportInner(requerimiento, reportConfig.reportOptions));
        });
        return zip(...requestsRequerimiento);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(requerimientoData: IRequerimientoJustificacionReportData, reportOptions: IReportOptions):
    Observable<IRequerimientoJustificacionReportData> {
    return concat(
      this.getDataReportListadoGeneral(requerimientoData),
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    requerimientoData: IRequerimientoJustificacionReportData
  ): Observable<IRequerimientoJustificacionReportData> {
    return this.requerimientoJustificacionListadoExportService.getData(requerimientoData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  protected getColumns(
    resultados: IRequerimientoJustificacionReportData[],
    reportConfig: IReportConfig<IRequerimientoJustificacionReportOptions>
  ): Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.requerimientoJustificacionListadoExportService.fillColumns(resultados, reportConfig));

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
