import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TIPO_SEGUIMIENTO_MAP } from '@core/enums/tipo-seguimiento';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
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

const PERIODO_SEGUIMIENTO_KEY = marker('csp.convocatoria-seguimiento-cientifico');
const PERIODO_SEGUIMIENTO_FIELD = 'periodoSeguimiento';
const PERIODO_SEGUIMIENTO_NUMERO_PERIODO_KEY = marker('csp.convocatoria-seguimiento-cientifico.numero-periodo');
const PERIODO_SEGUIMIENTO_NUMERO_FIELD = 'numeroPeriodoSeguimiento';
const PERIODO_SEGUIMIENTO_TIPO_KEY = marker('csp.convocatoria-seguimiento-cientifico.tipo-seguimiento');
const PERIODO_SEGUIMIENTO_TIPO_FIELD = 'tipoSeguimiento';
const PERIODO_SEGUIMIENTO_MES_INICIO_KEY = marker('csp.convocatoria-seguimiento-cientifico.mes-inicial');
const PERIODO_SEGUIMIENTO_FECHA_INICIO_FIELD = 'fechaInicioSeguimiento';
const PERIODO_SEGUIMIENTO_MES_FIN_KEY = marker('csp.convocatoria-seguimiento-cientifico.mes-final');
const PERIODO_SEGUIMIENTO_FECHA_FIN_FIELD = 'fechaFinSeguimiento';
const PERIODO_SEGUIMIENTO_FECHA_INICIO_PRESENTACION_KEY = marker('csp.convocatoria-seguimiento-cientifico.fecha-inicio-presentacion');
const PERIODO_SEGUIMIENTO_FECHA_INICIO_PRESENTACION_FIELD = 'fechaInicioPresentacion';
const PERIODO_SEGUIMIENTO_FECHA_FIN_PRESENTACION_KEY = marker('csp.convocatoria-seguimiento-cientifico.fecha-fin-presentacion');
const PERIODO_SEGUIMIENTO_FECHA_FIN_PRESENTACION_FIELD = 'fechaFinPresentacion';
const PERIODO_SEGUIMIENTO_PERIODO_KEY = marker('csp.proyecto-periodo-seguimiento-cientifico.informe-title-short');
const PERIODO_SEG_CIENTIFICO_PERIODO_KEY = marker('csp.proyecto-periodo-seguimiento-cientifico.informe-title');

