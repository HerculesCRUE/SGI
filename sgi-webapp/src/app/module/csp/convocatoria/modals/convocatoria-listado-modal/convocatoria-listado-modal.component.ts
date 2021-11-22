import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export-modal/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReportType } from '@core/models/rep/output-report.enum';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { ConvocatoriaListadoService } from '../../convocatoria-listado.service';

const CONVOCATORIA_KEY = marker('csp.convocatoria');
const REPORT_TITLE_KEY = marker('csp.convocatoria.listado');

export interface IConvocatoriaListadoModalData {
  findOptions: SgiRestFindOptions;
}

@Component({
  templateUrl: './convocatoria-listado-modal.component.html',
  styleUrls: ['./convocatoria-listado-modal.component.scss']
})
export class ConvocatoriaListadoModalComponent extends BaseExportModalComponent<IReportOptions> implements OnInit {
  reportTitle: string;

  constructor(
    matDialogRef: MatDialogRef<ConvocatoriaListadoModalComponent>,
    snackBarService: SnackBarService,
    translate: TranslateService,
    convocatoriaListadoService: ConvocatoriaListadoService,
    @Inject(MAT_DIALOG_DATA) private modalData: IConvocatoriaListadoModalData
  ) {
    super(convocatoriaListadoService, snackBarService, translate, matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.reportTitle = this.translate.instant(REPORT_TITLE_KEY);
    this.formGroup = this.buildFormGroup();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      outputReportType: new FormControl(this.outputReportType, Validators.required),
      reportTitle: new FormControl(this.reportTitle, Validators.required),
      showMiembrosEquipo: new FormControl(false),
      showEntidadesConvocantes: new FormControl(false),
    });
  }

  protected getReportOptions(): IReportConfig<IReportOptions> {
    const reportModalData: IReportConfig<IReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
      outputType: this.formGroup.controls.outputReportType.value,
      reportOptions: {
        relationsOrientationTable: this.formGroup.controls.outputReportType.value === OutputReportType.PDF,
        findOptions: this.modalData.findOptions,
        columnMinWidth: 120
      }
    };
    return reportModalData;
  }

  protected getKey(): string {
    return CONVOCATORIA_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.FEMALE;
  }


}
