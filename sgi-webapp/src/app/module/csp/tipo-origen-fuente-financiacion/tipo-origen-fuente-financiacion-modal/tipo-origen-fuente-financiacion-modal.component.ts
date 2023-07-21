import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipos-configuracion';
import { TipoOrigenFuenteFinanciacionService } from '@core/services/csp/tipo-origen-fuente-financiacion/tipo-origen-fuente-financiacion.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_FASE_KEY = marker('csp.tipo-origen-fuente-financiacion');
const TIPO_FASE_NOMBRE_KEY = marker('csp.tipo-origen-fuente-financiacion.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-tipo-origen-fuente-financiacion-modal',
  templateUrl: './tipo-origen-fuente-financiacion-modal.component.html',
  styleUrls: ['./tipo-origen-fuente-financiacion-modal.component.scss']
})
export class TipoOrigenFuenteFinanciacionModalComponent extends DialogActionComponent<ITipoOrigenFuenteFinanciacion> implements OnInit, OnDestroy {

  private readonly tipoOrigenFuenteFinanciacion: ITipoOrigenFuenteFinanciacion;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    matDialogRef: MatDialogRef<TipoOrigenFuenteFinanciacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoOrigenFuenteFinanciacion,
    private readonly tipoOrigenFuenteFinanciacionService: TipoOrigenFuenteFinanciacionService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.tipoOrigenFuenteFinanciacion = { ...data };
    } else {
      this.tipoOrigenFuenteFinanciacion = { activo: true } as ITipoOrigenFuenteFinanciacion;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.matDialogRef.updateSize('30vw');
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

  protected getValue(): ITipoOrigenFuenteFinanciacion {
    this.tipoOrigenFuenteFinanciacion.nombre = this.formGroup.controls.nombre.value;
    return this.tipoOrigenFuenteFinanciacion;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoOrigenFuenteFinanciacion?.nombre ?? '', Validators.required)
    });

    return formGroup;
  }

  protected saveOrUpdate(): Observable<ITipoOrigenFuenteFinanciacion> {
    const tipoFase = this.getValue();
    return this.isEdit() ? this.tipoOrigenFuenteFinanciacionService.update(tipoFase.id, tipoFase) :
      this.tipoOrigenFuenteFinanciacionService.create(tipoFase);
  }
}
