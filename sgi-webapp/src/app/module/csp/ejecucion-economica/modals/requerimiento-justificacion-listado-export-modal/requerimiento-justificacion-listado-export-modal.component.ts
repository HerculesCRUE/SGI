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
import { IRequerimientoJustificacionReportOptions, RequerimientoJustificacionListadoExportService } from '../../requerimiento-justificacion-listado-export.service';

const CONVOCATORIA_KEY = marker('csp.requerimiento-justificacion');
const REPORT_TITLE_KEY = marker('csp.requerimiento-justificacion.listado');

export interface IRequerimientoJustificacionListadoModalData extends IBaseExportModalData {
  idsProyectoSge: string[];
}

export const OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP: Map<OutputReport, string> = new Map([
  [OutputReport.XLSX, marker('export.type.xlsx')],
  [OutputReport.CSV, marker('export.type.csv')],
]);

@Component({
  templateUrl: './requerimiento-justificacion-listado-export-modal.component.html',
  styleUrls: ['./requerimiento-justificacion-listado-export-modal.component.scss']
})
export class RequerimientoJustificacionListadoExportModalComponent extends BaseExportModalComponent<IReportOptions> implements OnInit {

  readonly OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP = OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP;
  public reportTitle: string;

  constructor(
    matDialogRef: MatDialogRef<RequerimientoJustificacionListadoExportModalComponent>,
    translate: TranslateService,
    requerimientoJustificacionExportService: RequerimientoJustificacionListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IRequerimientoJustificacionListadoModalData
  ) {
    super(requerimientoJustificacionExportService, translate, matDialogRef);
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
    });
  }

  protected getReportOptions(): IReportConfig<IRequerimientoJustificacionReportOptions> {
    const reportModalData: IReportConfig<IRequerimientoJustificacionReportOptions> = {
      title: this.translate.instant(REPORT_TITLE_KEY),
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        idsProyectoSge: this.modalData.idsProyectoSge,
        columnMinWidth: 200
      }
    };
    return reportModalData;
  }

  protected getKey(): string {
    return CONVOCATORIA_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.FEMALE;
  }


}
