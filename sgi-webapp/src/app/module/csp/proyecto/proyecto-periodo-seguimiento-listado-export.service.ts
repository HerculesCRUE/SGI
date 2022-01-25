import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TIPO_SEGUIMIENTO_MAP } from '@core/enums/tipo-seguimiento';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
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

const PERIODO_SEGUIMIENTO_KEY = marker('menu.csp.proyectos.seguimientos-cientificos');
const PERIODO_SEGUIMIENTO_FIELD = 'periodoSeguimiento';
const PERIODO_SEGUIMIENTO_NUMERO_KEY = marker('csp.proyecto-periodo-seguimiento-cientifico.numero-periodo');
const PERIODO_SEGUIMIENTO_NUMERO_FIELD = 'numeroPeriodoSeguimiento';
const PERIODO_SEGUIMIENTO_TIPO_KEY = marker('csp.proyecto-periodo-seguimiento-cientifico.tipo-seguimiento');
const PERIODO_SEGUIMIENTO_TIPO_FIELD = 'tipoSeguimiento';
const PERIODO_SEGUIMIENTO_FECHA_INICIO_KEY = marker('csp.proyecto-periodo-seguimiento-cientifico.fecha-inicio');
const PERIODO_SEGUIMIENTO_FECHA_INICIO_FIELD = 'fechaInicioSeguimiento';
const PERIODO_SEGUIMIENTO_FECHA_FIN_KEY = marker('csp.proyecto-periodo-seguimiento-cientifico.fecha-fin');
const PERIODO_SEGUIMIENTO_FECHA_FIN_FIELD = 'fechaFinSeguimiento';
const PERIODO_SEGUIMIENTO_FECHA_INICIO_PRESENTACION_KEY = marker('csp.proyecto-periodo-seguimiento-cientifico.fecha-inicio-presentacion');
const PERIODO_SEGUIMIENTO_FECHA_INICIO_PRESENTACION_FIELD = 'fechaInicioPresentacion';
const PERIODO_SEGUIMIENTO_FECHA_FIN_PRESENTACION_KEY = marker('csp.proyecto-periodo-seguimiento-cientifico.fecha-fin-presentacion');
const PERIODO_SEGUIMIENTO_FECHA_FIN_PRESENTACION_FIELD = 'fechaFinPresentacion';
const PERIODO_SEGUIMIENTO_PERIODO_KEY = marker('csp.proyecto-periodo-justificacion.periodo');

