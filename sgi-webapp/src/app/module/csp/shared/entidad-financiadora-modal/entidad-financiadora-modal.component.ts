import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NumberValidator } from '@core/validators/number-validator';
import { TranslateService } from '@ngx-translate/core';

export interface EntidadFinanciadoraDataModal {
  title: string;
  entidad: IEntidadFinanciadora;
  readonly: boolean;
}

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const ENTIDAD_FINANCIADORA_KEY = marker('csp.entidad-financiadora');
const ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_KEY = marker('csp.entidad-financiadora.porcentaje-financiacion');
const ENTIDAD_FINANCIADORA_IMPORTE_FINANCIACION_KEY = marker('csp.entidad-financiadora.importe-financiacion');

@Component({
  templateUrl: './entidad-financiadora-modal.component.html',
  styleUrls: ['./entidad-financiadora-modal.component.scss']
})
export class EntidadFinanciadoraModalComponent extends DialogFormComponent<IEntidadFinanciadora> implements OnInit {

  textSaveOrUpdate: string;
  title: string;

  msgParamPorcentajeFinanciacionEntity = {};
  msgParamImporteFinanciacionEntity = {};
  msgParamEmpresaEntity = {};

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<EntidadFinanciadoraModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EntidadFinanciadoraDataModal,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !data.entidad?.empresa);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.textSaveOrUpdate = this.data.entidad?.empresa ? MSG_ACEPTAR : MSG_ANADIR;
    this.title = this.data.title;
  }

  private setupI18N(): void {
    this.translate.get(
      ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEmpresaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPorcentajeFinanciacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      ENTIDAD_FINANCIADORA_IMPORTE_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamImporteFinanciacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });
  }

  protected getValue(): IEntidadFinanciadora {
    const entidad = this.data.entidad;
    entidad.empresa = this.formGroup.get('empresa').value;
    entidad.fuenteFinanciacion = this.formGroup.get('fuenteFinanciacion').value;
    entidad.tipoFinanciacion = this.formGroup.get('tipoFinanciacion').value;
    entidad.porcentajeFinanciacion = this.formGroup.get('porcentajeFinanciacion').value;
    entidad.importeFinanciacion = this.formGroup.get('importeFinanciacion').value;
    return entidad;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      empresa: new FormControl(
        {
          value: this.data.entidad.empresa,
          disabled: this.data.entidad.empresa || this.data.readonly
        },
        [Validators.required]),
      fuenteFinanciacion: new FormControl(this.data.entidad.fuenteFinanciacion),
      tipoFinanciacion: new FormControl(this.data.entidad.tipoFinanciacion),
      porcentajeFinanciacion: new FormControl(this.data.entidad.porcentajeFinanciacion, [
        Validators.min(0),
        Validators.max(100),
        NumberValidator.maxDecimalDigits(2)
      ]),
      importeFinanciacion: new FormControl(this.data.entidad.importeFinanciacion, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ])
    });
    if (this.data.readonly) {
      formGroup.disable();
    }
    return formGroup;
  }
}
