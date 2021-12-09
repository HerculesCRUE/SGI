import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { IEjecucionPresupuestariaReportOptions } from '../../../common/ejecucion-presupuestaria-report-options';
import { IDesgloseEconomicoExportData } from '../../desglose-economico.fragment';
import { FacturasGastosExportService } from './facturas-gastos-export.service';

const FACTURAS_JUSTIFICANTES = marker('menu.csp.ejecucion-economica.facturas-justificantes');
const FACTURAS_JUSTIFICANTES_GASTOS = marker('menu.csp.ejecucion-economica.facturas-justificantes.facturas-gastos');

@Component({
  templateUrl: './facturas-gastos-export-modal.component.html',
  styleUrls: ['./facturas-gastos-export-modal.component.scss']
})
export class FacturasGastosExportModalComponent
  extends BaseExportModalComponent<IEjecucionPresupuestariaReportOptions> implements OnInit {

  constructor(
    matDialogRef: MatDialogRef<FacturasGastosExportModalComponent>,
    snackBarService: SnackBarService,
    translate: TranslateService,
    facturasGastosExportService: FacturasGastosExportService,
    @Inject(MAT_DIALOG_DATA) private modalData: IDesgloseEconomicoExportData
  ) {
    super(facturasGastosExportService, snackBarService, translate, matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formGroup = this.buildFormGroup();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      outputType: new FormControl(this.outputType, Validators.required),
      reportTitle: new FormControl(this.title, Validators.required),
    });
  }

  protected getReportOptions(): IReportConfig<IEjecucionPresupuestariaReportOptions> {
    const reportModalData: IReportConfig<IEjecucionPresupuestariaReportOptions> = {
      title: this.formGroup.controls.reportTitle.value,
      outputType: this.formGroup.controls.outputType.value,
      reportOptions: {
        data: this.modalData.data,
        columns: this.modalData.columns,
        relationsTypeView: this.getRelationsTypeView(this.formGroup.controls.outputType.value),
        columnMinWidth: 120
      }
    };
    return reportModalData;
  }


  protected setupI18N(): void {
    this.title = this.translate.instant(FACTURAS_JUSTIFICANTES) + ' - ' + this.translate.instant(FACTURAS_JUSTIFICANTES_GASTOS);
  }

  protected getKey(): string {
    return null;
  }

  protected getGender() {
    return null;
  }


}
