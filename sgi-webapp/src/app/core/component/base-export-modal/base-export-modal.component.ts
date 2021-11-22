import { Directive, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { HttpProblem, Problem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReportType } from '@core/models/rep/output-report.enum';
import { IExportService, IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subscription } from 'rxjs';

const MSG_DOWNLOAD_ERROR = marker('error.file.download');

export const OUTPUT_REPORT_TYPE_MAP: Map<OutputReportType, string> = new Map([
  [OutputReportType.PDF, marker('export.type.pdf')],
  [OutputReportType.XLSX, marker('export.type.xlsx')],
  [OutputReportType.RTF, marker('export.type.rtf')],
  [OutputReportType.CSV, marker('export.type.csv')],
  [OutputReportType.HTML, marker('export.type.html')]
]);

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class BaseExportModalComponent<T> implements OnInit, OnDestroy {

  OUTPUT_REPORT_TYPE_MAP = OUTPUT_REPORT_TYPE_MAP;
  outputReportType: OutputReportType = OutputReportType.PDF;

  formGroup: FormGroup = new FormGroup({});

  title: string;

  protected subscriptions: Subscription[] = [];

  public readonly problems$ = new BehaviorSubject<Problem[]>([]);

  constructor(
    private exportService: IExportService<T>,
    protected readonly snackBarService: SnackBarService,
    protected readonly translate: TranslateService,
    private matDialog: MatDialogRef<any>
  ) { }

  ngOnInit(): void {
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(subscription => subscription.unsubscribe());
  }

  protected setupI18N(): void {
    this.translate.get(
      this.getKey(),
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.title = value);
  }

  close() {
    this.matDialog.close();
  }

  protected abstract buildFormGroup(): FormGroup;

  protected abstract getReportOptions(): IReportConfig<T>;

  protected abstract getKey(): string;

  protected abstract getGender(): any;

  export(): void {
    this.problems$.next([]);
    this.subscriptions.push(this.exportService.export(this.getReportOptions()).subscribe(
      () => {
        this.close();
      },
      ((error) => {
        if (error instanceof HttpProblem) {
          this.problems$.next([error]);
        } else {
          this.snackBarService.showError(MSG_DOWNLOAD_ERROR);
        }
      })
    ));
  }

}
