import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { IPersona } from '@core/models/sgp/persona';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import { catchError, delay, filter, map, switchMap, tap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const INVENCION_INVENTOR_KEY = marker('pii.invencion.inventor');
const INVENCION_INVENTOR_PARTICIPACION_KEY = marker('pii.invencion-inventor.participacion');
const INVENCION_INVENTOR_REPARTO_KEY = marker('pii.invencion-inventor.reparto-universidad');
const INVENCION_INVENTOR_INVENTOR_IN_USE_ERROR = marker('pii.invencion-inventor.inventor.in-use.error');

export interface InvencionInventorModalData {
  invencionInventor: StatusWrapper<IInvencionInventor>;
  isEdit: boolean;
  inventoresNotAllowed: IPersona[];
}

@Component({
  selector: 'sgi-invencion-inventor-modal',
  templateUrl: './invencion-inventor-modal.component.html',
  styleUrls: ['./invencion-inventor-modal.component.scss']
})
export class InvencionInventorModalComponent extends DialogFormComponent<StatusWrapper<IInvencionInventor>> implements OnInit {

  invencionInventor: StatusWrapper<IInvencionInventor>;
  msgParamNombreEntity = {};
  msgParamParticipacionEntity = {};
  msgParamRepartoEntity = {};
  msgInventorInUseError = '';

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TipoColectivo() {
    return TipoColectivo;
  }

  protected getValue(): StatusWrapper<IInvencionInventor> {

    if (this.formGroup.touched) {
      this.invencionInventor.value.inventor = this.formGroup.controls.inventor.value;
      this.invencionInventor.value.participacion = this.formGroup.controls.participacion.value;
      this.invencionInventor.value.repartoUniversidad = this.formGroup.controls.isRepartoUniversidad.value;
      if (this.data.isEdit) { this.invencionInventor.setEdited(); }
    }

    return this.invencionInventor;
  }

  protected buildFormGroup(): FormGroup {

    const formGroup = new FormGroup({
      inventor: new FormControl(this.invencionInventor.value.inventor, Validators.required),
      participacion: new FormControl(this.invencionInventor.value.participacion,
        [Validators.required, Validators.min(0.01), Validators.max(100)]),
      isRepartoUniversidad: new FormControl(this.invencionInventor.value.repartoUniversidad, Validators.required),
    });
    if (!this.invencionInventor.created) {
      formGroup.controls.inventor.disable();
    }

    formGroup.controls.inventor.valueChanges.
      pipe(
        filter(elem => elem != null),
        delay(0),
        tap(inventorSelected => {
          this.clearProblems();
          if (this.data.inventoresNotAllowed.some(elem => elem.id === inventorSelected.id)) {
            this.formGroup.controls.inventor.setValue(null);
            this.pushProblems(new SgiError(this.msgInventorInUseError));
          } else {
            if (inventorSelected.entidadPropia?.id && !inventorSelected.entidadPropia.nombre) {
              this.subscriptions.push(
                this.empresaService.findById(inventorSelected.entidadPropia.id).pipe(
                  map(empresa => inventorSelected.entidadPropia = empresa),
                  catchError(err => {
                    this.processError(err);
                    return of(err);
                  })
                ).subscribe()
              );
            }
          }
        })
      )
      .subscribe();

    return formGroup;
  }

  constructor(
    matDialogRef: MatDialogRef<InvencionInventorModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: InvencionInventorModalData,
    private readonly translate: TranslateService,
    private readonly empresaService: EmpresaService
  ) {
    super(matDialogRef, data.isEdit);
    this.invencionInventor = data.invencionInventor;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {

    this.translate.get(
      INVENCION_INVENTOR_KEY,
      this.MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INVENCION_INVENTOR_PARTICIPACION_KEY,
      this.MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamParticipacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INVENCION_INVENTOR_REPARTO_KEY,
      this.MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamRepartoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INVENCION_INVENTOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          INVENCION_INVENTOR_INVENTOR_IN_USE_ERROR,
          { entity: value }
        );
      })
    ).subscribe((value) => this.msgInventorInUseError = value);

  }

}
