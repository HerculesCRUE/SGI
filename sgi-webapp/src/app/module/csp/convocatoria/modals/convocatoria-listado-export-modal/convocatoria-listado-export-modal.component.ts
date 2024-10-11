import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { ConvocatoriaListadoExportService, IConvocatoriaReportOptions } from '../../convocatoria-listado-export.service';

const CONVOCATORIA_KEY = marker('csp.convocatoria');
@Component({
  templateUrl: './convocatoria-listado-export-modal.component.html',
  styleUrls: ['./convocatoria-listado-export-modal.component.scss']
})
export class ConvocatoriaListadoExportModalComponent extends BaseExportModalComponent<IReportOptions> implements OnInit {

  constructor(
    matDialogRef: MatDialogRef<ConvocatoriaListadoExportModalComponent>,
    translate: TranslateService,
    convocatoriaListadoExportService: ConvocatoriaListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IBaseExportModalData
  ) {
    super(convocatoriaListadoExportService, translate, matDialogRef);
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TOTAL_REG_EXP_EXCEL() {
    return this.modalData.totalRegistrosExportacionExcel;
  }

  get LIMITE_REG_EXP_EXCEL() {
    return this.modalData.limiteRegistrosExportacionExcel;
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
      showAreasTematicas: new FormControl(true),
      showEntidadesConvocantes: new FormControl(true),
      showPlanesInvestigacion: new FormControl(true),
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

  protected getReportOptions(): IReportConfig<IConvocatoriaReportOptions> {
    const reportModalData: IReportConfig<IConvocatoriaReportOptions> = {
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showAreasTematicas: this.formGroup.controls.showAreasTematicas.value,
        showEntidadesConvocantes: this.formGroup.controls.showEntidadesConvocantes.value,
        showPlanesInvestigacion: this.formGroup.controls.showPlanesInvestigacion.value,
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
        columnMinWidth: 200
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
