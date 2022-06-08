import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { IConvocatoriaReunionReportData, IConvocatoriaReunionReportOptions } from './convocatoria-reunion-listado-export.service';

const CONVOCATORIA_REUNION_REPORT_COMITE_KEY = marker('eti.convocatoria-reunion.report.comite');
const CONVOCATORIA_REUNION_REPORT_FECHA_EVALUACION_KEY = marker('eti.convocatoria-reunion.report.fecha-evaluacion');
const CONVOCATORIA_REUNION_REPORT_CODIGO_KEY = marker('eti.convocatoria-reunion.report.codigo');
const CONVOCATORIA_REUNION_REPORT_TIPO_CONVOCATORIA_KEY = marker('eti.convocatoria-reunion.report.tipo-convocatoria');
const CONVOCATORIA_REUNION_REPORT_ORDEN_DEL_DIA_KEY = marker('eti.convocatoria-reunion.report.orden-del-dia');

@Injectable()
export class ConvocatoriaReunionGeneralListadoExportService extends
  AbstractTableExportFillService<IConvocatoriaReunionReportData, IConvocatoriaReunionReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService
  ) {
    super(translate);
  }

  public getData(peticionEvaluacionData: IConvocatoriaReunionReportData): Observable<IConvocatoriaReunionReportData> {
    return of(peticionEvaluacionData);
  }

  public fillColumns(
    convocatorias: IConvocatoriaReunionReportData[],
    reportConfig: IReportConfig<IConvocatoriaReunionReportOptions>
  ): ISgiColumnReport[] {

    const columns: ISgiColumnReport[] = [];

    return [
      {
        title: this.translate.instant(CONVOCATORIA_REUNION_REPORT_COMITE_KEY),
        name: 'comite',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(CONVOCATORIA_REUNION_REPORT_FECHA_EVALUACION_KEY),
        name: 'fechaEvaluacion',
        type: ColumnType.DATE,
      }, {
        title: this.translate.instant(CONVOCATORIA_REUNION_REPORT_CODIGO_KEY),
        name: 'codigo',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(CONVOCATORIA_REUNION_REPORT_TIPO_CONVOCATORIA_KEY),
        name: 'tipoConvocatoria',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(CONVOCATORIA_REUNION_REPORT_ORDEN_DEL_DIA_KEY),
        name: 'ordenDelDia',
        type: ColumnType.STRING
      }
    ] as ISgiColumnReport[];
  }

  public fillRows(
    convocatorias: IConvocatoriaReunionReportData[],
    index: number, reportConfig: IReportConfig<IConvocatoriaReunionReportOptions>): any[] {

    const convocatoriaData = convocatorias[index];

    return [
      convocatoriaData.comite?.comite ?? '',
      LuxonUtils.toBackend(convocatoriaData.fechaEvaluacion) ?? '',
      convocatoriaData.codigo ?? '',
      convocatoriaData.tipoConvocatoriaReunion?.nombre ?? '',
      convocatoriaData.ordenDia ?? '',
    ];
  }
}
