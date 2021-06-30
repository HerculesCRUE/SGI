import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoFinanciacion } from '@core/models/csp/tipos-configuracion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const TIPO_FINANCIACION_KEY = marker('csp.tipo-financiacion');
const TIPO_FINANCIACION_NOMBRE_KEY = marker('csp.tipo-financiacion.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');
@Component({
  selector: 'sgi-tipo-financiacion-modal',
  templateUrl: './tipo-financiacion-modal.component.html',
  styleUrls: ['./tipo-financiacion-modal.component.scss']
})
export class TipoFinanciacionModalComponent extends
  BaseModalComponent<ITipoFinanciacion, TipoFinanciacionModalComponent> implements OnInit {
  fxLayoutProperties: FxLayoutProperties;
  public tipoFinanciacion: ITipoFinanciacion;
  textSaveOrUpdate: string;

  msgParamNombreEntity = {};
  title: string;

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<TipoFinanciacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) tipoFinanciacion: ITipoFinanciacion,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, tipoFinanciacion);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    if (tipoFinanciacion.id) {
      this.tipoFinanciacion = { ...tipoFinanciacion };
      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.tipoFinanciacion = { activo: true } as ITipoFinanciacion;
      this.textSaveOrUpdate = MSG_ANADIR;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_FINANCIACION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.tipoFinanciacion.nombre) {

      this.translate.get(
        TIPO_FINANCIACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_FINANCIACION_KEY,
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

  protected getDatosForm(): ITipoFinanciacion {
    this.tipoFinanciacion.nombre = this.formGroup.controls.nombre.value;
    this.tipoFinanciacion.descripcion = this.formGroup.controls.descripcion.value;
    return this.tipoFinanciacion;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoFinanciacion?.nombre),
      descripcion: new FormControl(this.tipoFinanciacion?.descripcion),
    });

    return formGroup;
  }
}
