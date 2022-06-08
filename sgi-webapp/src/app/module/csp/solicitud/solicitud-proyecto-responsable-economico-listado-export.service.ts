import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ISolicitudProyectoResponsableEconomico } from '@core/models/csp/solicitud-proyecto-responsable-economico';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IPersona } from '@core/models/sgp/persona';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ISolicitudReportData, ISolicitudReportOptions } from './solicitud-listado-export.service';

const RESPONSABLE_ECONOMICO_KEY = marker('csp.solicitud-proyecto-responsable-economico');
const INVESTIGADOR_KEY = marker('csp.solicitud-proyecto-responsable-economico');
const INVESTIGADOR_SHORT_KEY = marker('csp.proyecto-resp-economico');
const INVESTIGADOR_NOMBRE_APELLIDOS_KEY = marker('csp.solicitud-proyecto-responsable-economico.nombre-apellidos');
const INVESTIGADOR_NOMBRE_KEY = marker('sgp.nombre');
const INVESTIGADOR_APELLIDOS_KEY = marker('csp.solicitud-proyecto-responsable-economico.apellidos');
const INVESTIGADOR_EMAIL_KEY = marker('csp.solicitud-proyecto-responsable-economico.email');
const INVESTIGADOR_FECHA_INICIO_KEY = marker('csp.solicitud-proyecto-responsable-economico.mes-inicial');
const INVESTIGADOR_FECHA_FIN_KEY = marker('csp.solicitud-proyecto-responsable-economico.mes-final');
const PROYECTO_KEY = marker('menu.csp.solicitudes.datos-proyecto');
const RESPONSABLE_ECONOMICO_FIELD = 'responsableEconomico';
const INVESTIGADOR_NOMBRE_FIELD = 'nombreResponsableEconomico';
const INVESTIGADOR_APELLIDOS_FIELD = 'apellidosResponsableEconomico';
const INVESTIGADOR_EMAIL_FIELD = 'emailResponsableEconomico';
const INVESTIGADOR_FECHA_INICIO_FIELD = 'fechaInicioResponsableEconomico';
const INVESTIGADOR_FECHA_FIN_FIELD = 'fechaFinResponsableEconomico';

@Injectable()
export class SolicitudProyectoResponsableEconomicoListadoExportService
  extends AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
    private personaService: PersonaService,
  ) {
    super(translate);
  }

  public getData(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    return this.solicitudService.findAllSolicitudProyectoResponsablesEconomicos(solicitudData.id).pipe(
      map((responseResponsableEconomico) => {
        solicitudData.responsablesEconomicos = [];
        return responseResponsableEconomico;
      }),
      switchMap(responseResponsableEconomico => {
        if (responseResponsableEconomico.total === 0) {
          return of(solicitudData);
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

            solicitudData.responsablesEconomicos = responsablesEconomicos;
            return solicitudData;

          })
        );
      })
    );
  }

  public fillColumns(
    solicitudes: ISolicitudReportData[],
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsResponsableEconomicoNotExcel();
    } else {
      return this.getColumnsResponsableEconomicoExcel(solicitudes);
    }
  }

  private getColumnsResponsableEconomicoNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: RESPONSABLE_ECONOMICO_FIELD,
      title: this.translate.instant(INVESTIGADOR_SHORT_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(PROYECTO_KEY) + ': ' + this.translate.instant(RESPONSABLE_ECONOMICO_KEY) +
      ' (' + this.translate.instant(INVESTIGADOR_NOMBRE_APELLIDOS_KEY) +
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

  private getColumnsResponsableEconomicoExcel(solicitudes: ISolicitudReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumResponsableEconomicos = Math.max(...solicitudes.map(s => s.responsablesEconomicos ? s.responsablesEconomicos?.length : 0));
    const titleResponsableEconomico = this.translate.instant(PROYECTO_KEY) + ': ' + this.translate.instant(INVESTIGADOR_KEY);
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
        type: ColumnType.STRING,
      };
      columns.push(columnFechaInicioResponsableEconomico);
      const columnFechaFinResponsableEconomico: ISgiColumnReport = {
        name: INVESTIGADOR_FECHA_FIN_FIELD + idResponsableEconomico,
        title: titleResponsableEconomico + idResponsableEconomico + ': ' + this.translate.instant(INVESTIGADOR_FECHA_FIN_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaFinResponsableEconomico);
    }
    return columns;
  }

  public fillRows(solicitudes: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {

    const solicitud = solicitudes[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsResponsableEconomicoNotExcel(solicitud, elementsRow);
    } else {
      const maxNumResponsableEconomico = Math.max(...solicitudes.map(s => s.responsablesEconomicos ? s.responsablesEconomicos?.length : 0));
      for (let i = 0; i < maxNumResponsableEconomico; i++) {
        const responsableEconomico = solicitud.responsablesEconomicos && solicitud.responsablesEconomicos.length > 0 ? solicitud.responsablesEconomicos[i] : null;
        this.fillRowsResponsableEconomicoExcel(elementsRow, responsableEconomico);
      }
    }
    return elementsRow;
  }

  private fillRowsResponsableEconomicoNotExcel(solicitud: ISolicitudReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    solicitud.responsablesEconomicos?.forEach(responsableEconomico => {
      const responsableEconomicoElementsRow: any[] = [];

      let responsableEconomicoTable = responsableEconomico?.persona?.nombre + ' ' + responsableEconomico?.persona?.apellidos;
      responsableEconomicoTable += '\n';
      responsableEconomicoTable += this.getEmailPrincipal(responsableEconomico?.persona) ?? '';
      responsableEconomicoTable += '\n';
      responsableEconomicoTable +=
        responsableEconomico?.mesInicio ?? '';
      responsableEconomicoTable += '\n';
      responsableEconomicoTable += responsableEconomico?.mesFin ?? '';

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

  private fillRowsResponsableEconomicoExcel(elementsRow: any[], responsableEconomico: ISolicitudProyectoResponsableEconomico) {
    if (responsableEconomico) {
      elementsRow.push(responsableEconomico.persona?.nombre ?? '');
      elementsRow.push(responsableEconomico.persona?.apellidos ?? '');
      elementsRow.push(this.getEmailPrincipal(responsableEconomico.persona));
      elementsRow.push(responsableEconomico.mesInicio?.toString() ?? '');
      elementsRow.push(responsableEconomico.mesFin?.toString() ?? '');
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
