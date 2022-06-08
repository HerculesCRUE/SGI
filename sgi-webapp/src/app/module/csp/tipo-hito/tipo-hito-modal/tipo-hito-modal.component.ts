import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { TipoHitoService } from '@core/services/csp/tipo-hito.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_HITO_KEY = marker('csp.tipo-hito');
const TIPO_HITO_NOMBRE_KEY = marker('csp.tipo-hito.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-tipo-hito-modal',
  templateUrl: './tipo-hito-modal.component.html',
  styleUrls: ['./tipo-hito-modal.component.scss']
})
export class TipoHitoModalComponent extends DialogActionComponent<ITipoHito> implements OnInit, OnDestroy {

  private readonly tipoHito: ITipoHito;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    matDialogRef: MatDialogRef<TipoHitoModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoHito,
    private readonly tipoHitoService: TipoHitoService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.tipoHito = { ...data };
    } else {
      this.tipoHito = { activo: true } as ITipoHito;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.matDialogRef.updateSize('30vw');
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_HITO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
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

  protected getValue(): ITipoHito {
    this.tipoHito.nombre = this.formGroup.controls.nombre.value;
    this.tipoHito.descripcion = this.formGroup.controls.descripcion.value;
    return this.tipoHito;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoHito?.nombre ?? '', Validators.required),
      descripcion: new FormControl(this.tipoHito?.descripcion ?? '')
    });
    return formGroup;
  }

  protected saveOrUpdate(): Observable<ITipoHito> {
    const tipoHito = this.getValue();
    return this.isEdit() ? this.tipoHitoService.update(tipoHito.id, tipoHito) :
      this.tipoHitoService.create(tipoHito);
  }
}
