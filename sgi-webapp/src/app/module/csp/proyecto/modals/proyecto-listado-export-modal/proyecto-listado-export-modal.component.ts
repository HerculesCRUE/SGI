import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { IProyectoReportOptions, ProyectoListadoExportService } from '../../proyecto-listado-export.service';

const PROYECTO_KEY = marker('csp.proyecto');
const REPORT_TITLE_KEY = marker('csp.ejecucion-economica.proyectos');

export interface IProyectoListadoModalData {
  findOptions: SgiRestFindOptions;
}

@Component({
  templateUrl: './proyecto-listado-export-modal.component.html',
  styleUrls: ['./proyecto-listado-export-modal.component.scss']
})
export class ProyectoListadoExportModalComponent extends BaseExportModalComponent<IProyectoReportOptions> implements OnInit {
  private reportTitle: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<ProyectoListadoExportModalComponent>,
    snackBarService: SnackBarService,
    translate: TranslateService,
    proyectoListadoExportService: ProyectoListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IProyectoListadoModalData
  ) {
    super(proyectoListadoExportService, snackBarService, translate, matDialogRef);
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

      showAreasConocimiento: new FormControl(true),
      showClasificaciones: new FormControl(true),
      showRelaciones: new FormControl(true),
      showEntidadGestora: new FormControl(true),
      showEntidadesConvocantes: new FormControl(true),
      showEntidadesFinanciadoras: new FormControl(true),
      showMiembrosEquipo: new FormControl(true),
      showResponsablesEconomicos: new FormControl(true),
      showSocios: new FormControl(true),
      showProrrogas: new FormControl(true),
      showConvocatoria: new FormControl(true),
      showSolicitud: new FormControl(true),
      showSeguimientosCientificos: new FormControl(true),
      showEligibilidad: new FormControl(true),
      showPartidasPresupuestarias: new FormControl(true),
      showPresupuesto: new FormControl(true),
      showCalendarioJustificacion: new FormControl(true),
      showCalendarioFacturacion: new FormControl(true)
    });
  }

  protected getReportOptions(): IReportConfig<IProyectoReportOptions> {
    const reportModalData: IReportConfig<IProyectoReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showAreasConocimiento: this.formGroup.controls.showAreasConocimiento.value,
        showClasificaciones: this.formGroup.controls.showClasificaciones.value,
        showRelaciones: this.formGroup.controls.showRelaciones.value,
        showEntidadGestora: this.formGroup.controls.showEntidadGestora.value,
        showEntidadesConvocantes: this.formGroup.controls.showEntidadesConvocantes.value,
        showEntidadesFinanciadoras: this.formGroup.controls.showEntidadesFinanciadoras.value,
        showMiembrosEquipo: this.formGroup.controls.showMiembrosEquipo.value,
        showResponsablesEconomicos: this.formGroup.controls.showResponsablesEconomicos.value,
        showSocios: this.formGroup.controls.showSocios.value,
        showProrrogas: this.formGroup.controls.showProrrogas.value,
        showConvocatoria: this.formGroup.controls.showConvocatoria.value,
        showSolicitud: this.formGroup.controls.showSolicitud.value,
        showSeguimientosCientificos: this.formGroup.controls.showSeguimientosCientificos.value,
        showElegibilidad: this.formGroup.controls.showEligibilidad.value,
        showPartidasPresupuestarias: this.formGroup.controls.showPartidasPresupuestarias.value,
        showPresupuesto: this.formGroup.controls.showPresupuesto.value,
        showCalendarioJustificacion: this.formGroup.controls.showCalendarioJustificacion.value,
        showCalendarioFacturacion: this.formGroup.controls.showCalendarioFacturacion.value,
        columnMinWidth: 120
      }
    };
    return reportModalData;
  }


  protected getKey(): string {
    return PROYECTO_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.MALE;
  }


}
