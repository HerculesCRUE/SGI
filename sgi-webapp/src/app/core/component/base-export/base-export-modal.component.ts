import { Directive, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { HttpProblem, Problem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IExportService, IReportConfig, RelationsTypeView } from '@core/services/rep/abstract-table-export.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subscription } from 'rxjs';

const MSG_DOWNLOAD_ERROR = marker('error.file.download');

export const OUTPUT_REPORT_TYPE_MAP: Map<OutputReport, string> = new Map([
  [OutputReport.PDF, marker('export.type.pdf')],
  [OutputReport.XLSX, marker('export.type.xlsx')],
  [OutputReport.RTF, marker('export.type.rtf')],
  [OutputReport.CSV, marker('export.type.csv')],
  [OutputReport.HTML, marker('export.type.html')]
]);

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class BaseExportModalComponent<T> implements OnInit, OnDestroy {

  readonly OUTPUT_REPORT_TYPE_MAP = OUTPUT_REPORT_TYPE_MAP;
  readonly outputType: OutputReport = OutputReport.PDF;

  protected subscriptions: Subscription[] = [];

  public readonly problems$ = new BehaviorSubject<Problem[]>([]);

  formGroup: FormGroup = new FormGroup({});

  title: string;

  constructor(
    protected readonly exportService: IExportService<T>,
    protected readonly snackBarService: SnackBarService,
    protected readonly translate: TranslateService,
    private matDialog: MatDialogRef<any>
  ) {
  }

  ngOnInit(): void {
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(subscription => subscription.unsubscribe());
  }

  close() {
    this.matDialog.close();
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

  protected getRelationsTypeView(outputTypeValue: OutputReport): RelationsTypeView {
    return outputTypeValue === OutputReport.PDF || outputTypeValue === OutputReport.RTF ? RelationsTypeView.TABLE : RelationsTypeView.LIST;
  }

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
