import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { DateTime } from 'luxon';

export interface ProyectoInfoModificarFechasModalData {
  fechaInicio: DateTime;
  fechaFinDefinitiva: DateTime;
}

@Component({
  templateUrl: './proyecto-info-modificar-fechas-modal.component.html',
  styleUrls: ['./proyecto-info-modificar-fechas-modal.component.scss']
})
export class ProyectoInfoModificarFechasModalComponent extends DialogCommonComponent implements OnInit {

  get isFechaFinDefinivaLowerThanFechaInicio(): boolean {
    return this.data?.fechaFinDefinitiva && this.data?.fechaInicio && this.data.fechaFinDefinitiva < this.data.fechaInicio;
  }

  constructor(
    matDialogRef: MatDialogRef<ProyectoInfoModificarFechasModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoInfoModificarFechasModalData
  ) {
    super(matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  aceptar(): void {
    this.close(true);
  }

}

