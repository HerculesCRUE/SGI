import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IPersona } from '@core/models/sgp/persona';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IGrupoReportData, IGrupoReportOptions } from '../grupo/grupo-listado-export.service';

const EQUIPO_KEY = marker('csp.grupo-equipo');
const MIEMBRO_KEY = marker('csp.grupo-equipo.miembro');
const EQUIPO_NOMBRE_KEY = marker('csp.grupo-equipo.nombre');
const EQUIPO_APELLIDOS_KEY = marker('csp.grupo-equipo.apellidos');
const EQUIPO_EMAIL_KEY = marker('csp.grupo-equipo.email');
const EQUIPO_ROL_KEY = marker('csp.grupo-equipo.rol');
const EQUIPO_FECHA_INICIO_KEY = marker('csp.grupo-equipo.fecha-inicio');
const EQUIPO_FECHA_FIN_KEY = marker('csp.grupo-equipo.fecha-fin');
const EQUIPO_PARTICIPACION_KEY = marker('csp.grupo-equipo.participacion');

const EQUIPO_NOMBRE_FIELD = 'nombreEquipo';
const EQUIPO_APELLIDOS_FIELD = 'apellidosEquipo';
const EQUIPO_EMAIL_FIELD = 'emailEquipo';
const EQUIPO_ROL_FIELD = 'rolEquipo';
const EQUIPO_FECHA_INICIO_FIELD = 'fechaInicioEquipo';
const EQUIPO_FECHA_FIN_FIELD = 'fechaFinEquipo';
const EQUIPO_PARTICIPACION_FIELD = 'participacion';

@Injectable()
export class GrupoEquipoListadoExportService extends AbstractTableExportFillService<IGrupoReportData, IGrupoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly grupoService: GrupoService,
    private personaService: PersonaService,
  ) {
    super(translate);
  }

  public getData(grupoData: IGrupoReportData): Observable<IGrupoReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.grupoService.findMiembrosEquipo(grupoData.id, findOptions).pipe(
      map((responseEquipo) => {
        grupoData.equiposInvestigacion = [];
        return responseEquipo;
      }),
      switchMap(responseEquipo => {
        if (responseEquipo.total === 0) {
          return of(grupoData);
        }
        const miembrosEquipo = responseEquipo.items;

        const miembrosEquipoIds = new Set<string>(responseEquipo.items.map((miembroEquipo) => miembroEquipo.persona.id));
        if (!miembrosEquipoIds?.size) {
          return of(grupoData);
        }

        return this.personaService.findAllByIdIn([...miembrosEquipoIds]).pipe(
          map((result) => {
            const personas = result.items;

            miembrosEquipo.forEach((miembroEquipo) => {
              miembroEquipo.persona = personas.find((persona) => miembroEquipo.persona.id === persona.id);
            });

            grupoData.equiposInvestigacion = miembrosEquipo;
            return grupoData;

          })
        );
      })
    );
  }

  public fillColumns(
    grupos: IGrupoReportData[],
    reportConfig: IReportConfig<IGrupoReportOptions>
  ): ISgiColumnReport[] {

    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsEquipoExcel(grupos);
    }
  }

  private getColumnsEquipoExcel(grupos: IGrupoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEquipos = Math.max(...grupos.map(g => g.equiposInvestigacion?.length));
    const titleEquipo = this.translate.instant(EQUIPO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) + ' - ' + this.translate.instant(MIEMBRO_KEY);
    for (let i = 0; i < maxNumEquipos; i++) {
      const idEquipo: string = String(i + 1);
      const columnNombreEquipo: ISgiColumnReport = {
        name: EQUIPO_NOMBRE_FIELD + idEquipo,
        title: titleEquipo + idEquipo + ': ' + this.translate.instant(EQUIPO_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombreEquipo);
      const columnApellidosEquipo: ISgiColumnReport = {
        name: EQUIPO_APELLIDOS_FIELD + idEquipo,
        title: titleEquipo + idEquipo + ': ' + this.translate.instant(EQUIPO_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnApellidosEquipo);
      const columnEmailEquipo: ISgiColumnReport = {
        name: EQUIPO_EMAIL_FIELD + idEquipo,
        title: titleEquipo + idEquipo + ': ' + this.translate.instant(EQUIPO_EMAIL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEmailEquipo);
      const columnRolEquipo: ISgiColumnReport = {
        name: EQUIPO_ROL_FIELD + idEquipo,
        title: titleEquipo + idEquipo + ': ' + this.translate.instant(EQUIPO_ROL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnRolEquipo);
      const columnFechaInicioEquipo: ISgiColumnReport = {
        name: EQUIPO_FECHA_INICIO_FIELD + idEquipo,
        title: titleEquipo + idEquipo + ': ' + this.translate.instant(EQUIPO_FECHA_INICIO_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaInicioEquipo);
      const columnFechaFinEquipo: ISgiColumnReport = {
        name: EQUIPO_FECHA_FIN_FIELD + idEquipo,
        title: titleEquipo + idEquipo + ': ' + this.translate.instant(EQUIPO_FECHA_FIN_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinEquipo);
      const columnParticipacion: ISgiColumnReport = {
        name: EQUIPO_PARTICIPACION_FIELD + idEquipo,
        title: titleEquipo + idEquipo + ': ' + this.translate.instant(EQUIPO_PARTICIPACION_KEY),
        type: ColumnType.NUMBER,
        format: '#,#" "%'
      };
      columns.push(columnParticipacion);
    }
    return columns;
  }

  public fillRows(grupos: IGrupoReportData[], index: number, reportConfig: IReportConfig<IGrupoReportOptions>): any[] {
    const grupo = grupos[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumEquipo = Math.max(...grupos.map(g => g.equiposInvestigacion?.length));
      for (let i = 0; i < maxNumEquipo; i++) {
        const equipo = grupo.equiposInvestigacion[i] ?? null;
        this.fillRowsEquipoExcel(elementsRow, equipo);
      }
    }
    return elementsRow;
  }


  private fillRowsEquipoExcel(elementsRow: any[], miembroEquipo: IGrupoEquipo) {
    if (miembroEquipo) {
      elementsRow.push(miembroEquipo.persona?.nombre ?? '');
      elementsRow.push(miembroEquipo.persona?.apellidos ?? '');
      elementsRow.push(this.getEmailPrincipal(miembroEquipo.persona));
      elementsRow.push(miembroEquipo.rol?.nombre ?? '');
      elementsRow.push(LuxonUtils.toBackend(miembroEquipo.fechaInicio) ?? '');
      elementsRow.push(LuxonUtils.toBackend(miembroEquipo.fechaFin) ?? '');
      elementsRow.push(miembroEquipo?.participacion ? miembroEquipo?.participacion / 100 : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private getEmailPrincipal({ emails }: IPersona): string {
    if (!emails) {
      return '';
    }
    const emailDataPrincipal = emails.find(emailData => emailData.principal);
    return emailDataPrincipal?.email ?? '';
  }
}
