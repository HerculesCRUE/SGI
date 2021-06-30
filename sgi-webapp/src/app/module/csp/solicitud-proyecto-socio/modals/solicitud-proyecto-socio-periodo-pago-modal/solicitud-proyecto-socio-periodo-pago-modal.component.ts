import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoSocioPeriodoPago } from '@core/models/csp/solicitud-proyecto-socio-periodo-pago';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { RangeValidator } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_SOCIO_PERIODO_PAGO_KEY = marker('csp.proyecto-socio-periodo-pago');
const PROYECTO_SOCIO_PERIODO_PAGO_MES_KEY = marker('csp.proyecto-socio.periodo-pago.mes');
const PROYECTO_SOCIO_PERIODO_PAGO_IMPORTE_KEY = marker('csp.proyecto-socio.periodo-pago.importe');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface SolicitudProyectoSocioPeriodoPagoModalData {
  solicitudProyectoPeriodoPago: ISolicitudProyectoSocioPeriodoPago;
  duracion: number;
  selectedMeses: number[];
  mesInicioSolicitudProyectoSocio: number;
  mesFinSolicitudProyectoSocio: number;
  isEdit: boolean;
  readonly: boolean;
}

@Component({
  templateUrl: './solicitud-proyecto-socio-periodo-pago-modal.component.html',
  styleUrls: ['./solicitud-proyecto-socio-periodo-pago-modal.component.scss']
})
export class SolicitudProyectoSocioPeriodoPagoModalComponent extends
  BaseModalComponent<SolicitudProyectoSocioPeriodoPagoModalData, SolicitudProyectoSocioPeriodoPagoModalComponent>
  implements OnInit {
  textSaveOrUpdate: string;
  title: string;

  msgParamEntity = {};
  msgParamEntities = {};
  msgParamMesEntity = {};
  msgParamImporteEntity = {};

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<SolicitudProyectoSocioPeriodoPagoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudProyectoSocioPeriodoPagoModalData,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';
    this.textSaveOrUpdate = this.data.isEdit ? MSG_ACEPTAR : MSG_ANADIR;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_SOCIO_PERIODO_PAGO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_PAGO_MES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMesEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_PAGO_IMPORTE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamImporteEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data.isEdit) {
      this.translate.get(
        PROYECTO_SOCIO_PERIODO_PAGO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_SOCIO_PERIODO_PAGO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);

    }
  }

  protected getDatosForm(): SolicitudProyectoSocioPeriodoPagoModalData {
    this.data.solicitudProyectoPeriodoPago.numPeriodo = this.formGroup.get('numPeriodo').value;
    this.data.solicitudProyectoPeriodoPago.mes = this.formGroup.get('mes').value;
    this.data.solicitudProyectoPeriodoPago.importe = this.formGroup.get('importe').value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const mesInicio = this.data.mesInicioSolicitudProyectoSocio;
    const mesFinal = this.data.mesFinSolicitudProyectoSocio;
    const duracion = this.data.duracion;
    const formGroup = new FormGroup(
      {
        numPeriodo: new FormControl({
          value: this.data.solicitudProyectoPeriodoPago.numPeriodo,
          disabled: true
        },
          [Validators.required]
        ),
        mes: new FormControl(
          this.data.solicitudProyectoPeriodoPago.mes,
          [
            Validators.min(mesInicio ? mesInicio : 1),
            Validators.max(mesFinal ? mesFinal : (isNaN(duracion) ? GLOBAL_CONSTANTS.integerMaxValue : duracion)),
            Validators.required,
            RangeValidator.contains(this.data.selectedMeses)
          ]
        ),
        importe: new FormControl(
          this.data.solicitudProyectoPeriodoPago.importe,
          [
            Validators.min(1),
            Validators.max(GLOBAL_CONSTANTS.integerMaxValue)
          ]
        ),
      }
    );

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }
}
