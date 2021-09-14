import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-proyecto';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';

const PROYECTO_CAMBIO_ESTADO_COMENTARIO = marker('csp.proyecto.estado-proyecto.comentario');
const PROYECTO_CAMBIO_ESTADO_NUEVO_ESTADO = marker('csp.proyecto.cambio-estado.nuevo');
const MSG_CAMBIO_ESTADO_CONFIRMACION = marker('confirmacion.csp.proyecto.cambio-estado');

export interface ProyectoCambioEstadoModalComponentData {
  estadoActual: Estado;
  estadoNuevo: Estado;
  comentario: string;
}
@Component({
  selector: 'sgi-cambio-estado-modal',
  templateUrl: './cambio-estado-modal.component.html',
  styleUrls: ['./cambio-estado-modal.component.scss']
})
export class CambioEstadoModalComponent extends
  BaseModalComponent<ProyectoCambioEstadoModalComponentData, CambioEstadoModalComponent> implements OnInit, OnDestroy {

  fxLayoutProperties: FxLayoutProperties;

  msgParamComentarioEntity = {};
  msgParamNuevoEstadoEntity = {};
  readonly estadosNuevos: Map<string, string>;

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    public matDialogRef: MatDialogRef<CambioEstadoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoCambioEstadoModalComponentData,
    protected snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private confirmDialogService: DialogService) {
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
      PROYECTO_CAMBIO_ESTADO_COMENTARIO
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_CAMBIO_ESTADO_NUEVO_ESTADO
    ).subscribe((value) => this.msgParamNuevoEstadoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected getDatosForm(): ProyectoCambioEstadoModalComponentData {
    this.data.estadoActual = this.formGroup.controls.estadoActual.value;
    this.data.estadoNuevo = this.formGroup.controls.estadoNuevo.value;
    this.data.comentario = this.formGroup.controls.comentario.value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {

    const formGroup = new FormGroup({
      estadoActual: new FormControl({ value: this.data.estadoActual, disabled: true }),
      estadoNuevo: new FormControl(this.data.estadoNuevo, [Validators.required]),
      comentario: new FormControl('', [Validators.maxLength(2000)])
    });

    return formGroup;
  }

  saveOrUpdate(): void {

    this.confirmDialogService.showConfirmation(MSG_CAMBIO_ESTADO_CONFIRMACION).subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          super.saveOrUpdate();
        }
      });
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

}
