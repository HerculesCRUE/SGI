import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TIPO_PARTIDA_MAP } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

const PARTIDA_PRESUPUESTARIA_KEY = marker('csp.convocatoria-partida-presupuestaria');
const PARTIDA_PRESUPUESTARIA_CODIGO_KEY = marker('csp.convocatoria-partida-presupuestaria.codigo');
const PARTIDA_KEY = marker('csp.convocatoria-partida-presupuestaria.partida');
const PARTIDA_PRESUPUESTARIA_FIELD = 'partidaPresupuestaria';
const PARTIDA_PRESUPUESTARIA_TIPO_KEY = marker('csp.convocatoria-partida-presupuestaria.tipo-partida');
const PARTIDA_PRESUPUESTARIA_TIPO_FIELD = 'tipoPartidaPresupuestaria';

@Injectable()
export class ConvocatoriaPartidaPresupuestariaListadoExportService
  extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly convocatoriaService: ConvocatoriaService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.convocatoriaService.findPartidasPresupuestarias(convocatoriaData?.convocatoria?.id, findOptions).pipe(
      map((responsePartidaPresupuestaria) => {
        convocatoriaData.partidasPresupuestarias = [];
        return responsePartidaPresupuestaria;
      }),
      switchMap(responsePartidaPresupuestaria => {
        if (responsePartidaPresupuestaria.total === 0) {
          return of(convocatoriaData);
        }

        convocatoriaData.partidasPresupuestarias = responsePartidaPresupuestaria.items;
        return of(convocatoriaData);
      })
    );
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {

    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsPartidaPresupuestariaNotExcel();
    } else {
      return this.getColumnsPartidaPresupuestariaExcel(convocatorias);
    }
  }

  private getColumnsPartidaPresupuestariaNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: PARTIDA_PRESUPUESTARIA_FIELD,
      title: this.translate.instant(PARTIDA_PRESUPUESTARIA_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(PARTIDA_PRESUPUESTARIA_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) +
      ' (' + this.translate.instant(PARTIDA_PRESUPUESTARIA_CODIGO_KEY) +
      ' - ' + this.translate.instant(PARTIDA_PRESUPUESTARIA_TIPO_KEY) +
      ')';
    const columnPartidaPresupuestaria: ISgiColumnReport = {
      name: PARTIDA_PRESUPUESTARIA_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnPartidaPresupuestaria];
  }

  private getColumnsPartidaPresupuestariaExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumPartidaPresupuestarias = Math.max(...convocatorias.map(p => p.partidasPresupuestarias?.length));
    const titlePartidaPresupuestaria = this.translate.instant(PARTIDA_PRESUPUESTARIA_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);
    for (let i = 0; i < maxNumPartidaPresupuestarias; i++) {
      const idPartidaPresupuestaria: string = String(i + 1);
      const columnNombrePartidaPresupuestaria: ISgiColumnReport = {
        name: PARTIDA_PRESUPUESTARIA_FIELD + idPartidaPresupuestaria,
        title: titlePartidaPresupuestaria + ': ' + this.translate.instant(PARTIDA_KEY) + idPartidaPresupuestaria,
        type: ColumnType.STRING,
      };
      columns.push(columnNombrePartidaPresupuestaria);

      const columnTipoPartidaPresupuestaria: ISgiColumnReport = {
        name: PARTIDA_PRESUPUESTARIA_TIPO_FIELD + idPartidaPresupuestaria,
        title: titlePartidaPresupuestaria + ': ' + this.translate.instant(PARTIDA_PRESUPUESTARIA_TIPO_KEY) + ' ' + this.translate.instant(PARTIDA_KEY) + idPartidaPresupuestaria,
        type: ColumnType.STRING,
      };
      columns.push(columnTipoPartidaPresupuestaria);
    }
    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {

    const convocatoria = convocatorias[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsPartidaPresupuestariaNotExcel(convocatoria, elementsRow);
    } else {
      const maxNumPartidaPresupuestaria = Math.max(...convocatorias.map(p => p.partidasPresupuestarias?.length));
      for (let i = 0; i < maxNumPartidaPresupuestaria; i++) {
        const partidaPresupuestaria = convocatoria.partidasPresupuestarias[i] ?? null;
        this.fillRowsPartidaPresupuestariaExcel(elementsRow, partidaPresupuestaria);
      }
    }
    return elementsRow;
  }

  private fillRowsPartidaPresupuestariaNotExcel(convocatoria: IConvocatoriaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    convocatoria.partidasPresupuestarias?.forEach(partidaPresupuestaria => {
      const partidaPresupuestariaElementsRow: any[] = [];

      let partidaPresupuestariaTable = String(partidaPresupuestaria?.codigo);
      partidaPresupuestariaTable += ' - ';
      partidaPresupuestariaTable += partidaPresupuestaria?.tipoPartida ?
        this.translate.instant(TIPO_PARTIDA_MAP.get(partidaPresupuestaria?.tipoPartida)) : '';

      partidaPresupuestariaElementsRow.push(partidaPresupuestariaTable);

      const rowReport: ISgiRowReport = {
        elements: partidaPresupuestariaElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsPartidaPresupuestariaExcel(elementsRow: any[], partidaPresupuestaria: IConvocatoriaPartidaPresupuestaria) {
    if (partidaPresupuestaria) {
      elementsRow.push(partidaPresupuestaria.codigo ?? '');
      elementsRow.push(partidaPresupuestaria?.tipoPartida ?
        this.translate.instant(TIPO_PARTIDA_MAP.get(partidaPresupuestaria?.tipoPartida)) : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
