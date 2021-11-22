import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { FieldOrientationType } from '@core/models/rep/field-orientation.enum';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { TypeColumnReportEnum } from '@core/models/rep/type-column-report-enum';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestListResult, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of, zip } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { IProyectoListadoData } from './proyecto-listado/proyecto-listado.component';


interface IProyectoReportData extends IProyectoListadoData {
  equipo: IProyectoEquipo[];
  entidadesConvocantes: IProyectoEntidadConvocante[];
  entidadesFinanciadoras: IEntidadFinanciadora[];
}

export interface IProyectoReportOptions extends IReportOptions {
  showMiembrosEquipo: boolean;
  showEntidadesConvocantes: boolean;
}

const TITULO_KEY = marker('csp.proyecto.titulo');
const EQUIPO_KEY = marker('csp.proyecto-equipo');
const INVESTIGADOR_KEY = marker('csp.miembro-equipo-proyecto.miembro');
const ENTIDAD_CONVOCANTE_KEY = 'csp.proyecto-entidad-convocante.programa';
const ENTIDAD_CONVOCANTE_NOMBRE_KEY = 'csp.convocatoria-entidad-convocante.nombre';


const ENTIDAD_CONVOCANTE_CIF_KEY = marker('csp.convocatoria-entidad-convocante.cif');
const ESTADO_KEY = marker('csp.proyecto.estado');
const ACRONIMO_KEY = marker('csp.proyecto.acronimo');
const FECHA_INICIO_KEY = marker('csp.proyecto.fecha-inicio');
const FECHA_FIN_KEY = marker('csp.proyecto.fecha-fin');

export const EQUIPO_FIELD = 'equipo';
export const INVESTIGADOR_FIELD = 'investigador';
export const ENTIDAD_CONVOCANTE_FIELD = 'entidadConvocante';
export const ENTIDAD_CONVOCANTE_CIF_FIELD = 'cifEntidadConvocante';

