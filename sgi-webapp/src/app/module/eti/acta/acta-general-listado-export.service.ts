import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { IActaReportData, IActaReportOptions } from './acta-listado-export.service';

const COMITE_KEY = marker('label.eti.comite');
const FECHA_EVALUACION_KEY = marker('eti.acta.fecha-evaluacion');
const NUMERO_ACTA_KEY = marker('eti.acta.numero');
const TIPO_CONVOCATORIA_KEY = marker('eti.acta.tipo-convocatoria');
const NUM_INICIALES_KEY = marker('eti.acta.numero-iniciales');
const NUM_REVISIONES_KEY = marker('eti.acta.numero-revisiones');
const NUM_TOTAL_KEY = marker('eti.acta.numero-total');
const ESTADO_KEY = marker('eti.acta.estado');

@Injectable()
export class ActaGeneralListadoExportService
  extends AbstractTableExportFillService<IActaReportData, IActaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
  ) {
    super(translate);
  }

  public getData(actaData: IActaReportData): Observable<IActaReportData> {
    return of(actaData);
  }

  public fillColumns(resultados: IActaReportData[], reportConfig: IReportConfig<IReportOptions>):
    ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(COMITE_KEY),
        name: 'comite',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(FECHA_EVALUACION_KEY),
        name: 'fechaEvaluacion',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(NUMERO_ACTA_KEY),
        name: 'numeroActa',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(TIPO_CONVOCATORIA_KEY),
        name: 'tipoConvocatoria',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(NUM_INICIALES_KEY),
        name: 'numIniciales',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(NUM_REVISIONES_KEY),
        name: 'numRevisiones',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(NUM_TOTAL_KEY),
        name: 'numTotal',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(ESTADO_KEY),
        name: 'estado',
        type: ColumnType.STRING,
        format: '#'
      },
    ];
    return columns;
  }

  public fillRows(resultados: IActaReportData[], index: number, reportConfig: IReportConfig<IActaReportOptions>): any[] {
    const acta = resultados[index];

    const elementsRow: any[] = [];
    elementsRow.push(acta.comite ?? '');
    elementsRow.push(this.luxonDatePipe.transform(
      LuxonUtils.toBackend(acta.fechaEvaluacion, true), 'shortDate') ?? '');
    elementsRow.push(acta.numeroActa ? acta.numeroActa.toString() : '');
    elementsRow.push(acta.convocatoria ?? '');
    elementsRow.push(acta.numEvaluaciones ? acta.numEvaluaciones.toString() : '0');
    elementsRow.push(acta.numRevisiones ? acta.numRevisiones.toString() : '0');
    elementsRow.push(acta.numTotal ? acta.numTotal.toString() : '0');
    elementsRow.push(acta.estadoActa ? acta.estadoActa.nombre ?? '' : '');

    return elementsRow;
  }

}
