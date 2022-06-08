import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';

@Component({
  templateUrl: './personal-contratado-modal.component.html',
  styleUrls: ['./personal-contratado-modal.component.scss']
})
export class PersonalContratadoModalComponent extends DialogCommonComponent {

  constructor(
    matDialogRef: MatDialogRef<PersonalContratadoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IDatoEconomicoDetalle
  ) {
    super(matDialogRef);
  }

}
