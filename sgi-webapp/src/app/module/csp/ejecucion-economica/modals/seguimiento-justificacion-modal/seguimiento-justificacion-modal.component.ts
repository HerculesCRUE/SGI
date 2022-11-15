import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoSeguimientoEjecucionEconomica } from '@core/models/csp/proyecto-seguimiento-ejecucion-economica';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { startWith } from 'rxjs/operators';
import { IProyectoSeguimientoJustificacionWithFechaJustificacion } from '../../ejecucion-economica-formulario/seguimiento-justificacion-resumen/seguimiento-justificacion-resumen.fragment';

const JUSTIFICANTE_REINTEGRO_KEY = marker('csp.seguimiento-justificacion.num-justificante-ultimo-reintegro');

export interface ISeguimientoJustificacionModalData {
  proyecto: IProyectoSeguimientoEjecucionEconomica;
  seguimientoJustificacion: StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>;
}

@Component({
  selector: 'sgi-seguimiento-justificacion-modal',
  templateUrl: './seguimiento-justificacion-modal.component.html',
  styleUrls: ['./seguimiento-justificacion-modal.component.scss']
})
export class SeguimientoJustificacionModalComponent extends
  DialogFormComponent<StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>> implements OnInit {

  msgParamJustificanteReintegroEntity = {};

  constructor(
    matDialogRef: MatDialogRef<SeguimientoJustificacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: ISeguimientoJustificacionModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, true);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      JUSTIFICANTE_REINTEGRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamJustificanteReintegroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      proyectoId: new FormControl({ value: this.data.proyecto.proyectoId, disabled: true }),
      tituloProyecto: new FormControl({ value: this.data.proyecto.nombre, disabled: true }),
      fechaUltimaJustificacion: new FormControl(
        { value: this.data.seguimientoJustificacion.value.fechaUltimaJustificacion, disabled: true }
      ),
      importeConcedido: new FormControl({ value: this.data.proyecto.importeConcedido, disabled: true }),
      importeConcedidoCostesIndirectos: new FormControl({ value: this.data.proyecto.importeConcedidoCostesIndirectos, disabled: true }),
      importeJustificado: new FormControl(this.data.seguimientoJustificacion.value.importeJustificado),
      importeJustificadoCD: new FormControl(this.data.seguimientoJustificacion.value.importeJustificadoCD),
      importeJustificadoCI: new FormControl(this.data.seguimientoJustificacion.value.importeJustificadoCI),
      importeAceptado: new FormControl(this.data.seguimientoJustificacion.value.importeAceptado),
      importeAceptadoCD: new FormControl(this.data.seguimientoJustificacion.value.importeAceptadoCD),
      importeAceptadoCI: new FormControl(this.data.seguimientoJustificacion.value.importeAceptadoCI),
      importeRechazado: new FormControl(this.data.seguimientoJustificacion.value.importeRechazado),
      importeRechazadoCD: new FormControl(this.data.seguimientoJustificacion.value.importeRechazadoCD),
      importeRechazadoCI: new FormControl(this.data.seguimientoJustificacion.value.importeRechazadoCI),
      importeAlegado: new FormControl(this.data.seguimientoJustificacion.value.importeAlegado),
      importeAlegadoCD: new FormControl(this.data.seguimientoJustificacion.value.importeAlegadoCD),
      importeAlegadoCI: new FormControl(this.data.seguimientoJustificacion.value.importeAlegadoCI),
      importeNoEjecutado: new FormControl(this.data.seguimientoJustificacion.value.importeNoEjecutado),
      importeNoEjecutadoCD: new FormControl(this.data.seguimientoJustificacion.value.importeNoEjecutadoCD),
      importeNoEjecutadoCI: new FormControl(this.data.seguimientoJustificacion.value.importeNoEjecutadoCI),
      importeReintegrar: new FormControl(this.data.seguimientoJustificacion.value.importeReintegrar),
      importeReintegrarCD: new FormControl(this.data.seguimientoJustificacion.value.importeReintegrarCD),
      importeReintegrarCI: new FormControl(this.data.seguimientoJustificacion.value.importeReintegrarCI),
      importeReintegrado: new FormControl(this.data.seguimientoJustificacion.value.importeReintegrado),
      importeReintegradoCD: new FormControl(this.data.seguimientoJustificacion.value.importeReintegradoCD),
      importeReintegradoCI: new FormControl(this.data.seguimientoJustificacion.value.importeReintegradoCI),
      interesesReintegrar: new FormControl(this.data.seguimientoJustificacion.value.interesesReintegrar),
      interesesReintegrados: new FormControl(this.data.seguimientoJustificacion.value.interesesReintegrados),
      fechaReintegro: new FormControl(this.data.seguimientoJustificacion.value.fechaReintegro),
      justificanteReintegro: new FormControl(this.data.seguimientoJustificacion.value.justificanteReintegro, Validators.maxLength(250)),
      reintegroPendiente: new FormControl({ value: null, disabled: true }),
      interesesPendientes: new FormControl({ value: null, disabled: true }),
    });

    this.subscriptions.push(
      combineLatest([
        formGroup.controls.importeReintegrar.valueChanges.pipe(
          startWith(this.data.seguimientoJustificacion.value.importeReintegrar)
        ),
        formGroup.controls.importeReintegrado.valueChanges.pipe(
          startWith(this.data.seguimientoJustificacion.value.importeReintegrado)
        )
      ]).subscribe(([importeReintegrar, importeReintegrado]) =>
        formGroup.controls.reintegroPendiente.setValue(this.calculateSubstraction(importeReintegrar, importeReintegrado))
      )
    );

    this.subscriptions.push(
      combineLatest([
        formGroup.controls.interesesReintegrar.valueChanges.pipe(
          startWith(this.data.seguimientoJustificacion.value.interesesReintegrar)
        ),
        formGroup.controls.interesesReintegrados.valueChanges.pipe(
          startWith(this.data.seguimientoJustificacion.value.interesesReintegrados)
        )
      ]).subscribe(([interesesReintegrar, interesesReintegrados]) =>
        formGroup.controls.interesesPendientes.setValue(this.calculateSubstraction(interesesReintegrar, interesesReintegrados))
      )
    );

    return formGroup;
  }

  private calculateSubstraction(a: number, b: number): number | null {
    if (typeof a !== 'number' && typeof b !== 'number') {
      return null;
    }

    return (a ?? 0) - (b ?? 0);
  }

  protected getValue(): StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion> {
    this.data.seguimientoJustificacion.value.fechaReintegro = this.formGroup.controls.fechaReintegro.value;
    this.data.seguimientoJustificacion.value.importeAceptado = this.formGroup.controls.importeAceptado.value;
    this.data.seguimientoJustificacion.value.importeAceptadoCD = this.formGroup.controls.importeAceptadoCD.value;
    this.data.seguimientoJustificacion.value.importeAceptadoCI = this.formGroup.controls.importeAceptadoCI.value;
    this.data.seguimientoJustificacion.value.importeAlegado = this.formGroup.controls.importeAlegado.value;
    this.data.seguimientoJustificacion.value.importeAlegadoCD = this.formGroup.controls.importeAlegadoCD.value;
    this.data.seguimientoJustificacion.value.importeAlegadoCI = this.formGroup.controls.importeAlegadoCI.value;
    this.data.seguimientoJustificacion.value.importeJustificado = this.formGroup.controls.importeJustificado.value;
    this.data.seguimientoJustificacion.value.importeJustificadoCD = this.formGroup.controls.importeJustificadoCD.value;
    this.data.seguimientoJustificacion.value.importeJustificadoCI = this.formGroup.controls.importeJustificadoCI.value;
    this.data.seguimientoJustificacion.value.importeNoEjecutado = this.formGroup.controls.importeNoEjecutado.value;
    this.data.seguimientoJustificacion.value.importeNoEjecutadoCD = this.formGroup.controls.importeNoEjecutadoCD.value;
    this.data.seguimientoJustificacion.value.importeNoEjecutadoCI = this.formGroup.controls.importeNoEjecutadoCI.value;
    this.data.seguimientoJustificacion.value.importeRechazado = this.formGroup.controls.importeRechazado.value;
    this.data.seguimientoJustificacion.value.importeRechazadoCD = this.formGroup.controls.importeRechazadoCD.value;
    this.data.seguimientoJustificacion.value.importeRechazadoCI = this.formGroup.controls.importeRechazadoCI.value;
    this.data.seguimientoJustificacion.value.importeReintegrado = this.formGroup.controls.importeReintegrado.value;
    this.data.seguimientoJustificacion.value.importeReintegradoCD = this.formGroup.controls.importeReintegradoCD.value;
    this.data.seguimientoJustificacion.value.importeReintegradoCI = this.formGroup.controls.importeReintegradoCI.value;
    this.data.seguimientoJustificacion.value.importeReintegrar = this.formGroup.controls.importeReintegrar.value;
    this.data.seguimientoJustificacion.value.importeReintegrarCD = this.formGroup.controls.importeReintegrarCD.value;
    this.data.seguimientoJustificacion.value.importeReintegrarCI = this.formGroup.controls.importeReintegrarCI.value;
    this.data.seguimientoJustificacion.value.interesesReintegrados = this.formGroup.controls.interesesReintegrados.value;
    this.data.seguimientoJustificacion.value.interesesReintegrar = this.formGroup.controls.interesesReintegrar.value;
    this.data.seguimientoJustificacion.value.justificanteReintegro = this.formGroup.controls.justificanteReintegro.value;

    return this.data.seguimientoJustificacion;
  }
}
