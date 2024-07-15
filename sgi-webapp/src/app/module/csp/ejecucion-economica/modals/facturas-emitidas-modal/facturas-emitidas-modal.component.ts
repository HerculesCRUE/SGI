import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { SgiError } from '@core/errors/sgi-error';
import { IDocumento } from '@core/models/sge/documento';
import { IFacturaEmitidaDetalle } from '@core/models/sge/factura-emitida-detalle';
import { triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { DocumentoService } from '@core/services/sge/documento.service';

const MSG_DOWNLOAD_ERROR = marker('error.file.download');

@Component({
  templateUrl: './facturas-emitidas-modal.component.html',
  styleUrls: ['./facturas-emitidas-modal.component.scss']
})
export class FacturasEmitidasModalComponent extends DialogCommonComponent {

  constructor(
    matDialogRef: MatDialogRef<FacturasEmitidasModalComponent>,
    private documentoService: DocumentoService,
    @Inject(MAT_DIALOG_DATA) public data: IFacturaEmitidaDetalle
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


