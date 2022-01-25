import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TIPO_PARTIDA_MAP } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const PARTIDA_PRESUPUESTARIA_KEY = 'csp.proyecto-partida-presupuestaria';
const PARTIDA_PRESUPUESTARIA_CODIGO_KEY = 'csp.proyecto-partida-presupuestaria.codigo';
const PARTIDA_PRESUPUESTARIA_FIELD = 'partidaPresupuestaria';
const PARTIDA_PRESUPUESTARIA_TIPO_KEY = marker('csp.proyecto-partida-presupuestaria.tipo-partida');
const PARTIDA_PRESUPUESTARIA_TIPO_FIELD = 'tipoPartidaPresupuestaria';

@Injectable()
export class ProyectoPartidaPresupuestariaListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private readonly proyectoService: ProyectoService
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.proyectoService.findAllProyectoPartidas(proyectoData.id, findOptions).pipe(
      map((responsePartidaPresupuestaria) => {
        proyectoData.partidasPresupuestarias = [];
        return responsePartidaPresupuestaria;
      }),
      switchMap(responsePartidaPresupuestaria => {
        if (responsePartidaPresupuestaria.total === 0) {
          return of(proyectoData);
        }

        proyectoData.partidasPresupuestarias = responsePartidaPresupuestaria.items;
        return of(proyectoData);
      })
    );
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {

    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsPartidaPresupuestariaNotExcel();
    } else {
      return this.getColumnsPartidaPresupuestariaExcel(proyectos);
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

  private getColumnsPartidaPresupuestariaExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumPartidaPresupuestarias = Math.max(...proyectos.map(p => p.partidasPresupuestarias?.length));
    const titlePartidaPresupuestaria = this.translate.instant(PARTIDA_PRESUPUESTARIA_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);
    for (let i = 0; i < maxNumPartidaPresupuestarias; i++) {
      const idPartidaPresupuestaria: string = String(i + 1);
      const columnNombrePartidaPresupuestaria: ISgiColumnReport = {
        name: PARTIDA_PRESUPUESTARIA_FIELD + idPartidaPresupuestaria,
        title: titlePartidaPresupuestaria + idPartidaPresupuestaria + ': ' + this.translate.instant(PARTIDA_PRESUPUESTARIA_CODIGO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombrePartidaPresupuestaria);

      const columnTipoPartidaPresupuestaria: ISgiColumnReport = {
        name: PARTIDA_PRESUPUESTARIA_TIPO_FIELD + idPartidaPresupuestaria,
        title: titlePartidaPresupuestaria + idPartidaPresupuestaria + ': ' + this.translate.instant(PARTIDA_PRESUPUESTARIA_TIPO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTipoPartidaPresupuestaria);
    }
    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {

    const proyecto = proyectos[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsPartidaPresupuestariaNotExcel(proyecto, elementsRow);
    } else {
      const maxNumPartidaPresupuestaria = Math.max(...proyectos.map(p => p.partidasPresupuestarias?.length));
      for (let i = 0; i < maxNumPartidaPresupuestaria; i++) {
        const partidaPresupuestaria = proyecto.partidasPresupuestarias[i] ?? null;
        this.fillRowsPartidaPresupuestariaExcel(elementsRow, partidaPresupuestaria);
      }
    }
    return elementsRow;
  }

  private fillRowsPartidaPresupuestariaNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.partidasPresupuestarias?.forEach(partidaPresupuestaria => {
      const partidaPresupuestariaElementsRow: any[] = [];

      let partidaPresupuestariaTable = String(partidaPresupuestaria?.codigo);
      partidaPresupuestariaTable += '\n';
      partidaPresupuestariaTable += partidaPresupuestaria?.tipoPartida ?
        this.translate.instant(TIPO_PARTIDA_MAP.get(partidaPresupuestaria?.tipoPartida)) : '';
      partidaPresupuestariaTable += '\n';

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

  private fillRowsPartidaPresupuestariaExcel(elementsRow: any[], partidaPresupuestaria: IProyectoPartida) {
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
