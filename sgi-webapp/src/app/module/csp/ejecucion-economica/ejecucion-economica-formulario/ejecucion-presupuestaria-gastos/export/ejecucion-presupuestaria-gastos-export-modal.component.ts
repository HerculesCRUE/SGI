import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { SnackBarService } from '@core/services/snack-bar.service';
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

  constructor(
    matDialogRef: MatDialogRef<EjecucionPresupuestariaGastosExportModalComponent>,
    snackBarService: SnackBarService,
    translate: TranslateService,
    ejecucionPresupuestariaGastosExportService: EjecucionPresupuestariaGastosExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IDesgloseEconomicoExportData
  ) {
    super(ejecucionPresupuestariaGastosExportService, snackBarService, translate, matDialogRef);
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
        relationsTypeView: this.getRelationsTypeView(this.formGroup.controls.outputType.value),

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
