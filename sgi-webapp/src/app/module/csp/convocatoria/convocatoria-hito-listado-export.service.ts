import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
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

const HITO_KEY = marker('csp.hito');
const HITO_FECHA_KEY = marker('csp.hito.fecha');
const HITO_TIPO_KEY = marker('csp.hito.tipo');

const HITO_FIELD = 'hito';
const HITO_TIPO_FIELD = 'hitoTipo';
const HITO_URL_FIELD = 'hitoUrl';

@Injectable()
export class ConvocatoriaHitoListadoExportService extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private convocatoriaService: ConvocatoriaService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return this.convocatoriaService.findHitosConvocatoria(convocatoriaData?.convocatoria?.id).pipe(
      map((response) => {
        convocatoriaData.hitos = response.items;
        return convocatoriaData;
      })
    );
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsHitoNotExcel();
    } else {
      return this.getColumnsHitoExcel(convocatorias);
    }
  }

  private getColumnsHitoNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: HITO_FIELD,
      title: this.translate.instant(HITO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(HITO_KEY, MSG_PARAMS.CARDINALIRY.PLURAL) +
      ' (' + this.translate.instant(HITO_TIPO_KEY) +
      ' - ' + this.translate.instant(HITO_FECHA_KEY) +
      ')';
    const columnEntidad: ISgiColumnReport = {
      name: HITO_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsHitoExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumHitoes = Math.max(...convocatorias.map(c => c.hitos ? c.hitos?.length : 0));
    const titleHito = this.translate.instant(HITO_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);

    for (let i = 0; i < maxNumHitoes; i++) {
      const idHito: string = String(i + 1);

      const columnTipoHito: ISgiColumnReport = {
        name: HITO_TIPO_FIELD + idHito,
        title: titleHito + idHito + ': ' + this.translate.instant(HITO_TIPO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTipoHito);

      const columnFecha: ISgiColumnReport = {
        name: HITO_URL_FIELD + idHito,
        title: titleHito + idHito + ': ' + this.translate.instant(HITO_FECHA_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFecha);
    }

    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsHitoNotExcel(convocatoria, elementsRow);
    } else {
      const maxNumHitoes = Math.max(...convocatorias.map(c => c.hitos ? c.hitos?.length : 0));
      for (let i = 0; i < maxNumHitoes; i++) {
        const hito = convocatoria.hitos ? convocatoria.hitos[i] ?? null : null;
        this.fillRowsEntidadExcel(elementsRow, hito);
      }
    }
    return elementsRow;
  }

  private fillRowsHitoNotExcel(convocatoria: IConvocatoriaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    convocatoria.hitos?.forEach(convocatoriaHito => {
      const hitoElementsRow: any[] = [];

      let hitoContent = convocatoriaHito?.tipoHito ? convocatoriaHito.tipoHito?.nombre ?? '' : '';
      hitoContent += ' - ';
      hitoContent += this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoriaHito?.fecha, false), 'short') ?? '';

      hitoElementsRow.push(hitoContent);

      const rowReport: ISgiRowReport = {
        elements: hitoElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], convocatoriaHito: IConvocatoriaHito) {
    if (convocatoriaHito) {
      elementsRow.push(convocatoriaHito.tipoHito ? convocatoriaHito.tipoHito?.nombre ?? '' : '');
      elementsRow.push(this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoriaHito?.fecha, false), 'short') ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
