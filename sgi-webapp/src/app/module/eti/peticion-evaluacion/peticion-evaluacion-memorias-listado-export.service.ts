import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { ESTADO_MEMORIA_MAP } from '@core/models/eti/tipo-estado-memoria';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { mergeMap, switchMap } from 'rxjs/operators';
import {
  IPeticionEvaluacionMemoriaReportData,
  IPeticionEvaluacionReportData,
  IPeticionEvaluacionReportOptions
} from './peticion-evaluacion-listado-export.service';

const MEMORIA_KEY = marker('eti.peticion-evaluacion.report.memoria');

const MEMORIA_REFERENCIA_KEY = marker('eti.peticion-evaluacion.report.memoria.referencia');
const MEMORIA_COMITE_KEY = marker('eti.peticion-evaluacion.report.memoria.comite');
const MEMORIA_ESTADO_KEY = marker('eti.peticion-evaluacion.report.memoria.estado');
const MEMORIA_FECHA_EVALUACION_KEY = marker('eti.peticion-evaluacion.report.memoria.fecha-evaluacion');

@Injectable()
export class PeticionEvaluacionMemoriasListadoExportService extends
  AbstractTableExportFillService<IPeticionEvaluacionReportData, IPeticionEvaluacionReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly peticionEvaluacionService: PeticionEvaluacionService,
    private readonly memoriaService: MemoriaService
  ) {
    super(translate);
  }

  public getData(peticionData: IPeticionEvaluacionReportData): Observable<IPeticionEvaluacionReportData> {

    return this.peticionEvaluacionService.findMemorias(peticionData.id).pipe(
      switchMap(responseMemorias => {
        if (responseMemorias.items?.length === 0) {
          return of([]);
        }
        return of(responseMemorias.items);
      }), switchMap((memorias: IMemoriaPeticionEvaluacion[]) => {
        return from(memorias).pipe(
          mergeMap((memoria: IMemoriaPeticionEvaluacion) => {
            if (peticionData.memorias === undefined || peticionData.memorias === null) {
              peticionData.memorias = [];
            }
            peticionData.memorias.push({
              referencia: memoria.numReferencia,
              comite: memoria.comite.comite,
              estado: ESTADO_MEMORIA_MAP.get(memoria.estadoActual.id),
              fechaEvaluacion: memoria.fechaEvaluacion
            });
            return of(peticionData);
          })
        );
      })
    );
  }

  public fillColumns(
    peticiones: IPeticionEvaluacionReportData[],
    reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>
  ): ISgiColumnReport[] {

    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsExcel(peticiones);
    }
  }

  private getColumnsExcel(peticiones: IPeticionEvaluacionReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumMemorias = Math.max(...peticiones.map(peticion => peticion.memorias ?
      peticion.memorias?.length : 0));

    const prefixTitleColumn = this.translate.instant(MEMORIA_KEY);

    for (let i = 0; i < maxNumMemorias; i++) {
      const idMemoriaCol: string = String(i + 1);

      const columnReferencia: ISgiColumnReport = {
        name: 'referencia' + idMemoriaCol,
        title: prefixTitleColumn + idMemoriaCol + ': ' + this.translate.instant(MEMORIA_REFERENCIA_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnReferencia);

      const columnComite: ISgiColumnReport = {
        name: 'comite' + idMemoriaCol,
        title: prefixTitleColumn + idMemoriaCol + ': ' + this.translate.instant(MEMORIA_COMITE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnComite);

      const columnEstado: ISgiColumnReport = {
        name: 'estado' + idMemoriaCol,
        title: prefixTitleColumn + idMemoriaCol + ': ' + this.translate.instant(MEMORIA_ESTADO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEstado);

      const columnFechaEvaluacion: ISgiColumnReport = {
        name: 'fechaEvaluacion' + idMemoriaCol,
        title: prefixTitleColumn + idMemoriaCol + ': ' + this.translate.instant(MEMORIA_FECHA_EVALUACION_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaEvaluacion);
    }

    return columns;
  }

  public fillRows(
    peticionesData: IPeticionEvaluacionReportData[], index: number, reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>): any[] {

    const peticion = peticionesData[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumMemorias = Math.max(...peticionesData.map(peticionItem => peticionItem.memorias ?
        peticionItem.memorias?.length : 0));
      for (let i = 0; i < maxNumMemorias; i++) {
        const memoria = peticion.memorias ? peticion.memorias[i] ?? null : null;
        this.fillRowsExcel(elementsRow, memoria);
      }
    }
    return elementsRow;
  }

  private async fillRowsExcel(elementsRow: any[], memorias: IPeticionEvaluacionMemoriaReportData) {
    if (memorias) {
      elementsRow.push(memorias.referencia ?? '');
      elementsRow.push(memorias.comite ?? '');
      elementsRow.push(this.translate.instant(memorias.estado) ?? '');
      elementsRow.push(LuxonUtils.toBackend(memorias.fechaEvaluacion) ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
