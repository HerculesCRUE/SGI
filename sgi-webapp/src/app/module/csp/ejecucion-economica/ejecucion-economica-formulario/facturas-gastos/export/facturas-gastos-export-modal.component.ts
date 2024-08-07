import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { IEjecucionPresupuestariaReportOptions } from '../../../common/ejecucion-presupuestaria-report-options';
import { IDesgloseEconomicoExportData } from '../../desglose-economico.fragment';
import { FacturasGastosExportService } from './facturas-gastos-export.service';

const FACTURAS_JUSTIFICANTES = marker('menu.csp.ejecucion-economica.facturas-justificantes');
const FACTURAS_JUSTIFICANTES_GASTOS = marker('menu.csp.ejecucion-economica.facturas-justificantes.facturas-gastos');

@Component({
  templateUrl: './facturas-gastos-export-modal.component.html',
  styleUrls: ['./facturas-gastos-export-modal.component.scss']
})
export class FacturasGastosExportModalComponent
  extends BaseExportModalComponent<IEjecucionPresupuestariaReportOptions> implements OnInit {

  get TOTAL_REG_EXP_EXCEL() {
    return this.modalData.totalRegistrosExportacionExcel;
  }

  get LIMITE_REG_EXP_EXCEL() {
    return this.modalData.limiteRegistrosExportacionExcel;
  }

  constructor(
    matDialogRef: MatDialogRef<FacturasGastosExportModalComponent>,
    translate: TranslateService,
    facturasGastosExportService: FacturasGastosExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IDesgloseEconomicoExportData
  ) {
    super(facturasGastosExportService, translate, matDialogRef);
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
        columnMinWidth: 120,
        rowConfig: this.modalData.rowConfig
      }
    };
    return reportModalData;
  }


  protected setupI18N(): void {
    this.title = this.translate.instant(FACTURAS_JUSTIFICANTES) + ' - ' + this.translate.instant(FACTURAS_JUSTIFICANTES_GASTOS);
  }

  protected getKey(): string {
    return null;
  }

  protected getGender() {
    return null;
  }


}
