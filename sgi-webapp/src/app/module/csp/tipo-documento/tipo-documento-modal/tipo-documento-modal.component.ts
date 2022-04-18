import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { TipoDocumentoService } from '@core/services/csp/tipo-documento.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_DOCUMENTO_KEY = marker('csp.tipo-documento');
const TIPO_DOCUMENTO_NOMBRE_KEY = marker('csp.tipo-documento.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './tipo-documento-modal.component.html',
  styleUrls: ['./tipo-documento-modal.component.scss']
})
export class TipoDocumentoModalComponent extends DialogActionComponent<ITipoDocumento> implements OnInit, OnDestroy {

  private readonly tipoDocumento: ITipoDocumento;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    matDialogRef: MatDialogRef<TipoDocumentoModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoDocumento,
    private readonly tipoDocumentoService: TipoDocumentoService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);
    if (this.isEdit()) {
      this.tipoDocumento = { ...data };
    } else {
      this.tipoDocumento = { activo: true } as ITipoDocumento;
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

    if (this.isEdit()) {
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

  protected getValue(): ITipoDocumento {
    const tipoDocumento = this.tipoDocumento;
    tipoDocumento.nombre = this.formGroup.get('nombre').value;
    tipoDocumento.descripcion = this.formGroup.get('descripcion').value;
    return tipoDocumento;
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(this.tipoDocumento?.nombre ?? '', Validators.required),
      descripcion: new FormControl(this.tipoDocumento?.descripcion ?? '')
    });
  }

  protected saveOrUpdate(): Observable<ITipoDocumento> {
    const tipoDocumento = this.getValue();
    return this.isEdit() ? this.tipoDocumentoService.update(tipoDocumento.id, tipoDocumento) :
      this.tipoDocumentoService.create(tipoDocumento);
  }
}
