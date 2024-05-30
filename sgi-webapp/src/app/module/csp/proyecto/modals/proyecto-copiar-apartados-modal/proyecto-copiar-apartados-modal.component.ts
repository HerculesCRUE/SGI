import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { IProyectoApartadosToBeCopied } from '@core/models/csp/proyecto-aparatados-to-be-copied';
import { IProyectoApartadosWithDates } from '@core/models/csp/proyecto-aparatados-with-dates';

export interface ProyectoCopiarApartadosModalData {
  apartadosToBeCopied: IProyectoApartadosToBeCopied;
  apartadosWithDates: IProyectoApartadosWithDates;
}

@Component({
  templateUrl: './proyecto-copiar-apartados-modal.component.html',
  styleUrls: ['./proyecto-copiar-apartados-modal.component.scss']
})
export class ProyectoCopiarAparatadosModalComponent extends DialogCommonComponent implements OnInit {

  constructor(
    matDialogRef: MatDialogRef<ProyectoCopiarAparatadosModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoCopiarApartadosModalData
  ) {
    super(matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  aceptar(): void {
    this.close(true);
  }

  hasApartadosWithDates(): boolean {
    return this.data.apartadosWithDates?.elegibilidad
      || this.data.apartadosWithDates?.equiposSocios
      || this.data.apartadosWithDates?.equipo
      || this.data.apartadosWithDates?.responsableEconomico
      || this.data.apartadosWithDates?.socios;
  }

  hasApartadosToBeCopied(): boolean {
    return this.data.apartadosToBeCopied?.elegibilidad
      || this.data.apartadosToBeCopied?.equiposSocios
      || this.data.apartadosToBeCopied?.equipo
      || this.data.apartadosToBeCopied?.responsableEconomico
      || this.data.apartadosToBeCopied?.socios;
  }

}

