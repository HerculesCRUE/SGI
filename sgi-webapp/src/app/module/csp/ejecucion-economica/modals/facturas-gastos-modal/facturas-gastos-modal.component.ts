import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { IRowConfig } from '../../ejecucion-economica-formulario/desglose-economico.fragment';

export interface FacturasGastosDetalleModalData extends IDatoEconomicoDetalle {
  rowConfig: IRowConfig;
}

@Component({
  templateUrl: './facturas-gastos-modal.component.html',
  styleUrls: ['./facturas-gastos-modal.component.scss']
})
export class FacturasGastosModalComponent extends DialogCommonComponent {

  constructor(
    matDialogRef: MatDialogRef<FacturasGastosModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: FacturasGastosDetalleModalData
  ) {
    super(matDialogRef);
  }

}
