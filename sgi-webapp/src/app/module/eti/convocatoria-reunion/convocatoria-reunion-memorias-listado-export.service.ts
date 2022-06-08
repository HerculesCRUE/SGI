import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import { IConvocatoriaReunionMemoriaReportData, IConvocatoriaReunionReportData, IConvocatoriaReunionReportOptions } from './convocatoria-reunion-listado-export.service';

const CONVOCATORIA_REUNION_REPORT_MEMORIA_KEY = marker('eti.convocatoria-reunion.report.memoria');

const CONVOCATORIA_REUNION_REPORT_MEMORIA_REFERENCIA_KEY = marker('eti.peticion-evaluacion.report.memoria.referencia');

const CONVOCATORIA_REUNION_REPORT_MEMORIA_SOLICITANTE_KEY = marker('eti.convocatoria-reunion.report.memoria.solicitante');
const CONVOCATORIA_REUNION_REPORT_MEMORIA_SOLICITANTE_NOMBRE_KEY = marker('eti.convocatoria-reunion.report.memoria.solicitante.nombre');
const CONVOCATORIA_REUNION_REPORT_MEMORIA_SOLICITANTE_APELLIDOS_KEY =
  marker('eti.convocatoria-reunion.report.memoria.solicitante.apellidos');

const CONVOCATORIA_REUNION_REPORT_MEMORIA_EVALUADOR1_KEY = marker('eti.convocatoria-reunion.report.memoria.evaluador1');
const CONVOCATORIA_REUNION_REPORT_MEMORIA_EVALUADOR1_NOMBRE_KEY = marker('eti.convocatoria-reunion.report.memoria.evaluador1.nombre');
const CONVOCATORIA_REUNION_REPORT_MEMORIA_EVALUADOR1_APELLIDOS_KEY = marker('eti.convocatoria-reunion.report.memoria.evaluador1.apellidos');

const CONVOCATORIA_REUNION_REPORT_MEMORIA_EVALUADOR2_KEY = marker('eti.convocatoria-reunion.report.memoria.evaluador2');
const CONVOCATORIA_REUNION_REPORT_MEMORIA_EVALUADOR2_NOMBRE_KEY = marker('eti.convocatoria-reunion.report.memoria.evaluador2.nombre');
const CONVOCATORIA_REUNION_REPORT_MEMORIA_EVALUADOR2_APELLIDOS_KEY = marker('eti.convocatoria-reunion.report.memoria.evaluador2.apellidos');

