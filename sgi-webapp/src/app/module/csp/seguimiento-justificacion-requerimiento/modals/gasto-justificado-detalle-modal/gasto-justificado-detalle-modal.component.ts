import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { IGastoJustificado } from '@core/models/sge/gasto-justificado';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { SeguimientoJustificacionService } from '@core/services/sge/seguimiento-justificacion/seguimiento-justificacion.service';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IGastoJustificadoDetalleWithProyectoSgiId } from '../../common/detalle-gasto-justificado/detalle-gasto-justificado.component';

@Component({
  selector: 'sgi-gasto-justificado-detalle-modal',
  templateUrl: './gasto-justificado-detalle-modal.component.html',
  styleUrls: ['./gasto-justificado-detalle-modal.component.scss']
})
export class GastoJustificadoDetalleModalComponent extends DialogCommonComponent implements OnInit {
  gastoJustificadoDetalleWithProyectoPeriodoJusificacion$: Observable<IGastoJustificadoDetalleWithProyectoSgiId>;

  constructor(
    matDialogRef: MatDialogRef<GastoJustificadoDetalleModalComponent>,
    @Inject(MAT_DIALOG_DATA) private gastoJustificado: IGastoJustificado,
    private readonly seguimientoJustificacionService: SeguimientoJustificacionService,
    private readonly proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService) {
    super(matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.gastoJustificadoDetalleWithProyectoPeriodoJusificacion$ =
      this.seguimientoJustificacionService.findById(
        this.gastoJustificado.id,
        { justificacionId: this.gastoJustificado.justificacionId, proyectoId: this.gastoJustificado.proyectoId }
      )
        .pipe(
          switchMap(gastoJustificadoDetalle =>
            this.proyectoPeriodoJustificacionService.findByIdentificadorJustificacion(gastoJustificadoDetalle.justificacionId)
              .pipe(
                map(proyectoPeriodoJustificacion =>
                ({
                  ...gastoJustificadoDetalle,
                  proyectoSgiId: proyectoPeriodoJustificacion?.proyecto?.id
                } as IGastoJustificadoDetalleWithProyectoSgiId)
                )
              )
          )
        );
  }

  onHandleError(error: Error): void {
    this.processError(error);
  }
}
