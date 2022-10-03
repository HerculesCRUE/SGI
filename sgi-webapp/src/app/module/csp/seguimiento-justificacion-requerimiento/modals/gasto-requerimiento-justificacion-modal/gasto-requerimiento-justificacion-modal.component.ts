import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { SeguimientoJustificacionService } from '@core/services/sge/seguimiento-justificacion/seguimiento-justificacion.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IGastoJustificadoDetalleWithProyectoSgiId } from '../../common/detalle-gasto-justificado/detalle-gasto-justificado.component';
import { IGastoRequerimientoJustificacionTableData } from '../../seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-gastos/seguimiento-justificacion-requerimiento-gastos.fragment';

export interface GastoRequerimientoJustificacionModalData {
  gastoRequerimiento: IGastoRequerimientoJustificacionTableData;
  readonly: boolean;
}

const ALEGACION_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.gastos.gasto-requerimiento.datos-sgi.datos-requerimiento.alegacion');
const INCIDENCIA_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.gastos.gasto-requerimiento.datos-sgi.datos-requerimiento.incidencia');

@Component({
  selector: 'sgi-gasto-requerimiento-justificacion-modal',
  templateUrl: './gasto-requerimiento-justificacion-modal.component.html',
  styleUrls: ['./gasto-requerimiento-justificacion-modal.component.scss']
})
export class GastoRequerimientoJustificacionModalComponent
  extends DialogFormComponent<IGastoRequerimientoJustificacionTableData> implements OnInit {
  gastoJustificadoDetalleWithProyectoPeriodoJusificacion$: Observable<IGastoJustificadoDetalleWithProyectoSgiId>;

  msgParamAlegacionEntity = {};
  msgParamIncidenciaEntity = {};

  get gastoRequerimiento(): IGastoRequerimientoJustificacionTableData {
    return this.data.gastoRequerimiento;
  }

  constructor(
    matDialogRef: MatDialogRef<GastoRequerimientoJustificacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) private data: GastoRequerimientoJustificacionModalData,
    private readonly seguimientoJustificacionService: SeguimientoJustificacionService,
    private readonly translate: TranslateService) {
    super(matDialogRef, !data.readonly);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.gastoJustificadoDetalleWithProyectoPeriodoJusificacion$ =
      this.seguimientoJustificacionService.findById(
        this.data.gastoRequerimiento.gasto.id,
        { justificacionId: this.data.gastoRequerimiento.gasto.justificacionId, proyectoId: this.data.gastoRequerimiento.gasto.proyectoId }
      )
        .pipe(
          map(gastoJustificadoDetalle =>
          ({
            ...gastoJustificadoDetalle,
            proyectoSgiId: this.data.gastoRequerimiento.proyectoSgiId
          } as IGastoJustificadoDetalleWithProyectoSgiId)
          )
        );
    // Setting aceptado to true enables ok button
    if (typeof this.formGroup.controls.aceptado !== 'boolean') {
      this.formGroup.controls.aceptado.setValue(true);
    }
  }

  protected getValue(): IGastoRequerimientoJustificacionTableData {
    this.gastoRequerimiento.aceptado = this.formGroup.controls.aceptado.value;
    this.gastoRequerimiento.importeAceptado = this.formGroup.controls.importeAceptado.value;
    this.gastoRequerimiento.importeRechazado = this.formGroup.controls.importeRechazado.value;
    this.gastoRequerimiento.incidencia = this.formGroup.controls.incidencia.value;
    this.gastoRequerimiento.importeAlegado = this.formGroup.controls.importeAlegado.value;
    this.gastoRequerimiento.alegacion = this.formGroup.controls.alegacion.value;

    return this.gastoRequerimiento;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      aceptado: new FormControl(this.gastoRequerimiento.aceptado, Validators.required),
      importeAceptado: new FormControl(this.gastoRequerimiento.importeAceptado),
      importeRechazado: new FormControl(this.gastoRequerimiento.importeRechazado),
      incidencia: new FormControl(this.gastoRequerimiento.incidencia, Validators.maxLength(2000)),
      importeAlegado: new FormControl(this.gastoRequerimiento.importeAlegado),
      alegacion: new FormControl(this.gastoRequerimiento.alegacion, Validators.maxLength(2000)),
    });

    if (!this.isEdit()) {
      formGroup.disable();
    }

    return formGroup;
  }

  onHandleError(error: Error): void {
    this.processError(error);
  }

  private setupI18N(): void {
    this.translate.get(
      ALEGACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamAlegacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
    this.translate.get(
      INCIDENCIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamIncidenciaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
  }
}
