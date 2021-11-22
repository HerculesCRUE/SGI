import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { IDocumento } from '@core/models/sge/documento';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';

const MSG_DOWNLOAD_ERROR = marker('error.file.download');

@Component({
  templateUrl: './facturas-emitidas-modal.component.html',
  styleUrls: ['./facturas-emitidas-modal.component.scss']
})
export class FacturasEmitidasModalComponent
  extends BaseModalComponent<IDatoEconomicoDetalle, FacturasEmitidasModalComponent>
  implements OnInit {

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<FacturasEmitidasModalComponent>,
    private documentoService: DocumentoService,
    @Inject(MAT_DIALOG_DATA) public data: IDatoEconomicoDetalle
  ) {
    super(snackBarService, matDialogRef, data);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected getFormGroup(): FormGroup {
    return new FormGroup({});
  }

  protected getDatosForm(): IDatoEconomicoDetalle {
    return this.data;
  }

  download(documento: IDocumento): void {
    this.subscriptions.push(this.documentoService.downloadFichero(documento.id).subscribe(
      (data) => {
        triggerDownloadToUser(data, documento.nombreFichero);
      },
      () => {
        this.snackBarService.showError(MSG_DOWNLOAD_ERROR);
      }
    ));
  }

}


