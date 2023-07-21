import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';
import { TipoFacturacionService } from '@core/services/csp/tipo-facturacion/tipo-facturacion.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_FACTURACION_KEY = marker('csp.tipo-facturacion');
const TIPO_FACTURACION_NOMBRE_KEY = marker('csp.tipo-facturacion.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-tipo-facturacion-modal',
  templateUrl: './tipo-facturacion-modal.component.html',
  styleUrls: ['./tipo-facturacion-modal.component.scss']
})
export class TipoFacturacionModalComponent extends DialogActionComponent<ITipoFacturacion> implements OnInit, OnDestroy {

  private readonly tipoFacturacion: ITipoFacturacion;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    matDialogRef: MatDialogRef<TipoFacturacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoFacturacion,
    private readonly tipoFacturacionService: TipoFacturacionService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);
    if (this.isEdit()) {
      this.tipoFacturacion = { ...data };
    } else {
      this.tipoFacturacion = { activo: true } as ITipoFacturacion;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.matDialogRef.updateSize('30vw');
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_FACTURACION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        TIPO_FACTURACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_FACTURACION_KEY,
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

  protected getValue(): ITipoFacturacion {
    const tipoFacturacion = this.tipoFacturacion;
    tipoFacturacion.nombre = this.formGroup.get('nombre').value;
    tipoFacturacion.incluirEnComunicado = this.formGroup.get('incluirEnComunicado').value;
    return tipoFacturacion;
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(this.tipoFacturacion?.nombre ?? '', Validators.required),
      incluirEnComunicado: new FormControl(this.tipoFacturacion?.incluirEnComunicado ?? false)
    });
  }

  protected saveOrUpdate(): Observable<ITipoFacturacion> {
    const tipoFacturacion = this.getValue();
    return this.isEdit() ? this.tipoFacturacionService.update(tipoFacturacion.id, tipoFacturacion) :
      this.tipoFacturacionService.create(tipoFacturacion);
  }
}
