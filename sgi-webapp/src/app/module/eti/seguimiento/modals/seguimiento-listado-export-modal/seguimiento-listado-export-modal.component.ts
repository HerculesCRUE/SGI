import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { ISeguimientoReportOptions, RolPersona, SeguimientoListadoExportService } from '../../seguimiento-listado-export.service';

const REPORT_TITLE_KEY = marker('eti.seguimiento.report.title');

export interface ISeguimientoListadoModalData extends IBaseExportModalData {
  rolPersona: RolPersona;
}

export const OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP: Map<OutputReport, string> = new Map([
  [OutputReport.XLSX, marker('export.type.xlsx')],
  [OutputReport.CSV, marker('export.type.csv')],
]);

@Component({
  templateUrl: './seguimiento-listado-export-modal.component.html',
  styleUrls: ['./seguimiento-listado-export-modal.component.scss']
})
export class SeguimientoListadoExportModalComponent extends
  BaseExportModalComponent<ISeguimientoReportOptions> implements OnInit {

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
    matDialogRef: MatDialogRef<SeguimientoListadoExportModalComponent>,
    translate: TranslateService,
    seguimientoListadoExportService: SeguimientoListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: ISeguimientoListadoModalData
  ) {
    super(seguimientoListadoExportService, translate, matDialogRef);
    seguimientoListadoExportService.rolPersona = modalData.rolPersona;
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

      showEvaluacionesAnteriores: new FormControl(true)
    });
  }

  protected getReportOptions(): IReportConfig<ISeguimientoReportOptions> {
    const reportModalData: IReportConfig<ISeguimientoReportOptions> = {
      title: this.translate.instant(REPORT_TITLE_KEY),
      outputType: this.formGroup.controls.outputType.value,
      hideBlocksIfNoData: this.formGroup.controls.hideBlocksIfNoData.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showEvaluacionesAnteriores: this.formGroup.controls.showEvaluacionesAnteriores.value,
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
