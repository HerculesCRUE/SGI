import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { SgiError } from '@core/errors/sgi-error';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { IDocumento } from '@core/models/sge/documento';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';

const MSG_DOWNLOAD_ERROR = marker('error.file.download');

export interface GastoDetalleModalData extends IDatoEconomicoDetalle {
  estado: string;
  gastoProyecto: IGastoProyecto;
}

@Component({
  templateUrl: './validacion-gastos-modal.component.html',
  styleUrls: ['./validacion-gastos-modal.component.scss']
})
export class ValidacionGastosModalComponent extends DialogCommonComponent {

  constructor(
    matDialogRef: MatDialogRef<ValidacionGastosModalComponent>,
    private documentoService: DocumentoService,
    @Inject(MAT_DIALOG_DATA) public data: GastoDetalleModalData
  ) {
    super(matDialogRef);
  }

  download(documento: IDocumento): void {
    this.subscriptions.push(this.documentoService.downloadFichero(documento.id).subscribe(
      (data) => {
        triggerDownloadToUser(data, documento.nombreFichero);
      },
      (error) => {
        if (error instanceof SgiError) {
          this.problems$.next([error]);
        } else {
          this.problems$.next([new SgiError(MSG_DOWNLOAD_ERROR)]);
        }
      }
    ));
  }

}
