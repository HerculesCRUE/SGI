import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFindOptions } from '@sgi/framework/http';
import { IInvencionReportOptions } from '../../invencion-listado-export.service';
import { ISolicitudProteccionReportOptions, SolicitudProteccionListadoExportService } from '../../solicitud-proteccion-listado-export.service';

const SOLICITUD_PROTECCION_KEY = marker('pii.solicitud-proteccion');
const REPORT_TITLE_KEY = marker('pii.solicitud-proteccion.export-report.title');

export interface ISolicitudProteccionListadoModalData {
  findOptions: SgiRestFindOptions;
  invencionId: number;
}

export const OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP: Map<OutputReport, string> = new Map([
  [OutputReport.XLSX, marker('export.type.xlsx')],
  [OutputReport.CSV, marker('export.type.csv')],
]);

@Component({
  templateUrl: './solicitud-proteccion-listado-export-modal.component.html',
  styleUrls: ['./solicitud-proteccion-listado-export-modal.component.scss']
})
export class SolicitudProteccionListadoExportModalComponent extends BaseExportModalComponent<ISolicitudProteccionReportOptions>
  implements OnInit {

  readonly OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP = OUTPUT_REPORT_TYPE_EXCEL_CSV_MAP;
  public reportTitle: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<SolicitudProteccionListadoExportModalComponent>,
    translate: TranslateService,
    solicitudProteccionListadoExportService: SolicitudProteccionListadoExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: ISolicitudProteccionListadoModalData
  ) {
    super(solicitudProteccionListadoExportService, translate, matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.reportTitle = this.translate.instant(REPORT_TITLE_KEY);
    this.formGroup = this.buildFormGroup();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      outputType: new FormControl(OutputReport.XLSX, Validators.required),
      showSolicitudesDeProteccion: new FormControl(true),
      showEquipoInventor: new FormControl(true),
    });
  }

  protected getReportOptions(): IReportConfig<ISolicitudProteccionReportOptions> {
    const reportModalData: IReportConfig<ISolicitudProteccionReportOptions> = {
      title: this.translate.instant(REPORT_TITLE_KEY),
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        findOptions: this.modalData.findOptions,
        invencionId: this.modalData.invencionId,
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
