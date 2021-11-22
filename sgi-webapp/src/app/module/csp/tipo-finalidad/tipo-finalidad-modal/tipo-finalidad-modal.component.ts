import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { TipoFinalidadService } from '@core/services/csp/tipo-finalidad.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_FINALIDAD_KEY = marker('csp.tipo-finalidad');
const TIPO_FINALIDAD_NOMBRE_KEY = marker('csp.tipo-finalidad.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './tipo-finalidad-modal.component.html',
  styleUrls: ['./tipo-finalidad-modal.component.scss']
})
export class TipoFinalidadModalComponent extends DialogActionComponent<ITipoFinalidad, ITipoFinalidad> implements OnInit, OnDestroy {

  private readonly tipoFinalidad: ITipoFinalidad;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    matDialogRef: MatDialogRef<TipoFinalidadModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoFinalidad,
    private readonly tipoFinalidadService: TipoFinalidadService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);
    if (this.isEdit()) {
      this.tipoFinalidad = { ...data };
    } else {
      this.tipoFinalidad = { activo: true } as ITipoFinalidad;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_FINALIDAD_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        TIPO_FINALIDAD_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_FINALIDAD_KEY,
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

  protected getValue(): ITipoFinalidad {
    const tipoFinalidad = this.tipoFinalidad;
    tipoFinalidad.nombre = this.formGroup.get('nombre').value;
    tipoFinalidad.descripcion = this.formGroup.get('descripcion').value;
    return tipoFinalidad;
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(this.tipoFinalidad?.nombre ?? '', Validators.required),
      descripcion: new FormControl(this.tipoFinalidad?.descripcion ?? '')
    });
  }

  protected saveOrUpdate(): Observable<ITipoFinalidad> {
    const tipoFinalidad = this.getValue();
    return this.isEdit() ? this.tipoFinalidadService.update(tipoFinalidad.id, tipoFinalidad) :
      this.tipoFinalidadService.create(tipoFinalidad);
  }
}
