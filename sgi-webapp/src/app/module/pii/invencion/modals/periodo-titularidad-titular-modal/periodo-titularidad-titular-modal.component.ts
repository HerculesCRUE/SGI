import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IPeriodoTitularidadTitular } from '@core/models/pii/periodo-titularidad-titular';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { delay, filter, switchMap, tap } from 'rxjs/operators';

const PERIODO_TITULARIDAD_TITULAR_KEY = marker('pii.invencion-titularidad.periodo-titularidad-titular');
const PERIODO_TITULARIDAD_TITULAR_PARTICIPACION_KEY = marker('pii.invencion-titularidad.periodo-titularidad-titular.participacion');
const PERIODO_TITULARIDAD_TITULAR_IN_USE_ERROR = marker('error.pii.invencion-titularidad.periodo-titularidad-titular.in-use');
const MSG_ACEPTAR = marker('btn.ok');
const MSG_ADD = marker('btn.add');

export interface PeriodoTitularidadTitularModalData {
  periodoTitularidadTitular: StatusWrapper<IPeriodoTitularidadTitular>;
  isEdit: boolean;
  titularesNotAllowed: IEmpresa[];
}

@Component({
  selector: 'sgi-periodo-titularidad-titular-modal',
  templateUrl: './periodo-titularidad-titular-modal.component.html',
  styleUrls: ['./periodo-titularidad-titular-modal.component.scss']
})
export class PeriodoTitularidadTitularModalComponent
  extends DialogFormComponent<StatusWrapper<IPeriodoTitularidadTitular>> implements OnInit {

  msgParamTitle = {};
  msgParamNombreEntity = {};
  msgParamParticipacionEntity = {};
  msgTitularInUseError = '';
  textSaveOrUpdate: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get isEditionMode() {
    return this.data.isEdit;
  }

  get titularesNotAllowed() {
    return this.data.titularesNotAllowed;
  }

  constructor(
    matDialogRef: MatDialogRef<PeriodoTitularidadTitularModalComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: PeriodoTitularidadTitularModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, data.isEdit);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {

    this.translate.get(
      PERIODO_TITULARIDAD_TITULAR_KEY,
      this.MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PERIODO_TITULARIDAD_TITULAR_PARTICIPACION_KEY,
      this.MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamParticipacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PERIODO_TITULARIDAD_TITULAR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          PERIODO_TITULARIDAD_TITULAR_IN_USE_ERROR,
          {
            ...MSG_PARAMS.CARDINALIRY.SINGULAR,
            entity: value
          }
        );
      })
    ).subscribe((value) => this.msgTitularInUseError = value);

    this.textSaveOrUpdate = this.isEditionMode ? MSG_ACEPTAR : MSG_ADD;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        titular: new FormControl(this.data.periodoTitularidadTitular?.value.titular, [
          Validators.required,
        ]),
        participacion: new FormControl(this.data.periodoTitularidadTitular?.value.participacion, [
          Validators.required, Validators.min(0.00), Validators.max(100)
        ]),

      }
    );

    formGroup.controls.titular.valueChanges.
      pipe(
        filter(elem => elem != null),
        delay(0),
        tap(titularSelected => {
          this.clearProblems();
          if (this.data.titularesNotAllowed.some(elem => elem.id === titularSelected.id)) {
            this.formGroup.controls.titular.setValue(null);
            this.pushProblems(new SgiError(this.msgTitularInUseError));
          }
        })
      )
      .subscribe();
    return formGroup;
  }

  protected getValue(): StatusWrapper<IPeriodoTitularidadTitular> {

    if (this.formGroup.touched) {
      this.data.periodoTitularidadTitular.value.titular = this.formGroup.controls.titular.value;
      this.data.periodoTitularidadTitular.value.participacion = this.formGroup.controls.participacion.value;
      if (!this.data.periodoTitularidadTitular.created) {
        this.data.periodoTitularidadTitular.setEdited();
      }
    }

    return this.data.periodoTitularidadTitular;
  }

}
