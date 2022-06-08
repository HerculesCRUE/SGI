import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { GrupoListadoExportService, IGrupoReportOptions } from '../../grupo-listado-export.service';

const GRUPO_KEY = marker('csp.grupo');
const REPORT_TITLE_KEY = marker('csp.grupo.listado');

export interface IGrupoListadoModalData {
  findOptions: SgiRestFindOptions;
}

@Component({
  templateUrl: './grupo-listado-export-modal.component.html',
  styleUrls: ['./grupo-listado-export-modal.component.scss']
})
export class GrupoListadoExportModalComponent extends BaseExportModalComponent<IGrupoReportOptions> implements OnInit {
  private reportTitle: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<GrupoListadoExportModalComponent>,
    translate: TranslateService,
    grupoListadoExportService: GrupoListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IGrupoListadoModalData
  ) {
    super(grupoListadoExportService, translate, matDialogRef);
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

      showEquiposInvestigacion: new FormControl(true),
      showEnlaces: new FormControl(true),
      showResponsablesEconomicos: new FormControl(true),
      showPersonasAutorizadas: new FormControl(true),
      showLineasInvestigacion: new FormControl(true),
      showEquiposInstrumentales: new FormControl(true),
    });
  }

  protected getReportOptions(): IReportConfig<IGrupoReportOptions> {
    const reportModalData: IReportConfig<IGrupoReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
      outputType: this.formGroup.controls.outputType.value,
      hideBlocksIfNoData: this.formGroup.controls.hideBlocksIfNoData.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showEquiposInvestigacion: this.formGroup.controls.showEquiposInvestigacion.value,
        showEnlaces: this.formGroup.controls.showEnlaces.value,
        showResponsablesEconomicos: this.formGroup.controls.showResponsablesEconomicos.value,
        showPersonasAutorizadas: this.formGroup.controls.showPersonasAutorizadas.value,
        showLineasInvestigacion: this.formGroup.controls.showLineasInvestigacion.value,
        showEquiposInstrumentales: this.formGroup.controls.showEquiposInstrumentales.value,
        columnMinWidth: 120
      }
    };
    return reportModalData;
  }


  protected getKey(): string {
    return GRUPO_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.MALE;
  }


}
