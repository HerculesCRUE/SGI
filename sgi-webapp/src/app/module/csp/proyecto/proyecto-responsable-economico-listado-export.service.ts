import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoResponsableEconomico } from '@core/models/csp/proyecto-responsable-economico';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IPersona } from '@core/models/sgp/persona';
import { ProyectoService } from '@core/services/csp/proyecto.service';
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
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const RESPONSABLE_ECONOMICO_KEY = marker('csp.proyecto-responsable-economico');
const INVESTIGADOR_KEY = marker('csp.proyecto-responsable-economico');
const INVESTIGADOR_SHORT_KEY = marker('csp.proyecto-resp-economico');
const INVESTIGADOR_NOMBRE_KEY = marker('csp.solicitud-proyecto-responsable-economico.nombre');
const INVESTIGADOR_APELLIDOS_KEY = marker('csp.solicitud-proyecto-responsable-economico.apellidos');
const INVESTIGADOR_EMAIL_KEY = marker('csp.solicitud-proyecto-responsable-economico.email');
const INVESTIGADOR_FECHA_INICIO_KEY = marker('csp.solicitud-proyecto.fecha-inicio');
const INVESTIGADOR_FECHA_FIN_KEY = marker('csp.solicitud-proyecto.fecha-fin');

const RESPONSABLE_ECONOMICO_FIELD = 'responsableEconomico';
const INVESTIGADOR_NOMBRE_FIELD = 'nombreResponsableEconomico';
const INVESTIGADOR_APELLIDOS_FIELD = 'apellidosResponsableEconomico';
const INVESTIGADOR_EMAIL_FIELD = 'emailResponsableEconomico';
const INVESTIGADOR_FECHA_INICIO_FIELD = 'fechaInicioResponsableEconomico';
const INVESTIGADOR_FECHA_FIN_FIELD = 'fechaFinResponsableEconomico';

@Injectable()
export class ProyectoResponsableEconomicoListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private readonly proyectoService: ProyectoService,
    private personaService: PersonaService,
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.proyectoService.findAllProyectoResponsablesEconomicos(proyectoData.id, findOptions).pipe(
      map((responseResponsableEconomico) => {
        proyectoData.responsablesEconomicos = [];
        return responseResponsableEconomico;
      }),
      switchMap(responseResponsableEconomico => {
        if (responseResponsableEconomico.total === 0) {
          return of(proyectoData);
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

            proyectoData.responsablesEconomicos = responsablesEconomicos;
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
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsResponsableEconomicoNotExcel();
    } else {
      return this.getColumnsResponsableEconomicoExcel(proyectos);
    }
  }

  private getColumnsResponsableEconomicoNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: RESPONSABLE_ECONOMICO_FIELD,
      title: this.translate.instant(INVESTIGADOR_SHORT_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(RESPONSABLE_ECONOMICO_KEY) +
      ' (' + this.translate.instant(INVESTIGADOR_NOMBRE_KEY) +
      ' - ' + this.translate.instant(INVESTIGADOR_EMAIL_KEY) +
      ' - ' + this.translate.instant(INVESTIGADOR_FECHA_INICIO_KEY) +
      ' - ' + this.translate.instant(INVESTIGADOR_FECHA_FIN_KEY) +
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

  private getColumnsResponsableEconomicoExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumResponsableEconomicos = Math.max(...proyectos.map(p => p.responsablesEconomicos?.length));
    const titleResponsableEconomico = this.translate.instant(INVESTIGADOR_KEY);
    for (let i = 0; i < maxNumResponsableEconomicos; i++) {
      const idResponsableEconomico: string = String(i + 1);
      const columnNombreResponsableEconomico: ISgiColumnReport = {
        name: INVESTIGADOR_NOMBRE_FIELD + idResponsableEconomico,
        title: titleResponsableEconomico + idResponsableEconomico + ': ' + this.translate.instant(INVESTIGADOR_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombreResponsableEconomico);
      const columnApellidosResponsableEconomico: ISgiColumnReport = {
        name: INVESTIGADOR_APELLIDOS_FIELD + idResponsableEconomico,
        title: titleResponsableEconomico + idResponsableEconomico + ': ' + this.translate.instant(INVESTIGADOR_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnApellidosResponsableEconomico);
      const columnEmailResponsableEconomico: ISgiColumnReport = {
        name: INVESTIGADOR_EMAIL_FIELD + idResponsableEconomico,
        title: titleResponsableEconomico + idResponsableEconomico + ': ' + this.translate.instant(INVESTIGADOR_EMAIL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEmailResponsableEconomico);
      const columnFechaInicioResponsableEconomico: ISgiColumnReport = {
        name: INVESTIGADOR_FECHA_INICIO_FIELD + idResponsableEconomico,
        title: titleResponsableEconomico + idResponsableEconomico + ': ' + this.translate.instant(INVESTIGADOR_FECHA_INICIO_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaInicioResponsableEconomico);
      const columnFechaFinResponsableEconomico: ISgiColumnReport = {
        name: INVESTIGADOR_FECHA_FIN_FIELD + idResponsableEconomico,
        title: titleResponsableEconomico + idResponsableEconomico + ': ' + this.translate.instant(INVESTIGADOR_FECHA_FIN_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinResponsableEconomico);
    }
    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {

    const proyecto = proyectos[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsResponsableEconomicoNotExcel(proyecto, elementsRow);
    } else {
      const maxNumResponsableEconomico = Math.max(...proyectos.map(p => p.responsablesEconomicos?.length));
      for (let i = 0; i < maxNumResponsableEconomico; i++) {
        const responsableEconomico = proyecto.responsablesEconomicos[i] ?? null;
        this.fillRowsResponsableEconomicoExcel(elementsRow, responsableEconomico);
      }
    }
    return elementsRow;
  }

  private fillRowsResponsableEconomicoNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.responsablesEconomicos?.forEach(responsableEconomico => {
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

  private fillRowsResponsableEconomicoExcel(elementsRow: any[], responsableEconomico: IProyectoResponsableEconomico) {
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

  private getEmailPrincipal(persona: IPersona): string {
    if (!persona?.emails) {
      return '';
    }
    const emailDataPrincipal = persona.emails.find(emailData => emailData.principal);
    return emailDataPrincipal?.email ?? '';
  }


}
