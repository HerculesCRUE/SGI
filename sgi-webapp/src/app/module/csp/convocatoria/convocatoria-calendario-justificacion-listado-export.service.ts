import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

const CALENDARIO_JUSTIFICACION_KEY = marker('menu.csp.proyectos.periodos-justificacion');
const CALENDARIO_JUSTIFICACION_ITEM_KEY = marker('csp.convocatoria-periodo-justificacion.informe-title');
const CALENDARIO_JUSTIFICACION_NUM_PERIODO_KEY = marker('csp.convocatoria-periodo-justificacion.periodo');
const CALENDARIO_JUSTIFICACION_TIPO_KEY = marker('csp.convocatoria-periodo-justificacion.tipo');
const CALENDARIO_JUSTIFICACION_MES_INICIO_KEY = marker('csp.convocatoria-periodo-justificacion.mes-inicial');
const CALENDARIO_JUSTIFICACION_MES_FIN_KEY = marker('csp.convocatoria-periodo-justificacion.mes-final');
const CALENDARIO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_KEY = marker('csp.convocatoria-periodo-justificacion.fecha-inicio');
const CALENDARIO_JUSTIFICACION_FECHA_FIN_PRESENTACION_KEY = marker('csp.convocatoria-periodo-justificacion.fecha-fin');

const CALENDARIO_JUSTIFICACION_FIELD = 'calendarioJustificacion';
const CALENDARIO_NUM_PERIODO_JUSTIFICACION_FIELD = 'numPeriodoJustificacion';
const CALENDARIO_JUSTIFICACION_TIPO_FIELD = 'tipoCalendarioJustificacion';
const CALENDARIO_JUSTIFICACION_FECHA_INICIO_FIELD = 'fechaInicioJustificacion';
const CALENDARIO_JUSTIFICACION_FECHA_FIN_FIELD = 'fechaFinJustificacion';
const CALENDARIO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_FIELD = 'fechaInicioPresentacionJustificacion';
const CALENDARIO_JUSTIFICACION_FECHA_FIN_PRESENTACION_FIELD = 'fechaFinPresentacionJustificacion';

