import { Directive, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReportType } from '@core/models/rep/sgi-dynamic-report';
import { IReportModalData } from '@core/services/rep/abstract-export.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';

export const OUTPUT_REPORT_TYPE_MAP: Map<OutputReportType, string> = new Map([
  [OutputReportType.PDF, marker('export.type.pdf')],
  [OutputReportType.XLSX, marker('export.type.xlsx')],
  [OutputReportType.RTF, marker('export.type.rtf')],
  [OutputReportType.CSV, marker('export.type.csv')],
  [OutputReportType.HTML, marker('export.type.html')]
]);

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class BaseExportModalComponent implements OnInit, OnDestroy {

  OUTPUT_REPORT_TYPE_MAP = OUTPUT_REPORT_TYPE_MAP;

  title: string;

  protected subscriptions: Subscription[] = [];

  formGroup: FormGroup = new FormGroup({});

  constructor(
    protected reportModalData: IReportModalData,
    protected readonly translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.setupI18N();
    this.formGroup = this.getFormGroup();
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

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      outputReportType: new FormControl(this.reportModalData.outputReportType, Validators.required),
    });
    this.reportModalData.visibilityColumns?.forEach(column =>
      formGroup.addControl(column.name, new FormControl(column.value))
    );

    return formGroup;
  }

  protected getValue(): IReportModalData {
    this.reportModalData.outputReportType = this.formGroup.controls.outputReportType.value;
    this.reportModalData.visibilityColumns?.forEach(column => {
      column.value = this.formGroup.controls[column.name].value;
    });

    return this.reportModalData;
  }

  abstract export(): void;

  protected abstract getKey(): string;

  protected abstract getGender(): any;
}
