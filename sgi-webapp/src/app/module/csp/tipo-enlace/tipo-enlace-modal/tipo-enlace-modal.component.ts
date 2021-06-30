import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const TIPO_ENLACE_KEY = marker('csp.tipo-enlace');
const TIPO_ENLACE_NOMBRE_KEY = marker('csp.tipo-enlace.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './tipo-enlace-modal.component.html',
  styleUrls: ['./tipo-enlace-modal.component.scss']
})
export class TipoEnlaceModalComponent extends
  BaseModalComponent<ITipoEnlace, TipoEnlaceModalComponent> implements OnInit {
  fxLayoutProperties: FxLayoutProperties;

  textSaveOrUpdate: string;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<TipoEnlaceModalComponent>,
    @Inject(MAT_DIALOG_DATA) public tipoEnlace: ITipoEnlace,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, tipoEnlace);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    if (tipoEnlace.id) {
      this.tipoEnlace = { ...tipoEnlace };
      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.tipoEnlace = { activo: true } as ITipoEnlace;
      this.textSaveOrUpdate = MSG_ANADIR;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_ENLACE_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.tipoEnlace.nombre) {
      this.translate.get(
        TIPO_ENLACE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_ENLACE_KEY,
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

  protected getDatosForm(): ITipoEnlace {
    this.tipoEnlace.nombre = this.formGroup.controls.nombre.value;
    this.tipoEnlace.descripcion = this.formGroup.controls.descripcion.value;
    return this.tipoEnlace;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoEnlace?.nombre),
      descripcion: new FormControl(this.tipoEnlace?.descripcion)
    });

    return formGroup;
  }

}
