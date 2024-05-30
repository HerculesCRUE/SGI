import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEvaluacionWithNumComentario } from '@core/models/eti/evaluacion-with-num-comentario';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { IEvaluacionReportData, IEvaluacionReportOptions, TipoComentario } from './evaluacion-listado-export.service';

const EVALUACION_KEY = marker('eti.evaluacion.report.evaluacion');

const EVALUACION_VERSION_KEY = marker('eti.evaluacion.report.version');
const EVALUACION_FECHA_EVALUACION_KEY = marker('eti.evaluacion.report.fecha-evaluacion');
const EVALUACION_DICTAMEN_KEY = marker('eti.evaluacion.report.dictamen');

@Injectable()
export class EvaluacionEvaluacionesAnterioresListadoExportService extends
  AbstractTableExportFillService<IEvaluacionReportData, IEvaluacionReportOptions>{

  private _tipoComentario: TipoComentario;

  public set tipoComentario(value) {
    this._tipoComentario = value;
  }

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly memoriaService: MemoriaService,
    private readonly personaService: PersonaService
  ) {
    super(translate);
  }

  public getData(evaluacionData: IEvaluacionReportData): Observable<IEvaluacionReportData> {

    return this.memoriaService.getEvaluacionesAnteriores(evaluacionData.memoria?.id, evaluacionData.id, this._tipoComentario).pipe(
      switchMap((responseEvaluacion: SgiRestListResult<IEvaluacionWithNumComentario>) => {
        evaluacionData.evaluacionesAnteriores = responseEvaluacion.items;
        return of(evaluacionData);
      })
    );
  }

  public fillColumns(
    convocatoriasData: IEvaluacionReportData[],
    reportConfig: IReportConfig<IEvaluacionReportOptions>
  ): ISgiColumnReport[] {

    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsExcel(convocatoriasData);
    }
  }

  private getColumnsExcel(convocatoriasData: IEvaluacionReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumMemorias = Math.max(...convocatoriasData.map(convocatoriaData => convocatoriaData.evaluacionesAnteriores ?
      convocatoriaData.evaluacionesAnteriores?.length : 0));

    const prefixTitleColumn = this.translate.instant(EVALUACION_KEY);


    for (let i = 0; i < maxNumMemorias; i++) {
      const idEvaluacionCol: string = String(i + 1);

      const columnVersion: ISgiColumnReport = {
        name: 'version' + idEvaluacionCol,
        title: prefixTitleColumn + idEvaluacionCol + ': ' + this.translate.instant(EVALUACION_VERSION_KEY),
        type: ColumnType.NUMBER,
        format: '#'
      };
      columns.push(columnVersion);

      const columnFechaEvaluacion: ISgiColumnReport = {
        name: 'fechaEvaluacion' + idEvaluacionCol,
        title: prefixTitleColumn + idEvaluacionCol + ': ' + this.translate.instant(EVALUACION_FECHA_EVALUACION_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaEvaluacion);

      const columnDictamen: ISgiColumnReport = {
        name: 'dictamen' + idEvaluacionCol,
        title: prefixTitleColumn + idEvaluacionCol + ': ' + this.translate.instant(EVALUACION_DICTAMEN_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnDictamen);

    }

    return columns;
  }

  public fillRows(
    evaluacionesData: IEvaluacionReportData[],
    index: number, reportConfig: IReportConfig<IEvaluacionReportOptions>): any[] {

    const evaluacionData = evaluacionesData[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumMemorias = Math.max(...evaluacionesData.map(evaluacionItem => evaluacionItem.evaluacionesAnteriores ?
        evaluacionItem.evaluacionesAnteriores?.length : 0));
      for (let i = 0; i < maxNumMemorias; i++) {
        const evaluacion = evaluacionData.evaluacionesAnteriores ? evaluacionData.evaluacionesAnteriores[i] ?? null : null;
        this.fillRowsExcel(elementsRow, evaluacion);
      }
    }
    return elementsRow;
  }

  private fillRowsExcel(elementsRow: any[], evaluacion: IEvaluacionWithNumComentario) {
    if (evaluacion) {
      elementsRow.push(evaluacion.evaluacion?.version ?? '');
      elementsRow.push(LuxonUtils.toBackend(evaluacion.evaluacion?.fechaDictamen) ?? '');
      elementsRow.push(evaluacion.evaluacion?.dictamen?.nombre ?? '');

    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
