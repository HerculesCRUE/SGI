import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { ActaListadoExportService, IActaReportOptions } from '../../acta-listado-export.service';

const ACTA_KEY = marker('eti.acta');
const REPORT_TITLE_KEY = marker('eti.acta.listado');

@Component({
  templateUrl: './acta-listado-export-modal.component.html',
  styleUrls: ['./acta-listado-export-modal.component.scss']
})
export class ActaListadoExportModalComponent extends BaseExportModalComponent<IReportOptions> implements OnInit {
  private reportTitle: string;

  constructor(
    matDialogRef: MatDialogRef<ActaListadoExportModalComponent>,
    translate: TranslateService,
    actaListadoExportService: ActaListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IBaseExportModalData
  ) {
    super(actaListadoExportService, translate, matDialogRef);
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TOTAL_REG_EXP_EXCEL() {
    return this.modalData.totalRegistrosExportacionExcel;
  }

  get LIMITE_REG_EXP_EXCEL() {
    return this.modalData.limiteRegistrosExportacionExcel;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.reportTitle = this.translate.instant(REPORT_TITLE_KEY);
    this.formGroup = this.buildFormGroup();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      outputType: new FormControl(OutputReport.XLSX, Validators.required),
      reportTitle: new FormControl(this.reportTitle, Validators.required),

      showMemorias: new FormControl(true),
    });
  }


  protected getReportOptions(): IReportConfig<IActaReportOptions> {
    const reportModalData: IReportConfig<IActaReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showMemorias: this.formGroup.controls.showMemorias.value,
        columnMinWidth: 120
      }
    };
    return reportModalData;
  }

  protected getKey(): string {
    return ACTA_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.FEMALE;
  }


}
