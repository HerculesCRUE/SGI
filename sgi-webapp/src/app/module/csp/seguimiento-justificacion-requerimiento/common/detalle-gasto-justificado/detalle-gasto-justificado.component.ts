import { Component, EventEmitter, Input, OnDestroy, Output } from '@angular/core';
import { IDocumento } from '@core/models/sge/documento';
import { IGastoJustificadoDetalle } from '@core/models/sge/gasto-justificado-detalle';
import { triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { DocumentoService } from '@core/services/sge/documento.service';
import { Subscription } from 'rxjs';

export interface IGastoJustificadoDetalleWithProyectoSgiId extends IGastoJustificadoDetalle {
  proyectoSgiId: number;
}

@Component({
  selector: 'sgi-detalle-gasto-justificado',
  templateUrl: './detalle-gasto-justificado.component.html',
  styleUrls: ['./detalle-gasto-justificado.component.scss']
})
export class DetalleGastoJustificadoComponent implements OnDestroy {
  readonly displayColumns = ['nombre', 'acciones'];
  private subscriptions: Subscription[] = [];

  @Input() gasto: IGastoJustificadoDetalleWithProyectoSgiId;
  @Output() handleError: EventEmitter<Error> = new EventEmitter();

  constructor(private documentoService: DocumentoService) { }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
  }

  download(documento: IDocumento): void {
    this.subscriptions.push(this.documentoService.downloadFichero(documento.id).subscribe(
      (data) => {
        triggerDownloadToUser(data, documento.nombreFichero);
      },
      (error) => {
        this.handleError.next(error);
      }
    ));
  }
}
