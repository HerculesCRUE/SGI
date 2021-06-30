import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { TipoHitoService } from '@core/services/csp/tipo-hito.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { requiredChecked } from '@core/validators/checkbox-validator';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

export interface ModeloEjecucionTipoHitoModalData {
  modeloTipoHito: IModeloTipoHito;
  tipoHitos: ITipoHito[];
}

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const MODELO_EJECUCION_TIPO_HITO_KEY = marker('csp.tipo-hito');
const MODELO_EJECUCION_TIPO_HITO_PROYECTOS_KEY = marker('csp.modelo-ejecucion-tipo-hito.proyectos');
const MODELO_EJECUCION_TIPO_HITO_TIPO_KEY = marker('csp.modelo-ejecucion-tipo-hito.tipo');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-modelo-ejecucion-tipo-hito-modal',
  templateUrl: './modelo-ejecucion-tipo-hito-modal.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-hito-modal.component.scss']
})
export class ModeloEjecucionTipoHitoModalComponent extends
  BaseModalComponent<IModeloTipoHito, ModeloEjecucionTipoHitoModalComponent> implements OnInit {
  tipoHitos$: Observable<ITipoHito[]>;

  textSaveOrUpdate: string;
  title: string;
  msgParamTipoEntity = {};
  msgParamProyectosEntity = {};

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ModeloEjecucionTipoHitoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ModeloEjecucionTipoHitoModalData,
    private tipoHitoService: TipoHitoService,
    protected dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data.modeloTipoHito);

    this.tipoHitos$ = this.tipoHitoService.findTodos().pipe(
      map(response => {
        if (!this.data.modeloTipoHito.tipoHito) {
          return response.items.filter(tipoHito => {
            return !this.data.tipoHitos.some(currentTipo => currentTipo.id === tipoHito.id);
          });
        }
        return response.items;
      })
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    if (!this.data.modeloTipoHito.tipoHito) {
      this.textSaveOrUpdate = MSG_ANADIR;
    } else {
      this.textSaveOrUpdate = MSG_ACEPTAR;
    }
    this.translate.get(
      MODELO_EJECUCION_TIPO_HITO_KEY,
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

  private setupI18N(): void {
    this.translate.get(
      MODELO_EJECUCION_TIPO_HITO_PROYECTOS_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamProyectosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      MODELO_EJECUCION_TIPO_HITO_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected getDatosForm(): IModeloTipoHito {
    const modeloTipoHito = this.data.modeloTipoHito;
    const disponible = this.formGroup.controls.disponible as FormGroup;
    modeloTipoHito.tipoHito = this.formGroup.get('tipoHito').value;
    modeloTipoHito.convocatoria = disponible.get('convocatoria').value;
    modeloTipoHito.proyecto = disponible.get('proyecto').value;
    modeloTipoHito.solicitud = disponible.get('solicitud').value;
    return modeloTipoHito;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoHito: new FormControl({
        value: this.data.modeloTipoHito?.tipoHito,
        disabled: this.data.modeloTipoHito.tipoHito
      }, Validators.required),
      disponible: new FormGroup({
        convocatoria: new FormControl(this.data.modeloTipoHito?.convocatoria),
        proyecto: new FormControl(this.data.modeloTipoHito?.proyecto),
        solicitud: new FormControl(this.data.modeloTipoHito?.solicitud)
      }, [requiredChecked(1)]),
    });
    return formGroup;
  }

}
