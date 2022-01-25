import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { Estado, ESTADO_MAP, IEstadoAutorizacion } from '@core/models/csp/estado-autorizacion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { Observable } from 'rxjs';

const AUTORIZACION_CAMBIO_ESTADO_COMENTARIO = marker('csp.autorizacion.estado-autorizacion.comentario');
const AUTORIZACION_CAMBIO_ESTADO_NUEVO_ESTADO = marker('csp.autorizacion.cambio-estado.nuevo');
const AUTORIZACION_CAMBIO_ESTADO_FECHA_ESTADO = marker('csp.autorizacion.estado-autorizacion.fecha');

export interface AutorizacionCambioEstadoModalComponentData {
  estadoActual: Estado;
  autorizacion: IAutorizacion;
}

@Component({
  selector: 'sgi-cambio-estado-modal',
  templateUrl: './cambio-estado-modal.component.html',
  styleUrls: ['./cambio-estado-modal.component.scss']
})
export class CambioEstadoModalComponent
  extends DialogActionComponent<AutorizacionCambioEstadoModalComponentData, IEstadoAutorizacion> {

  fxLayoutProperties: FxLayoutProperties;

  msgParamComentarioEntity = {};
  msgParamFechaEstadoEntity = {};
  msgParamNuevoEstadoEntity = {};
  confirmDialogService: any;

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    matDialogRef: MatDialogRef<CambioEstadoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AutorizacionCambioEstadoModalComponentData,
    protected snackBarService: SnackBarService,
    private autorizacionService: AutorizacionService,
    private readonly translate: TranslateService) {
    super(matDialogRef, true);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      AUTORIZACION_CAMBIO_ESTADO_COMENTARIO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      AUTORIZACION_CAMBIO_ESTADO_NUEVO_ESTADO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNuevoEstadoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });


    this.translate.get(
      AUTORIZACION_CAMBIO_ESTADO_FECHA_ESTADO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaEstadoEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.FEMALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

  }

  protected getValue(): IEstadoAutorizacion {
    return {
      id: undefined,
      autorizacion: this.data.autorizacion,
      estado: this.formGroup.controls.estadoNuevo.value,
      fecha: this.formGroup.controls.fechaEstado.value,
      comentario: this.formGroup.controls.comentario.value
    };
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      estadoActual: new FormControl({ value: this.data.estadoActual, disabled: true }),
      estadoNuevo: new FormControl(null),
      fechaEstado: new FormControl(DateTime.now(), Validators.required),
      comentario: new FormControl('', [Validators.maxLength(2000)])
    });
  }

  protected saveOrUpdate(): Observable<IEstadoAutorizacion> {
    const estadoNew = this.getValue();

    return this.autorizacionService.cambiarEstado(this.data.autorizacion.id, estadoNew);
  }



}
