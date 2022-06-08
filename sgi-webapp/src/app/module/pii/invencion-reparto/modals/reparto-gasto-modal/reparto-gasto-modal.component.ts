import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IRepartoGasto } from '@core/models/pii/reparto-gasto';
import { NumberValidator } from '@core/validators/number-validator';
import { TranslateService } from '@ngx-translate/core';

const REPARTO_IMPORTE_PENDIENTE_COMPENSAR_KEY = marker('pii.reparto.gasto.importe-pendiente-compensar');
const REPARTO_IMPORTE_COMPENSAR_KEY = marker('pii.reparto.gasto.importe-compensar');

@Component({
  selector: 'sgi-reparto-gasto-modal',
  templateUrl: './reparto-gasto-modal.component.html',
  styleUrls: ['./reparto-gasto-modal.component.scss']
})
export class RepartoGastoModalComponent extends DialogFormComponent<IRepartoGasto> implements OnInit {
  title: string;
  msgParamImportePendienteCompensarEntity = {};
  msgParamImporteACompensarEntity = {};

  constructor(
    private readonly translate: TranslateService,
    matDialogRef: MatDialogRef<RepartoGastoModalComponent>,
    @Inject(MAT_DIALOG_DATA) readonly data: IRepartoGasto,
  ) {
    super(matDialogRef, true);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected getValue(): IRepartoGasto {
    this.data.importeADeducir = this.formGroup.controls.importeACompensar.value;
    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      importePendienteCompensar: new FormControl({
        value: this.data.invencionGasto?.importePendienteDeducir,
        disabled: true
      }),
      importeACompensar: new FormControl(
        this.data.importeADeducir, [Validators.required, Validators.min(0.01), NumberValidator.maxDecimalDigits(2)]
      )
    }, NumberValidator.isBeforeOrEqual('importePendienteCompensar', 'importeACompensar'));
  }

  private setupI18N(): void {
    this.translate.get(
      REPARTO_IMPORTE_PENDIENTE_COMPENSAR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamImportePendienteCompensarEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      REPARTO_IMPORTE_COMPENSAR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamImporteACompensarEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
  }
}
