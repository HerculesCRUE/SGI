import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { ISolicitudReportOptions, SolicitudListadoExportService } from '../../solicitud-listado-export.service';

const SOLICITUD_KEY = marker('csp.solicitud');
const REPORT_TITLE_KEY = marker('csp.solicitud.listado');

export interface ISolicitudListadoDataExportModalData {
  findOptions: SgiRestFindOptions;
}

@Component({
  templateUrl: './solicitud-listado-export-modal.component.html',
  styleUrls: ['./solicitud-listado-export-modal.component.scss']
})
export class SolicitudListadoExportModalComponent extends BaseExportModalComponent<IReportOptions> implements OnInit {
  private reportTitle: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<SolicitudListadoExportModalComponent>,
    translate: TranslateService,
    solicitudListadoExportService: SolicitudListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: ISolicitudListadoDataExportModalData
  ) {
    super(solicitudListadoExportService, translate, matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.reportTitle = this.translate.instant(REPORT_TITLE_KEY);
    this.formGroup = this.buildFormGroup();
  }

  selectUnselectAll($event: MatCheckboxChange): void {
    Object.keys(this.formGroup.controls).forEach(key => {
      if (key.startsWith('show')) {
        this.formGroup.get(key).patchValue($event.checked);
      }
    });
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      outputType: new FormControl(this.outputType, Validators.required),
      reportTitle: new FormControl(this.reportTitle, Validators.required),

      hideBlocksIfNoData: new FormControl(true),

      showSolicitudEntidadesConvocantes: new FormControl(true),
      showSolicitudProyectoFichaGeneral: new FormControl(true),
      showSolicitudProyectoAreasConocimiento: new FormControl(true),
      showSolicitudProyectoClasificaciones: new FormControl(true),
      showSolicitudProyectoEquipo: new FormControl(true),
      showSolicitudProyectoResponsableEconomico: new FormControl(true),
      showSolicitudProyectoSocios: new FormControl(true),
      showSolicitudProyectoEntidadesFinanciadoras: new FormControl(true)
    });
  }

  protected getReportOptions(): IReportConfig<ISolicitudReportOptions> {
    const reportModalData: IReportConfig<ISolicitudReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
      outputType: this.formGroup.controls.outputType.value,
      hideBlocksIfNoData: this.formGroup.controls.hideBlocksIfNoData.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showSolicitudEntidadesConvocantes: this.formGroup.controls.showSolicitudEntidadesConvocantes.value,
        showSolicitudProyectoFichaGeneral: this.formGroup.controls.showSolicitudProyectoFichaGeneral.value,
        showSolicitudProyectoAreasConocimiento: this.formGroup.controls.showSolicitudProyectoAreasConocimiento.value,
        showSolicitudProyectoClasificaciones: this.formGroup.controls.showSolicitudProyectoClasificaciones.value,
        showSolicitudProyectoEquipo: this.formGroup.controls.showSolicitudProyectoEquipo.value,
        showSolicitudProyectoResponsableEconomico: this.formGroup.controls.showSolicitudProyectoResponsableEconomico.value,
        showSolicitudProyectoSocios: this.formGroup.controls.showSolicitudProyectoSocios.value,
        showSolicitudProyectoEntidadesFinanciadoras: this.formGroup.controls.showSolicitudProyectoEntidadesFinanciadoras.value,
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
