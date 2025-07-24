import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { IRowConfig } from '../../ejecucion-economica-formulario/desglose-economico.fragment';

export interface EjecucionPresupuestariaGastosModalData extends IDatoEconomicoDetalle {
  rowConfig: IRowConfig;
}

@Component({
  templateUrl: './ejecucion-presupuestaria-gastos-modal.component.html',
  styleUrls: ['./ejecucion-presupuestaria-gastos-modal.component.scss']
})
export class EjecucionPresupuestariaGastosModalComponent extends DialogCommonComponent {

  constructor(
    matDialogRef: MatDialogRef<EjecucionPresupuestariaGastosModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EjecucionPresupuestariaGastosModalData
  ) {
    super(matDialogRef);
  }

}