@Injectable()
export class ConvocatoriaCalendarioJustificacionListadoExportService
  extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private readonly convocatoriaService: ConvocatoriaService,
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return this.convocatoriaService.getPeriodosJustificacion(convocatoriaData?.convocatoria?.id).pipe(
      map(response => response.items.map(item => item as IConvocatoriaPeriodoJustificacion)),
      map(responseCalendarioJustificacion => {
        convocatoriaData.calendarioJustificacion = responseCalendarioJustificacion;
        return convocatoriaData;
      })
    );
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsCalendarioJustificacionNotExcel();
    } else {
      return this.getColumnsCalendarioJustificacionExcel(convocatorias);
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
      ' - ' + this.translate.instant(CALENDARIO_JUSTIFICACION_MES_INICIO_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_JUSTIFICACION_MES_FIN_KEY) +
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

  private getColumnsCalendarioJustificacionExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumCalendarioJustificaciones = Math.max(...convocatorias.map(p => p.calendarioJustificacion?.length));
    const titleCalendarioJustificacion = this.translate.instant(CALENDARIO_JUSTIFICACION_ITEM_KEY);

    for (let i = 0; i < maxNumCalendarioJustificaciones; i++) {
      const idCalendarioJustificacion: string = String(i + 1);
      const columnNumPeriodoCalendarioJustificacion: ISgiColumnReport = {
        name: CALENDARIO_NUM_PERIODO_JUSTIFICACION_FIELD + idCalendarioJustificacion,
        title: titleCalendarioJustificacion + idCalendarioJustificacion + ': ' + this.translate.instant(CALENDARIO_JUSTIFICACION_NUM_PERIODO_KEY),
        type: ColumnType.NUMBER,
        format: '#'
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
        title: titleCalendarioJustificacion + idCalendarioJustificacion + ': ' + this.translate.instant(CALENDARIO_JUSTIFICACION_MES_INICIO_KEY),
        type: ColumnType.NUMBER,
        format: '#'
      };
      columns.push(columnFechaInicioCalendarioJustificacion);

      const columnFechaFinCalendarioJustificacion: ISgiColumnReport = {
        name: CALENDARIO_JUSTIFICACION_FECHA_FIN_FIELD + idCalendarioJustificacion,
        title: titleCalendarioJustificacion + idCalendarioJustificacion + ': ' + this.translate.instant(CALENDARIO_JUSTIFICACION_MES_FIN_KEY),
        type: ColumnType.NUMBER,
        format: '#'
      };
      columns.push(columnFechaFinCalendarioJustificacion);

      const columnFechaInicioPresentacionCalendarioJustificacion: ISgiColumnReport = {
        name: CALENDARIO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_FIELD + idCalendarioJustificacion,
        title: titleCalendarioJustificacion + idCalendarioJustificacion + ': ' + this.translate.instant(CALENDARIO_JUSTIFICACION_FECHA_INICIO_PRESENTACION_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaInicioPresentacionCalendarioJustificacion);

      const columnFechaFinPresentacionCalendarioJustificacion: ISgiColumnReport = {
        name: CALENDARIO_JUSTIFICACION_FECHA_FIN_PRESENTACION_FIELD + idCalendarioJustificacion,
        title: titleCalendarioJustificacion + idCalendarioJustificacion + ': ' + this.translate.instant(CALENDARIO_JUSTIFICACION_FECHA_FIN_PRESENTACION_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinPresentacionCalendarioJustificacion);
    }

    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {

    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsCalendarioJustificacionNotExcel(convocatoria, elementsRow);
    } else {
      const maxNumCalendarioJustificaciones = Math.max(...convocatorias.map(p => p.calendarioJustificacion?.length));
      for (let i = 0; i < maxNumCalendarioJustificaciones; i++) {
        const calendarioJustificacion = convocatoria.calendarioJustificacion[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, calendarioJustificacion);
      }
    }
    return elementsRow;
  }

  private fillRowsCalendarioJustificacionNotExcel(convocatoria: IConvocatoriaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    convocatoria.calendarioJustificacion?.forEach(convocatoriaCalendarioJustificacion => {
      const calendarioJustificacionElementsRow: any[] = [];

      let calendarioJustificacionContent = convocatoriaCalendarioJustificacion?.numPeriodo ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += convocatoriaCalendarioJustificacion.tipo ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += convocatoriaCalendarioJustificacion?.mesInicial ? convocatoriaCalendarioJustificacion?.mesInicial.toString() ?? '' : '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += convocatoriaCalendarioJustificacion?.mesFinal ? convocatoriaCalendarioJustificacion?.mesFinal.toString() ?? '' : '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += this.luxonDatePipe.transform(
        LuxonUtils.toBackend(convocatoriaCalendarioJustificacion?.fechaInicioPresentacion, false), 'short') ?? '';
      calendarioJustificacionContent += '\n';
      calendarioJustificacionContent += this.luxonDatePipe.transform(
        LuxonUtils.toBackend(convocatoriaCalendarioJustificacion?.fechaFinPresentacion, false), 'short') ?? '';
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

  private fillRowsEntidadExcel(elementsRow: any[], convocatoriaCalendarioJustificacion: IConvocatoriaPeriodoJustificacion) {
    if (convocatoriaCalendarioJustificacion) {
      elementsRow.push(convocatoriaCalendarioJustificacion.numPeriodo ?? '');
      elementsRow.push(convocatoriaCalendarioJustificacion.tipo ?? '');
      elementsRow.push(convocatoriaCalendarioJustificacion?.mesInicial ? convocatoriaCalendarioJustificacion?.mesInicial.toString() ?? '' : '');
      elementsRow.push(convocatoriaCalendarioJustificacion?.mesFinal ? convocatoriaCalendarioJustificacion?.mesFinal.toString() ?? '' : '');
      elementsRow.push(this.luxonDatePipe.transform(
        LuxonUtils.toBackend(convocatoriaCalendarioJustificacion?.fechaInicioPresentacion, false), 'short') ?? '');
      elementsRow.push(this.luxonDatePipe.transform(
        LuxonUtils.toBackend(convocatoriaCalendarioJustificacion?.fechaFinPresentacion, false), 'short') ?? '');
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
