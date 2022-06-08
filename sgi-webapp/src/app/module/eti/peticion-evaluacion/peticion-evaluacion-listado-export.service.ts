import { Injectable } from '@angular/core';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IPersona } from '@core/models/sgp/persona';
import { IVinculacion } from '@core/models/sgp/vinculacion';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { SgiRestListResult } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap, takeLast, tap } from 'rxjs/operators';
import { PeticionEvaluacionAsignacionTareasListadoExportService } from './peticion-evaluacion-asignacion-tareas-listado-export.service';
import { PeticionEvaluacionEquipoInvestigadorListadoExportService } from './peticion-evaluacion-equipo-investigador-listado-export.service';
import { PeticionEvaluacionGeneralListadoExportService } from './peticion-evaluacion-general-listado-export.service';
import { PeticionEvaluacionMemoriasListadoExportService } from './peticion-evaluacion-memorias-listado-export.service';

export interface IPeticionEvaluacionMiembroReportData {
  persona: IPersona;
  vinculacion: IVinculacion;
}

export interface IPeticionEvaluacionAsignacionTareaReportData {
  nombre: string;
  apellidos: string;
  memoria: string;
  tarea: string;
  experiencia: string;
}

export interface IPeticionEvaluacionMemoriaReportData {
  referencia: string;
  comite: string;
  estado: string;
  fechaEvaluacion: DateTime;
}

export interface IPeticionEvaluacionReportData extends IPeticionEvaluacion {
  equipoInvestigador: IPeticionEvaluacionMiembroReportData[];
  asignacionTareas: IPeticionEvaluacionAsignacionTareaReportData[];
  memorias: IPeticionEvaluacionMemoriaReportData[];
  codigoSolicitudConvocatoria: string;
}

export interface IPeticionEvaluacionReportOptions extends IReportOptions {
  showEquipoInvestigador: boolean;
  showAsignacionTareas: boolean;
  showMemorias: boolean;
}

@Injectable()
export class PeticionEvaluacionListadoExportService extends
  AbstractTableExportService<IPeticionEvaluacionReportData, IPeticionEvaluacionReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    private authService: SgiAuthService,
    private readonly peticionEvaluacionService: PeticionEvaluacionService,
    private readonly peticionEvaluacionGeneralListadoExportService: PeticionEvaluacionGeneralListadoExportService,
    private readonly peticionEvaluacionEquipoInvestigadorListadoExportService: PeticionEvaluacionEquipoInvestigadorListadoExportService,
    private readonly peticionEvaluacionAsignacionTareasListadoExportService: PeticionEvaluacionAsignacionTareasListadoExportService,
    private readonly peticionEvaluacionMemoriasListadoExportService: PeticionEvaluacionMemoriasListadoExportService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getRows(peticiones: IPeticionEvaluacionReportData[], reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>):
    Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    peticiones.forEach((peticion, index) => {
      requestsRow.push(this.getRowsInner(peticiones, index, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(
    peticiones: IPeticionEvaluacionReportData[],
    index: number,
    reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>
  ): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {

        row.elements.push(...this.peticionEvaluacionGeneralListadoExportService.fillRows(peticiones, index, reportConfig));

        if (reportConfig.reportOptions?.showEquipoInvestigador) {
          row.elements.push(...this.peticionEvaluacionEquipoInvestigadorListadoExportService.fillRows(peticiones, index, reportConfig));
        }

        if (reportConfig.reportOptions?.showAsignacionTareas) {
          row.elements.push(...this.peticionEvaluacionAsignacionTareasListadoExportService.fillRows(peticiones, index, reportConfig));
        }

        if (reportConfig.reportOptions?.showMemorias) {
          row.elements.push(...this.peticionEvaluacionMemoriasListadoExportService.fillRows(peticiones, index, reportConfig));
        }

        return row;
      }), catchError(err => {
        this.logger.error(err);
        return of(err);
      })
    );
  }

  protected getDataReport(reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>): Observable<IPeticionEvaluacionReportData[]> {
    let observable$: Observable<SgiRestListResult<IPeticionEvaluacion>> = null;
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    observable$ = this.peticionEvaluacionService.findAll(findOptions);

    return observable$.pipe(
      map((peticiones) => {
        return peticiones.items.map((peticion) => peticion as IPeticionEvaluacionReportData);
      }),
      switchMap((peticionesReportData) => {
        const requestsPeticion: Observable<IPeticionEvaluacionReportData>[] = [];

        peticionesReportData.forEach(peticion => {
          requestsPeticion.push(this.getDataReportInner(peticion, reportConfig.reportOptions));
        });
        return zip(...requestsPeticion);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(peticionData: IPeticionEvaluacionReportData, reportOptions: IPeticionEvaluacionReportOptions):
    Observable<IPeticionEvaluacionReportData> {
    return merge(
      this.getDataReportListadoGeneral(peticionData),
      this.getDataReportEquipoInvestigador(peticionData, reportOptions),
      this.getDataReportAsignacionTareas(peticionData, reportOptions),
      this.getDataReportMemorias(peticionData, reportOptions)
    ).pipe(
      takeLast(1),
      catchError((err) => {
        this.logger.error(err);
        throw err;
      }));
  }

  private getDataReportListadoGeneral(
    peticionData: IPeticionEvaluacionReportData
  ): Observable<IPeticionEvaluacionReportData> {
    return this.peticionEvaluacionGeneralListadoExportService.getData(peticionData)
      .pipe(tap({ error: (err) => this.logger.error(err) }));
  }

  private getDataReportAsignacionTareas(
    peticionData: IPeticionEvaluacionReportData,
    reportOptions: IPeticionEvaluacionReportOptions
  ): Observable<IPeticionEvaluacionReportData> {
    if (reportOptions?.showAsignacionTareas) {
      return this.peticionEvaluacionAsignacionTareasListadoExportService.getData(peticionData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(peticionData);
    }
  }

  private getDataReportEquipoInvestigador(
    peticionData: IPeticionEvaluacionReportData,
    reportOptions: IPeticionEvaluacionReportOptions
  ): Observable<IPeticionEvaluacionReportData> {
    if (reportOptions?.showEquipoInvestigador) {
      return this.peticionEvaluacionEquipoInvestigadorListadoExportService.getData(peticionData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(peticionData);
    }
  }

  private getDataReportMemorias(
    peticionData: IPeticionEvaluacionReportData,
    reportOptions: IPeticionEvaluacionReportOptions
  ): Observable<IPeticionEvaluacionReportData> {
    if (reportOptions?.showMemorias) {
      return this.peticionEvaluacionMemoriasListadoExportService.getData(peticionData)
        .pipe(tap({ error: (err) => this.logger.error(err) }));
    } else {
      return of(peticionData);
    }
  }

  protected getColumns(resultados: IPeticionEvaluacionReportData[], reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [];

    columns.push(... this.peticionEvaluacionGeneralListadoExportService.fillColumns(resultados, reportConfig));

    if (reportConfig.reportOptions?.showEquipoInvestigador) {
      columns.push(... this.peticionEvaluacionEquipoInvestigadorListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showAsignacionTareas) {
      columns.push(... this.peticionEvaluacionAsignacionTareasListadoExportService.fillColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showMemorias) {
      columns.push(... this.peticionEvaluacionMemoriasListadoExportService.fillColumns(resultados, reportConfig));
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
