import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { IInvencionReportOptions, InvencionListadoExportService } from '../../invencion-listado-export.service';

const INVENCION_KEY = marker('pii.invencion');
const REPORT_TITLE_KEY = marker('pii.invencion.export.title');

export const OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP: Map<OutputReport, string> = new Map([
  [OutputReport.XLSX, marker('export.type.xlsx')],
  [OutputReport.CSV, marker('export.type.csv')],
]);

@Component({
  templateUrl: './invencion-listado-export-modal.component.html',
  styleUrls: ['./invencion-listado-export-modal.component.scss']
})
export class InvencionListadoExportModalComponent extends BaseExportModalComponent<IInvencionReportOptions> implements OnInit {

  readonly OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP = OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP;
  private reportTitle: string;

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
    matDialogRef: MatDialogRef<InvencionListadoExportModalComponent>,
    translate: TranslateService,
    invencionListadoExportService: InvencionListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IBaseExportModalData
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

      showTodos: new FormControl(true),
      showSolicitudesDeProteccion: new FormControl(true),
      showEquipoInventor: new FormControl(true),
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

  protected getReportOptions(): IReportConfig<IInvencionReportOptions> {
    const reportModalData: IReportConfig<IInvencionReportOptions> = {
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showSolicitudesDeProteccion: this.formGroup.controls.showSolicitudesDeProteccion.value,
        showEquipoInventor: this.formGroup.controls.showEquipoInventor.value,
        columnMinWidth: 180
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
