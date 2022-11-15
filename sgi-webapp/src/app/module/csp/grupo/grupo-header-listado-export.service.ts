
import { Injectable } from '@angular/core';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { IGrupoReportData, IGrupoReportOptions } from './grupo-listado-export.service';

@Injectable()
export class GrupoHeaderListadoExportService
  extends AbstractTableExportFillService<IGrupoReportData, IGrupoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IGrupoReportData): Observable<IGrupoReportData> {
    return of(convocatoriaData);
  }

  public fillColumns(resultados: IGrupoReportData[], reportConfig: IReportConfig<IReportOptions>):
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

  public fillRows(resultados: IGrupoReportData[], index: number, reportConfig: IReportConfig<IGrupoReportOptions>): any[] {
    const grupo = resultados[index];
    const elementsRow: any[] = [];

    elementsRow.push('Grupo: ' + grupo.nombre ?? '');

    return elementsRow;
  }

}