@Injectable()
export class ConvocatoriaReunionMemoriasListadoExportService extends
  AbstractTableExportFillService<IConvocatoriaReunionReportData, IConvocatoriaReunionReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly evaluacionService: EvaluacionService,
    private readonly personaService: PersonaService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReunionReportData): Observable<IConvocatoriaReunionReportData> {

    return this.evaluacionService.findAllByConvocatoriaReunionIdAndNoEsRevMinima(convocatoriaData.id).pipe(
      switchMap((responseEvaluacion: SgiRestListResult<IEvaluacion>) => {
        if (responseEvaluacion.items?.length === 0) {
          return of([]);
        }
        return of(responseEvaluacion.items);
      }), switchMap((evaluaciones: IEvaluacion[]) => {
        return from(evaluaciones).pipe(
          mergeMap((evaluacion: IEvaluacion) => this.fillEvaluadoresAndSolicitanteData(convocatoriaData, evaluacion))
        );
      })
    );
  }

  private fillEvaluadoresAndSolicitanteData(data: IConvocatoriaReunionReportData, evaluacion: IEvaluacion):
    Observable<IConvocatoriaReunionReportData> {
    const personasIds = new Set<string>();

    personasIds.add(evaluacion?.evaluador1?.persona?.id);
    personasIds.add(evaluacion?.evaluador2?.persona?.id);
    personasIds.add(evaluacion?.memoria?.peticionEvaluacion?.solicitante?.id);

    return this.personaService.findAllByIdIn([...personasIds]).pipe(
      map(result => {
        if (data.memorias === undefined || data.memorias === null) {
          data.memorias = [];
        }

        data.memorias.push({
          referencia: evaluacion.memoria.numReferencia,
          solicitante: result.items.find(persona => persona.id === evaluacion.memoria?.peticionEvaluacion?.solicitante?.id),
          evaluador1: result.items.find(persona => persona.id === evaluacion.evaluador1?.persona?.id),
          evaluador2: result.items.find(persona => persona.id === evaluacion.evaluador2?.persona?.id)
        });
        return data;
      }), catchError(err => {
        this.logger.error(err);
        return of(err);
      })
    );


  }

  public fillColumns(
    convocatoriasData: IConvocatoriaReunionReportData[],
    reportConfig: IReportConfig<IConvocatoriaReunionReportOptions>
  ): ISgiColumnReport[] {

    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsExcel(convocatoriasData);
    }
  }

  private getColumnsExcel(convocatoriasData: IConvocatoriaReunionReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumMemorias = Math.max(...convocatoriasData.map(convocatoriaData => convocatoriaData.memorias ?
      convocatoriaData.memorias?.length : 0));

    const prefixTitleColumn = this.translate.instant(CONVOCATORIA_REUNION_REPORT_MEMORIA_KEY);
    const prefixSolicitante = this.translate.instant(CONVOCATORIA_REUNION_REPORT_MEMORIA_SOLICITANTE_KEY);
    const prefixEvaluador1 = this.translate.instant(CONVOCATORIA_REUNION_REPORT_MEMORIA_EVALUADOR1_KEY);
    const prefixEvaluador2 = this.translate.instant(CONVOCATORIA_REUNION_REPORT_MEMORIA_EVALUADOR2_KEY);

    for (let i = 0; i < maxNumMemorias; i++) {
      const idMemoriaCol: string = String(i + 1);

      const columnReferencia: ISgiColumnReport = {
        name: 'referencia' + idMemoriaCol,
        title: prefixTitleColumn + idMemoriaCol + ': ' + this.translate.instant(CONVOCATORIA_REUNION_REPORT_MEMORIA_REFERENCIA_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnReferencia);

      const columnSolicitanteNombre: ISgiColumnReport = {
        name: 'solNombre' + idMemoriaCol,
        title: prefixTitleColumn + idMemoriaCol + ': ' + prefixSolicitante + ': ' +
          this.translate.instant(CONVOCATORIA_REUNION_REPORT_MEMORIA_SOLICITANTE_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnSolicitanteNombre);

      const columnSolicitanteApellidos: ISgiColumnReport = {
        name: 'solApellidos' + idMemoriaCol,
        title: prefixTitleColumn + idMemoriaCol + ': ' + prefixSolicitante + ': ' +
          this.translate.instant(CONVOCATORIA_REUNION_REPORT_MEMORIA_SOLICITANTE_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnSolicitanteApellidos);

      const columnEvaluador1Nombre: ISgiColumnReport = {
        name: 'evaluador1Nombre' + idMemoriaCol,
        title: prefixTitleColumn + idMemoriaCol + ': ' + prefixEvaluador1 + ': ' +
          this.translate.instant(CONVOCATORIA_REUNION_REPORT_MEMORIA_EVALUADOR1_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEvaluador1Nombre);

      const columnEvaluador1Apellidos: ISgiColumnReport = {
        name: 'evaluador1Apellidos' + idMemoriaCol,
        title: prefixTitleColumn + idMemoriaCol + ': ' + prefixEvaluador1 + ': ' +
          this.translate.instant(CONVOCATORIA_REUNION_REPORT_MEMORIA_EVALUADOR1_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEvaluador1Apellidos);

      const columnEvaluador2Nombre: ISgiColumnReport = {
        name: 'evaluador2Nombre' + idMemoriaCol,
        title: prefixTitleColumn + idMemoriaCol + ': ' + prefixEvaluador2 + ': ' +
          this.translate.instant(CONVOCATORIA_REUNION_REPORT_MEMORIA_EVALUADOR2_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEvaluador2Nombre);

      const columnEvaluador2Apellidos: ISgiColumnReport = {
        name: 'evaluado21Apellidos' + idMemoriaCol,
        title: prefixTitleColumn + idMemoriaCol + ': ' + prefixEvaluador2 + ': ' +
          this.translate.instant(CONVOCATORIA_REUNION_REPORT_MEMORIA_EVALUADOR2_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEvaluador2Apellidos);

    }

    return columns;
  }

  public fillRows(
    convocatoriasData: IConvocatoriaReunionReportData[],
    index: number, reportConfig: IReportConfig<IConvocatoriaReunionReportOptions>): any[] {

    const convocatoriaData = convocatoriasData[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumMemorias = Math.max(...convocatoriasData.map(peticionItem => peticionItem.memorias ?
        peticionItem.memorias?.length : 0));
      for (let i = 0; i < maxNumMemorias; i++) {
        const memoria = convocatoriaData.memorias ? convocatoriaData.memorias[i] ?? null : null;
        this.fillRowsExcel(elementsRow, memoria);
      }
    }
    return elementsRow;
  }

  private fillRowsExcel(elementsRow: any[], memoria: IConvocatoriaReunionMemoriaReportData) {
    if (memoria) {
      elementsRow.push(memoria.referencia ?? '');

      elementsRow.push(memoria.solicitante?.nombre ?? '');
      elementsRow.push(memoria.solicitante?.apellidos ?? '');

      elementsRow.push(memoria.evaluador1?.nombre ?? '');
      elementsRow.push(memoria.evaluador1?.apellidos ?? '');

      elementsRow.push(memoria.evaluador2?.nombre ?? '');
      elementsRow.push(memoria.evaluador2?.apellidos ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
