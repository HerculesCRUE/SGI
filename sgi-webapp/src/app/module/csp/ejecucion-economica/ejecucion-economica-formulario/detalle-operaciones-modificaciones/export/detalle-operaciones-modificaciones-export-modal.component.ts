import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { IEjecucionPresupuestariaReportOptions } from '../../../common/ejecucion-presupuestaria-report-options';
import { IDesgloseEconomicoExportData } from '../../desglose-economico.fragment';
import { DetalleOperacionesModificacionesExportService } from './detalle-operaciones-modificaciones-export.service';

const DETALLE_OPERACIONES = marker('menu.csp.ejecucion-economica.detalle-operaciones');
const DETALLE_OPERACIONES_MODIFICACIONES = marker('menu.csp.ejecucion-economica.detalle-operaciones.modificaciones');

@Component({
  templateUrl: './detalle-operaciones-modificaciones-export-modal.component.html',
  styleUrls: ['./detalle-operaciones-modificaciones-export-modal.component.scss']
})
export class DetalleOperacionesModificacionesExportModalComponent
  extends BaseExportModalComponent<IEjecucionPresupuestariaReportOptions> implements OnInit {

  get TOTAL_REG_EXP_EXCEL() {
    return this.modalData.totalRegistrosExportacionExcel;
  }

  get LIMITE_REG_EXP_EXCEL() {
    return this.modalData.limiteRegistrosExportacionExcel;
  }

  constructor(
    matDialogRef: MatDialogRef<DetalleOperacionesModificacionesExportModalComponent>,
    translate: TranslateService,
    detalleOperacionesModificacionesExportService: DetalleOperacionesModificacionesExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IDesgloseEconomicoExportData
  ) {
    super(detalleOperacionesModificacionesExportService, translate, matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formGroup = this.buildFormGroup();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      outputType: new FormControl(this.outputType, Validators.required),
      reportTitle: new FormControl(this.title, Validators.required),
    });
  }

  protected getReportOptions(): IReportConfig<IEjecucionPresupuestariaReportOptions> {
    const reportModalData: IReportConfig<IEjecucionPresupuestariaReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
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
    this.title = this.translate.instant(DETALLE_OPERACIONES) + ' - ' + this.translate.instant(DETALLE_OPERACIONES_MODIFICACIONES);
  }
  protected getKey(): string {
    return null;
  }

  protected getGender() {
    return null;
  }


}
