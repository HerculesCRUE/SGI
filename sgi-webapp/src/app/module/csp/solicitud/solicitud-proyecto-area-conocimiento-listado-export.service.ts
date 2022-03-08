import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ISolicitudProyectoAreaConocimiento } from '@core/models/csp/solicitud-proyecto-area-conocimiento';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { ISolicitudReportData, ISolicitudReportOptions } from './solicitud-listado-export.service';

const AREAS_CONOCIMIENTO_KEY = marker('label.areas-conocimiento');
const AREA_CONOCIMIENTO_KEY = marker('title.csp.proyecto-area-conocimiento');
const AREA_CONOCIMIENTO_FIELD = 'areaConocimiento';
const PROYECTO_KEY = marker('menu.csp.solicitudes.datos-proyecto');

@Injectable()
export class SolicitudProyectoAreaConocimientoListadoExportService
  extends AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
    private areaConocimientoService: AreaConocimientoService
  ) {
    super(translate);
  }

  public getData(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    return this.solicitudService.findAllSolicitudProyectoAreaConocimiento(solicitudData?.id).pipe(
      switchMap(responseAreaConocimiento => {
        if (responseAreaConocimiento.items.length === 0) {
          return of(responseAreaConocimiento);
        }
        return from(responseAreaConocimiento.items).pipe(
          mergeMap(solicitudAreaConocimiento => {
            return this.getAreaConocimiento(solicitudAreaConocimiento);
          }),
          map(() => responseAreaConocimiento)
        );
      }),
      map(responseAreaConocimiento => {
        solicitudData.areasConocimiento = responseAreaConocimiento.items;
        return solicitudData;
      }),
      takeLast(1)
    );
  }

  private getAreaConocimiento(solicitudAreaConocimiento: ISolicitudProyectoAreaConocimiento): Observable<ISolicitudProyectoAreaConocimiento> {
    return this.areaConocimientoService.findById(solicitudAreaConocimiento.areaConocimiento.id).pipe(
      map(areaConocimiento => {
        solicitudAreaConocimiento.areaConocimiento = areaConocimiento;
        return solicitudAreaConocimiento;
      })
    );
  }

  public fillColumns(
    solicitudes: ISolicitudReportData[],
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsAreaConocimientoNotExcel();
    } else {
      return this.getColumnsAreaConocimientoExcel(solicitudes);
    }
  }

  private getColumnsAreaConocimientoNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: AREA_CONOCIMIENTO_FIELD,
      title: this.translate.instant(AREA_CONOCIMIENTO_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(PROYECTO_KEY) + ': ' + this.translate.instant(AREAS_CONOCIMIENTO_KEY);
    const columnEntidad: ISgiColumnReport = {
      name: AREA_CONOCIMIENTO_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsAreaConocimientoExcel(solicitudes: ISolicitudReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumAreasConocimiento = Math.max(...solicitudes.map(s => s.areasConocimiento ? s.areasConocimiento?.length : 0));
    const titleAreaConocimiento = this.translate.instant(PROYECTO_KEY) + ': ' + this.translate.instant(AREA_CONOCIMIENTO_KEY);

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

  public fillRows(solicitudes: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {
    const solicitud = solicitudes[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsAreaConocimientoNotExcel(solicitud, elementsRow);
    } else {
      const maxNumAreasConocimiento = Math.max(...solicitudes.map(s => s.areasConocimiento ? s.areasConocimiento?.length : 0));
      for (let i = 0; i < maxNumAreasConocimiento; i++) {
        const areaConocimiento = solicitud.areasConocimiento && solicitud.areasConocimiento.length > 0 ? solicitud.areasConocimiento[i] : null;
        this.fillRowsEntidadExcel(elementsRow, areaConocimiento);
      }
    }
    return elementsRow;
  }

  private fillRowsAreaConocimientoNotExcel(solicitud: ISolicitudReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    solicitud.areasConocimiento?.forEach(solicitudAreaConocimiento => {
      const areaConocimientoElementsRow: any[] = [];

      const areaConocimientoContent = solicitudAreaConocimiento.areaConocimiento?.nombre ?? '';
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

  private fillRowsEntidadExcel(elementsRow: any[], solicitudAreaConocimiento: ISolicitudProyectoAreaConocimiento) {
    const content = solicitudAreaConocimiento ? solicitudAreaConocimiento.areaConocimiento?.nombre ?? '' : '';
    elementsRow.push(content);
  }
}
