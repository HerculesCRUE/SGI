import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { IRowConfig } from '../../ejecucion-economica-formulario/desglose-economico.fragment';

export interface DetalleOperacionesGastosModalData extends IDatoEconomicoDetalle {
  rowConfig: IRowConfig;
}

@Component({
  templateUrl: './detalle-operaciones-gastos-modal.component.html',
  styleUrls: ['./detalle-operaciones-gastos-modal.component.scss']
})
export class DetalleOperacionesGastosModalComponent extends DialogCommonComponent {

  constructor(
    matDialogRef: MatDialogRef<DetalleOperacionesGastosModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DetalleOperacionesGastosModalData
  ) {
    super(matDialogRef);
  }

}