@Injectable()
export class ConvocatoriaPeriodoSeguimientoListadoExportService
  extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly convocatoriaService: ConvocatoriaService,
    private luxonDatePipe: LuxonDatePipe
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return this.convocatoriaService.findSeguimientosCientificos(convocatoriaData?.convocatoria.id).pipe(
      map(response => {
        convocatoriaData.periodosSeguimientos = response.items;
        return convocatoriaData;
      })
    );
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsPeriodoSeguimientoNotExcel();
    } else {
      return this.getColumnsPeriodoSeguimientoExcel(convocatorias);
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
      ' (' + this.translate.instant(PERIODO_SEGUIMIENTO_NUMERO_PERIODO_KEY) +
      ' - ' + this.translate.instant(PERIODO_SEGUIMIENTO_TIPO_KEY) +
      ' - ' + this.translate.instant(PERIODO_SEGUIMIENTO_MES_INICIO_KEY) +
      ' - ' + this.translate.instant(PERIODO_SEGUIMIENTO_MES_FIN_KEY) +
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

  private getColumnsPeriodoSeguimientoExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumPeriodosSeguimiento = Math.max(...convocatorias.map(p => p.periodosSeguimientos?.length));
    const titlePeriodoSeguimiento = this.translate.instant(PERIODO_SEG_CIENTIFICO_PERIODO_KEY);

    for (let i = 0; i < maxNumPeriodosSeguimiento; i++) {
      const idPeriodoSeguimiento: string = String(i + 1);
      const titleColumn = titlePeriodoSeguimiento + idPeriodoSeguimiento + ': ';
      const columnNumeroPeriodoSeguimiento: ISgiColumnReport = {
        name: PERIODO_SEGUIMIENTO_NUMERO_FIELD + idPeriodoSeguimiento,
        title: titleColumn + this.translate.instant(PERIODO_SEGUIMIENTO_NUMERO_PERIODO_KEY),
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
        title: titleColumn + this.translate.instant(PERIODO_SEGUIMIENTO_MES_INICIO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaInicioPeriodoSeguimiento);

      const columnFechaFinPeriodoSeguimiento: ISgiColumnReport = {
        name: PERIODO_SEGUIMIENTO_FECHA_FIN_FIELD + idPeriodoSeguimiento,
        title: titleColumn + this.translate.instant(PERIODO_SEGUIMIENTO_MES_FIN_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaFinPeriodoSeguimiento);

      const columnFechaInicioPresentacionPeriodoSeguimiento: ISgiColumnReport = {
        name: PERIODO_SEGUIMIENTO_FECHA_INICIO_PRESENTACION_FIELD + idPeriodoSeguimiento,
        title: titleColumn + this.translate.instant(PERIODO_SEGUIMIENTO_FECHA_INICIO_PRESENTACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaInicioPresentacionPeriodoSeguimiento);

      const columnFechaFinPresentacionPeriodoSeguimiento: ISgiColumnReport = {
        name: PERIODO_SEGUIMIENTO_FECHA_FIN_PRESENTACION_FIELD + idPeriodoSeguimiento,
        title: titleColumn + this.translate.instant(PERIODO_SEGUIMIENTO_FECHA_FIN_PRESENTACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaFinPresentacionPeriodoSeguimiento);
    }

    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {

    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsPeriodoSeguimientoNotExcel(convocatoria, elementsRow);
    } else {
      const maxNumPeriodoSeguimientoes = Math.max(...convocatorias.map(p => p.periodosSeguimientos?.length));
      for (let i = 0; i < maxNumPeriodoSeguimientoes; i++) {
        const periodoSeguimiento = convocatoria.periodosSeguimientos[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, periodoSeguimiento);
      }
    }
    return elementsRow;
  }

  private fillRowsPeriodoSeguimientoNotExcel(convocatoria: IConvocatoriaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    convocatoria.periodosSeguimientos?.forEach(convocatoriaPeriodoSeguimiento => {
      const periodoSeguimientoElementsRow: any[] = [];

      let periodoSeguimientoContent = convocatoriaPeriodoSeguimiento?.numPeriodo ?? '';
      periodoSeguimientoContent += '\n';
      periodoSeguimientoContent += convocatoriaPeriodoSeguimiento?.tipoSeguimiento ?
        this.translate.instant(TIPO_SEGUIMIENTO_MAP.get(convocatoriaPeriodoSeguimiento.tipoSeguimiento)) : '';
      periodoSeguimientoContent += '\n';
      periodoSeguimientoContent += convocatoriaPeriodoSeguimiento?.mesInicial ? convocatoriaPeriodoSeguimiento?.mesInicial.toString() ?? '' : '';
      periodoSeguimientoContent += '\n';
      periodoSeguimientoContent += convocatoriaPeriodoSeguimiento?.mesFinal ? convocatoriaPeriodoSeguimiento?.mesFinal.toString() ?? '' : '';
      periodoSeguimientoContent += '\n';
      periodoSeguimientoContent +=
        this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoriaPeriodoSeguimiento?.fechaInicioPresentacion, false), 'short') ?? '';
      periodoSeguimientoContent += '\n';
      periodoSeguimientoContent +=
        this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoriaPeriodoSeguimiento?.fechaFinPresentacion, false), 'short') ?? '';
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

  private fillRowsEntidadExcel(elementsRow: any[], periodo: IConvocatoriaPeriodoSeguimientoCientifico) {
    if (periodo) {
      elementsRow.push(periodo.numPeriodo ?? '');
      elementsRow.push(periodo?.tipoSeguimiento ? this.translate.instant(TIPO_SEGUIMIENTO_MAP.get(periodo.tipoSeguimiento)) : '');
      elementsRow.push(periodo?.mesInicial ? periodo?.mesInicial.toString() ?? '' : '');
      elementsRow.push(periodo?.mesInicial ? periodo?.mesFinal.toString() ?? '' : '');
      elementsRow.push(this.luxonDatePipe.transform(LuxonUtils.toBackend(periodo?.fechaInicioPresentacion, false), 'short') ?? '');
      elementsRow.push(this.luxonDatePipe.transform(LuxonUtils.toBackend(periodo?.fechaFinPresentacion, false), 'short') ?? '');
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
