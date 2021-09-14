import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { SnackBarService } from '@core/services/snack-bar.service';

@Component({
  templateUrl: './personal-contratado-modal.component.html',
  styleUrls: ['./personal-contratado-modal.component.scss']
})
export class PersonalContratadoModalComponent
  extends BaseModalComponent<IDatoEconomicoDetalle, PersonalContratadoModalComponent>
  implements OnInit {

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<PersonalContratadoModalComponent>,
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

}
