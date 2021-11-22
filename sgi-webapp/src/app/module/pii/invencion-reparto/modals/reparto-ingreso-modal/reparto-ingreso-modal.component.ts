import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IRepartoIngreso } from '@core/models/pii/reparto-ingreso';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NumberValidator } from '@core/validators/number-validator';
import { TranslateService } from '@ngx-translate/core';

const REPARTO_IMPORTE_PENDIENTE_REPARTIR_KEY = marker('pii.reparto.ingreso.importe-pendiente-repartir');
const REPARTO_IMPORTE_REPARTIR_KEY = marker('pii.reparto.ingreso.importe-repartir');

@Component({
  selector: 'sgi-reparto-ingreso-modal',
  templateUrl: './reparto-ingreso-modal.component.html',
  styleUrls: ['./reparto-ingreso-modal.component.scss']
})
export class RepartoIngresoModalComponent extends BaseModalComponent<IRepartoIngreso, RepartoIngresoModalComponent> implements OnInit {
  title: string;
  msgParamImportePendienteRepartirEntity = {};
  msgParamImporteARepartirEntity = {};

  constructor(
    private readonly translate: TranslateService,
    public matDialogRef: MatDialogRef<RepartoIngresoModalComponent>,
    protected readonly snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) readonly data: IRepartoIngreso,
  ) {
    super(snackBarService, matDialogRef, data);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected getDatosForm(): IRepartoIngreso {
    this.entity.importeARepartir = this.formGroup.controls.importeARepartir.value;
    return this.entity;
  }

  protected getFormGroup(): FormGroup {
    return new FormGroup({
      importePendienteRepartir: new FormControl({
        value: this.entity.invencionIngreso?.importePendienteRepartir,
        disabled: true
      }),
      importeARepartir: new FormControl(
        this.entity.importeARepartir, [Validators.required, Validators.min(0.01), NumberValidator.maxDecimalDigits(2)]
      )
    }, NumberValidator.isBeforeOrEqual('importePendienteRepartir', 'importeARepartir'));
  }

  private setupI18N(): void {
    this.translate.get(
      REPARTO_IMPORTE_PENDIENTE_REPARTIR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamImportePendienteRepartirEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      REPARTO_IMPORTE_REPARTIR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamImporteARepartirEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
  }
}
