import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloTipoEnlace } from '@core/models/csp/modelo-tipo-enlace';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MODELO_EJECUCION_TIPO_ENLACE = marker('csp.tipo-enlace');
const MODELO_EJECUCION_TIPO_ENLACE_TIPO = marker('csp.modelo-ejecucion-tipo-enlace.tipo');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

export interface ModeloEjecucionTipoEnlaceModalData {
  modeloTipoEnlace: IModeloTipoEnlace;
  tipoEnlaces: ITipoEnlace[];
}

@Component({
  templateUrl: './modelo-ejecucion-tipo-enlace-modal.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-enlace-modal.component.scss']
})
export class ModeloEjecucionTipoEnlaceModalComponent extends DialogFormComponent<IModeloTipoEnlace> implements OnInit {

  msgParamTipoEntiy = {};
  title: string;

  textSaveOrUpdate: string;

  constructor(
    matDialogRef: MatDialogRef<ModeloEjecucionTipoEnlaceModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ModeloEjecucionTipoEnlaceModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data.modeloTipoEnlace?.tipoEnlace);
    this.textSaveOrUpdate = this.data.modeloTipoEnlace?.tipoEnlace ? MSG_ACEPTAR : MSG_ANADIR;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      MODELO_EJECUCION_TIPO_ENLACE_TIPO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntiy = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      MODELO_EJECUCION_TIPO_ENLACE,
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

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoEnlace: new FormControl(this.data.modeloTipoEnlace?.tipoEnlace, Validators.required)
    });
    return formGroup;
  }

  protected getValue(): IModeloTipoEnlace {
    const modeloTipoEnlace = this.data.modeloTipoEnlace;
    modeloTipoEnlace.tipoEnlace = this.formGroup.get('tipoEnlace').value;
    return modeloTipoEnlace;
  }

}