@Injectable({
  providedIn: 'root',
})
export class ProyectoListadoService extends AbstractTableExportService<IProyectoReportData, IProyectoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private authService: SgiAuthService,
    private readonly proyectoService: ProyectoService,
    protected reportService: ReportService,
    private personaService: PersonaService,
    private empresaService: EmpresaService
  ) {
    super(reportService);
  }

  protected getRows(proyectos: IProyectoReportData[], reportConfig: IReportConfig<IProyectoReportOptions>): Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    proyectos.forEach(proyecto => {
      requestsRow.push(this.getRowsInner(proyecto, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(proyecto: IProyectoReportData, reportConfig: IReportConfig<IProyectoReportOptions>): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {
        row.elements.push(proyecto.titulo);
        return row;
      }),
      switchMap((row) => {
        return this.getEstadoByMap(proyecto.estado?.estado).pipe(
          map(estadoTranslate => {
            row.elements.push(estadoTranslate);
            return row;
          })
        );
      }),
      map((row) => {
        row.elements.push(proyecto.acronimo);
        row.elements.push(LuxonUtils.toBackend(proyecto.fechaInicio));
        row.elements.push(LuxonUtils.toBackend(proyecto.fechaFin));

        if (reportConfig.reportOptions?.showMiembrosEquipo) {
          this.fillRowsEquipo(proyecto, row.elements, reportConfig.reportOptions?.relationsOrientationTable);
        }

        if (reportConfig.reportOptions?.showEntidadesConvocantes) {
          this.fillRowsEntidadesConvocantes(proyecto, row.elements, reportConfig.reportOptions?.relationsOrientationTable);
        }
        return row;
      })
    );
  }

  private getEstadoByMap(estado: Estado): Observable<string> {
    return this.translate.get(ESTADO_MAP.get(estado));
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
          requestsProyecto.push(this.getDataReportInner(proyecto, reportConfig.reportOptions));
        });
        return zip(...requestsProyecto);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(proyectoData: IProyectoReportData, reportOptions: IProyectoReportOptions): Observable<IProyectoReportData> {
    return this.proyectoService.hasProyectoProrrogas(proyectoData.id).pipe(
      map(value => {
        proyectoData.prorrogado = value;
        return proyectoData;
      }),
      switchMap(() => this.proyectoService.findAllProyectosSgeProyecto(proyectoData.id).pipe(
        map(value => {
          proyectoData.proyectosSGE = value.items.map(element => element.proyectoSge.id).join(', ');
          return proyectoData;
        }))
      ),
      switchMap(() => {
        if (reportOptions?.showMiembrosEquipo) {
          return this.getEquipo(proyectoData);
        } else {
          return of(proyectoData);
        }
      }),
      switchMap(() => {
        if (reportOptions?.showEntidadesConvocantes) {
          return this.getEntidadesConvocantes(proyectoData);
        } else {
          return of(proyectoData);
        }
      })
    );
  }

  private getEntidadesConvocantes(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.proyectoService.findAllEntidadConvocantes(proyectoData.id, findOptions).pipe(
      switchMap(responseEntidadesConvocantes => {
        return from(responseEntidadesConvocantes.items).pipe(
          mergeMap(element => {
            return this.loadEmpresa(element);
          }),
          map(() => responseEntidadesConvocantes)
        );
      }),
      map(responseEquipo => {
        proyectoData.entidadesConvocantes = responseEquipo.items;
        return proyectoData;
      }));
  }

  private loadEmpresa(data: IProyectoEntidadConvocante): Observable<IProyectoEntidadConvocante> {
    return this.empresaService.findById(data.entidad.id).pipe(
      map(empresa => {
        data.entidad = empresa;
        return data;
      }),
      catchError((error) => {
        this.logger.error(error);
        return of(data);
      })
    );
  }

  private getEquipo(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.proyectoService.findAllProyectoEquipo(proyectoData.id, findOptions).pipe(
      switchMap(responseEquipo => {
        return from(responseEquipo.items).pipe(
          mergeMap(element => {
            return this.personaService.findById(element.persona.id).pipe(
              map(persona => {
                element.persona = persona;
                return element;
              })
            );
          }),
          map(() => responseEquipo)
        );
      }),
      map(responseEquipo => {
        proyectoData.equipo = responseEquipo.items;
        return proyectoData;
      }));
  }


  private getEquipoColumns(proyectos: IProyectoReportData[], reportConfig: IReportConfig<IProyectoReportOptions>): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    if (reportConfig.reportOptions?.relationsOrientationTable) {
      columns.push({
        name: INVESTIGADOR_FIELD,
        title: this.translate.instant(INVESTIGADOR_KEY),
        type: TypeColumnReportEnum.STRING
      });
    } else {
      const maxNumEquipos = Math.max(...proyectos.map(p => p.equipo.length));
      const titleInvestigador = this.translate.instant(INVESTIGADOR_KEY);
      for (let i = 0; i < maxNumEquipos; i++) {
        const idInvestigador: string = String(i + 1);
        const columnInvestigador: ISgiColumnReport = {
          name: INVESTIGADOR_FIELD + idInvestigador,
          title: titleInvestigador + idInvestigador,
          type: TypeColumnReportEnum.STRING,
        };
        columns.push(columnInvestigador);
      }
    }

    if (reportConfig.reportOptions?.relationsOrientationTable) {
      const columnEquipo: ISgiColumnReport = {
        name: EQUIPO_FIELD,
        title: this.translate.instant(EQUIPO_KEY),
        type: TypeColumnReportEnum.SUBREPORT,
        fieldOrientationType: FieldOrientationType.VERTICAL,
        columns
      };
      return [columnEquipo];
    } else {
      return columns;
    }
  }

  private getEntidadesConvocantesColumns(proyectos: IProyectoReportData[], reportConfig: IReportConfig<IProyectoReportOptions>):
    ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    if (reportConfig.reportOptions?.relationsOrientationTable) {
      columns.push({
        name: 'razonSocial',
        title: this.translate.instant(ENTIDAD_CONVOCANTE_NOMBRE_KEY),
        type: TypeColumnReportEnum.STRING
      });
      columns.push({
        name: 'cif',
        title: this.translate.instant(ENTIDAD_CONVOCANTE_CIF_KEY),
        type: TypeColumnReportEnum.STRING
      });
    } else {
      const maxNumEntidasConvocantes = Math.max(...proyectos.map(p => p.entidadesConvocantes.length));
      const titleEntidadConvocante = this.translate.instant(ENTIDAD_CONVOCANTE_NOMBRE_KEY);
      const titleCifEntidadConvocante = this.translate.instant(ENTIDAD_CONVOCANTE_CIF_KEY);

      for (let i = 0; i < maxNumEntidasConvocantes; i++) {
        const idEntidadConvocante: string = String(i + 1);
        const columnEntidadConvocante: ISgiColumnReport = {
          name: ENTIDAD_CONVOCANTE_FIELD + idEntidadConvocante,
          title: titleEntidadConvocante + idEntidadConvocante,
          type: TypeColumnReportEnum.STRING,
        };
        columns.push(columnEntidadConvocante);
        const columnCifEntidadConvocante: ISgiColumnReport = {
          name: ENTIDAD_CONVOCANTE_CIF_FIELD + idEntidadConvocante,
          title: titleCifEntidadConvocante + idEntidadConvocante,
          type: TypeColumnReportEnum.STRING,
        };
        columns.push(columnCifEntidadConvocante);
      }
    }

    if (reportConfig.reportOptions?.relationsOrientationTable) {
      const columnEquipo: ISgiColumnReport = {
        name: ENTIDAD_CONVOCANTE_FIELD,
        title: this.translate.instant(ENTIDAD_CONVOCANTE_KEY),
        type: TypeColumnReportEnum.SUBREPORT,
        fieldOrientationType: FieldOrientationType.HORIZONTAL,
        columns
      };
      return [columnEquipo];
    } else {
      return columns;
    }
  }

  protected getColumns(resultados: IProyectoReportData[], reportConfig: IReportConfig<IProyectoReportOptions>):
    Observable<ISgiColumnReport[]> {

    const columnsReport: ISgiColumnReport[] = [
      {
        title: this.translate.instant(TITULO_KEY),
        name: 'titulo',
        type: TypeColumnReportEnum.STRING,
      },
      {
        title: this.translate.instant(ESTADO_KEY),
        name: 'estado',
        type: TypeColumnReportEnum.STRING
      },
      {
        title: this.translate.instant(ACRONIMO_KEY),
        name: 'acronimo',
        type: TypeColumnReportEnum.STRING
      },
      {
        title: this.translate.instant(FECHA_INICIO_KEY),
        name: 'fechaInicio',
        type: TypeColumnReportEnum.DATE
      },
      {
        title: this.translate.instant(FECHA_FIN_KEY),
        name: 'fechaFin',
        type: TypeColumnReportEnum.DATE
      }

    ];
    if (reportConfig.reportOptions?.showMiembrosEquipo) {
      columnsReport.push(... this.getEquipoColumns(resultados, reportConfig));
    }
    if (reportConfig.reportOptions?.showEntidadesConvocantes) {
      columnsReport.push(... this.getEntidadesConvocantesColumns(resultados, reportConfig));
    }

    return of(columnsReport);
  }

  private fillRowsEquipo(proyecto: IProyectoReportData, elementsRow: any[], exportAsSubReport: boolean) {
    if (exportAsSubReport) {
      const rowsReport: ISgiRowReport[] = [];

      proyecto.equipo?.forEach(miembroEquipo => {
        const equipoElementsRow: any[] = [];
        equipoElementsRow.push(this.getInvestigador(miembroEquipo));

        const rowReport: ISgiRowReport = {
          elements: equipoElementsRow
        };
        rowsReport.push(rowReport);
      });

      elementsRow.push({
        rows: rowsReport
      });
    } else {
      proyecto.equipo?.forEach(miembroEquipo => {
        elementsRow.push(this.getInvestigador(miembroEquipo));
      });
    }
  }

  private getInvestigador(miembroEquipo: IProyectoEquipo): any {
    return miembroEquipo.persona.nombre + ' ' + miembroEquipo.persona.apellidos;
  }

  private fillRowsEntidadesConvocantes(proyecto: IProyectoReportData, elementsRow: any[], exportAsSubReport: boolean) {
    if (exportAsSubReport) {

      const rowsReport: ISgiRowReport[] = [];

      proyecto.entidadesConvocantes?.forEach(entidadConvocante => {
        const entidadConvocanteElementsRow: any[] = [];
        this.getColumnsEntidad(entidadConvocanteElementsRow, entidadConvocante.entidad);

        const rowReport: ISgiRowReport = {
          elements: entidadConvocanteElementsRow
        };
        rowsReport.push(rowReport);
      });

      elementsRow.push({
        rows: rowsReport
      });
    } else {
      proyecto.entidadesConvocantes?.forEach(entidadConvocante => {
        this.getColumnsEntidad(elementsRow, entidadConvocante.entidad);
      });
    }
  }

  private getColumnsEntidad(elementsRow: any[], entidad: IEmpresa) {
    elementsRow.push(entidad.nombre);
    elementsRow.push(entidad.numeroIdentificacion);
  }

  protected getGroupBy(): ISgiGroupReport {
    const groupBy: ISgiGroupReport = {
      name: 'titulo',
      visible: true
    };
    return groupBy;
  }
}
