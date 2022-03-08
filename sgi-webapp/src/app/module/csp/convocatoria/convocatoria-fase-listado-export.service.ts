import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
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

const FASE_KEY = marker('csp.convocatoria-fase');
const FASE_FECHA_INICIO_KEY = marker('csp.convocatoria-fase.fecha-inicio');
const FASE_FECHA_FIN_KEY = marker('csp.convocatoria-fase.fecha-fin');
const FASE_TIPO_KEY = marker('csp.convocatoria-fase.tipo');

const FASE_FIELD = 'fase';
const FASE_TIPO_FIELD = 'faseTipo';
const FASE_FECHA_INICIO_FIELD = 'fechaInicio';
const FASE_FECHA_FIN_FIELD = 'fechaFin';

@Injectable()
export class ConvocatoriaFaseListadoExportService extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private convocatoriaService: ConvocatoriaService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return this.convocatoriaService.findAllConvocatoriaFases(convocatoriaData?.convocatoria?.id).pipe(
      map((response) => {
        convocatoriaData.fases = response.items;
        return convocatoriaData;
      })
    );
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsFaseNotExcel();
    } else {
      return this.getColumnsFaseExcel(convocatorias);
    }
  }

  private getColumnsFaseNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: FASE_FIELD,
      title: this.translate.instant(FASE_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(FASE_KEY, MSG_PARAMS.CARDINALIRY.PLURAL) +
      ' (' + this.translate.instant(FASE_FECHA_INICIO_KEY) +
      ' - ' + this.translate.instant(FASE_FECHA_FIN_KEY) +
      ' - ' + this.translate.instant(FASE_TIPO_KEY) +
      ')';
    const columnEntidad: ISgiColumnReport = {
      name: FASE_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsFaseExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumFasees = Math.max(...convocatorias.map(c => c.fases ? c.fases?.length : 0));
    const titleFase = this.translate.instant(FASE_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);

    for (let i = 0; i < maxNumFasees; i++) {
      const idFase: string = String(i + 1);

      const columnFechaInicio: ISgiColumnReport = {
        name: FASE_FECHA_INICIO_FIELD + idFase,
        title: titleFase + idFase + ': ' + this.translate.instant(FASE_FECHA_INICIO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaInicio);

      const columnFechaFin: ISgiColumnReport = {
        name: FASE_FECHA_FIN_FIELD + idFase,
        title: titleFase + idFase + ': ' + this.translate.instant(FASE_FECHA_FIN_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaFin);

      const columnTipoFase: ISgiColumnReport = {
        name: FASE_TIPO_FIELD + idFase,
        title: titleFase + idFase + ': ' + this.translate.instant(FASE_TIPO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTipoFase);
    }

    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsFaseNotExcel(convocatoria, elementsRow);
    } else {
      const maxNumFasees = Math.max(...convocatorias.map(c => c.fases ? c.fases?.length : 0));
      for (let i = 0; i < maxNumFasees; i++) {
        const fase = convocatoria.fases ? convocatoria.fases[i] ?? null : null;
        this.fillRowsEntidadExcel(elementsRow, fase);
      }
    }
    return elementsRow;
  }

  private fillRowsFaseNotExcel(convocatoria: IConvocatoriaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    convocatoria.fases?.forEach(convocatoriaFase => {
      const faseElementsRow: any[] = [];

      let faseContent = this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoriaFase?.fechaInicio, false), 'short') ?? '';
      faseContent += ' - ';
      faseContent += this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoriaFase?.fechaFin, false), 'short') ?? '';
      faseContent += ' - ';
      faseContent += convocatoriaFase?.tipoFase ? convocatoriaFase.tipoFase.nombre ?? '' : '';

      faseElementsRow.push(faseContent);

      const rowReport: ISgiRowReport = {
        elements: faseElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], convocatoriaFase: IConvocatoriaFase) {
    if (convocatoriaFase) {
      elementsRow.push(this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoriaFase?.fechaInicio, false), 'short') ?? '');
      elementsRow.push(this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoriaFase?.fechaFin, false), 'short') ?? '');
      elementsRow.push(convocatoriaFase.tipoFase ? convocatoriaFase.tipoFase?.nombre ?? '' : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
