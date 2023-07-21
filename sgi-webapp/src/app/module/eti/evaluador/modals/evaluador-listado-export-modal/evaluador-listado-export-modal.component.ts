import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { EvaluadorListadoExportService, IEvaluadorReportOptions } from '../../evaluador-listado-export.service';

const REPORT_TITLE_KEY = marker('eti.evaluador.report.title');

export const OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP: Map<OutputReport, string> = new Map([
  [OutputReport.XLSX, marker('export.type.xlsx')],
  [OutputReport.CSV, marker('export.type.csv')],
]);

@Component({
  templateUrl: './evaluador-listado-export-modal.component.html',
  styleUrls: ['./evaluador-listado-export-modal.component.scss']
})
export class EvaluadorListadoExportModalComponent extends
  BaseExportModalComponent<IEvaluadorReportOptions> implements OnInit {

  readonly OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP = OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP;
  private reportTitle: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TOTAL_REG_EXP_EXCEL() {
    return this.modalData.totalRegistrosExportacionExcel;
  }

  get LIMITE_REG_EXP_EXCEL() {
    return this.modalData.limiteRegistrosExportacionExcel;
  }

  constructor(
    matDialogRef: MatDialogRef<EvaluadorListadoExportModalComponent>,
    translate: TranslateService,
    evaluadorListadoExportService: EvaluadorListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IEvaluadorReportOptions
  ) {

    super(evaluadorListadoExportService, translate, matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.reportTitle = this.translate.instant(REPORT_TITLE_KEY);
    this.formGroup = this.buildFormGroup();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      outputType: new FormControl(OutputReport.XLSX, Validators.required),
      hideBlocksIfNoData: new FormControl(true),

      showConflictos: new FormControl(true),
    });
  }

  protected getReportOptions(): IReportConfig<IEvaluadorReportOptions> {
    const reportModalData: IReportConfig<IEvaluadorReportOptions> = {
      outputType: this.formGroup.controls.outputType.value,
      hideBlocksIfNoData: this.formGroup.controls.hideBlocksIfNoData.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showConflictos: this.formGroup.controls.showConflictos.value,
        columnMinWidth: 200
      }
    };
    return reportModalData;
  }


  protected getKey(): string {
    return REPORT_TITLE_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.MALE;
  }
}
