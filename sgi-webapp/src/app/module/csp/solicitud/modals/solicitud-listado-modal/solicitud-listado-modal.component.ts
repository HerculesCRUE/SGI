import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { SolicitudListadoService } from '../../solicitud-listado.service';

const SOLICITUD_KEY = marker('csp.solicitud');
const REPORT_TITLE_KEY = marker('csp.solicitud.listado');

export interface ISolicitudListadoModalData {
  findOptions: SgiRestFindOptions;
}

@Component({
  templateUrl: './solicitud-listado-modal.component.html',
  styleUrls: ['./solicitud-listado-modal.component.scss']
})
export class SolicitudListadoModalComponent extends BaseExportModalComponent<IReportOptions> implements OnInit {
  private reportTitle: string;

  constructor(
    matDialogRef: MatDialogRef<SolicitudListadoModalComponent>,
    snackBarService: SnackBarService,
    translate: TranslateService,
    solicitudListadoService: SolicitudListadoService,
    @Inject(MAT_DIALOG_DATA) private modalData: ISolicitudListadoModalData
  ) {
    super(solicitudListadoService, snackBarService, translate, matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.reportTitle = this.translate.instant(REPORT_TITLE_KEY);
    this.formGroup = this.buildFormGroup();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      outputType: new FormControl(this.outputType, Validators.required),
      reportTitle: new FormControl(this.reportTitle, Validators.required)
    });
  }

  protected getReportOptions(): IReportConfig<IReportOptions> {
    const reportModalData: IReportConfig<IReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        columnMinWidth: 120
      }
    };
    return reportModalData;
  }

  protected getKey(): string {
    return SOLICITUD_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.FEMALE;
  }


}
