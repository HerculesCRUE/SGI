import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { IPeticionEvaluacionReportOptions, PeticionEvaluacionListadoExportService } from '../../peticion-evaluacion-listado-export.service';

const REPORT_TITLE_KEY = marker('eti.peticion-evaluacion.report.title');

export interface IPeticionEvaluacionListadoModalData {
  findOptions: SgiRestFindOptions;
}

export const OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP: Map<OutputReport, string> = new Map([
  [OutputReport.XLSX, marker('export.type.xlsx')],
  [OutputReport.CSV, marker('export.type.csv')],
]);

@Component({
  templateUrl: './peticion-evaluacion-listado-export-modal.component.html',
  styleUrls: ['./peticion-evaluacion-listado-export-modal.component.scss']
})
export class PeticionEvaluacionListadoExportModalComponent extends
  BaseExportModalComponent<IPeticionEvaluacionReportOptions> implements OnInit {

  readonly OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP = OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP;
  private reportTitle: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<PeticionEvaluacionListadoExportModalComponent>,
    translate: TranslateService,
    invencionListadoExportService: PeticionEvaluacionListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IPeticionEvaluacionListadoModalData
  ) {
    super(invencionListadoExportService, translate, matDialogRef);
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
    const formGroup = new FormGroup({
      outputType: new FormControl(OutputReport.XLSX, Validators.required),
      hideBlocksIfNoData: new FormControl(true),

      showTodos: new FormControl(true),
      showEquipoInvestigador: new FormControl(true),
      showAsignacionTareas: new FormControl(true),
      showMemorias: new FormControl(true)
    });

    Object.keys(formGroup.controls).forEach(key => {
      if (key.startsWith('show')) {
        this.subscriptions.push(formGroup.get(key).valueChanges.subscribe(() => {
          let cont = 0;
          Object.keys(formGroup.controls).forEach(key => {
            if (key.startsWith('show') && !formGroup.get(key).value) {
              formGroup.controls.showTodos.setValue(false, { emitEvent: false });
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

  protected getReportOptions(): IReportConfig<IPeticionEvaluacionReportOptions> {
    const reportModalData: IReportConfig<IPeticionEvaluacionReportOptions> = {
      title: this.translate.instant('eti.peticion-evaluacion.report.title'),
      outputType: this.formGroup.controls.outputType.value,
      hideBlocksIfNoData: this.formGroup.controls.hideBlocksIfNoData.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showEquipoInvestigador: this.formGroup.controls.showEquipoInvestigador.value,
        showAsignacionTareas: this.formGroup.controls.showAsignacionTareas.value,
        showMemorias: this.formGroup.controls.showMemorias.value,
        columnMinWidth: 200
      }
    };
    return reportModalData;
  }


  protected getKey(): string {
    return REPORT_TITLE_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.MALE;
  }
}
