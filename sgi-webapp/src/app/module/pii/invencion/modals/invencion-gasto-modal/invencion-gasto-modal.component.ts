import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP, IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { IDocumento } from '@core/models/sge/documento';
import { IDatoEconomicoDetalle } from '@core/models/sgepii/dato-economico-detalle';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { DocumentoService } from '@core/services/sge/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { IColumnDefinition } from 'src/app/module/csp/ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';

const MSG_ERROR_LOAD = marker('error.load');
const MSG_DOWNLOAD_ERROR = marker('error.file.download');

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
export class InvencionGastoModalComponent extends BaseModalComponent<IInvencionGasto, InvencionGastoModalComponent> implements OnInit {

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
    private readonly logger: NGXLogger,
    public matDialogRef: MatDialogRef<InvencionGastoModalComponent>,
    protected readonly snackBarService: SnackBarService,
    private readonly invencionService: InvencionService,
    @Inject(MAT_DIALOG_DATA) public data: InvencionGastoModalData,
    private documentoService: DocumentoService
  ) {
    super(snackBarService, matDialogRef, null);
    this.solicitudesProteccion$ = this.invencionService.findAllSolicitudesProteccion(data.selectedInvencionId).pipe(
      map(response => response.items),
      catchError((error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_LOAD);
        throw error;
      })
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected getDatosForm(): IInvencionGasto {
    this.data.selectedInvencionGasto.solicitudProteccion = this.formGroup.controls.solicitudProteccion.value as ISolicitudProteccion;

    return this.data.selectedInvencionGasto;
  }

  protected getFormGroup(): FormGroup {
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
      () => {
        this.snackBarService.showError(MSG_DOWNLOAD_ERROR);
      }
    ));
  }
}
