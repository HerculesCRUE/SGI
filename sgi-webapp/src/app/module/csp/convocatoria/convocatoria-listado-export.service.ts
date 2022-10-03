import { Injectable } from '@angular/core';
import { IConfiguracionSolicitud } from '@core/models/csp/configuracion-solicitud';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IConvocatoriaEntidadGestora } from '@core/models/csp/convocatoria-entidad-gestora';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { concat, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { ConvocatoriaAreaTematicaListadoExportService, IConvocatoriaAreaTematicaListadoExport } from './convocatoria-area-tematica-listado-export.service';
import { ConvocatoriaCalendarioJustificacionListadoExportService } from './convocatoria-calendario-justificacion-listado-export.service';
import { ConvocatoriaConceptoGastoListadoExportService, IConvocatoriaConceptoGastoListadoExport } from './convocatoria-concepto-gasto-listado-export.service';
import { ConvocatoriaConfiguracionSolicitudListadoExportService } from './convocatoria-configuracion-solicitud-listado-export.service';
import { ConvocatoriaEnlaceListadoExportService } from './convocatoria-enlace-listado-export.service';
import { ConvocatoriaEntidadConvocanteListadoExportService } from './convocatoria-entidad-convocante-listado-export.service';
import { ConvocatoriaEntidadFinanciadoraListadoExportService } from './convocatoria-entidad-financiadora-listado-export.service';
import { ConvocatoriaFaseListadoExportService } from './convocatoria-fase-listado-export.service';
import { ConvocatoriaFooterListadoExportService } from './convocatoria-footer-listado-export.service';
import { ConvocatoriaGeneralListadoExportService } from './convocatoria-general-listado-export.service';
import { ConvocatoriaHeaderListadoExportService } from './convocatoria-header-listado-export.service';
import { ConvocatoriaHitoListadoExportService } from './convocatoria-hito-listado-export.service';
import { IConvocatoriaListado } from './convocatoria-listado/convocatoria-listado.component';
import { ConvocatoriaPartidaPresupuestariaListadoExportService } from './convocatoria-partida-presupuestaria-listado-export.service';
import { ConvocatoriaPeriodoSeguimientoListadoExportService } from './convocatoria-periodo-seguimiento-listado-export.service';
import { ConvocatoriaRequisitoEquipoListadoExportService } from './convocatoria-requisito-equipo-listado-export.service';
import { ConvocatoriaRequisitoIPListadoExportService } from './convocatoria-requisito-ip-listado-export.service';

export interface IConvocatoriaReportData extends IConvocatoriaListado {
  entidadGestora: IConvocatoriaEntidadGestora;
  areasTematicas: IConvocatoriaAreaTematicaListadoExport[];
  entidadesConvocantes: IConvocatoriaEntidadConvocante[];
  entidadesFinanciadoras: IConvocatoriaEntidadFinanciadora[];
  enlaces: IConvocatoriaEnlace[];
  fases: IConvocatoriaFase[];
  calendarioJustificacion: IConvocatoriaPeriodoJustificacion[];
  periodosSeguimientos: IConvocatoriaPeriodoSeguimientoCientifico[];
  hitos: IConvocatoriaHito[];
  requisitoIP: IConvocatoriaRequisitoIP;
  requisitoEquipo: IConvocatoriaRequisitoEquipo;
  nivelesAcademicos: IRequisitoIPNivelAcademico[];
  categoriasProfesionales: IRequisitoIPCategoriaProfesional[];
  nivelesAcademicosEquipo: IRequisitoEquipoNivelAcademico[];
  categoriasProfesionalesEquipo: IRequisitoEquipoCategoriaProfesional[];
  conceptosGastos: IConvocatoriaConceptoGastoListadoExport[];
  partidasPresupuestarias: IConvocatoriaPartidaPresupuestaria[];
  configuracionSolicitudes: IConfiguracionSolicitud;
  documentosRequeridos: IDocumentoRequeridoSolicitud[];
}

export interface IConvocatoriaReportOptions extends IReportOptions {
  showAreasTematicas: boolean;
  showEntidadesConvocantes: boolean;
  showEntidadesFinanciadoras: boolean;
  showEnlaces: boolean;
  showFases: boolean;
  showCalendarioJustificacion: boolean;
  showPeriodosSeguimientoCientifico: boolean;
  showHitos: boolean;
  showRequisitosIP: boolean;
  showRequisitosEquipo: boolean;
  showElegibilidad: boolean;
  showPartidasPresupuestarias: boolean;
  showConfiguracionSolicitudes: boolean;
}

@Injectable()
export class ConvocatoriaListadoExportService extends AbstractTableExportService<IConvocatoriaReportData, IConvocatoriaReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly translate: TranslateService,
    private readonly convocatoriaService: ConvocatoriaService,
    protected reportService: ReportService,
    private readonly convocatoriaGeneralListadoExportService: ConvocatoriaGeneralListadoExportService,
    private readonly convocatoriaAreaTematicaListadoExportService: ConvocatoriaAreaTematicaListadoExportService,
    private readonly convocatoriaEntidadConvocanteListadoExportService: ConvocatoriaEntidadConvocanteListadoExportService,
    private readonly convocatoriaEntidadFinanciadoraListadoExportService: ConvocatoriaEntidadFinanciadoraListadoExportService,
    private readonly convocatoriaEnlaceListadoExportService: ConvocatoriaEnlaceListadoExportService,
    private readonly convocatoriaFaseListadoExportService: ConvocatoriaFaseListadoExportService,
    private readonly convocatoriaCalendarioJustificacionListadoExportService: ConvocatoriaCalendarioJustificacionListadoExportService,
    private readonly convocatoriaPeriodoSeguimientoListadoExportService: ConvocatoriaPeriodoSeguimientoListadoExportService,
    private readonly convocatoriaHitoListadoExportService: ConvocatoriaHitoListadoExportService,
    private readonly convocatoriaRequisitoIPListadoExportService: ConvocatoriaRequisitoIPListadoExportService,
    private readonly convocatoriaRequisitoEquipoListadoExportService: ConvocatoriaRequisitoEquipoListadoExportService,
    private readonly convocatoriaConceptoGastoListadoExportService: ConvocatoriaConceptoGastoListadoExportService,
    private readonly convocatoriaPartidaPresupuestariaListadoExportService: ConvocatoriaPartidaPresupuestariaListadoExportService,
    private readonly convocatoriaConfiguracionSolicitudListadoExportService: ConvocatoriaConfiguracionSolicitudListadoExportService,
    private readonly convocatoriaFooterListadoExportService: ConvocatoriaFooterListadoExportService,
    private readonly convocatoriaHeaderListadoExportService: ConvocatoriaHeaderListadoExportService
  ) {
    super(reportService);
  }

  protected getRows(convocatorias: IConvocatoriaReportData[], reportConfig: IReportConfig<IConvocatoriaReportOptions>)
    : Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];
    convocatorias.forEach((convocatoria, index) => {
      requestsRow.push(this.getRowsInner(convocatorias, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    convocatorias: IConvocatoriaReportData[],
    index: number,
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {
        row.elements.push(...this.convocatoriaGeneralListadoExportService.fillRows(convocatorias, index, reportConfig));
        if (reportConfig.outputType === OutputReport.PDF || reportConfig.outputType === OutputReport.RTF) {
          row.elements.push(...this.convocatoriaHeaderListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showAreasTematicas) {
          row.elements.push(...this.convocatoriaAreaTematicaListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showEntidadesConvocantes) {
          row.elements.push(...this.convocatoriaEntidadConvocanteListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showEntidadesFinanciadoras) {
          row.elements.push(...this.convocatoriaEntidadFinanciadoraListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showEnlaces) {
          row.elements.push(...this.convocatoriaEnlaceListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showFases) {
          row.elements.push(...this.convocatoriaFaseListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showCalendarioJustificacion) {
          row.elements.push(...this.convocatoriaCalendarioJustificacionListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showPeriodosSeguimientoCientifico) {
          row.elements.push(...this.convocatoriaPeriodoSeguimientoListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showHitos) {
          row.elements.push(...this.convocatoriaHitoListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showRequisitosIP) {
          row.elements.push(...this.convocatoriaRequisitoIPListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showRequisitosEquipo) {
          row.elements.push(...this.convocatoriaRequisitoEquipoListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showElegibilidad) {
          row.elements.push(...this.convocatoriaConceptoGastoListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showPartidasPresupuestarias) {
          row.elements.push(...this.convocatoriaPartidaPresupuestariaListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showConfiguracionSolicitudes) {
          row.elements.push(...this.convocatoriaConfiguracionSolicitudListadoExportService.fillRows(convocatorias, index, reportConfig));
        }
        if (reportConfig.outputType === OutputReport.PDF || reportConfig.outputType === OutputReport.RTF) {
          row.elements.push(...this.convocatoriaFooterListadoExportService.fillRows(convocatorias, index, reportConfig));
        }

        return row;
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<IConvocatoriaReportOptions>): Observable<IConvocatoriaReportData[]> {
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    return this.convocatoriaService.findAllTodosRestringidos(findOptions).pipe(
      map((convocatorias) => {
        return convocatorias.items.map((convocatoria) => {
          return {
            convocatoria,
            entidadConvocante: {} as IConvocatoriaEntidadConvocante,
            entidadConvocanteEmpresa: {} as IEmpresa,
            entidadFinanciadora: {} as IConvocatoriaEntidadFinanciadora,
            entidadFinanciadoraEmpresa: {} as IEmpresa,
            fase: {} as IConvocatoriaFase
          } as IConvocatoriaReportData;
        });
      }),
      switchMap((convocatoriasReportData) => {
        const requestsConvocatoria: Observable<IConvocatoriaReportData>[] = [];

        convocatoriasReportData.forEach(convocatoria => {
          requestsConvocatoria.push(this.getDataReportInner(convocatoria, reportConfig.reportOptions, reportConfig.outputType));
        });
        return zip(...requestsConvocatoria);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(convocatoriaData: IConvocatoriaReportData, reportOptions: IConvocatoriaReportOptions, output: OutputReport)
    : Observable<IConvocatoriaReportData> {
    return concat(
      this.getDataReportHeader(convocatoriaData, output),
      this.getDataReportListadoGeneral(convocatoriaData),
      this.getDataReportAreasTematicas(convocatoriaData, reportOptions),
      this.getDataReportEntidadesConvocantes(convocatoriaData, reportOptions),
      this.getDataReportEntidadesFinanciadoras(convocatoriaData, reportOptions),
      this.getDataReportEnlaces(convocatoriaData, reportOptions),
      this.getDataReportFases(convocatoriaData, reportOptions),
      this.getDataReportCalendarioJustificacion(convocatoriaData, reportOptions),
      this.getDataReportSeguimientos(convocatoriaData, reportOptions),
      this.getDataReportHitos(convocatoriaData, reportOptions),
      this.getDataReportRequisitosIP(convocatoriaData, reportOptions),
      this.getDataReportRequisitosEquipo(convocatoriaData, reportOptions),
      this.getDataReportElegibilidad(convocatoriaData, reportOptions),
      this.getDataReportPartidasPresupuestarias(convocatoriaData, reportOptions),
      this.getDataReportConfiguracionSolicitudes(convocatoriaData, reportOptions),
      this.getDataReportFooter(convocatoriaData, output)
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    convocatoriaData: IConvocatoriaReportData
  ): Observable<IConvocatoriaReportData> {
    return this.convocatoriaGeneralListadoExportService.getData(convocatoriaData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  private getDataReportAreasTematicas(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showAreasTematicas) {
      return this.convocatoriaAreaTematicaListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportEntidadesConvocantes(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showEntidadesConvocantes) {
      return this.convocatoriaEntidadConvocanteListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportEntidadesFinanciadoras(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showEntidadesFinanciadoras) {
      return this.convocatoriaEntidadFinanciadoraListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportEnlaces(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showEnlaces) {
      return this.convocatoriaEnlaceListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportFases(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showFases) {
      return this.convocatoriaFaseListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportCalendarioJustificacion(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showCalendarioJustificacion) {
      return this.convocatoriaCalendarioJustificacionListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportSeguimientos(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showPeriodosSeguimientoCientifico) {
      return this.convocatoriaPeriodoSeguimientoListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportHitos(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showHitos) {
      return this.convocatoriaHitoListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportRequisitosIP(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showRequisitosIP) {
      return this.convocatoriaRequisitoIPListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportRequisitosEquipo(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showRequisitosIP) {
      return this.convocatoriaRequisitoEquipoListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportElegibilidad(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showElegibilidad) {
      return this.convocatoriaConceptoGastoListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportPartidasPresupuestarias(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showPartidasPresupuestarias) {
      return this.convocatoriaPartidaPresupuestariaListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportConfiguracionSolicitudes(
    convocatoriaData: IConvocatoriaReportData,
    reportOptions: IConvocatoriaReportOptions
  ): Observable<IConvocatoriaReportData> {
    if (reportOptions?.showConfiguracionSolicitudes) {
      return this.convocatoriaConfiguracionSolicitudListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportHeader(convocatoriaData: IConvocatoriaReportData,
    output: OutputReport
  ): Observable<IConvocatoriaReportData> {
    if (output === OutputReport.PDF || output === OutputReport.RTF) {
      return this.convocatoriaHeaderListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportFooter(convocatoriaData: IConvocatoriaReportData,
    output: OutputReport
  ): Observable<IConvocatoriaReportData> {
    if (output === OutputReport.PDF || output === OutputReport.RTF) {
      return this.convocatoriaFooterListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  protected getColumns(resultados: IConvocatoriaReportData[], reportConfig: IReportConfig<IConvocatoriaReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];
    columns.push(... this.convocatoriaGeneralListadoExportService.fillColumns(resultados, reportConfig));
    if (reportConfig.outputType === OutputReport.PDF || reportConfig.outputType === OutputReport.RTF) {
      columns.push(... this.convocatoriaHeaderListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showAreasTematicas) {
      columns.push(... this.convocatoriaAreaTematicaListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showEntidadesConvocantes) {
      columns.push(... this.convocatoriaEntidadConvocanteListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showEntidadesFinanciadoras) {
      columns.push(... this.convocatoriaEntidadFinanciadoraListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showEnlaces) {
      columns.push(... this.convocatoriaEnlaceListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showFases) {
      columns.push(... this.convocatoriaFaseListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showCalendarioJustificacion) {
      columns.push(... this.convocatoriaCalendarioJustificacionListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showPeriodosSeguimientoCientifico) {
      columns.push(... this.convocatoriaPeriodoSeguimientoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showHitos) {
      columns.push(... this.convocatoriaHitoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showRequisitosIP) {
      columns.push(... this.convocatoriaRequisitoIPListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showRequisitosEquipo) {
      columns.push(... this.convocatoriaRequisitoEquipoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showElegibilidad) {
      columns.push(... this.convocatoriaConceptoGastoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showPartidasPresupuestarias) {
      columns.push(... this.convocatoriaPartidaPresupuestariaListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showConfiguracionSolicitudes) {
      columns.push(... this.convocatoriaConfiguracionSolicitudListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.outputType === OutputReport.PDF || reportConfig.outputType === OutputReport.RTF) {
      columns.push(... this.convocatoriaFooterListadoExportService.fillColumns(resultados, reportConfig));
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
