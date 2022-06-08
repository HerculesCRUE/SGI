import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP, IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { IDocumento } from '@core/models/sge/documento';
import { IDatoEconomicoDetalle } from '@core/models/sgepii/dato-economico-detalle';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { DocumentoService } from '@core/services/sge/documento.service';
import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { IColumnDefinition } from 'src/app/module/csp/ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';

export interface InvencionGastoModalData {
  selectedInvencionId: number;
  selectedInvencionGasto: IInvencionGasto;
  columns: IColumnDefinition[];
  gastoDetalle: IDatoEconomicoDetalle;
}

@Component({
  selector: 'sgi-invencion-gasto-modal',
  templateUrl: './invencion-gasto-modal.component.html',
  styleUrls: ['./invencion-gasto-modal.component.scss']
})
export class InvencionGastoModalComponent extends DialogFormComponent<IInvencionGasto> {

  readonly solicitudesProteccion$: Observable<ISolicitudProteccion[]>;
  readonly displayWith = (option: ISolicitudProteccion) => option?.titulo ?? '';
  readonly displayColumns = ['nombre', 'nombreFichero', 'acciones'];

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    matDialogRef: MatDialogRef<InvencionGastoModalComponent>,
    private readonly invencionService: InvencionService,
    @Inject(MAT_DIALOG_DATA) public data: InvencionGastoModalData,
    private documentoService: DocumentoService
  ) {
    super(matDialogRef, true);
    this.solicitudesProteccion$ = this.invencionService.findAllSolicitudesProteccion(data.selectedInvencionId).pipe(
      map(response => response.items),
      catchError((error) => {
        this.processError(error);
        throw error;
      })
    );
  }

  protected getValue(): IInvencionGasto {
    this.data.selectedInvencionGasto.solicitudProteccion = this.formGroup.controls.solicitudProteccion.value as ISolicitudProteccion;

    return this.data.selectedInvencionGasto;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      solicitudProteccion: new FormControl(this.data.selectedInvencionGasto?.solicitudProteccion)
    });

    return formGroup;
  }

  download(documento: IDocumento): void {
    this.subscriptions.push(this.documentoService.downloadFichero(documento.id).subscribe(
      (data) => {
        triggerDownloadToUser(data, documento.nombreFichero);
      },
      (error) => {
        this.processError(error);
      }
    ));
  }
}
