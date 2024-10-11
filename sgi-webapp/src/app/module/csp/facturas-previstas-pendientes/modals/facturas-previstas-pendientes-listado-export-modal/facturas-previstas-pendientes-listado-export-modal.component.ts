import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { FacturasPrevistasPendientesListadoExportService } from '../../facturas-previstas-pendientes-listado-export.service';

const FACTURAS_PREVISTAS_PENDIENTES_KEY = marker('menu.csp.facturas-previstas-pendientes');

@Component({
  templateUrl: './facturas-previstas-pendientes-listado-export-modal.component.html',
  styleUrls: ['./facturas-previstas-pendientes-listado-export-modal.component.scss']
})
export class FacturasPrevistasPendientesListadoExportModalComponent extends BaseExportModalComponent<IReportOptions> implements OnInit {

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
    matDialogRef: MatDialogRef<FacturasPrevistasPendientesListadoExportModalComponent>,
    translate: TranslateService,
    exportService: FacturasPrevistasPendientesListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IBaseExportModalData
  ) {
    super(exportService, translate, matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formGroup = this.buildFormGroup();
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      outputType: new FormControl(this.outputType, Validators.required)
    });

    return formGroup;
  }

  protected getReportOptions(): IReportConfig<IReportOptions> {
    const reportModalData: IReportConfig<IReportOptions> = {
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        findOptions: this.modalData.findOptions
      }
    };
    return reportModalData;
  }


  protected getKey(): string {
    return FACTURAS_PREVISTAS_PENDIENTES_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.FEMALE;
  }


}
