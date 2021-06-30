import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloUnidad } from '@core/models/csp/modelo-unidad';
import { ITipoUnidadGestion } from '@core/models/csp/tipos-configuracion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const MODELO_EJECUCION_UNIDAD_GESTION_KEY = marker('csp.modelo-ejecucion-unidad-gestion');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

export interface IModeloEjecucionTipoUnidadModal {
  modeloTipoUnidad: IModeloUnidad;
  tipoUnidades: ITipoUnidadGestion[];
}

@Component({
  selector: 'sgi-modelo-ejecucion-tipo-unidad-gestion',
  templateUrl: './modelo-ejecucion-tipo-unidad-gestion-modal.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-unidad-gestion-modal.component.scss']
})
export class ModeloEjecucionTipoUnidadGestionModalComponent extends
  BaseModalComponent<IModeloUnidad, ModeloEjecucionTipoUnidadGestionModalComponent> implements OnInit {
  tipoUnidad$: Observable<ITipoUnidadGestion[]>;

  msgParamUnidadGestionEntiy = {};
  title: string;
  textSaveOrUpdate: string;

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ModeloEjecucionTipoUnidadGestionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IModeloEjecucionTipoUnidadModal,
    unidadGestionService: UnidadGestionService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data.modeloTipoUnidad);
    this.textSaveOrUpdate = this.data.modeloTipoUnidad?.unidadGestion ? MSG_ACEPTAR : MSG_ANADIR;

    this.tipoUnidad$ = unidadGestionService.findAll().pipe(
      map(response => {
        return response.items.filter(tipoUnidadGestion => {
          return !this.data.tipoUnidades.some(currentTipo => currentTipo.id === tipoUnidadGestion.id);
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
      MODELO_EJECUCION_UNIDAD_GESTION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamUnidadGestionEntiy = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      MODELO_EJECUCION_UNIDAD_GESTION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          TITLE_NEW_ENTITY,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.title = value);
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoUnidad: new FormControl(this.data.modeloTipoUnidad?.unidadGestion, Validators.required)
    });
    return formGroup;
  }

  protected getDatosForm(): IModeloUnidad {
    const modeloTipoUnidadGestion = this.data.modeloTipoUnidad;
    modeloTipoUnidadGestion.unidadGestion = this.formGroup.get('tipoUnidad').value;
    return modeloTipoUnidadGestion;
  }
}
