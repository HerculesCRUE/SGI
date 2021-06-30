import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const TIPO_DOCUMENTO_KEY = marker('csp.tipo-documento');
const TIPO_DOCUMENTO_NOMBRE_KEY = marker('csp.tipo-documento.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');
@Component({
  templateUrl: './tipo-documento-modal.component.html',
  styleUrls: ['./tipo-documento-modal.component.scss']
})
export class TipoDocumentoModalComponent extends BaseModalComponent<ITipoDocumento, TipoDocumentoModalComponent> implements OnInit {

  textSaveOrUpdate: string;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<TipoDocumentoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public tipoDocumento: ITipoDocumento,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, tipoDocumento);
    if (tipoDocumento.id) {
      this.tipoDocumento = { ...tipoDocumento };
      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.tipoDocumento = { activo: true } as ITipoDocumento;
      this.textSaveOrUpdate = MSG_ANADIR;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_DOCUMENTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.tipoDocumento.nombre) {
      this.translate.get(
        TIPO_DOCUMENTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_DOCUMENTO_KEY,
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



  protected getDatosForm(): ITipoDocumento {
    const tipoDocumento = this.tipoDocumento;
    tipoDocumento.nombre = this.formGroup.get('nombre').value;
    tipoDocumento.descripcion = this.formGroup.get('descripcion').value;
    return tipoDocumento;
  }

  protected getFormGroup(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(this.tipoDocumento?.nombre),
      descripcion: new FormControl(this.tipoDocumento?.descripcion)
    });
  }
}
