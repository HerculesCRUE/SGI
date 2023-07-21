import { Directive, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiError, SgiProblem } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IExportService, IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { DialogCommonComponent } from '../dialog-common.component';

const MSG_DOWNLOAD_ERROR = marker('error.file.download');

export const OUTPUT_REPORT_TYPE_MAP: Map<OutputReport, string> = new Map([
  [OutputReport.PDF, marker('export.type.pdf')],
  [OutputReport.XLSX, marker('export.type.xlsx')],
  [OutputReport.RTF, marker('export.type.rtf')],
  [OutputReport.CSV, marker('export.type.csv')],
]);

export const OUTPUT_REPORT_XLS_TYPE_MAP: Map<OutputReport, string> = new Map([
  [OutputReport.XLSX, marker('export.type.xlsx')],
  [OutputReport.CSV, marker('export.type.csv')],
]);

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class BaseExportModalComponent<T> extends DialogCommonComponent implements OnInit {

  readonly OUTPUT_REPORT_TYPE_MAP = OUTPUT_REPORT_TYPE_MAP;
  readonly OUTPUT_REPORT_XLS_TYPE_MAP = OUTPUT_REPORT_XLS_TYPE_MAP;
  readonly outputType: OutputReport = OutputReport.XLSX;

  public readonly problems$ = new BehaviorSubject<SgiProblem[]>([]);

  formGroup: FormGroup = new FormGroup({});

  title: string;

  constructor(
    protected readonly exportService: IExportService<T>,
    protected readonly translate: TranslateService,
    matDialog: MatDialogRef<any>
  ) {
    super(matDialog);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected setupI18N(): void {
    this.translate.get(
      this.getKey(),
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.title = value);
  }

  protected abstract buildFormGroup(): FormGroup;

  protected abstract getKey(): string;

  protected abstract getGender(): any;

  protected abstract getReportOptions(): IReportConfig<T>;

  export(): void {
    this.problems$.next([]);
    this.subscriptions.push(this.exportService.export(this.getReportOptions()).subscribe(
      () => {
        this.close();
      },
      ((error: Error) => {
        if (error instanceof SgiError) {
          this.problems$.next([error]);
        } else {
          this.problems$.next([new SgiError(MSG_DOWNLOAD_ERROR)]);
        }
      })
    ));
  }

}
