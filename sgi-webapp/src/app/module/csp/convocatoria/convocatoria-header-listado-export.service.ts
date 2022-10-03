import { Injectable } from '@angular/core';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

@Injectable()
export class ConvocatoriaHeaderListadoExportService
  extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return of(convocatoriaData);
  }

  public fillColumns(resultados: IConvocatoriaReportData[], reportConfig: IReportConfig<IReportOptions>):
    ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [
      {
        title: 'Header',
        name: 'header',
        type: ColumnType.STRING,
        visible: false
      }
    ];
    return columns;
  }

  public fillRows(resultados: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = resultados[index];
    const elementsRow: any[] = [];

    elementsRow.push('Convocatoria: ' + convocatoria.convocatoria?.titulo ?? '');

    return elementsRow;
  }

}
