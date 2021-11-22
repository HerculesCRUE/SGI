import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoFinanciacion } from '@core/models/csp/tipos-configuracion';
import { TipoFinanciacionService } from '@core/services/csp/tipo-financiacion.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_FINANCIACION_KEY = marker('csp.tipo-financiacion');
const TIPO_FINANCIACION_NOMBRE_KEY = marker('csp.tipo-financiacion.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-tipo-financiacion-modal',
  templateUrl: './tipo-financiacion-modal.component.html',
  styleUrls: ['./tipo-financiacion-modal.component.scss']
})
export class TipoFinanciacionModalComponent
  extends DialogActionComponent<ITipoFinanciacion, ITipoFinanciacion> implements OnInit, OnDestroy {

  private readonly tipoFinanciacion: ITipoFinanciacion;
  msgParamNombreEntity = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<TipoFinanciacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoFinanciacion,
    private readonly tipoFinanciacionService: TipoFinanciacionService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.tipoFinanciacion = { ...data };
    } else {
      this.tipoFinanciacion = { activo: true } as ITipoFinanciacion;
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

    if (this.isEdit()) {
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

  protected getValue(): ITipoFinanciacion {
    this.tipoFinanciacion.nombre = this.formGroup.controls.nombre.value;
    this.tipoFinanciacion.descripcion = this.formGroup.controls.descripcion.value;
    return this.tipoFinanciacion;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoFinanciacion?.nombre ?? '', Validators.required),
      descripcion: new FormControl(this.tipoFinanciacion?.descripcion ?? ''),
    });

    return formGroup;
  }

  protected saveOrUpdate(): Observable<ITipoFinanciacion> {
    const tipoFinanciacion = this.getValue();
    return this.isEdit() ? this.tipoFinanciacionService.update(tipoFinanciacion.id, tipoFinanciacion) :
      this.tipoFinanciacionService.create(tipoFinanciacion);
  }
}
