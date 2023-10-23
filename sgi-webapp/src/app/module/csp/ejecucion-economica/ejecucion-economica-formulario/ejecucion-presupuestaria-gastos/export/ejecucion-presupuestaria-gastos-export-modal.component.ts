import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { IEjecucionPresupuestariaReportOptions } from '../../../common/ejecucion-presupuestaria-report-options';
import { IDesgloseEconomicoExportData } from '../../desglose-economico.fragment';
import { EjecucionPresupuestariaGastosExportService } from './ejecucion-presupuestaria-gastos-export.service';

const EJECUCION_PRESUPUESTARIA = marker('menu.csp.ejecucion-economica.ejecucion-presupuestaria');
const EJECUCION_PRESUPUESTARIA_GASTOS = marker('menu.csp.ejecucion-economica.ejecucion-presupuestaria.gastos');

@Component({
  templateUrl: './ejecucion-presupuestaria-gastos-export-modal.component.html',
  styleUrls: ['./ejecucion-presupuestaria-gastos-export-modal.component.scss']
})
export class EjecucionPresupuestariaGastosExportModalComponent
  extends BaseExportModalComponent<IEjecucionPresupuestariaReportOptions> implements OnInit {

  get TOTAL_REG_EXP_EXCEL() {
    return this.modalData.totalRegistrosExportacionExcel;
  }

  get LIMITE_REG_EXP_EXCEL() {
    return this.modalData.limiteRegistrosExportacionExcel;
  }

  constructor(
    matDialogRef: MatDialogRef<EjecucionPresupuestariaGastosExportModalComponent>,
    translate: TranslateService,
    ejecucionPresupuestariaGastosExportService: EjecucionPresupuestariaGastosExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IDesgloseEconomicoExportData
  ) {
    super(ejecucionPresupuestariaGastosExportService, translate, matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formGroup = this.buildFormGroup();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      outputType: new FormControl(this.outputType, Validators.required)
    });
  }

  protected getReportOptions(): IReportConfig<IEjecucionPresupuestariaReportOptions> {
    const reportModalData: IReportConfig<IEjecucionPresupuestariaReportOptions> = {
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        data: this.modalData.data,
        columns: this.modalData.columns,
        columnMinWidth: 120
      }
    };
    return reportModalData;
  }

  protected setupI18N(): void {
    this.title = this.translate.instant(EJECUCION_PRESUPUESTARIA) + ' - ' + this.translate.instant(EJECUCION_PRESUPUESTARIA_GASTOS);
  }

  protected getKey(): string {
    return null;
  }

  protected getGender() {
    return null;
  }


}
