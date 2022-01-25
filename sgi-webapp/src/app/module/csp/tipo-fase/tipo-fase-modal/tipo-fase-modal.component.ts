import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { TipoFaseService } from '@core/services/csp/tipo-fase.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_FASE_KEY = marker('csp.tipo-fase');
const TIPO_FASE_NOMBRE_KEY = marker('csp.tipo-fase.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-tipo-fase-modal',
  templateUrl: './tipo-fase-modal.component.html',
  styleUrls: ['./tipo-fase-modal.component.scss']
})
export class TipoFaseModalComponent extends DialogActionComponent<ITipoFase, ITipoFase> implements OnInit, OnDestroy {

  private readonly tipoFase: ITipoFase;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    matDialogRef: MatDialogRef<TipoFaseModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoFase,
    private readonly tipoFaseService: TipoFaseService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.tipoFase = { ...data };
    } else {
      this.tipoFase = { activo: true } as ITipoFase;
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

    if (this.isEdit()) {
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

  protected getValue(): ITipoFase {
    this.tipoFase.nombre = this.formGroup.controls.nombre.value;
    this.tipoFase.descripcion = this.formGroup.controls.descripcion.value;
    return this.tipoFase;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoFase?.nombre ?? '', Validators.required),
      descripcion: new FormControl(this.tipoFase?.descripcion ?? '')
    });

    return formGroup;
  }

  protected saveOrUpdate(): Observable<ITipoFase> {
    const tipoFase = this.getValue();
    return this.isEdit() ? this.tipoFaseService.update(tipoFase.id, tipoFase) :
      this.tipoFaseService.create(tipoFase);
  }
}
