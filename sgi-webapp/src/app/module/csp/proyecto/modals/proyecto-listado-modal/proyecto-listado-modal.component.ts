import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { IProyectoReportOptions, ProyectoListadoService } from '../../proyecto-listado.service';

const PROYECTO_KEY = marker('csp.proyecto');
const REPORT_TITLE_KEY = marker('csp.ejecucion-economica.proyectos');

export interface IProyectoListadoModalData {
  findOptions: SgiRestFindOptions;
}

@Component({
  templateUrl: './proyecto-listado-modal.component.html',
  styleUrls: ['./proyecto-listado-modal.component.scss']
})
export class ProyectoListadoModalComponent extends BaseExportModalComponent<IProyectoReportOptions> implements OnInit {
  private reportTitle: string;

  constructor(
    matDialogRef: MatDialogRef<ProyectoListadoModalComponent>,
    snackBarService: SnackBarService,
    translate: TranslateService,
    proyectoListadoService: ProyectoListadoService,
    @Inject(MAT_DIALOG_DATA) private modalData: IProyectoListadoModalData
  ) {
    super(proyectoListadoService, snackBarService, translate, matDialogRef);
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
      showMiembrosEquipo: new FormControl(false),
      showEntidadesConvocantes: new FormControl(false),
    });
  }

  protected getReportOptions(): IReportConfig<IProyectoReportOptions> {
    const reportModalData: IReportConfig<IProyectoReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showMiembrosEquipo: this.formGroup.controls.showMiembrosEquipo.value,
        showEntidadesConvocantes: this.formGroup.controls.showEntidadesConvocantes.value,
        relationsTypeView: this.getRelationsTypeView(this.formGroup.controls.outputType.value),
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
