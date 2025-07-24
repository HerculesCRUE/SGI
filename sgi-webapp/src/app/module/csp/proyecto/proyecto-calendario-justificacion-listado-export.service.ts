import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TIPO_JUSTIFICACION_MAP } from '@core/enums/tipo-justificacion';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
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

const CALENDARIO_JUSTIFICACION_ITEM_KEY = marker('csp.proyecto-periodo-justificacion');
const CALENDARIO_JUSTIFICACION_NUM_PERIODO_KEY = marker('csp.proyecto-periodo-justificacion.numero-periodo');
const CALENDARIO_JUSTIFICACION_TIPO_KEY = marker('csp.proyecto-periodo-justificacion.tipo-justificacion');
const CALENDARIO_JUSTIFICACION_FECHA_INICIO_KEY = marker('csp.proyecto-periodo-justificacion.fecha-inicio');
const CALENDARIO_JUSTIFICACION_FECHA_FIN_KEY = marker('csp.proyecto-periodo-justificacion.fecha-fin');
const CALENDARIO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_KEY = marker('csp.proyecto-periodo-justificacion.fecha-inicio-presentacion');
const CALENDARIO_JUSTIFICACION_FECHA_FIN_PRESENTACION_KEY = marker('csp.proyecto-periodo-justificacion.fecha-fin-presentacion');

const CALENDARIO_NUM_PERIODO_JUSTIFICACION_FIELD = 'numPeriodoJustificacion';
const CALENDARIO_JUSTIFICACION_TIPO_FIELD = 'tipoCalendarioJustificacion';
const CALENDARIO_JUSTIFICACION_FECHA_INICIO_FIELD = 'fechaInicioJustificacion';
const CALENDARIO_JUSTIFICACION_FECHA_FIN_FIELD = 'fechaFinJustificacion';
const CALENDARIO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_FIELD = 'fechaInicioPresentacionJustificacion';
const CALENDARIO_JUSTIFICACION_FECHA_FIN_PRESENTACION_FIELD = 'fechaFinPresentacionJustificacion';

@Injectable()
export class ProyectoCalendarioJustificacionListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions> {

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
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsCalendarioJustificacionExcel(proyectos);
    }
  }

  private getColumnsCalendarioJustificacionExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumCalendarioJustificaciones = Math.max(...proyectos.map(p => p.calendarioJustificacion?.length));
    const titleCalendarioJustificacion = this.translate.instant(CALENDARIO_JUSTIFICACION_ITEM_KEY);

    for (let i = 0; i < maxNumCalendarioJustificaciones; i++) {
      const idCalendarioJustificacion: string = String(i + 1);
      const columnNumPeriodoCalendarioJustificacion: ISgiColumnReport = {
        name: CALENDARIO_NUM_PERIODO_JUSTIFICACION_FIELD + idCalendarioJustificacion,
        title: titleCalendarioJustificacion + idCalendarioJustificacion + ': ' + this.translate.instant(CALENDARIO_JUSTIFICACION_NUM_PERIODO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNumPeriodoCalendarioJustificacion);

      const columnTipoCalendarioJustificacion: ISgiColumnReport = {
        name: CALENDARIO_JUSTIFICACION_TIPO_FIELD + idCalendarioJustificacion,
        title: titleCalendarioJustificacion + idCalendarioJustificacion + ': ' + this.translate.instant(CALENDARIO_JUSTIFICACION_TIPO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTipoCalendarioJustificacion);

      const columnFechaInicioCalendarioJustificacion: ISgiColumnReport = {
        name: CALENDARIO_JUSTIFICACION_FECHA_INICIO_FIELD + idCalendarioJustificacion,
        title: titleCalendarioJustificacion + idCalendarioJustificacion + ': ' + this.translate.instant(CALENDARIO_JUSTIFICACION_FECHA_INICIO_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaInicioCalendarioJustificacion);

      const columnFechaFinCalendarioJustificacion: ISgiColumnReport = {
        name: CALENDARIO_JUSTIFICACION_FECHA_FIN_FIELD + idCalendarioJustificacion,
        title: titleCalendarioJustificacion + idCalendarioJustificacion + ': ' + this.translate.instant(CALENDARIO_JUSTIFICACION_FECHA_FIN_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinCalendarioJustificacion);

      const columnFechaInicioPresentacionCalendarioJustificacion: ISgiColumnReport = {
        name: CALENDARIO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_FIELD + idCalendarioJustificacion,
        title: titleCalendarioJustificacion + idCalendarioJustificacion + ': ' + this.translate.instant(CALENDARIO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaInicioPresentacionCalendarioJustificacion);

      const columnFechaFinPresentacionCalendarioJustificacion: ISgiColumnReport = {
        name: CALENDARIO_JUSTIFICACION_FECHA_FIN_PRESENTACION_FIELD + idCalendarioJustificacion,
        title: titleCalendarioJustificacion + idCalendarioJustificacion + ': ' + this.translate.instant(CALENDARIO_JUSTIFICACION_FECHA_FIN_PRESENTACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaFinPresentacionCalendarioJustificacion);
    }

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = proyectos[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumCalendarioJustificaciones = Math.max(...proyectos.map(p => p.calendarioJustificacion?.length));
      for (let i = 0; i < maxNumCalendarioJustificaciones; i++) {
        const calendarioJustificacion = proyecto.calendarioJustificacion[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, calendarioJustificacion);
      }
    }
    return elementsRow;
  }

  private fillRowsEntidadExcel(elementsRow: any[], proyectoCalendarioJustificacion: IProyectoPeriodoJustificacion) {
    if (proyectoCalendarioJustificacion) {
      elementsRow.push(proyectoCalendarioJustificacion.numPeriodo ?? '');
      elementsRow.push(proyectoCalendarioJustificacion?.tipoJustificacion ? this.translate.instant(TIPO_JUSTIFICACION_MAP.get(proyectoCalendarioJustificacion?.tipoJustificacion)) : '');
      elementsRow.push(LuxonUtils.toBackend(proyectoCalendarioJustificacion?.fechaInicio) ?? '');
      elementsRow.push(LuxonUtils.toBackend(proyectoCalendarioJustificacion?.fechaFin) ?? '');
      elementsRow.push(this.luxonDatePipe.transform(
        LuxonUtils.toBackend(proyectoCalendarioJustificacion?.fechaInicioPresentacion, false), 'short') ?? '');
      elementsRow.push(this.luxonDatePipe.transform(
        LuxonUtils.toBackend(proyectoCalendarioJustificacion?.fechaFinPresentacion, false), 'short') ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
