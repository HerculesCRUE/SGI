import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoResponsableEconomico } from '@core/models/csp/grupo-responsable-economico';
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

const RESPONSABLE_ECONOMICO_KEY = marker('csp.grupo-responsable-economico');
const RESPONSABLE_SHORT_KEY = marker('csp.grupo-resp-economico');
const RESPONSABLE_NOMBRE_KEY = marker('csp.grupo-equipo.nombre');
const RESPONSABLE_APELLIDOS_KEY = marker('csp.grupo-equipo.apellidos');
const RESPONSABLE_EMAIL_KEY = marker('csp.grupo-equipo.email');
const RESPONSABLE_FECHA_INICIO_KEY = marker('csp.grupo-responsable-economico.fecha-inicio');
const RESPONSABLE_FECHA_FIN_KEY = marker('csp.grupo-responsable-economico.fecha-fin');

const RESPONSABLE_ECONOMICO_FIELD = 'responsableEconomico';
const RESPONSABLE_NOMBRE_FIELD = 'nombreResponsableEconomico';
const RESPONSABLE_APELLIDOS_FIELD = 'apellidosResponsableEconomico';
const RESPONSABLE_EMAIL_FIELD = 'emailResponsableEconomico';
const RESPONSABLE_FECHA_INICIO_FIELD = 'fechaInicioResponsableEconomico';
const RESPONSABLE_FECHA_FIN_FIELD = 'fechaFinResponsableEconomico';

