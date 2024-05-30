import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConfiguracion } from '@core/models/csp/configuracion';
import { GastoRequerimientoJustificacionService } from '@core/services/csp/gasto-requerimiento-justificacion/gasto-requerimiento-justificacion.service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { StringValidator } from '@core/validators/string-validator';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { IProyectoPeriodoJustificacionWithTituloProyecto } from '../../ejecucion-economica-formulario/seguimiento-justificacion-resumen/seguimiento-justificacion-resumen.fragment';

const PERIODO_JUSTIFICACION_ID_JUSTIFICACION_KEY = marker('csp.identificador-justificacion.identificador-justificacion');
const PERIODO_JUSTIFICACION_FECHA_JUSTIFICACION_KEY =
  marker('csp.identificador-justificacion.fecha-presentacion-justificacion');
const IDENTIFICADOR_JUSTIFICACION_VINCULADO_REQUERIMIENTO = marker('csp.identificador-justificacion.identificador-justificacion.identificador-vinculado-requerimiento');
const IDENTIFICADOR_JUSTIFICACION_VINCULADO_GASTO_REQUERIMIENTO = marker('csp.identificador-justificacion.identificador-justificacion.identificador-vinculado-gastos');

export interface IdentificadorJustificacionModalData {
  periodoJustificacion: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>;
  configuracion: IConfiguracion;
  othersPeriodosJustificacion: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>[];
}

@Component({
  selector: 'sgi-identificador-justificacion-modal',
  templateUrl: './identificador-justificacion-modal.component.html',
  styleUrls: ['./identificador-justificacion-modal.component.scss']
})
export class IdentificadorJustificacionModalComponent extends
  DialogFormComponent<StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>> implements OnInit {

  msgParamIdJustificacionEntity = {};
  msgParamFechaJustificacionEntity = {};
  textoIdentificadorVinculadoRequerimiento = "";
  textoIdentificadorVinculadoGastoRequerimiento = "";

  get plantillaFormatoIdentificadorJustificacion() {
    return { mask: this.data.configuracion.plantillaFormatoIdentificadorJustificacion };
  }

  constructor(
    matDialogRef: MatDialogRef<IdentificadorJustificacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: IdentificadorJustificacionModalData,
    private readonly translate: TranslateService,
    private readonly dialogService: DialogService,
    private readonly gastoRequerimientoJustificacionService: GastoRequerimientoJustificacionService,
    private readonly proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService
  ) {
    super(matDialogRef, true);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      PERIODO_JUSTIFICACION_ID_JUSTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamIdJustificacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PERIODO_JUSTIFICACION_FECHA_JUSTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaJustificacionEntity = {
      entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      IDENTIFICADOR_JUSTIFICACION_VINCULADO_REQUERIMIENTO,
    ).subscribe((value) => this.textoIdentificadorVinculadoRequerimiento = value);

    this.translate.get(
      IDENTIFICADOR_JUSTIFICACION_VINCULADO_GASTO_REQUERIMIENTO,
    ).subscribe((value) => this.textoIdentificadorVinculadoGastoRequerimiento = value);
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup(
      {
        identificadorJustificacion: new FormControl(this.data.periodoJustificacion?.value.identificadorJustificacion, [
          Validators.pattern(this.data.configuracion.formatoIdentificadorJustificacion), Validators.maxLength(255),
          StringValidator.notIn(this.data.othersPeriodosJustificacion.
            map(periodoJustificacion => periodoJustificacion.value.identificadorJustificacion))
        ]),
        fechaPresentacionJustificacion: new FormControl(this.data.periodoJustificacion?.value.fechaPresentacionJustificacion),

      }
    );
  }

  protected getValue(): StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto> {
    this.data.periodoJustificacion.value.identificadorJustificacion = this.formGroup.controls.identificadorJustificacion.value;
    this.data.periodoJustificacion.value.fechaPresentacionJustificacion = this.formGroup.controls.fechaPresentacionJustificacion.value;

    return this.data.periodoJustificacion;
  }

  doAction(): void {
    this.subscriptions.push(
      this.proyectoPeriodoJustificacionService.hasRequerimientosJustificacion(this.getValue().value.id)
        .pipe(
          switchMap(response => {
            if (!response) {
              return this.dialogService.showConfirmation(this.textoIdentificadorVinculadoRequerimiento).pipe(
                filter(aceptado => !!aceptado)
              )
            }

            return of(void 0);
          }),
          switchMap(() => this.gastoRequerimientoJustificacionService.existsWithIndentificadorJustificacion(this.getValue().value.identificadorJustificacion)
            .pipe(
              switchMap(response => {
                if (!response) {
                  return this.dialogService.showConfirmation(this.textoIdentificadorVinculadoGastoRequerimiento).pipe(
                    filter(aceptado => !!aceptado)
                  )
                }

                return of(void 0);
              })
            )
          )
        ).subscribe(() => {
          this.formGroup.markAllAsTouched();
          if (this.formGroup.valid) {
            this.close(this.getValue());
          }
        })
    );
  }
}
