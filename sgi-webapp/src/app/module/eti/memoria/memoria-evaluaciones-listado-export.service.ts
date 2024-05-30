import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { IMemoriaReportData, IMemoriaReportOptions } from './memoria-listado-export.service';


const EVALUACION_KEY = marker('eti.memoria.report.evaluacion');

const EVALUACION_TIPO_KEY = marker('eti.memoria.report.evaluacion.tipo');
const EVALUACION_VERSION_KEY = marker('eti.memoria.report.evaluacion.version');
const EVALUACION_DICTAMEN_KEY = marker('eti.memoria.report.evaluacion.dictamen');

@Injectable()
export class MemoriaEvaluacionesListadoExportService extends
  AbstractTableExportFillService<IMemoriaReportData, IMemoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    protected readonly memoriaService: MemoriaService
  ) {
    super(translate);
  }

  public getData(memoriaData: IMemoriaReportData): Observable<IMemoriaReportData> {

    return this.memoriaService.getEvaluacionesMemoria(memoriaData.id).pipe(
      switchMap(responseEvaluaciones => {
        if (responseEvaluaciones.items?.length === 0) {
          return of(memoriaData);
        }
        memoriaData.evaluaciones = responseEvaluaciones.items;
        return of(memoriaData);
      })
    );
  }

  public fillColumns(
    peticiones: IMemoriaReportData[],
    reportConfig: IReportConfig<IMemoriaReportOptions>
  ): ISgiColumnReport[] {

    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsExcel(peticiones);
    }
  }

  private getColumnsExcel(peticiones: IMemoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumTareas = Math.max(...peticiones.map(peticion => peticion.evaluaciones ?
      peticion.evaluaciones?.length : 0));

    const prefixTitleColumn = this.translate.instant(EVALUACION_KEY);

    for (let i = 0; i < maxNumTareas; i++) {
      const idTareaCol: string = String(i + 1);

      const columnTipo: ISgiColumnReport = {
        name: 'tipo' + idTareaCol,
        title: prefixTitleColumn + idTareaCol + ': ' + this.translate.instant(EVALUACION_TIPO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTipo);

      const columnVersion: ISgiColumnReport = {
        name: 'version' + idTareaCol,
        title: prefixTitleColumn + idTareaCol + ': ' + this.translate.instant(EVALUACION_VERSION_KEY),
        type: ColumnType.NUMBER,
        format: '#'
      };
      columns.push(columnVersion);

      const columnDictamen: ISgiColumnReport = {
        name: 'dictamen' + idTareaCol,
        title: prefixTitleColumn + idTareaCol + ': ' + this.translate.instant(EVALUACION_DICTAMEN_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnDictamen);
    }

    return columns;
  }

  public fillRows(memorias: IMemoriaReportData[],
    index: number, reportConfig: IReportConfig<IMemoriaReportOptions>): any[] {

    const memoria = memorias[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumMemorias = Math.max(...memorias.map(memoriaItem => memoriaItem.evaluaciones ?
        memoriaItem.evaluaciones?.length : 0));
      for (let i = 0; i < maxNumMemorias; i++) {
        const tarea = memoria.evaluaciones ? memoria.evaluaciones[i] ?? null : null;
        this.fillRowsExcel(elementsRow, tarea);
      }
    }
    return elementsRow;
  }

  private async fillRowsExcel(elementsRow: any[], evaluacion: IEvaluacion) {
    if (evaluacion) {
      elementsRow.push(evaluacion.tipoEvaluacion?.nombre ?? '');
      elementsRow.push(evaluacion.version ?? '');
      elementsRow.push(evaluacion.dictamen?.nombre ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
