import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoSocioPeriodoPago } from '@core/models/csp/proyecto-socio-periodo-pago';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DateValidator } from '@core/validators/date-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

const PROYECTO_SOCIO_PERIODO_PAGO_FECHA_PREVISTA_KEY = marker('csp.proyecto-socio.periodo-pago.fecha-prevista');
const PROYECTO_SOCIO_PERIODO_PAGO_IMPORTE = marker('csp.proyecto-socio.periodo-pago.importe');
const PROYECTO_SOCIO_PERIODO_PAGO_KEY = marker('csp.proyecto-socio.periodo-pago');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoSocioPeriodoPagoModalData {
  proyectoSocioPeriodoPago: IProyectoSocioPeriodoPago;
  fechaInicioProyectoSocio: DateTime;
  fechaFinProyectoSocio: DateTime;
  isEdit: boolean;
  readonly: boolean;
}

@Component({
  templateUrl: './proyecto-socio-periodo-pago-modal.component.html',
  styleUrls: ['./proyecto-socio-periodo-pago-modal.component.scss']
})
export class ProyectoSocioPeriodoPagoModalComponent extends
  BaseModalComponent<ProyectoSocioPeriodoPagoModalData, ProyectoSocioPeriodoPagoModalComponent>
  implements OnInit {
  textSaveOrUpdate: string;

  msgParamImporteEntity = {};
  msgParamFechaPrevistaEntity = {};
  title: string;

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ProyectoSocioPeriodoPagoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoSocioPeriodoPagoModalData,
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
      PROYECTO_SOCIO_PERIODO_PAGO_FECHA_PREVISTA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaPrevistaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_PAGO_IMPORTE,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamImporteEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

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

  protected getDatosForm(): ProyectoSocioPeriodoPagoModalData {
    this.data.proyectoSocioPeriodoPago.numPeriodo = this.formGroup.get('numPeriodo').value;
    this.data.proyectoSocioPeriodoPago.fechaPrevistaPago = this.formGroup.get('fechaPrevistaPago').value;
    this.data.proyectoSocioPeriodoPago.importe = this.formGroup.get('importe').value;
    this.data.proyectoSocioPeriodoPago.fechaPago = this.formGroup.get('fechaPago').value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        numPeriodo: new FormControl({
          value: this.data.proyectoSocioPeriodoPago.numPeriodo,
          disabled: true
        },
          [Validators.required]
        ),
        fechaPrevistaPago: new FormControl(
          this.data.proyectoSocioPeriodoPago.fechaPrevistaPago, [
          Validators.required,
          DateValidator.minDate(this.data.fechaInicioProyectoSocio),
          DateValidator.maxDate(this.data.fechaFinProyectoSocio)
        ]
        ),
        importe: new FormControl(
          this.data.proyectoSocioPeriodoPago.importe,
          [
            Validators.required,
            Validators.min(1),
            Validators.max(2_147_483_647)
          ]
        ),
        fechaPago: new FormControl(this.data.proyectoSocioPeriodoPago.fechaPago, []),
      }
    );

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }
}
