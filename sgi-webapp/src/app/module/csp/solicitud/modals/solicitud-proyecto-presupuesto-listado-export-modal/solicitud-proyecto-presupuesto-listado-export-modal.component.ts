import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { ISolicitudPresupuestoReportOptions, SolicitudProyectoPresupuestoListadoExportService } from '../../solicitud-proyecto-presupuesto-listado-export.service';

const SOLICITUD_PROYECTO_PRESUPUESTO_KEY = marker('csp.solicitud-proyecto-presupuesto.titulo');
const REPORT_TITLE_KEY = marker('csp.solicitud-proyecto-presupuesto.title-informe');

export interface ISolicitudProyectoPresupuetoModalData {
  solicitudId: number;
}

@Component({
  templateUrl: './solicitud-proyecto-presupuesto-listado-export-modal.component.html',
  styleUrls: ['./solicitud-proyecto-presupuesto-listado-export-modal.component.scss']
})
export class SolicitudProyectoPresupuestoListadoExportModalComponent extends BaseExportModalComponent<IReportOptions> implements OnInit {
  private reportTitle: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<SolicitudProyectoPresupuestoListadoExportModalComponent>,
    translate: TranslateService,
    solicitudProyectoPresupuestoListadoExportService: SolicitudProyectoPresupuestoListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: ISolicitudProyectoPresupuetoModalData
  ) {
    super(solicitudProyectoPresupuestoListadoExportService, translate, matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.reportTitle = this.translate.instant(REPORT_TITLE_KEY);
    this.formGroup = this.buildFormGroup();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      outputType: new FormControl(this.outputType, Validators.required),
      reportTitle: new FormControl(this.reportTitle, Validators.required),

    });
  }

  protected getReportOptions(): IReportConfig<ISolicitudPresupuestoReportOptions> {
    const reportModalData: IReportConfig<ISolicitudPresupuestoReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        solicitudId: this.modalData.solicitudId,
        columnMinWidth: 120
      }
    };
    return reportModalData;
  }

  protected getKey(): string {
    return SOLICITUD_PROYECTO_PRESUPUESTO_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.MALE;
  }

}
