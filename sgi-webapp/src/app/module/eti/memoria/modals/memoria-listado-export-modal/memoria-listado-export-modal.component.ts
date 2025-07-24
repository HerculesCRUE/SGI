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
import { SgiRestFindOptions } from '@sgi/framework/http';
import { IMemoriaReportOptions, MemoriaListadoExportService } from '../../memoria-listado-export.service';
import { switchMap } from 'rxjs/operators';

const REPORT_TITLE_KEY = marker('eti.memoria.report.title');
const ENTITY_KEY = marker('list.entity');
const MEMORIA_KEY = marker('eti.memoria');

export interface IMemoriaListadoModalData extends IBaseExportModalData {
  isInvestigador: boolean;
}

export const OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP: Map<OutputReport, string> = new Map([
  [OutputReport.XLSX, marker('export.type.xlsx')],
  [OutputReport.CSV, marker('export.type.csv')],
]);

@Component({
  templateUrl: './memoria-listado-export-modal.component.html',
  styleUrls: ['./memoria-listado-export-modal.component.scss']
})
export class MemoriaListadoExportModalComponent extends
  BaseExportModalComponent<IMemoriaReportOptions> implements OnInit {
  sheetTitleMemorias: string;

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
    matDialogRef: MatDialogRef<MemoriaListadoExportModalComponent>,
    translate: TranslateService,
    memoriaListadoExportService: MemoriaListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IMemoriaListadoModalData
  ) {

    super(memoriaListadoExportService, translate, matDialogRef);

    memoriaListadoExportService.isInvestigador = modalData.isInvestigador;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.reportTitle = this.translate.instant(REPORT_TITLE_KEY);
    this.formGroup = this.buildFormGroup();

    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          ENTITY_KEY,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.sheetTitleMemorias = value);
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      outputType: new FormControl(OutputReport.XLSX, Validators.required),
      hideBlocksIfNoData: new FormControl(true),

      showEvaluaciones: new FormControl(true),
    });
  }

  protected getReportOptions(): IReportConfig<IMemoriaReportOptions> {
    const reportModalData: IReportConfig<IMemoriaReportOptions> = {
      title: this.sheetTitleMemorias,
      outputType: this.formGroup.controls.outputType.value,
      hideBlocksIfNoData: this.formGroup.controls.hideBlocksIfNoData.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        showEvaluaciones: this.formGroup.controls.showEvaluaciones.value,
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
