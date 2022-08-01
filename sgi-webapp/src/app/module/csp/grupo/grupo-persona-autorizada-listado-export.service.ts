import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoPersonaAutorizada } from '@core/models/csp/grupo-persona-autorizada';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IPersona } from '@core/models/sgp/persona';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IGrupoReportData, IGrupoReportOptions } from './grupo-listado-export.service';

const PERSONA_AUTORIZADA_KEY = marker('csp.grupo-persona-autorizada');
const PERSONA_NOMBRE_KEY = marker('csp.grupo-equipo.nombre');
const PERSONA_APELLIDOS_KEY = marker('csp.grupo-equipo.apellidos');
const PERSONA_EMAIL_KEY = marker('csp.grupo-equipo.email');
const PERSONA_FECHA_INICIO_KEY = marker('csp.grupo-persona-autorizada.fecha-inicio');
const PERSONA_FECHA_FIN_KEY = marker('csp.grupo-persona-autorizada.fecha-fin');

const PERSONA_AUTORIZADA_FIELD = 'personaAutorizada';
const PERSONA_NOMBRE_FIELD = 'nombrePersonaAutorizada';
const PERSONA_APELLIDOS_FIELD = 'apellidosPersonaAutorizada';
const PERSONA_EMAIL_FIELD = 'emailPersonaAutorizada';
const PERSONA_FECHA_INICIO_FIELD = 'fechaInicioPersonaAutorizada';
const PERSONA_FECHA_FIN_FIELD = 'fechaFinPersonaAutorizada';

