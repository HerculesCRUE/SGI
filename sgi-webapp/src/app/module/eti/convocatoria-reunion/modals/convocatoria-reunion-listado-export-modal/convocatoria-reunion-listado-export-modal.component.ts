import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { ConvocatoriaReunionListadoExportService, IConvocatoriaReunionReportOptions } from '../../convocatoria-reunion-listado-export.service';

const REPORT_TITLE_KEY = marker('eti.convocatoria-reunion.report.title');

export interface IConvocatoriaReunionListadoModalData {
  findOptions: SgiRestFindOptions;
}

export const OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP: Map<OutputReport, string> = new Map([
  [OutputReport.XLSX, marker('export.type.xlsx')],
  [OutputReport.CSV, marker('export.type.csv')],
]);

@Component({
  templateUrl: './convocatoria-reunion-listado-export-modal.component.html',
  styleUrls: ['./convocatoria-reunion-listado-export-modal.component.scss']
})
export class ConvocatoriaReunionListadoExportModalComponent extends
  BaseExportModalComponent<IConvocatoriaReunionReportOptions> implements OnInit {

  readonly OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP = OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP;
  private reportTitle: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<ConvocatoriaReunionListadoExportModalComponent>,
    translate: TranslateService,
    convocatoriaReunionListadoExportService: ConvocatoriaReunionListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IConvocatoriaReunionListadoModalData
  ) {
    super(convocatoriaReunionListadoExportService, translate, matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.reportTitle = this.translate.instant(REPORT_TITLE_KEY);
    this.formGroup = this.buildFormGroup();
  }

  selectUnselectAll($event: MatCheckboxChange): void {
    Object.keys(this.formGroup.controls).forEach(key => {
      if (key.startsWith('show')) {
        this.formGroup.get(key).patchValue($event.checked);
      }
    });
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      outputType: new FormControl(OutputReport.XLSX, Validators.required),
      hideBlocksIfNoData: new FormControl(true),
      showMemorias: new FormControl(true)
    });
  }

  protected getReportOptions(): IReportConfig<IConvocatoriaReunionReportOptions> {
    const reportModalData: IReportConfig<IConvocatoriaReunionReportOptions> = {
      title: this.translate.instant('eti.convocatoria-reunion.report.title'),
      outputType: this.formGroup.controls.outputType.value,
      hideBlocksIfNoData: this.formGroup.controls.hideBlocksIfNoData.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showMemorias: this.formGroup.controls.showMemorias.value,
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
