import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { IProyectoPeriodoJustificacionSeguimiento } from '@core/models/csp/proyecto-periodo-justificacion-seguimiento';
import { ISeguimientoJustificacionAnualidad } from '@core/models/csp/seguimiento-justificacion-anualidad';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter, startWith, switchMap, tap } from 'rxjs/operators';

const ANUALIDAD_PRESUPUESTO_KEY = marker('csp.seguimiento-justificacion-anualidad.anualidad-presupuesto');
const JUSTIFICANTE_REINTEGRO_KEY = marker('csp.seguimiento-justificacion-anualidad.num-justificante-ultimo-reintegro');

export interface ISeguimientoJustificacionAnualidadModalData {
  seguimientoJustificacionAnualidad: StatusWrapper<ISeguimientoJustificacionAnualidad>;
  tituloProyecto: string;
}

@Component({
  selector: 'sgi-seguimiento-justificacion-anualidad-modal',
  templateUrl: './seguimiento-justificacion-anualidad-modal.component.html',
  styleUrls: ['./seguimiento-justificacion-anualidad-modal.component.scss']
})
export class SeguimientoJustificacionAnualidadModalComponent
  extends DialogFormComponent<StatusWrapper<ISeguimientoJustificacionAnualidad>> implements OnInit {

  msgParamAnualidadPresupuestoEntity = {};
  msgParamJustificanteReintegroEntity = {};

  constructor(
    matDialogRef: MatDialogRef<SeguimientoJustificacionAnualidadModalComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: ISeguimientoJustificacionAnualidadModalData,
    private readonly translate: TranslateService,
    private readonly proyectoAnualidadService: ProyectoAnualidadService,
  ) {
    super(matDialogRef, true);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      proyectoId: new FormControl({ value: this.data.seguimientoJustificacionAnualidad.value.proyectoId, disabled: true }),
      tituloProyecto: new FormControl({ value: this.data.tituloProyecto, disabled: true }),
      identificadorJustificacion: new FormControl(
        { value: this.data.seguimientoJustificacionAnualidad.value.identificadorJustificacion, disabled: true }
      ),
      fechaPresentacionJustificacion: new FormControl(
        { value: this.data.seguimientoJustificacionAnualidad.value.fechaPresentacionJustificacion, disabled: true }
      ),
      proyectoAnualidad: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.proyectoAnualidad,
        Validators.required
      ),
      importeConcedidoAnualidadCD: new FormControl({ value: null, disabled: true }),
      importeConcedidoAnualidadCI: new FormControl({ value: null, disabled: true }),
      importeJustificado: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeJustificado
      ),
      importeJustificadoCD: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeJustificadoCD)
      ,
      importeJustificadoCI: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeJustificadoCI
      ),
      importeAceptado: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeAceptado
      ),
      importeAceptadoCD: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeAceptadoCD
      ),
      importeAceptadoCI: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeAceptadoCI
      ),
      importeRechazado: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeRechazado
      ),
      importeRechazadoCD: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeRechazadoCD
      ),
      importeRechazadoCI: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeRechazadoCI
      ),
      importeAlegado: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeAlegado
      ),
      importeAlegadoCD: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeAlegadoCD
      ),
      importeAlegadoCI: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeAlegadoCI
      ),
      importeNoEjecutado: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeNoEjecutado
      ),
      importeNoEjecutadoCD: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeNoEjecutadoCD
      ),
      importeNoEjecutadoCI: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeNoEjecutadoCI
      ),
      importeReintegrar: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeReintegrar
      ),
      importeReintegrarCD: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeReintegrarCD
      ),
      importeReintegrarCI: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeReintegrarCI
      ),
      importeReintegrado: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeReintegrado
      ),
      importeReintegradoCD: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeReintegradoCD
      ),
      importeReintegradoCI: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeReintegradoCI
      ),
      interesesReintegrar: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.interesesReintegrar
      ),
      interesesReintegrados: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.interesesReintegrados
      ),
      fechaReintegro: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.fechaReintegro
      ),
      justificanteReintegro: new FormControl(
        this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.justificanteReintegro,
        Validators.maxLength(250)
      ),
      reintegroPendiente: new FormControl({ value: null, disabled: true }),
      interesesPendientes: new FormControl({ value: null, disabled: true }),
    });

    this.subscriptions.push(
      combineLatest([
        formGroup.controls.importeReintegrar.valueChanges.pipe(
          startWith(this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeReintegrar)
        ),
        formGroup.controls.importeReintegrado.valueChanges.pipe(
          startWith(this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.importeReintegrado)
        )
      ]).subscribe(([importeReintegrar, importeReintegrado]) =>
        formGroup.controls.reintegroPendiente.setValue(this.calculateSubstraction(importeReintegrar, importeReintegrado))
      )
    );

    this.subscriptions.push(
      combineLatest([
        formGroup.controls.interesesReintegrar.valueChanges.pipe(
          startWith(this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.interesesReintegrar)
        ),
        formGroup.controls.interesesReintegrados.valueChanges.pipe(
          startWith(this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.interesesReintegrados)
        )
      ]).subscribe(([interesesReintegrar, interesesReintegrados]) =>
        formGroup.controls.interesesPendientes.setValue(this.calculateSubstraction(interesesReintegrar, interesesReintegrados))
      )
    );

    this.subscriptions.push(
      formGroup.controls.proyectoAnualidad.valueChanges.pipe(
        startWith(this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento?.proyectoAnualidad),
        filter(proyectoAnualidad => !!proyectoAnualidad?.id),
        switchMap(proyectoAnualidad =>
          this.proyectoAnualidadService.getTotalImportesProyectoAnualidad(proyectoAnualidad.id)
            .pipe(
              tap(proyectoAnualidadGastosTotales => {
                formGroup.controls.importeConcedidoAnualidadCD.setValue(
                  proyectoAnualidadGastosTotales.importeConcendidoAnualidadCostesDirectos
                );
                formGroup.controls.importeConcedidoAnualidadCI.setValue(
                  proyectoAnualidadGastosTotales.importeConcendidoAnualidadCostesIndirectos
                );
              })
            )
        )
      ).subscribe()
    );

    return formGroup;
  }

  private calculateSubstraction(a: number, b: number): number | null {
    if (typeof a !== 'number' && typeof b !== 'number') {
      return null;
    }

    return (a ?? 0) - (b ?? 0);
  }

  protected getValue(): StatusWrapper<ISeguimientoJustificacionAnualidad> {
    const proyectoPeriodoJustificacionSeguimiento =
      this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento ??
      {} as IProyectoPeriodoJustificacionSeguimiento;

    proyectoPeriodoJustificacionSeguimiento.proyectoAnualidad = this.formGroup.controls.proyectoAnualidad.value;
    proyectoPeriodoJustificacionSeguimiento.fechaReintegro = this.formGroup.controls.fechaReintegro.value;
    proyectoPeriodoJustificacionSeguimiento.importeAceptado = this.formGroup.controls.importeAceptado.value;
    proyectoPeriodoJustificacionSeguimiento.importeAceptadoCD = this.formGroup.controls.importeAceptadoCD.value;
    proyectoPeriodoJustificacionSeguimiento.importeAceptadoCI = this.formGroup.controls.importeAceptadoCI.value;
    proyectoPeriodoJustificacionSeguimiento.importeAlegado = this.formGroup.controls.importeAlegado.value;
    proyectoPeriodoJustificacionSeguimiento.importeAlegadoCD = this.formGroup.controls.importeAlegadoCD.value;
    proyectoPeriodoJustificacionSeguimiento.importeAlegadoCI = this.formGroup.controls.importeAlegadoCI.value;
    proyectoPeriodoJustificacionSeguimiento.importeJustificado = this.formGroup.controls.importeJustificado.value;
    proyectoPeriodoJustificacionSeguimiento.importeJustificadoCD = this.formGroup.controls.importeJustificadoCD.value;
    proyectoPeriodoJustificacionSeguimiento.importeJustificadoCI = this.formGroup.controls.importeJustificadoCI.value;
    proyectoPeriodoJustificacionSeguimiento.importeNoEjecutado = this.formGroup.controls.importeNoEjecutado.value;
    proyectoPeriodoJustificacionSeguimiento.importeNoEjecutadoCD = this.formGroup.controls.importeNoEjecutadoCD.value;
    proyectoPeriodoJustificacionSeguimiento.importeNoEjecutadoCI = this.formGroup.controls.importeNoEjecutadoCI.value;
    proyectoPeriodoJustificacionSeguimiento.importeRechazado = this.formGroup.controls.importeRechazado.value;
    proyectoPeriodoJustificacionSeguimiento.importeRechazadoCD = this.formGroup.controls.importeRechazadoCD.value;
    proyectoPeriodoJustificacionSeguimiento.importeRechazadoCI = this.formGroup.controls.importeRechazadoCI.value;
    proyectoPeriodoJustificacionSeguimiento.importeReintegrado = this.formGroup.controls.importeReintegrado.value;
    proyectoPeriodoJustificacionSeguimiento.importeReintegradoCD = this.formGroup.controls.importeReintegradoCD.value;
    proyectoPeriodoJustificacionSeguimiento.importeReintegradoCI = this.formGroup.controls.importeReintegradoCI.value;
    proyectoPeriodoJustificacionSeguimiento.importeReintegrar = this.formGroup.controls.importeReintegrar.value;
    proyectoPeriodoJustificacionSeguimiento.importeReintegrarCD = this.formGroup.controls.importeReintegrarCD.value;
    proyectoPeriodoJustificacionSeguimiento.importeReintegrarCI = this.formGroup.controls.importeReintegrarCI.value;
    proyectoPeriodoJustificacionSeguimiento.interesesReintegrados = this.formGroup.controls.interesesReintegrados.value;
    proyectoPeriodoJustificacionSeguimiento.interesesReintegrar = this.formGroup.controls.interesesReintegrar.value;
    proyectoPeriodoJustificacionSeguimiento.justificanteReintegro = this.formGroup.controls.justificanteReintegro.value;
    proyectoPeriodoJustificacionSeguimiento.proyectoPeriodoJustificacion = {
      id: this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionId
    } as IProyectoPeriodoJustificacion;

    this.data.seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionSeguimiento = proyectoPeriodoJustificacionSeguimiento;

    return this.data.seguimientoJustificacionAnualidad;
  }

  private setupI18N(): void {
    this.translate.get(
      ANUALIDAD_PRESUPUESTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamAnualidadPresupuestoEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      JUSTIFICANTE_REINTEGRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamJustificanteReintegroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }
}
