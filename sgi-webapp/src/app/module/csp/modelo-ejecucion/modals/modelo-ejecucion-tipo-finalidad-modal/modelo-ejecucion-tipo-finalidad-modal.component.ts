import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloTipoFinalidad } from '@core/models/csp/modelo-tipo-finalidad';
import { ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MODELO_EJECUCION_TIPO_FINALIDAD_KEY = marker('csp.tipo-finalidad');
const MODELO_EJECUCION_TIPO_FINALIDAD_TIPO_KEY = marker('csp.modelo-ejecucion-tipo-finalidad.tipo');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

export interface ModeloEjecucionTipoFinalidadModalData {
  modeloTipoFinalidad: IModeloTipoFinalidad;
  tipoFinalidades: ITipoFinalidad[];
}

@Component({
  templateUrl: './modelo-ejecucion-tipo-finalidad-modal.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-finalidad-modal.component.scss']
})
export class ModeloEjecucionTipoFinalidadModalComponent extends DialogFormComponent<IModeloTipoFinalidad> implements OnInit {

  title: string;
  msgParamTipoEntiy = {};

  textSaveOrUpdate: string;

  constructor(
    public readonly matDialogRef: MatDialogRef<ModeloEjecucionTipoFinalidadModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ModeloEjecucionTipoFinalidadModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data.modeloTipoFinalidad?.tipoFinalidad);
    this.textSaveOrUpdate = this.data.modeloTipoFinalidad?.tipoFinalidad ? MSG_ACEPTAR : MSG_ANADIR;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      MODELO_EJECUCION_TIPO_FINALIDAD_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntiy = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      MODELO_EJECUCION_TIPO_FINALIDAD_KEY,
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
      tipoFinalidad: new FormControl(this.data.modeloTipoFinalidad?.tipoFinalidad, Validators.required)
    });
    return formGroup;
  }

  protected getValue(): IModeloTipoFinalidad {
    const modeloTipoFinalidad = this.data.modeloTipoFinalidad;
    modeloTipoFinalidad.tipoFinalidad = this.formGroup.get('tipoFinalidad').value;
    return modeloTipoFinalidad;
  }
}
