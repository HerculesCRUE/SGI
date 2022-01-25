import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { IEjecucionPresupuestariaReportOptions } from '../../../common/ejecucion-presupuestaria-report-options';
import { IDesgloseEconomicoExportData } from '../../desglose-economico.fragment';
import { DetalleOperacionesGastosExportService } from './detalle-operaciones-gastos-export.service';

const DETALLE_OPERACIONES = marker('menu.csp.ejecucion-economica.detalle-operaciones');
const DETALLE_OPERACIONES_GASTOS = marker('menu.csp.ejecucion-economica.detalle-operaciones.gastos');


@Component({
  templateUrl: './detalle-operaciones-gastos-export-modal.component.html',
  styleUrls: ['./detalle-operaciones-gastos-export-modal.component.scss']
})
export class DetalleOperacionesGastosExportModalComponent
  extends BaseExportModalComponent<IEjecucionPresupuestariaReportOptions> implements OnInit {

  constructor(
    matDialogRef: MatDialogRef<DetalleOperacionesGastosExportModalComponent>,
    snackBarService: SnackBarService,
    translate: TranslateService,
    detalleOperacionesGastosExportService: DetalleOperacionesGastosExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IDesgloseEconomicoExportData
  ) {
    super(detalleOperacionesGastosExportService, snackBarService, translate, matDialogRef);
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
    this.title = this.translate.instant(DETALLE_OPERACIONES) + ' - ' + this.translate.instant(DETALLE_OPERACIONES_GASTOS);
  }

  protected getKey(): string {
    return null;
  }

  protected getGender() {
    return null;
  }


}
