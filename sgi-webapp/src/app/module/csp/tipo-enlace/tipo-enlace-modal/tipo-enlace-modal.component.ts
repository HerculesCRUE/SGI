import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { TipoEnlaceService } from '@core/services/csp/tipo-enlace.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_ENLACE_KEY = marker('csp.tipo-enlace');
const TIPO_ENLACE_NOMBRE_KEY = marker('csp.tipo-enlace.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './tipo-enlace-modal.component.html',
  styleUrls: ['./tipo-enlace-modal.component.scss']
})
export class TipoEnlaceModalComponent extends DialogActionComponent<ITipoEnlace, ITipoEnlace> implements OnInit, OnDestroy {

  private readonly tipoEnlace: ITipoEnlace;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    matDialogRef: MatDialogRef<TipoEnlaceModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoEnlace,
    private readonly tipoEnlaceService: TipoEnlaceService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.tipoEnlace = { ...data };
    } else {
      this.tipoEnlace = { activo: true } as ITipoEnlace;
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

    if (this.isEdit()) {
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

  protected getValue(): ITipoEnlace {
    this.tipoEnlace.nombre = this.formGroup.controls.nombre.value;
    this.tipoEnlace.descripcion = this.formGroup.controls.descripcion.value;
    return this.tipoEnlace;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoEnlace?.nombre ?? '', Validators.required),
      descripcion: new FormControl(this.tipoEnlace?.descripcion ?? '')
    });

    return formGroup;
  }

  protected saveOrUpdate(): Observable<ITipoEnlace> {
    const tipoEnlace = this.getValue();
    return this.isEdit() ? this.tipoEnlaceService.update(tipoEnlace.id, tipoEnlace) :
      this.tipoEnlaceService.create(tipoEnlace);
  }
}
