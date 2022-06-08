import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoEnlace } from '@core/models/csp/grupo-enlace';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IGrupoReportData, IGrupoReportOptions } from './grupo-listado-export.service';

const ENLACE_KEY = marker('csp.grupo-enlace');
const ENLACE_URL_KEY = marker('csp.grupo-enlace.enlace');

const ENLACE_FIELD = 'enlace';
const ENLACE_URL_FIELD = 'enlaceUrl';

@Injectable()
export class GrupoEnlaceListadoExportService extends AbstractTableExportFillService<IGrupoReportData, IGrupoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private grupoService: GrupoService
  ) {
    super(translate);
  }

  public getData(grupoData: IGrupoReportData): Observable<IGrupoReportData> {
    return this.grupoService.findEnlaces(grupoData?.id).pipe(
      map((response) => {
        grupoData.enlaces = response.items;
        return grupoData;
      })
    );
  }

  public fillColumns(
    grupos: IGrupoReportData[],
    reportConfig: IReportConfig<IGrupoReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsEnlaceNotExcel();
    } else {
      return this.getColumnsEnlaceExcel(grupos);
    }
  }

  private getColumnsEnlaceNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: ENLACE_FIELD,
      title: this.translate.instant(ENLACE_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(ENLACE_KEY, MSG_PARAMS.CARDINALIRY.PLURAL) +
      ' (' + this.translate.instant(ENLACE_URL_KEY) + ')';
    const columnEntidad: ISgiColumnReport = {
      name: ENLACE_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsEnlaceExcel(grupos: IGrupoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEnlacees = Math.max(...grupos.map(g => g.enlaces ? g.enlaces?.length : 0));
    const titleEnlace = this.translate.instant(ENLACE_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);

    for (let i = 0; i < maxNumEnlacees; i++) {
      const idEnlace: string = String(i + 1);

      const columnUrl: ISgiColumnReport = {
        name: ENLACE_URL_FIELD + idEnlace,
        title: titleEnlace + idEnlace + ': ' + this.translate.instant(ENLACE_URL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnUrl);
    }

    return columns;
  }

  public fillRows(grupos: IGrupoReportData[], index: number, reportConfig: IReportConfig<IGrupoReportOptions>): any[] {
    const grupo = grupos[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsEnlaceNotExcel(grupo, elementsRow);
    } else {
      const maxNumEnlacees = Math.max(...grupos.map(g => g.enlaces ? g.enlaces?.length : 0));
      for (let i = 0; i < maxNumEnlacees; i++) {
        const enlace = grupo.enlaces ? grupo.enlaces[i] ?? null : null;
        this.fillRowsEntidadExcel(elementsRow, enlace);
      }
    }
    return elementsRow;
  }

  private fillRowsEnlaceNotExcel(grupo: IGrupoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    grupo.enlaces?.forEach(grupoEnlace => {
      const enlaceElementsRow: any[] = [];

      enlaceElementsRow.push(grupoEnlace?.enlace ?? '');

      const rowReport: ISgiRowReport = {
        elements: enlaceElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], grupoEnlace: IGrupoEnlace) {
    if (grupoEnlace) {
      elementsRow.push(grupoEnlace.enlace ?? '');
    } else {
      elementsRow.push('');
    }
  }
}
