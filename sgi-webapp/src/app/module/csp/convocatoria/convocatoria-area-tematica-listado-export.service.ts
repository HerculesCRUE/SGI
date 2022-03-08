import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

export interface IConvocatoriaAreaTematicaListadoExport {
  padre: IAreaTematica;
  convocatoriaAreaTematica: IConvocatoriaAreaTematica;
}

const AREAS_TEMATICAS_KEY = marker('menu.csp.configuraciones.areas-tematicas');
const AREA_TEMATICA_KEY = marker('csp.area-tematica.nombre');
const AREA_TEMATICA_FIELD = 'areaTematica';

@Injectable()
export class ConvocatoriaAreaTematicaListadoExportService
  extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly convocatoriaService: ConvocatoriaService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return this.convocatoriaService.findAreaTematicas(convocatoriaData.convocatoria.id).pipe(
      map(response => response.items),
      map(convocatoriasAreaTematicas => {
        const list: IConvocatoriaAreaTematicaListadoExport[] = [];
        convocatoriasAreaTematicas.forEach(
          convocatoriaAreaTematica => {
            const element: IConvocatoriaAreaTematicaListadoExport = {
              padre: undefined,
              convocatoriaAreaTematica,
            };
            list.push(this.loadAreaData(element));
          }
        );
        convocatoriaData.areasTematicas = list;
        return convocatoriaData;
      })
    );
  }

  private getSecondLevelAreaTematica(areaTematica: IAreaTematica): IAreaTematica {
    if (areaTematica?.padre?.padre) {
      return this.getSecondLevelAreaTematica(areaTematica.padre);
    }
    return areaTematica;
  }

  private loadAreaData(data: IConvocatoriaAreaTematicaListadoExport): IConvocatoriaAreaTematicaListadoExport {
    const areaTematica = data.convocatoriaAreaTematica.areaTematica;
    if (areaTematica) {
      const result = this.getSecondLevelAreaTematica(areaTematica);
      const padre = result.padre ? result.padre : areaTematica;
      const element: IConvocatoriaAreaTematicaListadoExport = {
        padre,
        convocatoriaAreaTematica: data.convocatoriaAreaTematica,
      };
      return element;
    }
    return data;
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsAreaTematicaNotExcel();
    } else {
      return this.getColumnsAreaTematicaExcel(convocatorias);
    }
  }

  private getColumnsAreaTematicaNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: AREA_TEMATICA_FIELD,
      title: this.translate.instant(AREA_TEMATICA_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(AREAS_TEMATICAS_KEY);
    const columnEntidad: ISgiColumnReport = {
      name: AREA_TEMATICA_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsAreaTematicaExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumAreasConocimiento = Math.max(...convocatorias.map(c => c.areasTematicas ? c.areasTematicas?.length : 0));
    const titleAreaTematica = this.translate.instant(AREA_TEMATICA_KEY);

    for (let i = 0; i < maxNumAreasConocimiento; i++) {
      const idAreaTematica: string = String(i + 1);
      const columnAreaTematica: ISgiColumnReport = {
        name: AREA_TEMATICA_FIELD + idAreaTematica,
        title: titleAreaTematica + idAreaTematica,
        type: ColumnType.STRING,
      };
      columns.push(columnAreaTematica);
    }

    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsAreaTematicaNotExcel(convocatoria, elementsRow);
    } else {
      const maxNumAreasConocimiento = Math.max(...convocatorias.map(c => c.areasTematicas ? c.areasTematicas?.length : 0));
      for (let i = 0; i < maxNumAreasConocimiento; i++) {
        const areaTematica = convocatoria.areasTematicas ? convocatoria.areasTematicas[i] : null;
        this.fillRowsEntidadExcel(elementsRow, areaTematica);
      }
    }
    return elementsRow;
  }

  private fillRowsAreaTematicaNotExcel(convocatoria: IConvocatoriaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    convocatoria.areasTematicas?.forEach(data => {
      const areaConocimientoElementsRow: any[] = [];

      const areaConocimientoContent = data.convocatoriaAreaTematica.areaTematica?.padre ?
        (data.convocatoriaAreaTematica.areaTematica?.nombre + ' - ' + data.convocatoriaAreaTematica.areaTematica?.padre.nombre) :
        (data.convocatoriaAreaTematica.areaTematica?.nombre ?? '');
      areaConocimientoElementsRow.push(areaConocimientoContent);

      const rowReport: ISgiRowReport = {
        elements: areaConocimientoElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], data: IConvocatoriaAreaTematicaListadoExport) {
    if (data && data.convocatoriaAreaTematica) {
      const content = data?.convocatoriaAreaTematica && data.convocatoriaAreaTematica.areaTematica?.padre ?
        (data.convocatoriaAreaTematica.areaTematica?.nombre + ' - ' + data.convocatoriaAreaTematica.areaTematica?.padre.nombre) :
        (data.convocatoriaAreaTematica?.areaTematica?.nombre ?? '');
      elementsRow.push(content);
    } else {
      elementsRow.push('');
    }
  }
}
