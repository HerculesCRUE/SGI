import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoEquipoInstrumental } from '@core/models/csp/grupo-equipo-instrumental';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { LanguageService } from '@core/services/language.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IGrupoReportData, IGrupoReportOptions } from './grupo-listado-export.service';

const EQUIPO_INSTRUMENTAL_KEY = marker('csp.grupo-equipo.equipo-instrumental');
const EQUIPO_INSTRUMENTAL_NOMBRE_KEY = marker('csp.grupo-equipo-instrumental.nombre');
const EQUIPO_INSTRUMENTAL_NUM_REGISTRO_KEY = marker('csp.grupo-equipo-instrumental.numero-registro');

const EQUIPO_INSTRUMENTAL_FIELD = 'equipoInstrumental';
const EQUIPO_INSTRUMENTAL_NOMBRE_FIELD = 'equipoInstrumentalNombre';
const EQUIPO_INSTRUMENTAL_NUM_REGISTRO_FIELD = 'equipoInstrumentalNumRegistro';

@Injectable()
export class GrupoEquipoInstrumentalListadoExportService extends AbstractTableExportFillService<IGrupoReportData, IGrupoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private grupoService: GrupoService,
    private readonly languageService: LanguageService

  ) {
    super(translate);
  }

  public getData(grupoData: IGrupoReportData): Observable<IGrupoReportData> {
    return this.grupoService.findEquiposInstrumentales(grupoData?.id).pipe(
      map((response) => {
        grupoData.equiposInstrumentales = response.items;
        return grupoData;
      })
    );
  }

  public fillColumns(
    grupos: IGrupoReportData[],
    reportConfig: IReportConfig<IGrupoReportOptions>
  ): ISgiColumnReport[] {
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsEquipoInstrumentalExcel(grupos);
    }
  }


  private getColumnsEquipoInstrumentalExcel(grupos: IGrupoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEquipoInstrumentales = Math.max(...grupos.map(g => g.equiposInstrumentales ? g.equiposInstrumentales?.length : 0));
    const titleEquipoInstrumental = this.translate.instant(EQUIPO_INSTRUMENTAL_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);

    for (let i = 0; i < maxNumEquipoInstrumentales; i++) {
      const idEquipoInstrumental: string = String(i + 1);

      const columnNombre: ISgiColumnReport = {
        name: EQUIPO_INSTRUMENTAL_NOMBRE_FIELD + idEquipoInstrumental,
        title: titleEquipoInstrumental + idEquipoInstrumental + ': ' + this.translate.instant(EQUIPO_INSTRUMENTAL_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombre);

      const columnNumRegistro: ISgiColumnReport = {
        name: EQUIPO_INSTRUMENTAL_NUM_REGISTRO_FIELD + idEquipoInstrumental,
        title: titleEquipoInstrumental + idEquipoInstrumental + ': ' + this.translate.instant(EQUIPO_INSTRUMENTAL_NUM_REGISTRO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNumRegistro);
    }

    return columns;
  }

  public fillRows(grupos: IGrupoReportData[], index: number, reportConfig: IReportConfig<IGrupoReportOptions>): any[] {
    const grupo = grupos[index];

    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumEquipoInstrumentales = Math.max(...grupos.map(g => g.equiposInstrumentales ? g.equiposInstrumentales?.length : 0));
      for (let i = 0; i < maxNumEquipoInstrumentales; i++) {
        const equipoInstrumental = grupo.equiposInstrumentales ? grupo.equiposInstrumentales[i] ?? null : null;
        this.fillRowsEntidadExcel(elementsRow, equipoInstrumental);
      }
    }
    return elementsRow;
  }

  private fillRowsEntidadExcel(elementsRow: any[], grupoEquipoInstrumental: IGrupoEquipoInstrumental) {
    if (grupoEquipoInstrumental) {
      elementsRow.push(this.languageService.getFieldValue(grupoEquipoInstrumental.nombre));
      elementsRow.push(grupoEquipoInstrumental.numRegistro ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
