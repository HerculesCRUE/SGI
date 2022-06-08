import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { NumberUtils } from '@core/utils/number.utils';
import { NumberValidator } from '@core/validators/number-validator';
import { TranslateService } from '@ngx-translate/core';
import { IRepartoEquipoInventorTableData } from '../../invencion-reparto-formulario/invencion-reparto-equipo-inventor/invencion-reparto-equipo-inventor.fragment';

const REPARTO_IMPORTE_TOTAL_KEY = marker('pii.reparto.equipo-inventor-modal.importe-total');
const REPARTO_IMPORTE_NOMINA_KEY = marker('pii.reparto.equipo-inventor-modal.importe-nomina');
const REPARTO_IMPORTE_PROYECTO_KEY = marker('pii.reparto.equipo-inventor-modal.importe-proyecto');
const REPARTO_IMPORTE_OTROS_KEY = marker('pii.reparto.equipo-inventor-modal.importe-otros');

@Component({
  selector: 'sgi-reparto-equipo-modal',
  templateUrl: './reparto-equipo-modal.component.html',
  styleUrls: ['./reparto-equipo-modal.component.scss']
})
export class RepartoEquipoModalComponent extends DialogFormComponent<IRepartoEquipoInventorTableData> implements OnInit {
  title: string;
  msgParamImporteTotalEntity = {};
  msgParamImporteNominaEntity = {};
  msgParamImporteProyectoEntity = {};
  msgParamImporteOtrosEntity = {};

  constructor(
    private readonly translate: TranslateService,
    matDialogRef: MatDialogRef<RepartoEquipoModalComponent>,
    @Inject(MAT_DIALOG_DATA) readonly data: IRepartoEquipoInventorTableData,
  ) {
    super(matDialogRef, true);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected getValue(): IRepartoEquipoInventorTableData {
    this.data.importeTotalInventor = this.formGroup.controls.importeTotal.value;
    this.data.repartoEquipoInventor.importeNomina = this.formGroup.controls.importeNomina.value;
    this.data.repartoEquipoInventor.importeProyecto = this.formGroup.controls.importeProyecto.value;
    this.data.repartoEquipoInventor.importeOtros = this.formGroup.controls.importeOtros.value;
    this.data.repartoEquipoInventor.proyecto = this.formGroup.controls.proyecto.value;

    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      importeTotal: new FormControl(
        NumberUtils.roundNumber(this.data.importeTotalInventor),
        [Validators.required, Validators.min(0), NumberValidator.maxDecimalDigits(2)]
      ),
      importeNomina: new FormControl(
        NumberUtils.roundNumber(this.data.repartoEquipoInventor?.importeNomina),
        [Validators.required, Validators.min(0), NumberValidator.maxDecimalDigits(2)]
      ),
      importeProyecto: new FormControl(
        NumberUtils.roundNumber(this.data.repartoEquipoInventor?.importeProyecto),
        [Validators.required, Validators.min(0), NumberValidator.maxDecimalDigits(2)]
      ),
      proyecto: new FormControl(
        this.data.repartoEquipoInventor?.proyecto
      ),
      importeOtros: new FormControl(
        NumberUtils.roundNumber(this.data.repartoEquipoInventor?.importeOtros),
        [Validators.required, Validators.min(0), NumberValidator.maxDecimalDigits(2)]
      ),
    },
      NumberValidator.fieldsSumEqualsToValue(
        NumberUtils.roundNumber(this.data.importeTotalInventor), 'importeNomina', 'importeProyecto', 'importeOtros'
      )
    );

    this.subscriptions.push(
      formGroup.controls.importeProyecto.valueChanges.subscribe(importeProyecto => {
        if (importeProyecto === 0) {
          formGroup.controls.proyecto.reset();
        }
      }
      )
    );
    this.subscriptions.push(
      formGroup.controls.importeTotal.valueChanges
        .subscribe(importeTotal => {
          if (formGroup.controls.importeTotal.status === 'VALID') {
            formGroup.setValidators(
              NumberValidator.fieldsSumEqualsToValue(importeTotal, 'importeNomina', 'importeProyecto', 'importeOtros')
            );
          }
        })
    );

    return formGroup;
  }

  private setupI18N(): void {
    this.translate.get(
      REPARTO_IMPORTE_TOTAL_KEY
    ).subscribe((value) =>
      this.msgParamImporteTotalEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
    this.translate.get(
      REPARTO_IMPORTE_NOMINA_KEY
    ).subscribe((value) =>
      this.msgParamImporteNominaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
    this.translate.get(
      REPARTO_IMPORTE_PROYECTO_KEY
    ).subscribe((value) =>
      this.msgParamImporteProyectoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
    this.translate.get(
      REPARTO_IMPORTE_OTROS_KEY
    ).subscribe((value) =>
      this.msgParamImporteOtrosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
  }
}
