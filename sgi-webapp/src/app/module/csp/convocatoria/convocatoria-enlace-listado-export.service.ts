import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { LanguageService } from '@core/services/language.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

const ENLACE_KEY = marker('csp.convocatoria-enlace');
const ENLACE_TIPO_KEY = marker('csp.convocatoria-enlace.tipo-enlace');
const ENLACE_URL_KEY = marker('csp.convocatoria-enlace.url');

const ENLACE_TIPO_FIELD = 'enlaceTipo';
const ENLACE_URL_FIELD = 'enlaceUrl';

@Injectable()
export class ConvocatoriaEnlaceListadoExportService extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private convocatoriaService: ConvocatoriaService,
    private languageService: LanguageService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return this.convocatoriaService.getEnlaces(convocatoriaData?.convocatoria?.id).pipe(
      map((response) => {
        convocatoriaData.enlaces = response.items;
        return convocatoriaData;
      })
    );
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsEnlaceExcel(convocatorias);
    }
  }

  private getColumnsEnlaceExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEnlacees = Math.max(...convocatorias.map(c => c.enlaces ? c.enlaces?.length : 0));
    const titleEnlace = this.translate.instant(ENLACE_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);

    for (let i = 0; i < maxNumEnlacees; i++) {
      const idEnlace: string = String(i + 1);

      const columnUrl: ISgiColumnReport = {
        name: ENLACE_URL_FIELD + idEnlace,
        title: titleEnlace + idEnlace + ': ' + this.translate.instant(ENLACE_URL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnUrl);

      const columnTipoEnlace: ISgiColumnReport = {
        name: ENLACE_TIPO_FIELD + idEnlace,
        title: titleEnlace + idEnlace + ': ' + this.translate.instant(ENLACE_TIPO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTipoEnlace);
    }

    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumEnlacees = Math.max(...convocatorias.map(c => c.enlaces ? c.enlaces?.length : 0));
      for (let i = 0; i < maxNumEnlacees; i++) {
        const enlace = convocatoria.enlaces ? convocatoria.enlaces[i] ?? null : null;
        this.fillRowsEntidadExcel(elementsRow, enlace);
      }
    }
    return elementsRow;
  }

  private fillRowsEntidadExcel(elementsRow: any[], convocatoriaEnlace: IConvocatoriaEnlace) {
    if (convocatoriaEnlace) {
      elementsRow.push(convocatoriaEnlace.url ?? '');
      elementsRow.push(convocatoriaEnlace.tipoEnlace ? convocatoriaEnlace.tipoEnlace?.nombre ? this.languageService.getFieldValue(convocatoriaEnlace.tipoEnlace?.nombre) : '' : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
