import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IPersona } from '@core/models/sgp/persona';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { LanguageService } from '@core/services/language.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const EQUIPO_KEY = marker('csp.proyecto-equipo');
const INVESTIGADOR_KEY = marker('csp.miembro-equipo-proyecto.miembro');
const INVESTIGADOR_NOMBRE_KEY = marker('csp.proyecto-equipo.nombre');
const INVESTIGADOR_APELLIDOS_KEY = marker('csp.proyecto-equipo.apellidos');
const INVESTIGADOR_EMAIL_KEY = marker('csp.proyecto-socio.equipo.email');
const INVESTIGADOR_ROL_KEY = marker('csp.proyecto-socio.equipo.rol-proyecto');
const INVESTIGADOR_FECHA_INICIO_PARTICIPACION_KEY = marker('csp.proyecto-socio.equipo.fecha-inicio-participacion');
const INVESTIGADOR_FECHA_FIN_PARTICIPACION_KEY = marker('csp.proyecto-socio.equipo.fecha-fin-participacion');

const INVESTIGADOR_NOMBRE_FIELD = 'nombreInvestigador';
const INVESTIGADOR_APELLIDOS_FIELD = 'apellidosInvestigador';
const INVESTIGADOR_EMAIL_FIELD = 'emailInvestigador';
const INVESTIGADOR_ROL_FIELD = 'rolInvestigador';
const INVESTIGADOR_FECHA_INICIO_FIELD = 'fechaInicioInvestigador';
const INVESTIGADOR_FECHA_FIN_FIELD = 'fechaFinInvestigador';

@Injectable()
export class ProyectoEquipoListadoExportService extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private personaService: PersonaService,
    private readonly languageService: LanguageService
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.proyectoService.findAllProyectoEquipo(proyectoData.id, findOptions).pipe(
      map((responseEquipo) => {
        proyectoData.equipo = [];
        return responseEquipo;
      }),
      switchMap(responseEquipo => {
        if (responseEquipo.total === 0) {
          return of(proyectoData);
        }
        const miembrosEquipo = responseEquipo.items;

        const miembrosEquipoIds = new Set<string>(responseEquipo.items.map((miembroEquipo) => miembroEquipo.persona.id));
        return this.personaService.findAllByIdIn([...miembrosEquipoIds]).pipe(
          map((result) => {
            const personas = result.items;

            miembrosEquipo.forEach((miembroEquipo) => {
              miembroEquipo.persona = personas.find((persona) => miembroEquipo.persona.id === persona.id);
            });

            proyectoData.equipo = miembrosEquipo;
            return proyectoData;

          })
        );
      })
    );
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsInvestigadorExcel(proyectos);
    }
  }

  private getColumnsInvestigadorExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEquipos = Math.max(...proyectos.map(p => p.equipo?.length));
    const titleInvestigador = this.translate.instant(EQUIPO_KEY) + ' - ' + this.translate.instant(INVESTIGADOR_KEY);
    for (let i = 0; i < maxNumEquipos; i++) {
      const idInvestigador: string = String(i + 1);
      const columnNombreInvestigador: ISgiColumnReport = {
        name: INVESTIGADOR_NOMBRE_FIELD + idInvestigador,
        title: titleInvestigador + idInvestigador + ': ' + this.translate.instant(INVESTIGADOR_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombreInvestigador);
      const columnApellidosInvestigador: ISgiColumnReport = {
        name: INVESTIGADOR_APELLIDOS_FIELD + idInvestigador,
        title: titleInvestigador + idInvestigador + ': ' + this.translate.instant(INVESTIGADOR_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnApellidosInvestigador);
      const columnEmailInvestigador: ISgiColumnReport = {
        name: INVESTIGADOR_EMAIL_FIELD + idInvestigador,
        title: titleInvestigador + idInvestigador + ': ' + this.translate.instant(INVESTIGADOR_EMAIL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEmailInvestigador);
      const columnRolInvestigador: ISgiColumnReport = {
        name: INVESTIGADOR_ROL_FIELD + idInvestigador,
        title: titleInvestigador + idInvestigador + ': ' + this.translate.instant(INVESTIGADOR_ROL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnRolInvestigador);
      const columnFechaInicioInvestigador: ISgiColumnReport = {
        name: INVESTIGADOR_FECHA_INICIO_FIELD + idInvestigador,
        title: titleInvestigador + idInvestigador + ': ' + this.translate.instant(INVESTIGADOR_FECHA_INICIO_PARTICIPACION_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaInicioInvestigador);
      const columnFechaFinInvestigador: ISgiColumnReport = {
        name: INVESTIGADOR_FECHA_FIN_FIELD + idInvestigador,
        title: titleInvestigador + idInvestigador + ': ' + this.translate.instant(INVESTIGADOR_FECHA_FIN_PARTICIPACION_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinInvestigador);
    }
    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = proyectos[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumEquipo = Math.max(...proyectos.map(p => p.equipo?.length));
      for (let i = 0; i < maxNumEquipo; i++) {
        const equipo = proyecto.equipo[i] ?? null;
        this.fillRowsInvestigadorExcel(elementsRow, equipo);
      }
    }
    return elementsRow;
  }

  private fillRowsInvestigadorExcel(elementsRow: any[], miembroEquipo: IProyectoEquipo) {
    if (miembroEquipo) {
      elementsRow.push(miembroEquipo.persona?.nombre ?? '');
      elementsRow.push(miembroEquipo.persona?.apellidos ?? '');
      elementsRow.push(this.getEmailPrincipal(miembroEquipo.persona));
      elementsRow.push(this.languageService.getFieldValue(miembroEquipo.rolProyecto?.nombre));
      elementsRow.push(LuxonUtils.toBackend(miembroEquipo.fechaInicio) ?? '');
      elementsRow.push(LuxonUtils.toBackend(miembroEquipo.fechaFin) ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private getEmailPrincipal(persona: IPersona): string {
    if (!persona?.emails) {
      return '';
    }
    const emailDataPrincipal = persona.emails.find(emailData => emailData.principal);
    return emailDataPrincipal?.email ?? '';
  }
}
