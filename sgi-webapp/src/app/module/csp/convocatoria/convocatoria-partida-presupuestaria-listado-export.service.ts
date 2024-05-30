import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TIPO_PARTIDA_MAP, TipoPartida } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PartidaPresupuestariaGastoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-gasto-sge.service';
import { PartidaPresupuestariaIngresoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-ingreso-sge.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, from, of } from 'rxjs';
import { map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

const PARTIDA_PRESUPUESTARIA_KEY = marker('csp.convocatoria-partida-presupuestaria');
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
    private readonly convocatoriaService: ConvocatoriaService,
    private readonly partidaPresupuestariaGastoSgeService: PartidaPresupuestariaGastoSgeService,
    private readonly partidaPresupuestariaIngresoSgeService: PartidaPresupuestariaIngresoSgeService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.convocatoriaService.findPartidasPresupuestarias(convocatoriaData?.convocatoria?.id, findOptions).pipe(
      map(responsePartidaPresupuestaria => responsePartidaPresupuestaria.items),
      switchMap(convocatoriaPartidas =>
        from(convocatoriaPartidas).pipe(
          mergeMap(convocatoriaPartidaPresupuestaria => {
            return this.getPartidaPresupuestariaSge(convocatoriaPartidaPresupuestaria.partidaSge?.id, convocatoriaPartidaPresupuestaria.tipoPartida).pipe(
              map(partidaPresupuestariaSge => {
                convocatoriaPartidaPresupuestaria.partidaSge = partidaPresupuestariaSge;
                return convocatoriaPartidaPresupuestaria;
              })
            )
          }),
          toArray(),
          map(() => {
            return convocatoriaPartidas;
          })
        )
      ),
      switchMap(proyectoPartidas => {
        convocatoriaData.partidasPresupuestarias = proyectoPartidas;
        return of(convocatoriaData);
      })
    );
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {
    return this.getColumnsPartidaPresupuestariaExcel(convocatorias);
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
    const maxNumPartidaPresupuestaria = Math.max(...convocatorias.map(p => p.partidasPresupuestarias?.length));
    for (let i = 0; i < maxNumPartidaPresupuestaria; i++) {
      const partidaPresupuestaria = convocatoria.partidasPresupuestarias[i] ?? null;
      this.fillRowsPartidaPresupuestariaExcel(elementsRow, partidaPresupuestaria);
    }

    return elementsRow;
  }

  private fillRowsPartidaPresupuestariaExcel(elementsRow: any[], partidaPresupuestaria: IConvocatoriaPartidaPresupuestaria) {
    if (partidaPresupuestaria) {
      elementsRow.push(partidaPresupuestaria.codigo ?? partidaPresupuestaria.partidaSge?.codigo ?? '');
      elementsRow.push(partidaPresupuestaria?.tipoPartida ?
        this.translate.instant(TIPO_PARTIDA_MAP.get(partidaPresupuestaria?.tipoPartida)) : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private getPartidaPresupuestariaSge(partidaSgeId: string, tipo: TipoPartida): Observable<IPartidaPresupuestariaSge> {
    if (!partidaSgeId || !tipo) {
      return of(null);
    }

    if (tipo === TipoPartida.GASTO) {
      return this.partidaPresupuestariaGastoSgeService.findById(partidaSgeId);
    } else {
      return this.partidaPresupuestariaIngresoSgeService.findById(partidaSgeId);
    }
  }

}
