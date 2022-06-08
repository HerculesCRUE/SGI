import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoResponsableEconomico } from '@core/models/csp/solicitud-proyecto-responsable-economico';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { NumberValidator } from '@core/validators/number-validator';
import { IRange } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_KEY = marker('csp.solicitud-proyecto-responsable-economico');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface SolicitudProyectoResponsableEconomicoModalData {
  entidad: ISolicitudProyectoResponsableEconomico;
  selectedEntidades: ISolicitudProyectoResponsableEconomico[];
  mesMax: number;
  isEdit: boolean;
  readonly: boolean;
}

@Component({
  templateUrl: './solicitud-proyecto-responsable-economico-modal.component.html',
  styleUrls: ['./solicitud-proyecto-responsable-economico-modal.component.scss']
})
export class SolicitudProyectoResponsableEconomicoModalComponent
  extends DialogFormComponent<SolicitudProyectoResponsableEconomicoModalData> implements OnInit {

  textSaveOrUpdate: string;
  title: string;

  msgParamMiembroEntity = {};
  msgParamEntity = {};

  get tipoColectivoResponsableEconomico() {
    return TipoColectivo.RESPONSABLE_ECONOMICO_CSP;
  }

  constructor(
    matDialogRef: MatDialogRef<SolicitudProyectoResponsableEconomicoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudProyectoResponsableEconomicoModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, data.isEdit);

    this.textSaveOrUpdate = this.data.isEdit ? MSG_ACEPTAR : MSG_ANADIR;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data.isEdit) {
      this.translate.get(
        SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_KEY,
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

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        responsable: new FormControl(this.data.entidad.persona, [
          Validators.required
        ]),
        mesInicio: new FormControl(this.data.entidad.mesInicio, [
          Validators.min(1),
          Validators.max(this.data.mesMax ?? GLOBAL_CONSTANTS.integerMaxValue)
        ]),
        mesFin: new FormControl(this.data.entidad.mesFin, [
          Validators.min(1),
          Validators.max(this.data.mesMax ?? GLOBAL_CONSTANTS.integerMaxValue)
        ]),
      },
      {
        validators: [
          NumberValidator.isAfterOptional('mesInicio', 'mesFin'),
          this.checkRangesMeses()
        ]
      }
    );

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  protected getValue(): SolicitudProyectoResponsableEconomicoModalData {
    this.data.entidad.persona = this.formGroup.get('responsable').value;
    this.data.entidad.mesInicio = this.formGroup.get('mesInicio').value;
    this.data.entidad.mesFin = this.formGroup.get('mesFin').value;
    return this.data;
  }

  private checkRangesMeses(): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {
      const responsableForm = formGroup.get('responsable');
      const mesInicioForm = formGroup.get('mesInicio');
      const mesFinForm = formGroup.get('mesFin');

      if ((mesInicioForm.errors && !mesInicioForm.errors.range)
        || (mesFinForm.errors && !mesFinForm.errors.range)
        || (responsableForm.errors && !responsableForm.errors.contains)) {
        return;
      }

      const mesInicio = mesInicioForm.value ? mesInicioForm.value : Number.MIN_VALUE;
      const mesFin = mesFinForm.value ? mesFinForm.value : Number.MAX_VALUE;
      const ranges = this.data.selectedEntidades
        .filter(element => !element.id || element.id !== this.data.entidad?.id)
        .map(responsableEconomico => {
          const range: IRange = {
            inicio: responsableEconomico.mesInicio ? responsableEconomico.mesInicio : Number.MIN_VALUE,
            fin: responsableEconomico.mesFin ? responsableEconomico.mesFin : Number.MAX_VALUE
          };
          return range;
        });

      if (ranges.some(range => (mesInicio <= range.fin && range.inicio <= mesFin))) {
        if (mesInicioForm.value) {
          this.addError(mesInicioForm, 'range');
        }
        if (mesFinForm.value) {
          this.addError(mesFinForm, 'range');
        }
        if (!mesInicioForm.value && !mesFinForm.value) {
          this.addError(responsableForm, 'contains');
        } else if (responsableForm.errors) {
          this.deleteError(responsableForm, 'contains');
        }
      } else {
        this.deleteError(mesInicioForm, 'range');
        this.deleteError(mesFinForm, 'range');
        this.deleteError(responsableForm, 'contains');
      }
    };
  }

  private deleteError(formControl: AbstractControl, errorName: string): void {
    if (formControl.errors) {
      delete formControl.errors[errorName];
      if (Object.keys(formControl.errors).length === 0) {
        formControl.setErrors(null);
      }
    }
  }

  private addError(formControl: AbstractControl, errorName: string): void {
    if (!formControl.errors) {
      formControl.setErrors({});
    }
    formControl.errors[errorName] = true;
    formControl.markAsTouched({ onlySelf: true });
  }

}
