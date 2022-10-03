import { Injectable } from '@angular/core';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAreaConocimiento } from '@core/models/csp/proyecto-area-conocimiento';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IProyectoEntidadGestora } from '@core/models/csp/proyecto-entidad-gestora';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { IProyectoPresupuestoTotales } from '@core/models/csp/proyecto-presupuesto-totales';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { IProyectoResponsableEconomico } from '@core/models/csp/proyecto-responsable-economico';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { ISolicitud } from '@core/models/csp/solicitud';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { concat, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { ProyectoAreaConocimientoListadoExportService } from './proyecto-area-conocimiento-listado-export.service';
import { ProyectoCalendarioFacturacionListadoExportService } from './proyecto-calendario-facturacion-listado-export.service';
import { ProyectoCalendarioJustificacionListadoExportService } from './proyecto-calendario-justificacion-listado-export.service';
import { ProyectoClasificacionListadoExportService } from './proyecto-clasificacion-listado-export.service';
import { IProyectoConceptoGastoListadoExport, ProyectoConceptoGastoListadoExportService } from './proyecto-concepto-gasto-listado-export.service';
import { ProyectoConvocatoriaListadoExportService } from './proyecto-convocatoria-listado-export.service';
import { ProyectoEntidadConvocanteListadoExportService } from './proyecto-entidad-convocante-listado-export.service';
import { ProyectoEntidadFinanciadoraListadoExportService } from './proyecto-entidad-financiadora-listado-export.service';
import { ProyectoEntidadGestoraListadoExportService } from './proyecto-entidad-gestora-listado-export.service';
import { ProyectoEquipoListadoExportService } from './proyecto-equipo-listado-export.service';
import { ProyectoFooterListadoExportService } from './proyecto-footer-listado-export.service';
import { IProyectoFacturacionData } from './proyecto-formulario/proyecto-calendario-facturacion/proyecto-calendario-facturacion.fragment';
import { ProyectoClasificacionListado } from './proyecto-formulario/proyecto-clasificaciones/proyecto-clasificaciones.fragment';
import { ProyectoGeneralListadoExportService } from './proyecto-general-listado-export.service';
import { ProyectoHeaderListadoExportService } from './proyecto-header-listado-export.service';
import { IProyectoListadoData } from './proyecto-listado/proyecto-listado.component';
import { ProyectoPartidaPresupuestariaListadoExportService } from './proyecto-partida-presupuestaria-listado-export.service';
import { ProyectoPeriodoSeguimientoListadoExportService } from './proyecto-periodo-seguimiento-listado-export.service';
import { ProyectoPresupuestoListadoExportService } from './proyecto-presupuesto-listado-export.service';
import { ProyectoProrrogaListadoExportService } from './proyecto-prorroga-listado-export.service';
import { ProyectoRelacionListadoExport, ProyectoRelacionListadoExportService } from './proyecto-relacion-listado-export.service';
import { ProyectoResponsableEconomicoListadoExportService } from './proyecto-responsable-economico-listado-export.service';
import { ProyectoSocioListadoExportService } from './proyecto-socio-listado-export.service';
import { ProyectoSolicitudListadoExportService } from './proyecto-solicitud-listado-export.service';

export interface IProyectoReportData extends IProyectoListadoData {
  contextoProyecto?: IProyectoContexto;
  areasConocimiento?: IProyectoAreaConocimiento[];
  clasificaciones?: ProyectoClasificacionListado[];
  relaciones?: ProyectoRelacionListadoExport[];
  entidadGestora?: IProyectoEntidadGestora;
  entidadesConvocantes?: IProyectoEntidadConvocante[];
  entidadesFinanciadoras?: IProyectoEntidadFinanciadora[];
  equipo?: IProyectoEquipo[];
  responsablesEconomicos?: IProyectoResponsableEconomico[];
  socios?: IProyectoSocio[];
  prorrogas?: IProyectoProrroga[];
  convocatoria?: IConvocatoria;
  solicitud?: ISolicitud;
  periodosSeguimientos?: IProyectoPeriodoSeguimiento[];
  conceptosGastos?: IProyectoConceptoGastoListadoExport[];
  partidasPresupuestarias?: IProyectoPartida[];
  presupuesto?: IProyectoPresupuestoTotales;
  calendarioFacturacion?: IProyectoFacturacionData[];
  calendarioJustificacion?: IProyectoPeriodoJustificacion[];
}

export interface IProyectoReportOptions extends IReportOptions {
  showAreasConocimiento: boolean;
  showClasificaciones: boolean;
  showRelaciones: boolean;
  showEntidadGestora: boolean;
  showEntidadesConvocantes: boolean;
  showEntidadesFinanciadoras: boolean;
  showMiembrosEquipo: boolean;
  showResponsablesEconomicos: boolean;
  showSocios: boolean;
  showProrrogas: boolean;
  showConvocatoria: boolean;
  showSolicitud: boolean;
  showSeguimientosCientificos: boolean;
  showElegibilidad: boolean;
  showPartidasPresupuestarias: boolean;
  showPresupuesto: boolean;
  showCalendarioJustificacion: boolean;
  showCalendarioFacturacion: boolean;
}

@Injectable()
export class ProyectoListadoExportService extends AbstractTableExportService<IProyectoReportData, IProyectoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    private authService: SgiAuthService,
    private readonly proyectoService: ProyectoService,
    private readonly proyectoGeneralListadoExportService: ProyectoGeneralListadoExportService,
    private readonly proyectoAreaConocimientoListadoExportService: ProyectoAreaConocimientoListadoExportService,
    private readonly proyectoClasificacionListadoExportService: ProyectoClasificacionListadoExportService,
    private readonly proyectoRelacionListadoExportService: ProyectoRelacionListadoExportService,
    private readonly proyectoEntidadGestoraListadoExportService: ProyectoEntidadGestoraListadoExportService,
    private readonly proyectoEntidadConvocanteListadoExportService: ProyectoEntidadConvocanteListadoExportService,
    private readonly proyectoEntidadFinanciadoraListadoExportService: ProyectoEntidadFinanciadoraListadoExportService,
    private readonly proyectoEquipoListadoExportService: ProyectoEquipoListadoExportService,
    private readonly proyectoResponsableEconomicoListadoExportService: ProyectoResponsableEconomicoListadoExportService,
    private readonly proyectoSocioListadoExportService: ProyectoSocioListadoExportService,
    private readonly proyectoProrrogaListadoExportService: ProyectoProrrogaListadoExportService,
    private readonly proyectoConvocatoriaListadoExportService: ProyectoConvocatoriaListadoExportService,
    private readonly proyectoSolicitudListadoExportService: ProyectoSolicitudListadoExportService,
    private readonly proyectoPeriodoSeguimientoListadoExportService: ProyectoPeriodoSeguimientoListadoExportService,
    private readonly proyectoConceptoGastoListadoExportService: ProyectoConceptoGastoListadoExportService,
    private readonly proyectoPartidaPresupuestariaListadoExportService: ProyectoPartidaPresupuestariaListadoExportService,
    private readonly proyectoPresupuestoListadoExportService: ProyectoPresupuestoListadoExportService,
    private readonly proyectoCalendarioJustificacionListadoExportService: ProyectoCalendarioJustificacionListadoExportService,
    private readonly proyectoCalendarioFacturacionListadoExportService: ProyectoCalendarioFacturacionListadoExportService,
    protected reportService: ReportService,
    private readonly proyectoHeaderListadoExportService: ProyectoHeaderListadoExportService,
    private readonly proyectoFooterListadoExportService: ProyectoFooterListadoExportService
  ) {
    super(reportService);
  }

  protected getRows(proyectos: IProyectoReportData[], reportConfig: IReportConfig<IProyectoReportOptions>): Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    proyectos.forEach((proyecto, index) => {
      requestsRow.push(this.getRowsInner(proyectos, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    proyectos: IProyectoReportData[],
    index: number,
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {
        row.elements.push(...this.proyectoGeneralListadoExportService.fillRows(proyectos, index, reportConfig));
        if (reportConfig.outputType === OutputReport.PDF || reportConfig.outputType === OutputReport.RTF) {
          row.elements.push(...this.proyectoHeaderListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showAreasConocimiento) {
          row.elements.push(...this.proyectoAreaConocimientoListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showClasificaciones) {
          row.elements.push(...this.proyectoClasificacionListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showRelaciones) {
          row.elements.push(...this.proyectoRelacionListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showEntidadGestora) {
          row.elements.push(...this.proyectoEntidadGestoraListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showEntidadesConvocantes) {
          row.elements.push(...this.proyectoEntidadConvocanteListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showEntidadesFinanciadoras) {
          row.elements.push(...this.proyectoEntidadFinanciadoraListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showMiembrosEquipo) {
          row.elements.push(...this.proyectoEquipoListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showResponsablesEconomicos) {
          row.elements.push(...this.proyectoResponsableEconomicoListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showSocios) {
          row.elements.push(...this.proyectoSocioListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showProrrogas) {
          row.elements.push(...this.proyectoProrrogaListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showConvocatoria) {
          row.elements.push(...this.proyectoConvocatoriaListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showSolicitud) {
          row.elements.push(...this.proyectoSolicitudListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showSeguimientosCientificos) {
          row.elements.push(...this.proyectoPeriodoSeguimientoListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showElegibilidad) {
          row.elements.push(...this.proyectoConceptoGastoListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showPartidasPresupuestarias) {
          row.elements.push(...this.proyectoPartidaPresupuestariaListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showPresupuesto) {
          row.elements.push(...this.proyectoPresupuestoListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showCalendarioJustificacion) {
          row.elements.push(...this.proyectoCalendarioJustificacionListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showCalendarioFacturacion) {
          row.elements.push(...this.proyectoCalendarioFacturacionListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        if (reportConfig.outputType === OutputReport.PDF || reportConfig.outputType === OutputReport.RTF) {
          row.elements.push(...this.proyectoFooterListadoExportService.fillRows(proyectos, index, reportConfig));
        }
        return row;
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<IProyectoReportOptions>): Observable<IProyectoReportData[]> {
    let observable$: Observable<SgiRestListResult<IProyecto>> = null;
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    if (this.authService.hasAuthorityForAnyUO('CSP-PRO-R')) {
      observable$ = this.proyectoService.findTodos(findOptions);
    } else {
      observable$ = this.proyectoService.findAll(findOptions);
    }

    return observable$.pipe(
      map((proyectos) => {
        return proyectos.items.map((pr) => pr as IProyectoReportData);
      }),
      switchMap((proyectosReportData) => {
        const requestsProyecto: Observable<IProyectoReportData>[] = [];

        proyectosReportData.forEach(proyecto => {
          requestsProyecto.push(this.getDataReportInner(proyecto, reportConfig.reportOptions, reportConfig.outputType));
        });
        return zip(...requestsProyecto);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(proyectoData: IProyectoReportData, reportOptions: IProyectoReportOptions, output: OutputReport): Observable<IProyectoReportData> {
    return concat(
      this.getDataReportListadoGeneral(proyectoData),
      this.getDataReportHeader(proyectoData, output),
      this.getDataReportAreasConocimiento(proyectoData, reportOptions),
      this.getDataReportClasificaciones(proyectoData, reportOptions),
      this.getDataReportRelaciones(proyectoData, reportOptions),
      this.getDataReportEntidadGestora(proyectoData, reportOptions),
      this.getDataReportEntidadesConvocantes(proyectoData, reportOptions),
      this.getDataReportEntidadesFinanciadoras(proyectoData, reportOptions),
      this.getDataReportEquipo(proyectoData, reportOptions),
      this.getDataReportResponsablesEconomicos(proyectoData, reportOptions),
      this.getDataReportSocios(proyectoData, reportOptions),
      this.getDataReportProrrogas(proyectoData, reportOptions),
      this.getDataReportConvocatoria(proyectoData, reportOptions),
      this.getDataReportSolicitud(proyectoData, reportOptions),
      this.getDataReportSeguimientosCientificos(proyectoData, reportOptions),
      this.getDataReportConceptoGasto(proyectoData, reportOptions),
      this.getDataReportPartidaPresupuestaria(proyectoData, reportOptions),
      this.getDataReportPresupuesto(proyectoData, reportOptions),
      this.getDataReportCalendarioJustificacion(proyectoData, reportOptions),
      this.getDataReportCalendarioFacturacion(proyectoData, reportOptions),
      this.getDataReportFooter(proyectoData, output)
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    proyectoData: IProyectoReportData
  ): Observable<IProyectoReportData> {
    return this.proyectoGeneralListadoExportService.getData(proyectoData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  private getDataReportAreasConocimiento(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showAreasConocimiento) {
      return this.proyectoAreaConocimientoListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportClasificaciones(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showClasificaciones) {
      return this.proyectoClasificacionListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportRelaciones(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showRelaciones) {
      return this.proyectoRelacionListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportEntidadGestora(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showEntidadGestora) {
      return this.proyectoEntidadGestoraListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportEntidadesConvocantes(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showEntidadesConvocantes) {
      return this.proyectoEntidadConvocanteListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportEntidadesFinanciadoras(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showEntidadesFinanciadoras) {
      return this.proyectoEntidadFinanciadoraListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportEquipo(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showMiembrosEquipo) {
      return this.proyectoEquipoListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportResponsablesEconomicos(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showResponsablesEconomicos) {
      return this.proyectoResponsableEconomicoListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportSocios(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showSocios) {
      return this.proyectoSocioListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportProrrogas(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showProrrogas) {
      return this.proyectoProrrogaListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportConvocatoria(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showConvocatoria) {
      return this.proyectoConvocatoriaListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportSolicitud(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showSolicitud) {
      return this.proyectoSolicitudListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportSeguimientosCientificos(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showSeguimientosCientificos) {
      proyectoData.periodosSeguimientos = [];
      return this.proyectoPeriodoSeguimientoListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportConceptoGasto(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showElegibilidad) {
      proyectoData.conceptosGastos = [];
      return this.proyectoConceptoGastoListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportPartidaPresupuestaria(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showPartidasPresupuestarias) {
      proyectoData.partidasPresupuestarias = [];
      return this.proyectoPartidaPresupuestariaListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportPresupuesto(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showPresupuesto) {
      return this.proyectoPresupuestoListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportCalendarioJustificacion(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showCalendarioJustificacion) {
      proyectoData.calendarioJustificacion = [];
      return this.proyectoCalendarioJustificacionListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportCalendarioFacturacion(
    proyectoData: IProyectoReportData,
    reportOptions: IProyectoReportOptions
  ): Observable<IProyectoReportData> {
    if (reportOptions?.showCalendarioFacturacion) {
      proyectoData.calendarioFacturacion = [];
      return this.proyectoCalendarioFacturacionListadoExportService.getData(proyectoData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(proyectoData);
    }
  }

  private getDataReportHeader(convocatoriaData: IProyectoReportData,
    output: OutputReport
  ): Observable<IProyectoReportData> {
    if (output === OutputReport.PDF || output === OutputReport.RTF) {
      return this.proyectoHeaderListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  private getDataReportFooter(convocatoriaData: IProyectoReportData,
    output: OutputReport
  ): Observable<IProyectoReportData> {
    if (output === OutputReport.PDF || output === OutputReport.RTF) {
      return this.proyectoFooterListadoExportService.getData(convocatoriaData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(convocatoriaData);
    }
  }

  protected getColumns(resultados: IProyectoReportData[], reportConfig: IReportConfig<IProyectoReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.proyectoGeneralListadoExportService.fillColumns(resultados, reportConfig));

    if (reportConfig.outputType === OutputReport.PDF || reportConfig.outputType === OutputReport.RTF) {
      columns.push(... this.proyectoHeaderListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showAreasConocimiento) {
      columns.push(... this.proyectoAreaConocimientoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showClasificaciones) {
      columns.push(... this.proyectoClasificacionListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showRelaciones) {
      columns.push(... this.proyectoRelacionListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showEntidadGestora) {
      columns.push(... this.proyectoEntidadGestoraListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showEntidadesConvocantes) {
      columns.push(... this.proyectoEntidadConvocanteListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showEntidadesFinanciadoras) {
      columns.push(... this.proyectoEntidadFinanciadoraListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showMiembrosEquipo) {
      columns.push(... this.proyectoEquipoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showResponsablesEconomicos) {
      columns.push(... this.proyectoResponsableEconomicoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showSocios) {
      columns.push(... this.proyectoSocioListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showProrrogas) {
      columns.push(... this.proyectoProrrogaListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showConvocatoria) {
      columns.push(... this.proyectoConvocatoriaListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showSolicitud) {
      columns.push(... this.proyectoSolicitudListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showSeguimientosCientificos) {
      columns.push(... this.proyectoPeriodoSeguimientoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showElegibilidad) {
      columns.push(... this.proyectoConceptoGastoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showPartidasPresupuestarias) {
      columns.push(... this.proyectoPartidaPresupuestariaListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showPresupuesto) {
      columns.push(...this.proyectoPresupuestoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showCalendarioJustificacion) {
      columns.push(... this.proyectoCalendarioJustificacionListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showCalendarioFacturacion) {
      columns.push(... this.proyectoCalendarioFacturacionListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.outputType === OutputReport.PDF || reportConfig.outputType === OutputReport.RTF) {
      columns.push(... this.proyectoFooterListadoExportService.fillColumns(resultados, reportConfig));
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
