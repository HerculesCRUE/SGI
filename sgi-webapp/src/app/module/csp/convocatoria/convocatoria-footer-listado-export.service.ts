import { Injectable } from '@angular/core';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';


@Injectable()
export class ConvocatoriaFooterListadoExportService
  extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
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
        title: '',
        name: '',
        type: ColumnType.STRING,
      }
    ];
    const columnEntidad: ISgiColumnReport = {
      name: '',
      title: '',
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  public fillRows(resultados: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = resultados[index];
    const rowsReport: ISgiRowReport[] = [];
    const elementsRow: any[] = [];

    const rowReport: ISgiRowReport = {
      elements: ['Convocatoria ' + (index + 1) + ' de ' + resultados.length]
    };
    rowsReport.push(rowReport);

    elementsRow.push({
      rows: rowsReport
    });
    return elementsRow;
  }

}
