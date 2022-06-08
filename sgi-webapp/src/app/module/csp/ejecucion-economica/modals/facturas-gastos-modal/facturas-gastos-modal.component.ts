import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';

@Component({
  templateUrl: './facturas-gastos-modal.component.html',
  styleUrls: ['./facturas-gastos-modal.component.scss']
})
export class FacturasGastosModalComponent extends DialogCommonComponent {

  constructor(
    matDialogRef: MatDialogRef<FacturasGastosModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IDatoEconomicoDetalle
  ) {
    super(matDialogRef);
  }

}