@Injectable()
export class ProyectoPeriodoSeguimientoListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private luxonDatePipe: LuxonDatePipe
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    return this.proyectoService.findAllProyectoPeriodoSeguimientoProyecto(proyectoData.id).pipe(
      map(response => {
        proyectoData.periodosSeguimientos = response.items;
        return proyectoData;
      })
    );
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsPeriodoSeguimientoNotExcel();
    } else {
      return this.getColumnsPeriodoSeguimientoExcel(proyectos);
    }
  }

  private getColumnsPeriodoSeguimientoNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    columns.push({
      name: PERIODO_SEGUIMIENTO_FIELD,
      title: this.translate.instant(PERIODO_SEGUIMIENTO_PERIODO_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(PERIODO_SEGUIMIENTO_KEY) +
      ' (' + this.translate.instant(PERIODO_SEGUIMIENTO_NUMERO_KEY) +
      ' - ' + this.translate.instant(PERIODO_SEGUIMIENTO_TIPO_KEY) +
      ' - ' + this.translate.instant(PERIODO_SEGUIMIENTO_FECHA_INICIO_KEY) +
      ' - ' + this.translate.instant(PERIODO_SEGUIMIENTO_FECHA_FIN_KEY) +
      ' - ' + this.translate.instant(PERIODO_SEGUIMIENTO_FECHA_INICIO_PRESENTACION_KEY) +
      ' - ' + this.translate.instant(PERIODO_SEGUIMIENTO_FECHA_FIN_PRESENTACION_KEY) + ')';
    const columnEntidad: ISgiColumnReport = {
      name: PERIODO_SEGUIMIENTO_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsPeriodoSeguimientoExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumPeriodosSeguimiento = Math.max(...proyectos.map(p => p.periodosSeguimientos?.length));
    const titlePeriodoSeguimiento = this.translate.instant(PERIODO_SEGUIMIENTO_KEY) +
      ' ' +
      this.translate.instant(PERIODO_SEGUIMIENTO_PERIODO_KEY);

    for (let i = 0; i < maxNumPeriodosSeguimiento; i++) {
      const idPeriodoSeguimiento: string = String(i + 1);
      const titleColumn = titlePeriodoSeguimiento + ' ' + idPeriodoSeguimiento + ': ';
      const columnNumeroPeriodoSeguimiento: ISgiColumnReport = {
        name: PERIODO_SEGUIMIENTO_NUMERO_FIELD + idPeriodoSeguimiento,
        title: titleColumn + this.translate.instant(PERIODO_SEGUIMIENTO_NUMERO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNumeroPeriodoSeguimiento);

      const columnTipoPeriodoSeguimiento: ISgiColumnReport = {
        name: PERIODO_SEGUIMIENTO_TIPO_FIELD + idPeriodoSeguimiento,
        title: titleColumn + this.translate.instant(PERIODO_SEGUIMIENTO_TIPO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTipoPeriodoSeguimiento);

      const columnFechaInicioPeriodoSeguimiento: ISgiColumnReport = {
        name: PERIODO_SEGUIMIENTO_FECHA_INICIO_FIELD + idPeriodoSeguimiento,
        title: titleColumn + this.translate.instant(PERIODO_SEGUIMIENTO_FECHA_INICIO_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaInicioPeriodoSeguimiento);

      const columnFechaFinPeriodoSeguimiento: ISgiColumnReport = {
        name: PERIODO_SEGUIMIENTO_FECHA_FIN_FIELD + idPeriodoSeguimiento,
        title: titleColumn + this.translate.instant(PERIODO_SEGUIMIENTO_FECHA_FIN_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinPeriodoSeguimiento);

      const columnFechaInicioPresentacionPeriodoSeguimiento: ISgiColumnReport = {
        name: PERIODO_SEGUIMIENTO_FECHA_INICIO_PRESENTACION_FIELD + idPeriodoSeguimiento,
        title: titleColumn + this.translate.instant(PERIODO_SEGUIMIENTO_FECHA_INICIO_PRESENTACION_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaInicioPresentacionPeriodoSeguimiento);

      const columnFechaFinPresentacionPeriodoSeguimiento: ISgiColumnReport = {
        name: PERIODO_SEGUIMIENTO_FECHA_FIN_PRESENTACION_FIELD + idPeriodoSeguimiento,
        title: titleColumn + this.translate.instant(PERIODO_SEGUIMIENTO_FECHA_FIN_PRESENTACION_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinPresentacionPeriodoSeguimiento);
    }

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {

    const proyecto = proyectos[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsPeriodoSeguimientoNotExcel(proyecto, elementsRow);
    } else {
      const maxNumPeriodoSeguimientoes = Math.max(...proyectos.map(p => p.periodosSeguimientos?.length));
      for (let i = 0; i < maxNumPeriodoSeguimientoes; i++) {
        const periodoSeguimiento = proyecto.periodosSeguimientos[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, periodoSeguimiento);
      }
    }
    return elementsRow;
  }

  private fillRowsPeriodoSeguimientoNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.periodosSeguimientos?.forEach(proyectoPeriodoSeguimiento => {
      const periodoSeguimientoElementsRow: any[] = [];

      let periodoSeguimientoContent = proyectoPeriodoSeguimiento?.numPeriodo ?? '';
      periodoSeguimientoContent += '\n';
      periodoSeguimientoContent += proyectoPeriodoSeguimiento?.tipoSeguimiento ?
        this.translate.instant(TIPO_SEGUIMIENTO_MAP.get(proyectoPeriodoSeguimiento.tipoSeguimiento)) : '';
      periodoSeguimientoContent += '\n';
      periodoSeguimientoContent += this.luxonDatePipe.transform(LuxonUtils.toBackend(proyectoPeriodoSeguimiento?.fechaInicio, true), 'shortDate') ?? '';
      periodoSeguimientoContent += '\n';
      periodoSeguimientoContent += this.luxonDatePipe.transform(LuxonUtils.toBackend(proyectoPeriodoSeguimiento?.fechaFin, true), 'shortDate') ?? '';
      periodoSeguimientoContent += '\n';
      periodoSeguimientoContent +=
        this.luxonDatePipe.transform(LuxonUtils.toBackend(proyectoPeriodoSeguimiento?.fechaInicioPresentacion, true), 'shortDate') ?? '';
      periodoSeguimientoContent += '\n';
      periodoSeguimientoContent +=
        this.luxonDatePipe.transform(LuxonUtils.toBackend(proyectoPeriodoSeguimiento?.fechaFinPresentacion, true), 'shortDate') ?? '';
      periodoSeguimientoElementsRow.push(periodoSeguimientoContent);

      const rowReport: ISgiRowReport = {
        elements: periodoSeguimientoElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], periodo: IProyectoPeriodoSeguimiento) {
    if (periodo) {
      elementsRow.push(periodo.numPeriodo ?? '');
      elementsRow.push(periodo?.tipoSeguimiento ? this.translate.instant(TIPO_SEGUIMIENTO_MAP.get(periodo.tipoSeguimiento)) : '');
      elementsRow.push(LuxonUtils.toBackend(periodo?.fechaInicio) ?? '');
      elementsRow.push(LuxonUtils.toBackend(periodo?.fechaFin) ?? '');
      elementsRow.push(LuxonUtils.toBackend(periodo?.fechaInicioPresentacion) ?? '');
      elementsRow.push(LuxonUtils.toBackend(periodo?.fechaFinPresentacion) ?? '');
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