@Injectable()
export class GrupoResponsableEconomicoListadoExportService
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
    return this.grupoService.findResponsablesEconomicos(grupoData.id, findOptions).pipe(
      map((responseResponsableEconomico) => {
        grupoData.responsablesEconomicos = [];
        return responseResponsableEconomico;
      }),
      switchMap(responseResponsableEconomico => {
        if (responseResponsableEconomico.total === 0) {
          return of(grupoData);
        }
        const responsablesEconomicos = responseResponsableEconomico.items;

        const responsablesEconomicosIds = new Set<string>(responseResponsableEconomico.items.map(
          (responsableEconomico) => responsableEconomico.persona.id)
        );
        return this.personaService.findAllByIdIn([...responsablesEconomicosIds]).pipe(
          map((result) => {
            const personas = result.items;

            responsablesEconomicos.forEach((responsableEconomico) => {
              responsableEconomico.persona = personas.find((persona) => responsableEconomico.persona.id === persona.id);
            });

            grupoData.responsablesEconomicos = responsablesEconomicos;
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
      return this.getColumnsResponsableEconomicoNotExcel();
    } else {
      return this.getColumnsResponsableEconomicoExcel(grupos);
    }
  }

  private getColumnsResponsableEconomicoNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: RESPONSABLE_ECONOMICO_FIELD,
      title: this.translate.instant(RESPONSABLE_SHORT_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(RESPONSABLE_ECONOMICO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) +
      ' (' + this.translate.instant(RESPONSABLE_NOMBRE_KEY) +
      ' - ' + this.translate.instant(RESPONSABLE_EMAIL_KEY) +
      ' - ' + this.translate.instant(RESPONSABLE_FECHA_INICIO_KEY) +
      ' - ' + this.translate.instant(RESPONSABLE_FECHA_FIN_KEY) +
      ')';
    const columnResponsableEconomico: ISgiColumnReport = {
      name: RESPONSABLE_ECONOMICO_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnResponsableEconomico];
  }

  private getColumnsResponsableEconomicoExcel(grupos: IGrupoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumResponsableEconomicos = Math.max(...grupos.map(p => p.responsablesEconomicos?.length));
    const titleResponsableEconomico = this.translate.instant(RESPONSABLE_ECONOMICO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);
    for (let i = 0; i < maxNumResponsableEconomicos; i++) {
      const idResponsableEconomico: string = String(i + 1);
      const columnNombreResponsableEconomico: ISgiColumnReport = {
        name: RESPONSABLE_NOMBRE_FIELD + idResponsableEconomico,
        title: titleResponsableEconomico + idResponsableEconomico + ': ' + this.translate.instant(RESPONSABLE_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombreResponsableEconomico);
      const columnApellidosResponsableEconomico: ISgiColumnReport = {
        name: RESPONSABLE_APELLIDOS_FIELD + idResponsableEconomico,
        title: titleResponsableEconomico + idResponsableEconomico + ': ' + this.translate.instant(RESPONSABLE_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnApellidosResponsableEconomico);
      const columnEmailResponsableEconomico: ISgiColumnReport = {
        name: RESPONSABLE_EMAIL_FIELD + idResponsableEconomico,
        title: titleResponsableEconomico + idResponsableEconomico + ': ' + this.translate.instant(RESPONSABLE_EMAIL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEmailResponsableEconomico);
      const columnFechaInicioResponsableEconomico: ISgiColumnReport = {
        name: RESPONSABLE_FECHA_INICIO_FIELD + idResponsableEconomico,
        title: titleResponsableEconomico + idResponsableEconomico + ': ' + this.translate.instant(RESPONSABLE_FECHA_INICIO_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaInicioResponsableEconomico);
      const columnFechaFinResponsableEconomico: ISgiColumnReport = {
        name: RESPONSABLE_FECHA_FIN_FIELD + idResponsableEconomico,
        title: titleResponsableEconomico + idResponsableEconomico + ': ' + this.translate.instant(RESPONSABLE_FECHA_FIN_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinResponsableEconomico);
    }
    return columns;
  }

  public fillRows(grupos: IGrupoReportData[], index: number, reportConfig: IReportConfig<IGrupoReportOptions>): any[] {

    const grupo = grupos[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsResponsableEconomicoNotExcel(grupo, elementsRow);
    } else {
      const maxNumResponsableEconomico = Math.max(...grupos.map(p => p.responsablesEconomicos?.length));
      for (let i = 0; i < maxNumResponsableEconomico; i++) {
        const responsableEconomico = grupo.responsablesEconomicos[i] ?? null;
        this.fillRowsResponsableEconomicoExcel(elementsRow, responsableEconomico);
      }
    }
    return elementsRow;
  }

  private fillRowsResponsableEconomicoNotExcel(grupo: IGrupoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    grupo.responsablesEconomicos?.forEach(responsableEconomico => {
      const responsableEconomicoElementsRow: any[] = [];

      let responsableEconomicoTable = responsableEconomico?.persona?.nombre + ' ' + responsableEconomico?.persona?.apellidos;
      responsableEconomicoTable += ' - ';
      responsableEconomicoTable += this.getEmailPrincipal(responsableEconomico?.persona) ?? '';
      responsableEconomicoTable += ' - ';
      responsableEconomicoTable +=
        this.luxonDatePipe.transform(LuxonUtils.toBackend(responsableEconomico?.fechaInicio, true), 'shortDate') ?? '';
      responsableEconomicoTable += ' - ';
      responsableEconomicoTable += this.luxonDatePipe.transform(LuxonUtils.toBackend(responsableEconomico?.fechaFin, true), 'shortDate') ?? '';

      responsableEconomicoElementsRow.push(responsableEconomicoTable);

      const rowReport: ISgiRowReport = {
        elements: responsableEconomicoElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsResponsableEconomicoExcel(elementsRow: any[], responsableEconomico: IGrupoResponsableEconomico) {
    if (responsableEconomico) {
      elementsRow.push(responsableEconomico.persona?.nombre ?? '');
      elementsRow.push(responsableEconomico.persona?.apellidos ?? '');
      elementsRow.push(this.getEmailPrincipal(responsableEconomico.persona));
      elementsRow.push(LuxonUtils.toBackend(responsableEconomico.fechaInicio) ?? '');
      elementsRow.push(LuxonUtils.toBackend(responsableEconomico.fechaFin) ?? '');
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
