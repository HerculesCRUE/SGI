import { Component, Input } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseExportModalComponent } from '@core/component/base-export/base-export-modal.component';
import { TranslateService } from '@ngx-translate/core';

const WARN_EXPORT_EXCEL_KEY = marker('msg.export.max-registros-warning');
@Component({
  selector: 'sgi-export-dialog',
  templateUrl: './export-dialog.component.html',
  styleUrls: ['./export-dialog.component.scss']
})
export class ExportDialogComponent<T> {

  @Input()
  title = '';
  @Input()
  totalRegistrosExportacionExcel: number;
  @Input()
  limiteRegistrosExportacionExcel: number;

  msgExportWarning: string;

  get modal(): BaseExportModalComponent<T> {
    return this.dialogRef.componentInstance;
  }

  get showExportWarning(): boolean {
    if (this.totalRegistrosExportacionExcel && this.limiteRegistrosExportacionExcel) {
      this.msgExportWarning = this.translate.instant(
        WARN_EXPORT_EXCEL_KEY,
        { max: this.limiteRegistrosExportacionExcel }
      );
      return this.totalRegistrosExportacionExcel > this.limiteRegistrosExportacionExcel;
    }
  }

  constructor(private dialogRef: MatDialogRef<any, any>, private readonly translate: TranslateService) {
    dialogRef.addPanelClass('sgi-dialog-container');
  }

}
