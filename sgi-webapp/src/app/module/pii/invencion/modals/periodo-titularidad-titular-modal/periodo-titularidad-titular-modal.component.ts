import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPeriodoTitularidadTitular } from '@core/models/pii/periodo-titularidad-titular';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SnackBarService } from '@core/services/snack-bar.service';
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
  extends BaseModalComponent<StatusWrapper<IPeriodoTitularidadTitular>,
  PeriodoTitularidadTitularModalComponent> implements OnInit, OnDestroy {


  msgParamTitle = {};
  msgParamNombreEntity = {};
  msgParamParticipacionEntity = {};
  msgTitularInUseError = '';
  textSaveOrUpdate: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get isEditionMode() {
    return this.periodoTitularidadTitularData.isEdit;
  }

  get titularesNotAllowed() {
    return this.periodoTitularidadTitularData.titularesNotAllowed;
  }

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<PeriodoTitularidadTitularModalComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly periodoTitularidadTitularData: PeriodoTitularidadTitularModalData,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, periodoTitularidadTitularData.periodoTitularidadTitular);
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

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        titular: new FormControl(this.entity?.value.titular, [
          Validators.required,
        ]),
        participacion: new FormControl(this.entity?.value.participacion, [
          Validators.required, Validators.min(0.00), Validators.max(100)
        ]),

      }
    );

    formGroup.controls.titular.valueChanges.
      pipe(
        filter(elem => elem != null),
        delay(0),
        tap(titularSelected => {
          if (this.periodoTitularidadTitularData.titularesNotAllowed.some(elem => elem.id === titularSelected.id)) {
            this.formGroup.controls.titular.setValue(null);
            this.snackBarService.showError(this.msgTitularInUseError);
          }
        })
      )
      .subscribe();
    return formGroup;
  }

  protected getDatosForm(): StatusWrapper<IPeriodoTitularidadTitular> {

    if (this.formGroup.touched) {
      this.entity.value.titular = this.formGroup.controls.titular.value;
      this.entity.value.participacion = this.formGroup.controls.participacion.value;
      if (!this.entity.created) {
        this.entity.setEdited();
      }
    }

    return this.entity;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

}
