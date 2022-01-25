import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const CALENDARIO_JUSTIFICACION_KEY = marker('menu.csp.proyectos.periodos-justificacion');
const CALENDARIO_JUSTIFICACION_ITEM_KEY = marker('csp.proyecto-periodo-justificacion');
const CALENDARIO_JUSTIFICACION_NUM_PERIODO_KEY = marker('csp.proyecto-periodo-justificacion.numero-periodo');
const CALENDARIO_JUSTIFICACION_TIPO_KEY = marker('csp.proyecto-periodo-justificacion.tipo-justificacion');
const CALENDARIO_JUSTIFICACION_FECHA_INICIO_KEY = marker('csp.proyecto-periodo-justificacion.fecha-inicio');
const CALENDARIO_JUSTIFICACION_FECHA_FIN_KEY = marker('csp.proyecto-periodo-justificacion.fecha-fin');
const CALENDARIO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_KEY = marker('csp.proyecto-periodo-justificacion.fecha-inicio-presentacion');
const CALENDARIO_JUSTIFICACION_FECHA_FIN_PRESENTACION_KEY = marker('csp.proyecto-periodo-justificacion.fecha-fin-presentacion');

const CALENDARIO_JUSTIFICACION_FIELD = 'calendarioJustificacion';
const CALENDARIO_JUSTIFICACION_TIPO_FIELD = 'tipoCalendarioJustificacion';

@Injectable()
export class ProyectoCalendarioJustificacionListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private readonly proyectoService: ProyectoService,
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    return this.proyectoService.findAllPeriodoJustificacion(proyectoData.id).pipe(
      map(response => response.items.map(item => item as IProyectoPeriodoJustificacion)),
      map(responseCalendarioJustificacion => {
        proyectoData.calendarioJustificacion = responseCalendarioJustificacion;
        return proyectoData;
      })
    );
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsCalendarioJustificacionNotExcel();
    } else {
      return this.getColumnsCalendarioJustificacionExcel(proyectos);
    }
  }

  private getColumnsCalendarioJustificacionNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: CALENDARIO_JUSTIFICACION_FIELD,
      title: this.translate.instant(CALENDARIO_JUSTIFICACION_ITEM_KEY),
      type: ColumnType.STRING
    });

    const titleI18n = this.translate.instant(CALENDARIO_JUSTIFICACION_KEY) +
      ' (' + this.translate.instant(CALENDARIO_JUSTIFICACION_NUM_PERIODO_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_JUSTIFICACION_TIPO_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_JUSTIFICACION_FECHA_INICIO_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_JUSTIFICACION_FECHA_FIN_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_JUSTIFICACION_FECHA_FIN_PRESENTACION_KEY) +
      ')';

    const columnEntidad: ISgiColumnReport = {
      name: CALENDARIO_JUSTIFICACION_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsCalendarioJustificacionExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumCalendarioJustificaciones = Math.max(...proyectos.map(p => p.calendarioJustificacion?.length));
    const titleCalendarioJustificacion = this.translate.instant(CALENDARIO_JUSTIFICACION_ITEM_KEY);

    for (let i = 0; i < maxNumCalendarioJustificaciones; i++) {
      const idCalendarioJustificacion: string = String(i + 1);
      const columnCalendarioJustificacion: ISgiColumnReport = {
        name: CALENDARIO_JUSTIFICACION_TIPO_FIELD + idCalendarioJustificacion,
        title: titleCalendarioJustificacion + idCalendarioJustificacion + ': ' + this.translate.instant(CALENDARIO_JUSTIFICACION_TIPO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnCalendarioJustificacion);
    }

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {

    const proyecto = proyectos[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsCalendarioJustificacionNotExcel(proyecto, elementsRow);
    } else {
      const maxNumCalendarioJustificaciones = Math.max(...proyectos.map(p => p.calendarioJustificacion?.length));
      for (let i = 0; i < maxNumCalendarioJustificaciones; i++) {
        const calendarioJustificacion = proyecto.calendarioJustificacion[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, calendarioJustificacion);
      }
    }
    return elementsRow;
  }

  private fillRowsCalendarioJustificacionNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.calendarioJustificacion?.forEach(proyectoCalendarioJustificacion => {
      const calendarioJustificacionElementsRow: any[] = [];

      let calendarioJustificacionContent = proyectoCalendarioJustificacion?.numPeriodo ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += proyectoCalendarioJustificacion.tipoJustificacion ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += this.luxonDatePipe.transform(
        LuxonUtils.toBackend(proyectoCalendarioJustificacion?.fechaInicio, true), 'shortDate') ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += this.luxonDatePipe.transform(LuxonUtils.toBackend(proyectoCalendarioJustificacion?.fechaFin, true), 'shortDate') ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += this.luxonDatePipe.transform(
        LuxonUtils.toBackend(proyectoCalendarioJustificacion?.fechaInicioPresentacion, true), 'shortDate') ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += this.luxonDatePipe.transform(
        LuxonUtils.toBackend(proyectoCalendarioJustificacion?.fechaFinPresentacion, true), 'shortDate') ?? '';
      calendarioJustificacionContent += '\n';

      calendarioJustificacionElementsRow.push(calendarioJustificacionContent);

      const rowReport: ISgiRowReport = {
        elements: calendarioJustificacionElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], proyectoCalendarioJustificacion: IProyectoPeriodoJustificacion) {
    if (proyectoCalendarioJustificacion) {
      let calendarioJustificacionContent = proyectoCalendarioJustificacion.numPeriodo ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += proyectoCalendarioJustificacion.tipoJustificacion ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += LuxonUtils.toBackend(proyectoCalendarioJustificacion?.fechaInicio) ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += LuxonUtils.toBackend(proyectoCalendarioJustificacion?.fechaFin) ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += LuxonUtils.toBackend(proyectoCalendarioJustificacion?.fechaInicioPresentacion) ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += LuxonUtils.toBackend(proyectoCalendarioJustificacion?.fechaFinPresentacion) ?? '';
      calendarioJustificacionContent += '\n';

      elementsRow.push(calendarioJustificacionContent);
    } else {
      elementsRow.push('');
    }
  }
}
