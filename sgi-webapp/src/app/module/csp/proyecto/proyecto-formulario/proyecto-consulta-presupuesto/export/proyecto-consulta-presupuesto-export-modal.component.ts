import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { IConsultaPresupuestoExportData, IConsultaPresupuestoReportOptions, ProyectoConsultaPresupuestoExportService } from './proyecto-consulta-presupuesto-export.service';

const TITLE = marker('csp.proyecto-consulta-presupuesto.export.title');

@Component({
  templateUrl: './proyecto-consulta-presupuesto-export-modal.component.html',
  styleUrls: ['./proyecto-consulta-presupuesto-export-modal.component.scss']
})
export class ProyectoConsultaPresupuestoExportModalComponent
  extends BaseExportModalComponent<IConsultaPresupuestoReportOptions> implements OnInit {

  constructor(
    matDialogRef: MatDialogRef<ProyectoConsultaPresupuestoExportModalComponent>,
    snackBarService: SnackBarService,
    translate: TranslateService,
    proyectoConsultaPresupuestoExportService: ProyectoConsultaPresupuestoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IConsultaPresupuestoExportData
  ) {
    super(proyectoConsultaPresupuestoExportService, snackBarService, translate, matDialogRef);
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

  protected getReportOptions(): IReportConfig<IConsultaPresupuestoReportOptions> {
    const reportModalData: IReportConfig<IConsultaPresupuestoReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        data: this.modalData.data,
        columns: this.modalData.columns ?? [],
        columnMinWidth: 120
      }
    };
    return reportModalData;
  }


  protected setupI18N(): void {
    this.title = this.translate.instant(TITLE);
  }

  protected getKey(): string {
    return null;
  }

  protected getGender() {
    return null;
  }


}
