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
import { ConvocatoriaListadoExportService, IConvocatoriaReportOptions } from '../../convocatoria-listado-export.service';

const CONVOCATORIA_KEY = marker('csp.convocatoria');
const REPORT_TITLE_KEY = marker('csp.convocatoria.listado');

export interface IConvocatoriaListadoModalData {
  findOptions: SgiRestFindOptions;
}

@Component({
  templateUrl: './convocatoria-listado-export-modal.component.html',
  styleUrls: ['./convocatoria-listado-export-modal.component.scss']
})
export class ConvocatoriaListadoExportModalComponent extends BaseExportModalComponent<IReportOptions> implements OnInit {
  private reportTitle: string;

  constructor(
    matDialogRef: MatDialogRef<ConvocatoriaListadoExportModalComponent>,
    snackBarService: SnackBarService,
    translate: TranslateService,
    convocatoriaListadoExportService: ConvocatoriaListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IConvocatoriaListadoModalData
  ) {
    super(convocatoriaListadoExportService, snackBarService, translate, matDialogRef);
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
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

      showAreasTematicas: new FormControl(true),
      showEntidadesConvocantes: new FormControl(true),
      showEntidadesFinanciadoras: new FormControl(true),
      showEnlaces: new FormControl(true),
      showFases: new FormControl(true),
      showCalendarioJustificacion: new FormControl(true),
      showPeriodosSeguimientoCientifico: new FormControl(true),
      showHitos: new FormControl(true),
      showRequisitosIP: new FormControl(true),
      showRequisitosEquipo: new FormControl(true),
      showElegibilidad: new FormControl(true),
      showPartidasPresupuestarias: new FormControl(true),
      showConfiguracionSolicitudes: new FormControl(true)
    });
  }


  protected getReportOptions(): IReportConfig<IConvocatoriaReportOptions> {
    const reportModalData: IReportConfig<IConvocatoriaReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showAreasTematicas: this.formGroup.controls.showAreasTematicas.value,
        showEntidadesConvocantes: this.formGroup.controls.showEntidadesConvocantes.value,
        showEntidadesFinanciadoras: this.formGroup.controls.showEntidadesFinanciadoras.value,
        showEnlaces: this.formGroup.controls.showEnlaces.value,
        showFases: this.formGroup.controls.showFases.value,
        showCalendarioJustificacion: this.formGroup.controls.showCalendarioJustificacion.value,
        showPeriodosSeguimientoCientifico: this.formGroup.controls.showPeriodosSeguimientoCientifico.value,
        showHitos: this.formGroup.controls.showHitos.value,
        showRequisitosIP: this.formGroup.controls.showRequisitosIP.value,
        showRequisitosEquipo: this.formGroup.controls.showRequisitosEquipo.value,
        showElegibilidad: this.formGroup.controls.showElegibilidad.value,
        showPartidasPresupuestarias: this.formGroup.controls.showPartidasPresupuestarias.value,
        showConfiguracionSolicitudes: this.formGroup.controls.showConfiguracionSolicitudes.value,
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
