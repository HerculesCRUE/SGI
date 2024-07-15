import { Component, Input } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { IDocumento } from '@core/models/sge/documento';
import { triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { DocumentoService } from '@core/services/sge/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Subscription } from 'rxjs';
import { IRowConfig } from '../../ejecucion-economica-formulario/desglose-economico.fragment';

const MSG_DOWNLOAD_ERROR = marker('error.file.download');

@Component({
  selector: 'sgi-ejecucion-economica-detalle-economico',
  templateUrl: './detalle-economico.component.html',
  styleUrls: ['./detalle-economico.component.scss']
})
export class DetalleEconomicoComponent {

  @Input()
  detalle: IDatoEconomicoDetalle;

  @Input()
  rowConfig: IRowConfig;

  private subscriptions: Subscription[] = [];

  constructor(private snackBarService: SnackBarService, private documentoService: DocumentoService) { }

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
