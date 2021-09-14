import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-solicitud';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';

const SOLICITUD_CAMBIO_ESTADO_COMENTARIO = marker('csp.solicitud.estado-solicitud.comentario');
const SOLICITUD_CAMBIO_ESTADO_FECHA_ESTADO = marker('csp.solicitud.estado-solicitud.fecha');

export interface SolicitudCambioEstadoModalComponentData {
  estadoActual: Estado;
  estadoNuevo: Estado;
  fechaEstado: DateTime;
  comentario: string;
  isInvestigador: boolean;
}
@Component({
  selector: 'sgi-cambio-estado-modal',
  templateUrl: './cambio-estado-modal.component.html',
  styleUrls: ['./cambio-estado-modal.component.scss']
})
export class CambioEstadoModalComponent extends
  BaseModalComponent<SolicitudCambioEstadoModalComponentData, CambioEstadoModalComponent> implements OnInit, OnDestroy {

  fxLayoutProperties: FxLayoutProperties;

  msgParamComentarioEntity = {};
  msgParamFechaEstadoEntity = {};
  readonly estadosNuevos: Map<string, string>;

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    public matDialogRef: MatDialogRef<CambioEstadoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudCambioEstadoModalComponentData,
    protected snackBarService: SnackBarService,
    private readonly translate: TranslateService) {
    super(snackBarService, matDialogRef, data);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    const estados = new Map<string, string>();
    ESTADO_MAP.forEach((value, key) => {
      if (key !== this.data.estadoActual) {
        estados.set(key, value);
      }
    });
    this.estadosNuevos = estados;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_CAMBIO_ESTADO_COMENTARIO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      SOLICITUD_CAMBIO_ESTADO_FECHA_ESTADO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaEstadoEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.FEMALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

  }

  protected getDatosForm(): SolicitudCambioEstadoModalComponentData {
    this.data.estadoActual = this.formGroup.controls.estadoActual.value;
    this.data.estadoNuevo = this.formGroup.controls.estadoNuevo.value;
    this.data.fechaEstado = this.formGroup.controls.fechaEstado.value;
    this.data.comentario = this.formGroup.controls.comentario.value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      estadoActual: new FormControl({ value: this.data.estadoActual, disabled: true }),
      estadoNuevo: new FormControl(this.data.estadoNuevo),
      fechaEstado: new FormControl({ value: DateTime.now(), disabled: this.data.isInvestigador }, Validators.required),
      comentario: new FormControl('', [Validators.maxLength(2000)])
    });
    return formGroup;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

}
