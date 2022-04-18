import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { IEjecucionPresupuestariaReportOptions } from '../../../common/ejecucion-presupuestaria-report-options';
import { IDesgloseEconomicoExportData } from '../../desglose-economico.fragment';
import { EjecucionPresupuestariaEstadoActualExportService } from './ejecucion-presupuestaria-estado-actual-export.service';

const EJECUCION_PRESUPUESTARIA = marker('menu.csp.ejecucion-economica.ejecucion-presupuestaria');
const EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL = marker('menu.csp.ejecucion-economica.ejecucion-presupuestaria.estado-actual');

@Component({
  templateUrl: './ejecucion-presupuestaria-estado-actual-export-modal.component.html',
  styleUrls: ['./ejecucion-presupuestaria-estado-actual-export-modal.component.scss']
})
export class EjecucionPresupuestariaEstadoActualExportModalComponent
  extends BaseExportModalComponent<IEjecucionPresupuestariaReportOptions> implements OnInit {

  constructor(
    matDialogRef: MatDialogRef<EjecucionPresupuestariaEstadoActualExportModalComponent>,
    translate: TranslateService,
    ejecucionPresupuestariaEstadoActualExportService: EjecucionPresupuestariaEstadoActualExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IDesgloseEconomicoExportData
  ) {
    super(ejecucionPresupuestariaEstadoActualExportService, translate, matDialogRef);
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
    this.title = this.translate.instant(EJECUCION_PRESUPUESTARIA) + ' - ' + this.translate.instant(EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL);
  }

  protected getKey(): string {
    return null;
  }

  protected getGender() {
    return null;
  }


}
