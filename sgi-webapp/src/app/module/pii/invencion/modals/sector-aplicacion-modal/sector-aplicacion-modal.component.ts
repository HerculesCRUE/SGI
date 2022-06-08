import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';

export interface SectorAplicacionModalData {
  selectedEntidades: ISectorAplicacion[];
}

@Component({
  selector: 'sgi-sector-aplicacion-modal',
  templateUrl: './sector-aplicacion-modal.component.html',
  styleUrls: ['./sector-aplicacion-modal.component.scss']
})
export class SectorAplicacionModalComponent extends DialogFormComponent<ISectorAplicacion> {

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<SectorAplicacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SectorAplicacionModalData,
  ) {
    super(matDialogRef, false);
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      sectorAplicacion: new FormControl(null, Validators.required)
    });
    return formGroup;
  }

  protected getValue(): ISectorAplicacion {
    return this.formGroup.controls.sectorAplicacion.value as ISectorAplicacion;
  }
}
