import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ISolicitudReportData, ISolicitudReportOptions } from './solicitud-listado-export.service';

const PROYECTO_FIELD = 'proyecto';
const PROYECTO_KEY = marker('menu.csp.solicitudes.datos-proyecto');
const REFERENCIA_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.codigo-externo');
const ACRONIMO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.acronimo');
const DURACION_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.duracion');
const COORDINADO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.proyecto-coordinado');
const COORDINADOR_EXTERNO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.coordinador-externo');
const COLABORATIVO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.proyecto-colaborativo');
const AREA_TEMATICA_KEY = marker('csp.area-tematica.nombre');

@Injectable()
export class SolicitudProyectoFichaGeneralListadoExportService extends
  AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
  ) {
    super(translate);
  }

  public getData(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    return this.solicitudService.findSolicitudProyecto(solicitudData.id).pipe(
      map(solicitudProyecto => {
        solicitudData.proyecto = solicitudProyecto;
        return solicitudData;
      })
    );
  }


  public fillColumns(
    solicitudes: ISolicitudReportData[],
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): ISgiColumnReport[] {

    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsProyectoNotExcel();
    } else {
      return this.getColumnsProyectoExcel();
    }
  }

  private getColumnsProyectoNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: PROYECTO_FIELD,
      title: this.translate.instant(PROYECTO_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(PROYECTO_KEY) +
      ' (' + this.translate.instant(REFERENCIA_KEY) +
      ' - ' + this.translate.instant(ACRONIMO_KEY) +
      ' - ' + this.translate.instant(DURACION_KEY) +
      ' - ' + this.translate.instant(COORDINADO_KEY) +
      ' - ' + this.translate.instant(COORDINADOR_EXTERNO_KEY) +
      ' - ' + this.translate.instant(COLABORATIVO_KEY) +
      ' - ' + this.translate.instant(AREA_TEMATICA_KEY) +
      ')';
    const columnProyecto: ISgiColumnReport = {
      name: PROYECTO_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnProyecto];
  }

  public getColumnsProyectoExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(REFERENCIA_KEY),
        name: 'codigoExterno',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(ACRONIMO_KEY),
        name: 'acronimo',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(DURACION_KEY),
        name: 'duracion',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(COORDINADO_KEY),
        name: 'coordinado',
        type: ColumnType.STRING,
      },
      {
        title: this.translate.instant(COORDINADOR_EXTERNO_KEY),
        name: 'coordinadorExterno',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(COLABORATIVO_KEY),
        name: 'colaborativo',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(AREA_TEMATICA_KEY),
        name: 'areaTematica',
        type: ColumnType.STRING,
        format: '#'
      },
    ];

    return columns;
  }

  public fillRows(solicitudes: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {

    const solicitud = solicitudes[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsProyectoNotExcel(solicitud, elementsRow);
    } else {
      this.fillRowsProyectoExcel(elementsRow, solicitud.proyecto);
    }
    return elementsRow;
  }


  private fillRowsProyectoNotExcel(solicitud: ISolicitudReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];
    if (solicitud.proyecto) {
      const proyecto = solicitud.proyecto;
      const proyectoElementsRow: any[] = [];

      let proyectoTable = proyecto?.codExterno ?? '';
      proyectoTable += '\n';
      proyectoTable += proyecto?.acronimo ?? '';
      proyectoTable += '\n';
      proyectoTable += proyecto?.duracion ? proyecto?.duracion.toString() : '';
      proyectoTable += '\n';
      proyectoTable += this.notIsNullAndNotUndefined(proyecto?.coordinado) ?
        this.getI18nBooleanYesNo(proyecto?.coordinado) : '';
      proyectoTable += '\n';
      proyectoTable += this.notIsNullAndNotUndefined(proyecto?.coordinadorExterno) ?
        this.getI18nBooleanYesNo(proyecto?.coordinadorExterno) : '';
      proyectoTable += '\n';
      proyectoTable += this.notIsNullAndNotUndefined(proyecto?.colaborativo) ?
        this.getI18nBooleanYesNo(proyecto?.colaborativo) : '';
      proyectoTable += '\n';
      proyectoTable += proyecto?.areaTematica ? proyecto?.areaTematica.nombre : '';

      proyectoElementsRow.push(proyectoTable);

      const rowReport: ISgiRowReport = {
        elements: proyectoElementsRow
      };
      rowsReport.push(rowReport);
    }

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsProyectoExcel(elementsRow: any[], proyecto: ISolicitudProyecto) {
    if (proyecto) {
      elementsRow.push(proyecto?.codExterno ?? '');
      elementsRow.push(proyecto?.acronimo ?? '');
      elementsRow.push(proyecto?.duracion ? proyecto?.duracion.toString() : '');
      elementsRow.push(this.notIsNullAndNotUndefined(proyecto?.coordinado) ?
        this.getI18nBooleanYesNo(proyecto?.coordinado) : '');
      elementsRow.push(this.notIsNullAndNotUndefined(proyecto?.coordinadorExterno) ?
        this.getI18nBooleanYesNo(proyecto?.coordinadorExterno) : '');
      elementsRow.push(this.notIsNullAndNotUndefined(proyecto?.colaborativo) ?
        this.getI18nBooleanYesNo(proyecto?.colaborativo) : '');
      elementsRow.push(proyecto?.areaTematica ? proyecto?.areaTematica.nombre : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
    return elementsRow;
  }

}
