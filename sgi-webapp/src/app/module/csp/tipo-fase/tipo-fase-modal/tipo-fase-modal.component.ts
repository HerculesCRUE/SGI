import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const TIPO_FASE_KEY = marker('csp.tipo-fase');
const TIPO_FASE_NOMBRE_KEY = marker('csp.tipo-fase.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-tipo-fase-modal',
  templateUrl: './tipo-fase-modal.component.html',
  styleUrls: ['./tipo-fase-modal.component.scss']
})
export class TipoFaseModalComponent extends
  BaseModalComponent<ITipoFase, TipoFaseModalComponent> implements OnInit {
  fxLayoutProperties: FxLayoutProperties;

  textSaveOrUpdate: string;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<TipoFaseModalComponent>,
    @Inject(MAT_DIALOG_DATA) public tipoFase: ITipoFase,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, tipoFase);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    if (tipoFase.id) {
      this.tipoFase = { ...tipoFase };
      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.tipoFase = { activo: true } as ITipoFase;
      this.textSaveOrUpdate = MSG_ANADIR;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_FASE_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.tipoFase.nombre) {
      this.translate.get(
        TIPO_FASE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_FASE_KEY,
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

  protected getDatosForm(): ITipoFase {
    this.tipoFase.nombre = this.formGroup.controls.nombre.value;
    this.tipoFase.descripcion = this.formGroup.controls.descripcion.value;
    return this.tipoFase;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoFase?.nombre),
      descripcion: new FormControl(this.tipoFase?.descripcion)
    });

    return formGroup;
  }
}

