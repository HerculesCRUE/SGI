import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ActaService } from '@core/services/eti/acta.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { MemoriaListado } from './acta-formulario/acta-memorias/acta-memorias.fragment';
import { IActaReportData, IActaReportOptions } from './acta-listado-export.service';

const MEMORIA_KEY = marker('eti.memoria');
const REFERENCIA_KEY = marker('eti.memoria.referencia-informe');
const VERSION_KEY = marker('eti.memoria.version');
const DICTAMEN_KEY = marker('eti.dictamen');

const MEMORIA_FIELD = 'memoria';
const REFERENCIA_FIELD = 'referencia';
const VERSION_FIELD = 'version';
const DICTAMEN_FIELD = 'dictamen';

@Injectable()
export class ActaMemoriaListadoExportService extends AbstractTableExportFillService<IActaReportData, IActaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly actaService: ActaService,
    private readonly convocatoriaReunionService: ConvocatoriaReunionService,
  ) {
    super(translate);
  }

  public getData(actaData: IActaReportData): Observable<IActaReportData> {
    return this.actaService.findById(actaData.id).pipe(
      map(value => {
        actaData.convocatoriaReunion = value.convocatoriaReunion;
        return actaData;
      }),
      switchMap(() => {
        return this.convocatoriaReunionService.findEvaluacionesActivas(actaData?.convocatoriaReunion?.id).pipe(
          switchMap((response) => {
            if (response.items) {
              const evaluacionesSinDuplicados = response.items.reduce(
                (evaluacionObject, evaluacion: IEvaluacion) => ({ ...evaluacionObject, [evaluacion.id]: evaluacion }), {}
              );
              const memorias: MemoriaListado[] = Object.keys(evaluacionesSinDuplicados).map(
                idEvaluacion => {
                  return {
                    id: evaluacionesSinDuplicados[idEvaluacion].memoria?.id,
                    numReferencia: evaluacionesSinDuplicados[idEvaluacion].memoria?.numReferencia,
                    version: evaluacionesSinDuplicados[idEvaluacion].version,
                    dictamen: evaluacionesSinDuplicados[idEvaluacion].dictamen
                  };
                });
              actaData.memorias = memorias;
              return of(actaData);
            }
            else {
              return of(actaData);
            }
          })
        );
      })
    );
  }

  public fillColumns(
    actas: IActaReportData[],
    reportConfig: IReportConfig<IActaReportOptions>
  ): ISgiColumnReport[] {

    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsInvestigadorNotExcel();
    } else {
      return this.getColumnsInvestigadorExcel(actas);
    }
  }

  private getColumnsInvestigadorNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: MEMORIA_FIELD,
      title: this.translate.instant(MEMORIA_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(MEMORIA_KEY, MSG_PARAMS.CARDINALIRY.PLURAL) + ': ' +
      ' (' + this.translate.instant(REFERENCIA_KEY) +
      ' - ' + this.translate.instant(VERSION_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) +
      ' - ' + this.translate.instant(DICTAMEN_KEY) +
      ')';
    const columnEquipo: ISgiColumnReport = {
      name: MEMORIA_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEquipo];
  }

  private getColumnsInvestigadorExcel(actas: IActaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumMemorias = Math.max(...actas.map(a => a.memorias ? a.memorias?.length : 0));
    const titleMemoria = this.translate.instant(MEMORIA_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);
    for (let i = 0; i < maxNumMemorias; i++) {
      const idMemoria: string = String(i + 1);
      const columnReferencia: ISgiColumnReport = {
        name: REFERENCIA_FIELD + idMemoria,
        title: titleMemoria + idMemoria + ': ' + this.translate.instant(REFERENCIA_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnReferencia);
      const columnVersion: ISgiColumnReport = {
        name: VERSION_FIELD + idMemoria,
        title: titleMemoria + idMemoria + ': ' + this.translate.instant(VERSION_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
        type: ColumnType.STRING,
      };
      columns.push(columnVersion);
      const columnDictamen: ISgiColumnReport = {
        name: DICTAMEN_FIELD + idMemoria,
        title: titleMemoria + idMemoria + ': ' + this.translate.instant(DICTAMEN_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnDictamen);
    }
    return columns;
  }

  public fillRows(actas: IActaReportData[], index: number, reportConfig: IReportConfig<IActaReportOptions>): any[] {
    const acta = actas[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsInvestigadorNotExcel(acta, elementsRow);
    } else {
      const maxMemorias = Math.max(...actas.map(a => a.memorias ? a.memorias?.length : 0));
      for (let i = 0; i < maxMemorias; i++) {
        const memoria = acta.memorias && acta.memorias.length > 0 ? acta.memorias[i] : null;
        this.fillRowsInvestigadorExcel(elementsRow, memoria);
      }
    }
    return elementsRow;
  }

  private fillRowsInvestigadorNotExcel(acta: IActaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    acta.memorias?.forEach(memoria => {
      const memoriaElementsRow: any[] = [];

      let memoriaTable = memoria.numReferencia ? memoria.numReferencia.toString() : '';
      memoriaTable += '\n';
      memoriaTable += memoria.version ? memoria.version.toString() : '';
      memoriaTable += '\n';
      memoriaTable += memoria.dictamen ? memoria.dictamen.nombre ?? '' : '';

      memoriaElementsRow.push(memoriaTable);

      const rowReport: ISgiRowReport = {
        elements: memoriaElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsInvestigadorExcel(elementsRow: any[], memoria: MemoriaListado) {
    if (memoria) {
      elementsRow.push(memoria.numReferencia ? memoria.numReferencia.toString() : '');
      elementsRow.push(memoria.version ? memoria.version.toString() : '');
      elementsRow.push(memoria.dictamen ? memoria.dictamen.nombre ?? '' : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

}
