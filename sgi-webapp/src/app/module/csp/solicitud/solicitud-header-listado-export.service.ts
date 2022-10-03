import { Injectable } from '@angular/core';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { ISolicitudReportData, ISolicitudReportOptions } from './solicitud-listado-export.service';

@Injectable()
export class SolicitudHeaderListadoExportService
  extends AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: ISolicitudReportData): Observable<ISolicitudReportData> {
    return of(convocatoriaData);
  }

  public fillColumns(resultados: ISolicitudReportData[], reportConfig: IReportConfig<IReportOptions>):
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

  public fillRows(resultados: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {
    const solicitud = resultados[index];
    const elementsRow: any[] = [];

    elementsRow.push('Solicitud: ' + solicitud.titulo ?? '');

    return elementsRow;
  }

}
