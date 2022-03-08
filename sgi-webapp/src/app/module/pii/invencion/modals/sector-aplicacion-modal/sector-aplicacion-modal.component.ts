import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';

const MSG_ERROR_LOAD = marker('error.load');

export interface SectorAplicacionModalData {
  selectedEntidades: ISectorAplicacion[];
}

@Component({
  selector: 'sgi-sector-aplicacion-modal',
  templateUrl: './sector-aplicacion-modal.component.html',
  styleUrls: ['./sector-aplicacion-modal.component.scss']
})
export class SectorAplicacionModalComponent
  extends BaseModalComponent<ISectorAplicacion, SectorAplicacionModalComponent> implements OnInit {

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    public matDialogRef: MatDialogRef<SectorAplicacionModalComponent>,
    protected readonly snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: SectorAplicacionModalData,
  ) {
    super(snackBarService, matDialogRef, null);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      sectorAplicacion: new FormControl(null, Validators.required)
    });
    return formGroup;
  }

  protected getDatosForm(): ISectorAplicacion {
    return this.formGroup.controls.sectorAplicacion.value as ISectorAplicacion;
  }
}
