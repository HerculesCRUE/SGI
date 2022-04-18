import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { IResultadoInformePatentibilidad } from '@core/models/pii/resultado-informe-patentabilidad';
import { ResultadoInformePatentabilidadService } from '@core/services/pii/resultado-informe-patentabilidad/resultado-informe-patentabilidad.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const RESULTADO_INFORME_PATENTABILIDAD_KEY = marker('pii.resultado-informe-patentabilidad');
const RESULTADO_INFORME_NOMBRE_KEY = marker('pii.resultado-informe-patentabilidad.nombre');
const RESULTADO_INFORME_DESCRIPCION_KEY = marker('pii.resultado-informe-patentabilidad.descripcion');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-resultado-informe-patentabilidad-modal',
  templateUrl: './resultado-informe-patentabilidad-modal.component.html',
  styleUrls: ['./resultado-informe-patentabilidad-modal.component.scss']
})
export class ResultadoInformePatentabilidadModalComponent
  extends DialogActionComponent<IResultadoInformePatentibilidad> implements OnInit, OnDestroy {

  private readonly resultadoInformePatentibilidad: IResultadoInformePatentibilidad;
  msgParamNombreEntity = {};
  msgParamDescripcionEntity = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<ResultadoInformePatentabilidadModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: IResultadoInformePatentibilidad,
    private readonly resultadoInformePatentabilidadService: ResultadoInformePatentabilidadService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.resultadoInformePatentibilidad = { ...data };
    } else {
      this.resultadoInformePatentibilidad = { activo: true } as IResultadoInformePatentibilidad;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      RESULTADO_INFORME_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      RESULTADO_INFORME_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        RESULTADO_INFORME_PATENTABILIDAD_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        RESULTADO_INFORME_PATENTABILIDAD_KEY,
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

  protected getValue(): IResultadoInformePatentibilidad {
    this.resultadoInformePatentibilidad.nombre = this.formGroup.controls.nombre.value;
    this.resultadoInformePatentibilidad.descripcion = this.formGroup.controls.descripcion.value;
    return this.resultadoInformePatentibilidad;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.resultadoInformePatentibilidad?.nombre ?? '', [Validators.required, Validators.maxLength(50)]),
      descripcion: new FormControl(this.resultadoInformePatentibilidad?.descripcion ?? '', [Validators.required, Validators.maxLength(250)]),
    });
    return formGroup;
  }

  protected saveOrUpdate(): Observable<IResultadoInformePatentibilidad> {
    const resultadoInformePatentabilidad = this.getValue();
    return this.isEdit()
      ? this.resultadoInformePatentabilidadService.update(resultadoInformePatentabilidad.id, resultadoInformePatentabilidad)
      : this.resultadoInformePatentabilidadService.create(resultadoInformePatentabilidad);
  }

}
