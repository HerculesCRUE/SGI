import { Directive } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { IReportConfig, IReportOptions } from './abstract-table-export.service';


const MSG_TRUE = marker('label.si');
const MSG_FALSE = marker('label.no');


@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class AbstractTableExportFillService<T, R extends IReportOptions>  {

  protected DEFAULT_CONCURRENT = 5;

  constructor(
    protected translate: TranslateService
  ) { }

  protected abstract getData(reportData: T): Observable<T>;

  protected abstract fillColumns(resultados: T[], reportConfig: IReportConfig<R>): ISgiColumnReport[];

  protected abstract fillRows(resultados: T[], index: number, reportConfig: IReportConfig<R>): any[];

  protected isExcelOrCsv(outputType: OutputReport): boolean {
    return (outputType === OutputReport.XLS || outputType === OutputReport.XLSX || outputType === OutputReport.CSV);
  }

  protected getI18nBooleanYesNo(field: boolean): string {
    return field ? this.translate.instant(MSG_TRUE) : this.translate.instant(MSG_FALSE);
  }

  protected notIsNullAndNotUndefined(value: any): boolean {
    return value !== null && value !== undefined;
  }
}
