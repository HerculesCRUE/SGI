import { Injectable } from '@angular/core';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEnlace } from '@core/models/csp/grupo-enlace';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { IGrupoEquipoInstrumental } from '@core/models/csp/grupo-equipo-instrumental';
import { IGrupoLineaEquipoInstrumental } from '@core/models/csp/grupo-linea-equipo-instrumental';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { IGrupoLineaInvestigador } from '@core/models/csp/grupo-linea-investigador';
import { IGrupoPersonaAutorizada } from '@core/models/csp/grupo-persona-autorizada';
import { IGrupoResponsableEconomico } from '@core/models/csp/grupo-responsable-economico';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { concat, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { GrupoLineaClasificacionListado } from '../grupo-linea-investigacion/grupo-linea-investigacion-formulario/grupo-linea-clasificaciones/grupo-linea-clasificaciones.fragment';
import { GrupoEnlaceListadoExportService } from './grupo-enlace-listado-export.service';
import { GrupoEquipoInstrumentalListadoExportService } from './grupo-equipo-instrumental-listado-export.service';
import { GrupoEquipoListadoExportService } from './grupo-equipo-listado-export.service';
import { GrupoFooterListadoExportService } from './grupo-footer-listado-export.service';
import { GrupoGeneralListadoExportService } from './grupo-general-listado-export.service';
import { GrupoHeaderListadoExportService } from './grupo-header-listado-export.service';
import { GrupoLineaInvestigacionListadoExportService } from './grupo-linea-investigacion-listado-export.service';
import { GrupoPersonaAutorizadaListadoExportService } from './grupo-persona-autorizada-listado-export.service';
import { GrupoResponsableEconomicoListadoExportService } from './grupo-responsable-economico-listado-export.service';

export interface IGrupoReportData extends IGrupo {
  investigadoresPrincipales?: string;
  equiposInvestigacion?: IGrupoEquipo[];
  responsablesEconomicos?: IGrupoResponsableEconomico[];
  enlaces?: IGrupoEnlace[];
  personasAutorizadas?: IGrupoPersonaAutorizada[];
  equiposInstrumentales?: IGrupoEquipoInstrumental[];
  lineasInvestigacion?: IGrupoLineaInvestigacion[];
  lineasInvestigador?: IGrupoLineaInvestigador[];
  clasificaciones?: GrupoLineaClasificacionListado[];
  lineasEquiposInstrumentales?: IGrupoLineaEquipoInstrumental[];
}

export interface IGrupoReportOptions extends IReportOptions {
  showEquiposInvestigacion: boolean;
  showEnlaces: boolean;
  showResponsablesEconomicos: boolean;
  showPersonasAutorizadas: boolean;
  showLineasInvestigacion: boolean;
  showEquiposInstrumentales: boolean;
}

@Injectable()
export class GrupoListadoExportService extends AbstractTableExportService<IGrupoReportData, IGrupoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    private readonly grupoService: GrupoService,
    private readonly grupoGeneralListadoExportService: GrupoGeneralListadoExportService,
    private readonly grupoEquipoInvestigacionListadoExportService: GrupoEquipoListadoExportService,
    private readonly grupoResponsableEconomicoListadoExportService: GrupoResponsableEconomicoListadoExportService,
    private readonly grupoLineaInvestigacionListadoExportService: GrupoLineaInvestigacionListadoExportService,
    private readonly grupoEnlaceListadoExportService: GrupoEnlaceListadoExportService,
    private readonly grupoPersonaAutorizadaListadoExportService: GrupoPersonaAutorizadaListadoExportService,
    private readonly grupoEquipoInstrumentalListadoExportService: GrupoEquipoInstrumentalListadoExportService,
    protected reportService: ReportService,
    private readonly grupoHeaderListadoExportService: GrupoHeaderListadoExportService,
    private readonly grupoFooterListadoExportService: GrupoFooterListadoExportService
  ) {
    super(reportService);
  }

  protected getRows(grupos: IGrupoReportData[], reportConfig: IReportConfig<IGrupoReportOptions>): Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    grupos.forEach((grupo, index) => {
      requestsRow.push(this.getRowsInner(grupos, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    grupos: IGrupoReportData[],
    index: number,
    reportConfig: IReportConfig<IGrupoReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {
        row.elements.push(...this.grupoGeneralListadoExportService.fillRows(grupos, index, reportConfig));

        if (reportConfig.outputType === OutputReport.PDF || reportConfig.outputType === OutputReport.RTF) {
          row.elements.push(...this.grupoHeaderListadoExportService.fillRows(grupos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showEquiposInvestigacion) {
          row.elements.push(...this.grupoEquipoInvestigacionListadoExportService.fillRows(grupos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showResponsablesEconomicos) {
          row.elements.push(...this.grupoResponsableEconomicoListadoExportService.fillRows(grupos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showLineasInvestigacion) {
          row.elements.push(...this.grupoLineaInvestigacionListadoExportService.fillRows(grupos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showEquiposInstrumentales) {
          row.elements.push(...this.grupoEquipoInstrumentalListadoExportService.fillRows(grupos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showEnlaces) {
          row.elements.push(...this.grupoEnlaceListadoExportService.fillRows(grupos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showPersonasAutorizadas) {
          row.elements.push(...this.grupoPersonaAutorizadaListadoExportService.fillRows(grupos, index, reportConfig));
        }
        if (reportConfig.outputType === OutputReport.PDF || reportConfig.outputType === OutputReport.RTF) {
          row.elements.push(...this.grupoFooterListadoExportService.fillRows(grupos, index, reportConfig));
        }
        return row;
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<IGrupoReportOptions>): Observable<IGrupoReportData[]> {
    let observable$: Observable<SgiRestListResult<IGrupo>> = null;
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    observable$ = this.grupoService.findTodos(findOptions);

    return observable$.pipe(
      map((grupos) => {
        return grupos.items.map((pr) => pr as IGrupoReportData);
      }),
      switchMap((gruposReportData) => {
        const requestsGrupo: Observable<IGrupoReportData>[] = [];

        gruposReportData.forEach(grupo => {
          requestsGrupo.push(this.getDataReportInner(grupo, reportConfig.reportOptions, reportConfig.outputType));
        });
        return zip(...requestsGrupo);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(grupoData: IGrupoReportData, reportOptions: IGrupoReportOptions, output: OutputReport): Observable<IGrupoReportData> {
    return concat(
      this.getDataReportListadoGeneral(grupoData),
      this.getDataReportHeader(grupoData, output),
      this.getDataReportEquipoInvestigacion(grupoData, reportOptions),
      this.getDataReportResponsableEconomico(grupoData, reportOptions),
      this.getDataReportLineaInvestigacion(grupoData, reportOptions),
      this.getDataReportEquipoInstrumental(grupoData, reportOptions),
      this.getDataReportEnlace(grupoData, reportOptions),
      this.getDataReportPersonaAutorizada(grupoData, reportOptions),
      this.getDataReportFooter(grupoData, output)
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    grupoData: IGrupoReportData
  ): Observable<IGrupoReportData> {
    return this.grupoGeneralListadoExportService.getData(grupoData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  private getDataReportEquipoInvestigacion(
    grupoData: IGrupoReportData,
    reportOptions: IGrupoReportOptions
  ): Observable<IGrupoReportData> {
    if (reportOptions?.showEquiposInvestigacion) {
      return this.grupoEquipoInvestigacionListadoExportService.getData(grupoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(grupoData);
    }
  }

  private getDataReportResponsableEconomico(
    grupoData: IGrupoReportData,
    reportOptions: IGrupoReportOptions
  ): Observable<IGrupoReportData> {
    if (reportOptions?.showResponsablesEconomicos) {
      return this.grupoResponsableEconomicoListadoExportService.getData(grupoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(grupoData);
    }
  }

  private getDataReportLineaInvestigacion(
    grupoData: IGrupoReportData,
    reportOptions: IGrupoReportOptions
  ): Observable<IGrupoReportData> {
    if (reportOptions?.showLineasInvestigacion) {
      return this.grupoLineaInvestigacionListadoExportService.getData(grupoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(grupoData);
    }
  }

  private getDataReportPersonaAutorizada(
    grupoData: IGrupoReportData,
    reportOptions: IGrupoReportOptions
  ): Observable<IGrupoReportData> {
    if (reportOptions?.showPersonasAutorizadas) {
      return this.grupoPersonaAutorizadaListadoExportService.getData(grupoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(grupoData);
    }
  }

  private getDataReportEnlace(
    grupoData: IGrupoReportData,
    reportOptions: IGrupoReportOptions
  ): Observable<IGrupoReportData> {
    if (reportOptions?.showEnlaces) {
      return this.grupoEnlaceListadoExportService.getData(grupoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(grupoData);
    }
  }

  private getDataReportEquipoInstrumental(
    grupoData: IGrupoReportData,
    reportOptions: IGrupoReportOptions
  ): Observable<IGrupoReportData> {
    if (reportOptions?.showEquiposInstrumentales) {
      return this.grupoEquipoInstrumentalListadoExportService.getData(grupoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(grupoData);
    }
  }

  private getDataReportHeader(grupoData: IGrupoReportData,
    output: OutputReport
  ): Observable<IGrupoReportData> {
    if (output === OutputReport.PDF || output === OutputReport.RTF) {
      return this.grupoHeaderListadoExportService.getData(grupoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(grupoData);
    }
  }

  private getDataReportFooter(grupoData: IGrupoReportData,
    output: OutputReport
  ): Observable<IGrupoReportData> {
    if (output === OutputReport.PDF || output === OutputReport.RTF) {
      return this.grupoFooterListadoExportService.getData(grupoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(grupoData);
    }
  }

  protected getColumns(resultados: IGrupoReportData[], reportConfig: IReportConfig<IGrupoReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.grupoGeneralListadoExportService.fillColumns(resultados, reportConfig));

    if (reportConfig.outputType === OutputReport.PDF || reportConfig.outputType === OutputReport.RTF) {
      columns.push(... this.grupoHeaderListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showEquiposInvestigacion) {
      columns.push(... this.grupoEquipoInvestigacionListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showResponsablesEconomicos) {
      columns.push(... this.grupoResponsableEconomicoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showLineasInvestigacion) {
      columns.push(... this.grupoLineaInvestigacionListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showEquiposInstrumentales) {
      columns.push(... this.grupoEquipoInstrumentalListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showEnlaces) {
      columns.push(... this.grupoEnlaceListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showPersonasAutorizadas) {
      columns.push(... this.grupoPersonaAutorizadaListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.outputType === OutputReport.PDF || reportConfig.outputType === OutputReport.RTF) {
      columns.push(... this.grupoFooterListadoExportService.fillColumns(resultados, reportConfig));
    }
    return of(columns);
  }

  protected getGroupBy(): ISgiGroupReport {
    const groupBy: ISgiGroupReport = {
      name: 'header',
      visible: true
    };
    return groupBy;
  }
}
