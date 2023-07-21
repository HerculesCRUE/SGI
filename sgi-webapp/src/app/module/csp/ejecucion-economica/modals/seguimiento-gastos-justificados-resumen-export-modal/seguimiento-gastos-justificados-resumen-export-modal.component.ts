import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { IGastosJustificadosReportOptions, SeguimientoGastosJustificadosResumenListadoExportService } from '../../seguimiento-gastos-justificados-listado-export.service';

const PROYECTO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export');
const REPORT_TITLE_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.export');

export interface ISeguimientoGastosJustificadosResumenExportModalData extends IBaseExportModalData {
  proyectoSgeRef: string;
}

@Component({
  templateUrl: './seguimiento-gastos-justificados-resumen-export-modal.component.html',
  styleUrls: ['./seguimiento-gastos-justificados-resumen-export-modal.component.scss']
})
export class SeguimientoGastosJustificadosResumenExportModalComponent extends BaseExportModalComponent<IGastosJustificadosReportOptions> implements OnInit {
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
    matDialogRef: MatDialogRef<SeguimientoGastosJustificadosResumenExportModalComponent>,
    translate: TranslateService,
    seguimientoGastosJustificadosResumenListadoExportService: SeguimientoGastosJustificadosResumenListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: ISeguimientoGastosJustificadosResumenExportModalData
  ) {
    super(seguimientoGastosJustificadosResumenListadoExportService, translate, matDialogRef);
    seguimientoGastosJustificadosResumenListadoExportService.proyectoSgeRef = modalData.proyectoSgeRef;
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

      hideBlocksIfNoData: new FormControl(true),
    });
  }

  protected getReportOptions(): IReportConfig<IGastosJustificadosReportOptions> {
    const reportModalData: IReportConfig<IGastosJustificadosReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
      outputType: this.formGroup.controls.outputType.value,
      hideBlocksIfNoData: this.formGroup.controls.hideBlocksIfNoData.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        columnMinWidth: 120
      }
    };
    return reportModalData;
  }


  protected getKey(): string {
    return PROYECTO_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.MALE;
  }


}
