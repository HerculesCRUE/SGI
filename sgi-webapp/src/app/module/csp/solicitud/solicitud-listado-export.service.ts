import { Injectable } from '@angular/core';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { ISolicitudProyectoAreaConocimiento } from '@core/models/csp/solicitud-proyecto-area-conocimiento';
import { ISolicitudProyectoEntidadFinanciadoraAjena } from '@core/models/csp/solicitud-proyecto-entidad-financiadora-ajena';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { ISolicitudProyectoResponsableEconomico } from '@core/models/csp/solicitud-proyecto-responsable-economico';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { ISolicitudRrhh } from '@core/models/csp/solicitud-rrhh';
import { ISolicitudRrhhMemoria } from '@core/models/csp/solicitud-rrhh-memoria';
import { ISolicitudRrhhRequisitoCategoria } from '@core/models/csp/solicitud-rrhh-requisito-categoria';
import { ISolicitudRrhhRequisitoNivelAcademico } from '@core/models/csp/solicitud-rrhh-requisito-nivel-academico';
import { ISolicitudRrhhTutor } from '@core/models/csp/solicitud-rrhh-tutor';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { concat, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { SolicitudEntidadConvocanteListadoExportService } from './solicitud-entidad-convocante-listado-export.service';
import { SolicitudDatosGenerales } from './solicitud-formulario/solicitud-datos-generales/solicitud-datos-generales.fragment';
import { SolicitudProyectoClasificacionListado } from './solicitud-formulario/solicitud-proyecto-clasificaciones/solicitud-proyecto-clasificaciones.fragment';
import { SolicitudGeneralListadoExportService } from './solicitud-general-listado-export.service';
import { ISolicitudListadoData } from './solicitud-listado/solicitud-listado.component';
import { SolicitudProyectoAreaConocimientoListadoExportService } from './solicitud-proyecto-area-conocimiento-listado-export.service';
import { SolicitudProyectoClasificacionListadoExportService } from './solicitud-proyecto-clasificacion-listado-export.service';
import { SolicitudProyectoEntidadFinanciadoraListadoExportService } from './solicitud-proyecto-entidad-financiadora-listado-export.service';
import { SolicitudProyectoEquipoListadoExportService } from './solicitud-proyecto-equipo-listado-export.service';
import { SolicitudProyectoFichaGeneralListadoExportService } from './solicitud-proyecto-ficha-general-listado-export.service';
import { SolicitudProyectoResponsableEconomicoListadoExportService } from './solicitud-proyecto-responsable-economico-listado-export.service';
import { SolicitudProyectoSocioListadoExportService } from './solicitud-proyecto-socio-listado-export.service';
import { SolicitudRrhhListadoExportService } from './solicitud-rrhh-listado-export.service';


export interface ISolicitudReportData extends ISolicitudListadoData {
  solicitud?: SolicitudDatosGenerales;
  entidadesConvocantes?: IConvocatoriaEntidadConvocante[];
  proyecto?: ISolicitudProyecto;
  contextoProyecto?: IProyectoContexto;
  areasConocimiento?: ISolicitudProyectoAreaConocimiento[];
  clasificaciones?: SolicitudProyectoClasificacionListado[];
  equipo?: ISolicitudProyectoEquipo[];
  responsablesEconomicos?: ISolicitudProyectoResponsableEconomico[];
  socios?: ISolicitudProyectoSocio[];
  entidadesFinanciadoras?: ISolicitudProyectoEntidadFinanciadoraAjena[];
  entidadesFinanciadorasConvocatoria?: IConvocatoriaEntidadFinanciadora[];
  modalidades?: ISolicitudModalidad[];
  solicitudRrhh?: ISolicitudRrhh;
  solicitudRrhhTutor: ISolicitudRrhhTutor;
  solicitudRrhhMemoria: ISolicitudRrhhMemoria;
  requisitosSolicitanteNivelAcademico: IRequisitoIPNivelAcademico[];
  requisitosSolicitanteCategoriaProfesional: IRequisitoIPCategoriaProfesional[];
  requisitosTutorNivelAcademico: IRequisitoEquipoNivelAcademico[];
  requisitosTutorCategoriaProfesional: IRequisitoEquipoCategoriaProfesional[];
  requisitosAcreditadosNivelAcademico: ISolicitudRrhhRequisitoNivelAcademico[];
  requisitosAcreditadosCategoriaProfesional: ISolicitudRrhhRequisitoCategoria[];
}

export interface ISolicitudReportOptions extends IReportOptions {
  showSolicitudEntidadesConvocantes: boolean;
  showSolicitudProyectoFichaGeneral: boolean;
  showSolicitudProyectoAreasConocimiento: boolean;
  showSolicitudProyectoClasificaciones: boolean;
  showSolicitudProyectoEquipo: boolean;
  showSolicitudProyectoResponsableEconomico: boolean;
  showSolicitudProyectoSocios: boolean;
  showSolicitudProyectoEntidadesFinanciadoras: boolean;
  showSolicitudRrhh: boolean;
}

@Injectable()
export class SolicitudListadoExportService extends AbstractTableExportService<ISolicitudReportData, IReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
    protected reportService: ReportService,
    private readonly solicitudGeneralListadoExportService: SolicitudGeneralListadoExportService,
    private readonly solicitudEntidadConvocanteListadoExportService: SolicitudEntidadConvocanteListadoExportService,
    private readonly solicitudProyectoFichaGeneralListadoExportService: SolicitudProyectoFichaGeneralListadoExportService,
    private readonly solicitudProyectoAreaConocimientoListadoExportService: SolicitudProyectoAreaConocimientoListadoExportService,
    private readonly solicitudProyectoClasificacionListadoExportService: SolicitudProyectoClasificacionListadoExportService,
    private readonly solicitudProyectoEquipoListadoExportService: SolicitudProyectoEquipoListadoExportService,
    private readonly solicitudProyectoResponsableEconomicoListadoExportService: SolicitudProyectoResponsableEconomicoListadoExportService,
    private readonly solicitudProyectoSocioListadoExportService: SolicitudProyectoSocioListadoExportService,
    private readonly solicitudProyectoEntidadFinanciadoraListadoExportService: SolicitudProyectoEntidadFinanciadoraListadoExportService,
    private readonly solicitudRrhhListadoExportService: SolicitudRrhhListadoExportService
  ) {
    super(reportService);
  }

  protected getRows(solicitudes: ISolicitudReportData[], reportConfig: IReportConfig<ISolicitudReportOptions>): Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    solicitudes.forEach((solicitud, index) => {
      requestsRow.push(this.getRowsInner(solicitudes, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    solicitudes: ISolicitudReportData[],
    index: number,
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {
        row.elements.push(...this.solicitudGeneralListadoExportService.fillRows(solicitudes, index, reportConfig));
        if (reportConfig.reportOptions?.showSolicitudEntidadesConvocantes) {
          row.elements.push(...this.solicitudEntidadConvocanteListadoExportService.fillRows(solicitudes, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showSolicitudProyectoFichaGeneral) {
          row.elements.push(...this.solicitudProyectoFichaGeneralListadoExportService.fillRows(solicitudes, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showSolicitudProyectoAreasConocimiento) {
          row.elements.push(...this.solicitudProyectoAreaConocimientoListadoExportService.fillRows(solicitudes, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showSolicitudProyectoClasificaciones) {
          row.elements.push(...this.solicitudProyectoClasificacionListadoExportService.fillRows(solicitudes, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showSolicitudProyectoEquipo) {
          row.elements.push(...this.solicitudProyectoEquipoListadoExportService.fillRows(solicitudes, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showSolicitudProyectoResponsableEconomico) {
          row.elements.push(...this.solicitudProyectoResponsableEconomicoListadoExportService.fillRows(solicitudes, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showSolicitudProyectoSocios) {
          row.elements.push(...this.solicitudProyectoSocioListadoExportService.fillRows(solicitudes, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showSolicitudProyectoEntidadesFinanciadoras) {
          row.elements.push(...this.solicitudProyectoEntidadFinanciadoraListadoExportService.fillRows(solicitudes, index, reportConfig));
        }
        if (reportConfig.reportOptions?.showSolicitudRrhh) {
          row.elements.push(...this.solicitudRrhhListadoExportService.fillRows(solicitudes, index, reportConfig));
        }
        return row;
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<ISolicitudReportOptions>): Observable<ISolicitudReportData[]> {
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    return this.solicitudService.findAllTodos(findOptions).pipe(
      map((solicitudes) => {
        return solicitudes.items.map((solicitud) => {
          return solicitud as ISolicitudReportData;
        });
      }),
      switchMap((solicitudesReportData) => {
        if (solicitudesReportData.length === 0) {
          return of(solicitudesReportData);
        }

        const requestsSolicitud: Observable<ISolicitudReportData>[] = [];

        solicitudesReportData.forEach(solicitud => {
          requestsSolicitud.push(this.getDataReportInner(solicitud, reportConfig.reportOptions));
        });
        return zip(...requestsSolicitud);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(solicitudData: ISolicitudReportData, reportOptions: ISolicitudReportOptions): Observable<ISolicitudReportData> {
    return concat(
      this.getDataReportListadoGeneral(solicitudData),
      this.getDataReportEntidadesConvocantes(solicitudData, reportOptions),
      this.getDataReportSolicitudProyectoFichaGeneral(solicitudData, reportOptions),
      this.getDataReportSolicitudProyectoAreasConocimiento(solicitudData, reportOptions),
      this.getDataReportSolicitudProyectoClasificaciones(solicitudData, reportOptions),
      this.getDataReportSolicitudProyectoEquipo(solicitudData, reportOptions),
      this.getDataReportSolicitudProyectoResponsableEconomico(solicitudData, reportOptions),
      this.getDataReportSolicitudProyectoSocio(solicitudData, reportOptions),
      this.getDataReportSolicitudProyectoEntidadFinanciadora(solicitudData, reportOptions),
      this.getDataReportSolicitudProyectoEntidadFinanciadoraConv(solicitudData, reportOptions),
      this.getDataReportSolicitudRrhh(solicitudData, reportOptions)
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    solicitudData: ISolicitudReportData
  ): Observable<ISolicitudReportData> {
    return this.solicitudGeneralListadoExportService.getData(solicitudData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  private getDataReportEntidadesConvocantes(
    solicitudData: ISolicitudReportData,
    reportOptions: ISolicitudReportOptions
  ): Observable<ISolicitudReportData> {
    if (reportOptions?.showSolicitudEntidadesConvocantes) {
      return this.solicitudEntidadConvocanteListadoExportService.getData(solicitudData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(solicitudData);
    }
  }

  private getDataReportSolicitudProyectoFichaGeneral(
    solicitudData: ISolicitudReportData,
    reportOptions: ISolicitudReportOptions
  ): Observable<ISolicitudReportData> {
    if (reportOptions?.showSolicitudProyectoFichaGeneral) {
      return this.solicitudProyectoFichaGeneralListadoExportService.getData(solicitudData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(solicitudData);
    }
  }

  private getDataReportSolicitudProyectoAreasConocimiento(
    solicitudData: ISolicitudReportData,
    reportOptions: ISolicitudReportOptions
  ): Observable<ISolicitudReportData> {
    if (reportOptions?.showSolicitudProyectoAreasConocimiento) {
      return this.solicitudProyectoAreaConocimientoListadoExportService.getData(solicitudData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(solicitudData);
    }
  }

  private getDataReportSolicitudProyectoClasificaciones(
    solicitudData: ISolicitudReportData,
    reportOptions: ISolicitudReportOptions
  ): Observable<ISolicitudReportData> {
    if (reportOptions?.showSolicitudProyectoClasificaciones) {
      return this.solicitudProyectoClasificacionListadoExportService.getData(solicitudData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(solicitudData);
    }
  }

  private getDataReportSolicitudProyectoEquipo(
    solicitudData: ISolicitudReportData,
    reportOptions: ISolicitudReportOptions
  ): Observable<ISolicitudReportData> {
    if (reportOptions?.showSolicitudProyectoEquipo) {
      return this.solicitudProyectoEquipoListadoExportService.getData(solicitudData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(solicitudData);
    }
  }

  private getDataReportSolicitudProyectoResponsableEconomico(
    solicitudData: ISolicitudReportData,
    reportOptions: ISolicitudReportOptions
  ): Observable<ISolicitudReportData> {
    if (reportOptions?.showSolicitudProyectoResponsableEconomico) {
      return this.solicitudProyectoResponsableEconomicoListadoExportService.getData(solicitudData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(solicitudData);
    }
  }

  private getDataReportSolicitudProyectoSocio(
    solicitudData: ISolicitudReportData,
    reportOptions: ISolicitudReportOptions
  ): Observable<ISolicitudReportData> {
    if (reportOptions?.showSolicitudProyectoSocios) {
      return this.solicitudProyectoSocioListadoExportService.getData(solicitudData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(solicitudData);
    }
  }

  private getDataReportSolicitudProyectoEntidadFinanciadora(
    solicitudData: ISolicitudReportData,
    reportOptions: ISolicitudReportOptions
  ): Observable<ISolicitudReportData> {
    if (reportOptions?.showSolicitudProyectoEntidadesFinanciadoras) {
      return this.solicitudProyectoEntidadFinanciadoraListadoExportService.getData(solicitudData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(solicitudData);
    }
  }

  private getDataReportSolicitudProyectoEntidadFinanciadoraConv(
    solicitudData: ISolicitudReportData,
    reportOptions: ISolicitudReportOptions
  ): Observable<ISolicitudReportData> {
    if (reportOptions?.showSolicitudProyectoEntidadesFinanciadoras) {
      return this.solicitudProyectoEntidadFinanciadoraListadoExportService.getDataConv(solicitudData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(solicitudData);
    }
  }

  private getDataReportSolicitudRrhh(
    solicitudData: ISolicitudReportData,
    reportOptions: ISolicitudReportOptions
  ): Observable<ISolicitudReportData> {
    if (reportOptions?.showSolicitudRrhh) {
      return this.solicitudRrhhListadoExportService.getData(solicitudData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(solicitudData);
    }
  }

  protected getColumns(resultados: ISolicitudReportData[], reportConfig: IReportConfig<ISolicitudReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.solicitudGeneralListadoExportService.fillColumns(resultados, reportConfig));

    if (reportConfig.reportOptions?.showSolicitudEntidadesConvocantes) {
      columns.push(... this.solicitudEntidadConvocanteListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showSolicitudProyectoFichaGeneral) {
      columns.push(... this.solicitudProyectoFichaGeneralListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showSolicitudProyectoAreasConocimiento) {
      columns.push(... this.solicitudProyectoAreaConocimientoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showSolicitudProyectoClasificaciones) {
      columns.push(... this.solicitudProyectoClasificacionListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showSolicitudProyectoEquipo) {
      columns.push(... this.solicitudProyectoEquipoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showSolicitudProyectoResponsableEconomico) {
      columns.push(... this.solicitudProyectoResponsableEconomicoListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showSolicitudProyectoSocios) {
      columns.push(... this.solicitudProyectoSocioListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showSolicitudProyectoEntidadesFinanciadoras) {
      columns.push(... this.solicitudProyectoEntidadFinanciadoraListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showSolicitudRrhh) {
      columns.push(... this.solicitudRrhhListadoExportService.fillColumns(resultados, reportConfig));
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
