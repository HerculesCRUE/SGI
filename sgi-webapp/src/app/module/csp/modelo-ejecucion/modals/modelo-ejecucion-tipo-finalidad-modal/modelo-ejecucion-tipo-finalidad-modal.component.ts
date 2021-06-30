import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloTipoFinalidad } from '@core/models/csp/modelo-tipo-finalidad';
import { ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { TipoFinalidadService } from '@core/services/csp/tipo-finalidad.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

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
export class ModeloEjecucionTipoFinalidadModalComponent extends
  BaseModalComponent<IModeloTipoFinalidad, ModeloEjecucionTipoFinalidadModalComponent> implements OnInit {

  tipoFinalidad$: Observable<ITipoFinalidad[]>;
  title: string;
  msgParamTipoEntiy = {};

  textSaveOrUpdate: string;

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ModeloEjecucionTipoFinalidadModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ModeloEjecucionTipoFinalidadModalData,
    readonly tipoFinalidadService: TipoFinalidadService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data.modeloTipoFinalidad);
    this.textSaveOrUpdate = this.data.modeloTipoFinalidad?.tipoFinalidad ? MSG_ACEPTAR : MSG_ANADIR;

    this.tipoFinalidad$ = tipoFinalidadService.findAll().pipe(
      map((response) => {
        return response.items.filter(tipoFinalidad => {
          return !this.data.tipoFinalidades.some(currentTipo => currentTipo.id === tipoFinalidad.id);
        });
      })
    );
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

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoFinalidad: new FormControl(this.data.modeloTipoFinalidad?.tipoFinalidad, Validators.required)
    });
    return formGroup;
  }

  protected getDatosForm(): IModeloTipoFinalidad {
    const modeloTipoFinalidad = this.data.modeloTipoFinalidad;
    modeloTipoFinalidad.tipoFinalidad = this.formGroup.get('tipoFinalidad').value;
    return modeloTipoFinalidad;
  }
}
