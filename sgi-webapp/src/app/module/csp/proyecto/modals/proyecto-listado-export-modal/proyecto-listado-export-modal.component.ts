import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { IProyectoReportOptions, ProyectoListadoExportService } from '../../proyecto-listado-export.service';

const PROYECTO_KEY = marker('csp.proyecto');

@Component({
  templateUrl: './proyecto-listado-export-modal.component.html',
  styleUrls: ['./proyecto-listado-export-modal.component.scss']
})
export class ProyectoListadoExportModalComponent extends BaseExportModalComponent<IProyectoReportOptions> implements OnInit {

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
    matDialogRef: MatDialogRef<ProyectoListadoExportModalComponent>,
    translate: TranslateService,
    proyectoListadoExportService: ProyectoListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IBaseExportModalData
  ) {
    super(proyectoListadoExportService, translate, matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
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
    const formGroup = new FormGroup({
      outputType: new FormControl(this.outputType, Validators.required),
      showTodos: new FormControl(true),
      showAreasConocimiento: new FormControl(true),
      showClasificaciones: new FormControl(true),
      showRelaciones: new FormControl(true),
      showEntidadGestora: new FormControl(true),
      showEntidadesConvocantes: new FormControl(true),
      showPlanesInvestigacion: new FormControl(true),
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

    Object.keys(formGroup.controls).forEach(key => {
      if (key.startsWith('show')) {
        this.subscriptions.push(formGroup.get(key).valueChanges.subscribe(() => {

          if (key === 'showEntidadesConvocantes' && !!formGroup.get(key).value) {
            formGroup.controls.showPlanesInvestigacion.setValue(true, { emitEvent: false });
          }

          let cont = 0;
          Object.keys(formGroup.controls).forEach(key => {
            if (key.startsWith('show') && !formGroup.get(key).value) {
              formGroup.controls.showTodos.setValue(false, { emitEvent: false });
              if (key === 'showEntidadesConvocantes') {
                formGroup.controls.showPlanesInvestigacion.setValue(false, { emitEvent: false });
              }
              cont++;
            } else if (cont === 0) {
              formGroup.controls.showTodos.setValue(true, { emitEvent: false });
            }
          });
        }))
      }
    });

    return formGroup;
  }

  protected getReportOptions(): IReportConfig<IProyectoReportOptions> {
    const reportModalData: IReportConfig<IProyectoReportOptions> = {
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showAreasConocimiento: this.formGroup.controls.showAreasConocimiento.value,
        showClasificaciones: this.formGroup.controls.showClasificaciones.value,
        showRelaciones: this.formGroup.controls.showRelaciones.value,
        showEntidadGestora: this.formGroup.controls.showEntidadGestora.value,
        showEntidadesConvocantes: this.formGroup.controls.showEntidadesConvocantes.value,
        showPlanesInvestigacion: this.formGroup.controls.showPlanesInvestigacion.value,
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
