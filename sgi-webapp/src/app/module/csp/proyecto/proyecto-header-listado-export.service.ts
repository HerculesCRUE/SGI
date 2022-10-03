import { Injectable } from '@angular/core';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

@Injectable()
export class ProyectoHeaderListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IProyectoReportData): Observable<IProyectoReportData> {
    return of(convocatoriaData);
  }

  public fillColumns(resultados: IProyectoReportData[], reportConfig: IReportConfig<IReportOptions>):
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

  public fillRows(resultados: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = resultados[index];
    const elementsRow: any[] = [];

    elementsRow.push('Proyecto: ' + proyecto.titulo ?? '');

    return elementsRow;
  }

}
