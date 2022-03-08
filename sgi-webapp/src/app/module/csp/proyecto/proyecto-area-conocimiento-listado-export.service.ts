import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoAreaConocimiento } from '@core/models/csp/proyecto-area-conocimiento';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const AREAS_CONOCIMIENTO_KEY = marker('label.areas-conocimiento');
const AREA_CONOCIMIENTO_KEY = marker('title.csp.proyecto-area-conocimiento');
const AREA_CONOCIMIENTO_FIELD = 'areaConocimiento';

@Injectable()
export class ProyectoAreaConocimientoListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private areaConocimientoService: AreaConocimientoService
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    return this.proyectoService.findAllProyectoAreaConocimiento(proyectoData.id).pipe(
      switchMap(responseAreaConocimiento => {
        if (responseAreaConocimiento.items.length === 0) {
          return of(responseAreaConocimiento);
        }
        return from(responseAreaConocimiento.items).pipe(
          mergeMap(proyectoAreaConocimiento => {
            return this.getAreaConocimiento(proyectoAreaConocimiento);
          }),
          map(() => responseAreaConocimiento)
        );
      }),
      map(responseAreaConocimiento => {
        proyectoData.areasConocimiento = responseAreaConocimiento.items;
        return proyectoData;
      }),
      takeLast(1)
    );
  }

  private getAreaConocimiento(proyectoAreaConocimiento: IProyectoAreaConocimiento): Observable<IProyectoAreaConocimiento> {
    return this.areaConocimientoService.findById(proyectoAreaConocimiento.areaConocimiento.id).pipe(
      map(areaConocimiento => {
        proyectoAreaConocimiento.areaConocimiento = areaConocimiento;
        return proyectoAreaConocimiento;
      })
    );
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsAreaConocimientoNotExcel();
    } else {
      return this.getColumnsAreaConocimientoExcel(proyectos);
    }
  }

  private getColumnsAreaConocimientoNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: AREA_CONOCIMIENTO_FIELD,
      title: this.translate.instant(AREA_CONOCIMIENTO_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(AREAS_CONOCIMIENTO_KEY);
    const columnEntidad: ISgiColumnReport = {
      name: AREA_CONOCIMIENTO_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsAreaConocimientoExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumAreasConocimiento = Math.max(...proyectos.map(p => p.areasConocimiento?.length));
    const titleAreaConocimiento = this.translate.instant(AREA_CONOCIMIENTO_KEY);

    for (let i = 0; i < maxNumAreasConocimiento; i++) {
      const idAreaConocimiento: string = String(i + 1);
      const columnAreaConocimiento: ISgiColumnReport = {
        name: AREA_CONOCIMIENTO_FIELD + idAreaConocimiento,
        title: titleAreaConocimiento + idAreaConocimiento,
        type: ColumnType.STRING,
      };
      columns.push(columnAreaConocimiento);
    }

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = proyectos[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsAreaConocimientoNotExcel(proyecto, elementsRow);
    } else {
      const maxNumAreasConocimiento = Math.max(...proyectos.map(p => p.areasConocimiento?.length));
      for (let i = 0; i < maxNumAreasConocimiento; i++) {
        const areaConocimiento = proyecto.areasConocimiento[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, areaConocimiento);
      }
    }
    return elementsRow;
  }

  private fillRowsAreaConocimientoNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.areasConocimiento?.forEach(proyectoAreaConocimiento => {
      const areaConocimientoElementsRow: any[] = [];

      const areaConocimientoContent = proyectoAreaConocimiento.areaConocimiento?.nombre ?? '';
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

  private fillRowsEntidadExcel(elementsRow: any[], proyectoAreaConocimiento: IProyectoAreaConocimiento) {
    const content = proyectoAreaConocimiento ? proyectoAreaConocimiento.areaConocimiento?.nombre ?? '' : '';
    elementsRow.push(content);
  }
}
