import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const TIPO_HITO_KEY = marker('csp.tipo-hito');
const TIPO_HITO_NOMBRE_KEY = marker('csp.tipo-hito.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');
@Component({
  selector: 'sgi-tipo-hito-modal',
  templateUrl: './tipo-hito-modal.component.html',
  styleUrls: ['./tipo-hito-modal.component.scss']
})
export class TipoHitoModalComponent extends
  BaseModalComponent<ITipoHito, TipoHitoModalComponent> implements OnInit {
  fxLayoutProperties: FxLayoutProperties;
  textSaveOrUpdate: string;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<TipoHitoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public tipoHito: ITipoHito,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, tipoHito);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    if (tipoHito.id) {
      this.tipoHito = { ...tipoHito };
      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.tipoHito = { activo: true } as ITipoHito;
      this.textSaveOrUpdate = MSG_ANADIR;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_HITO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.tipoHito.nombre) {
      this.translate.get(
        TIPO_HITO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_HITO_KEY,
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

  protected getDatosForm(): ITipoHito {
    this.tipoHito.nombre = this.formGroup.controls.nombre.value;
    this.tipoHito.descripcion = this.formGroup.controls.descripcion.value;
    return this.tipoHito;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoHito?.nombre),
      descripcion: new FormControl(this.tipoHito?.descripcion)
    });
    return formGroup;
  }

}
