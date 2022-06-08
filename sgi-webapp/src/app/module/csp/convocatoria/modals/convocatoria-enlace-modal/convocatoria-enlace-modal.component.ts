import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { StringValidator } from '@core/validators/string-validator';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_ENLACE_KEY = marker('csp.convocatoria-enlace');
const CONVOCATORIA_ENLACE_URL_KEY = marker('csp.convocatoria-enlace.url');
const CONVOCATORIA_ENLACE_DESCRIPCION_KEY = marker('csp.convocatoria-enlace.descripcion');
const CONVOCATORIA_ENLACE_TIPO_KEY = marker('csp.convocatoria-enlace.tipo-enlace');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ConvocatoriaEnlaceModalComponentData {
  enlace: IConvocatoriaEnlace;
  idModeloEjecucion: number;
  selectedUrls: string[];
  readonly: boolean;
  canEdit: boolean;
}
@Component({
  templateUrl: './convocatoria-enlace-modal.component.html',
  styleUrls: ['./convocatoria-enlace-modal.component.scss']
})
export class ConvocatoriaEnlaceModalComponent extends DialogFormComponent<ConvocatoriaEnlaceModalComponentData> implements OnInit {

  textSaveOrUpdate: string;

  msgParamUrlEntity = {};
  msgParamDescripcionEntity = {};
  msgParamTipoEnlaceEntity = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<ConvocatoriaEnlaceModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConvocatoriaEnlaceModalComponentData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !data.enlace.url);
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();
    this.textSaveOrUpdate = this.data.enlace.url ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_ENLACE_URL_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamUrlEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      CONVOCATORIA_ENLACE_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      CONVOCATORIA_ENLACE_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamTipoEnlaceEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data.enlace.url) {
      this.translate.get(
        CONVOCATORIA_ENLACE_KEY,
        MSG_PARAMS.CARDINALIRY.PLURAL
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        CONVOCATORIA_ENLACE_KEY,
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

  protected getValue(): ConvocatoriaEnlaceModalComponentData {
    this.data.enlace.url = this.formGroup.controls.url.value;
    this.data.enlace.descripcion = this.formGroup.controls.descripcion.value;
    this.data.enlace.tipoEnlace = this.formGroup.controls.tipoEnlace.value;
    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      url: new FormControl(this.data.enlace.url, [
        Validators.maxLength(250),
        StringValidator.notIn(this.data.selectedUrls.filter(url => url !== this.data.enlace.url))
      ]),
      descripcion: new FormControl(this.data.enlace.descripcion, [Validators.maxLength(250)]),
      tipoEnlace: new FormControl(this.data.enlace.tipoEnlace),
    });
    if (!this.data.canEdit) {
      formGroup.disable();
    }
    return formGroup;
  }

}
