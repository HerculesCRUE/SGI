import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { DialogCommonComponent } from '@core/component/dialog-common.component';

@Component({
  templateUrl: './proyecto-info-modificar-fechas-modal.component.html',
  styleUrls: ['./proyecto-info-modificar-fechas-modal.component.scss']
})
export class ProyectoInfoModificarFechasModalComponent extends DialogCommonComponent implements OnInit {

  constructor(
    matDialogRef: MatDialogRef<ProyectoInfoModificarFechasModalComponent>
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