@Injectable()
export class GrupoPersonaAutorizadaListadoExportService
  extends AbstractTableExportFillService<IGrupoReportData, IGrupoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private readonly grupoService: GrupoService,
    private personaService: PersonaService,
  ) {
    super(translate);
  }

  public getData(grupoData: IGrupoReportData): Observable<IGrupoReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.grupoService.findPersonasAutorizadas(grupoData.id, findOptions).pipe(
      map((responsePersonaAutorizada) => {
        grupoData.personasAutorizadas = [];
        return responsePersonaAutorizada;
      }),
      switchMap(responsePersonaAutorizada => {
        if (responsePersonaAutorizada.total === 0) {
          return of(grupoData);
        }
        const personasAutorizadas = responsePersonaAutorizada.items;

        const personasAutorizadasIds = new Set<string>(responsePersonaAutorizada.items.map(
          (personaAutorizada) => personaAutorizada.persona.id)
        );
        return this.personaService.findAllByIdIn([...personasAutorizadasIds]).pipe(
          map((result) => {
            const personas = result.items;

            personasAutorizadas.forEach((personaAutorizada) => {
              personaAutorizada.persona = personas.find((persona) => personaAutorizada.persona.id === persona.id);
            });

            grupoData.personasAutorizadas = personasAutorizadas;
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
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsPersonaAutorizadaNotExcel();
    } else {
      return this.getColumnsPersonaAutorizadaExcel(grupos);
    }
  }

  private getColumnsPersonaAutorizadaNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: PERSONA_AUTORIZADA_FIELD,
      title: this.translate.instant(PERSONA_AUTORIZADA_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(PERSONA_AUTORIZADA_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) +
      ' (' + this.translate.instant(PERSONA_NOMBRE_KEY) +
      ' - ' + this.translate.instant(PERSONA_EMAIL_KEY) +
      ' - ' + this.translate.instant(PERSONA_FECHA_INICIO_KEY) +
      ' - ' + this.translate.instant(PERSONA_FECHA_FIN_KEY) +
      ')';
    const columnPersonaAutorizada: ISgiColumnReport = {
      name: PERSONA_AUTORIZADA_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnPersonaAutorizada];
  }

  private getColumnsPersonaAutorizadaExcel(grupos: IGrupoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumPersonaAutorizadas = Math.max(...grupos.map(g => g.personasAutorizadas?.length));
    const titlePersonaAutorizada = this.translate.instant(PERSONA_AUTORIZADA_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);
    for (let i = 0; i < maxNumPersonaAutorizadas; i++) {
      const idPersonaAutorizada: string = String(i + 1);
      const columnNombrePersonaAutorizada: ISgiColumnReport = {
        name: PERSONA_NOMBRE_FIELD + idPersonaAutorizada,
        title: titlePersonaAutorizada + idPersonaAutorizada + ': ' + this.translate.instant(PERSONA_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombrePersonaAutorizada);
      const columnApellidosPersonaAutorizada: ISgiColumnReport = {
        name: PERSONA_APELLIDOS_FIELD + idPersonaAutorizada,
        title: titlePersonaAutorizada + idPersonaAutorizada + ': ' + this.translate.instant(PERSONA_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnApellidosPersonaAutorizada);
      const columnEmailPersonaAutorizada: ISgiColumnReport = {
        name: PERSONA_EMAIL_FIELD + idPersonaAutorizada,
        title: titlePersonaAutorizada + idPersonaAutorizada + ': ' + this.translate.instant(PERSONA_EMAIL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEmailPersonaAutorizada);
      const columnFechaInicioPersonaAutorizada: ISgiColumnReport = {
        name: PERSONA_FECHA_INICIO_FIELD + idPersonaAutorizada,
        title: titlePersonaAutorizada + idPersonaAutorizada + ': ' + this.translate.instant(PERSONA_FECHA_INICIO_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaInicioPersonaAutorizada);
      const columnFechaFinPersonaAutorizada: ISgiColumnReport = {
        name: PERSONA_FECHA_FIN_FIELD + idPersonaAutorizada,
        title: titlePersonaAutorizada + idPersonaAutorizada + ': ' + this.translate.instant(PERSONA_FECHA_FIN_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinPersonaAutorizada);
    }
    return columns;
  }

  public fillRows(grupos: IGrupoReportData[], index: number, reportConfig: IReportConfig<IGrupoReportOptions>): any[] {

    const grupo = grupos[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsPersonaAutorizadaNotExcel(grupo, elementsRow);
    } else {
      const maxNumPersonaAutorizada = Math.max(...grupos.map(g => g.personasAutorizadas?.length));
      for (let i = 0; i < maxNumPersonaAutorizada; i++) {
        const personaAutorizada = grupo.personasAutorizadas[i] ?? null;
        this.fillRowsPersonaAutorizadaExcel(elementsRow, personaAutorizada);
      }
    }
    return elementsRow;
  }

  private fillRowsPersonaAutorizadaNotExcel(grupo: IGrupoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    grupo.personasAutorizadas?.forEach(personaAutorizada => {
      const personaAutorizadaElementsRow: any[] = [];

      let personaAutorizadaTable = personaAutorizada?.persona?.nombre + ' ' + personaAutorizada?.persona?.apellidos;
      personaAutorizadaTable += '\n';
      personaAutorizadaTable += this.getEmailPrincipal(personaAutorizada?.persona) ?? '';
      personaAutorizadaTable += '\n';
      personaAutorizadaTable +=
        this.luxonDatePipe.transform(LuxonUtils.toBackend(personaAutorizada?.fechaInicio, true), 'shortDate') ?? '';
      personaAutorizadaTable += '\n';
      personaAutorizadaTable += this.luxonDatePipe.transform(LuxonUtils.toBackend(personaAutorizada?.fechaFin, true), 'shortDate') ?? '';

      personaAutorizadaElementsRow.push(personaAutorizadaTable);

      const rowReport: ISgiRowReport = {
        elements: personaAutorizadaElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsPersonaAutorizadaExcel(elementsRow: any[], personaAutorizada: IGrupoPersonaAutorizada) {
    if (personaAutorizada) {
      elementsRow.push(personaAutorizada.persona?.nombre ?? '');
      elementsRow.push(personaAutorizada.persona?.apellidos ?? '');
      elementsRow.push(this.getEmailPrincipal(personaAutorizada.persona));
      elementsRow.push(LuxonUtils.toBackend(personaAutorizada.fechaInicio) ?? '');
      elementsRow.push(LuxonUtils.toBackend(personaAutorizada.fechaFin) ?? '');
    } else {
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
