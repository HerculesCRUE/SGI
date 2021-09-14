import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IResultadoInformePatentibilidad } from '@core/models/pii/resultado-informe-patentabilidad';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const RESULTADO_INFORME_PATENTABILIDAD_KEY = marker('pii.resultado-informe-patentabilidad');
const RESULTADO_INFORME_NOMBRE_KEY = marker('pii.resultado-informe-patentabilidad.nombre');
const RESULTADO_INFORME_DESCRIPCION_KEY = marker('pii.resultado-informe-patentabilidad.descripcion');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

@Component({
  selector: 'sgi-resultado-informe-patentabilidad-modal',
  templateUrl: './resultado-informe-patentabilidad-modal.component.html',
  styleUrls: ['./resultado-informe-patentabilidad-modal.component.scss']
})
export class ResultadoInformePatentabilidadModalComponent
  extends BaseModalComponent<IResultadoInformePatentibilidad, ResultadoInformePatentabilidadModalComponent> implements OnInit, OnDestroy {
  resultadoInformePatentibilidad: IResultadoInformePatentibilidad;

  msgParamNombreEntity = {};
  msgParamDescripcionEntity = {};
  title: string;

  textSaveOrUpdate: string;

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ResultadoInformePatentabilidadModalComponent>,
    @Inject(MAT_DIALOG_DATA) resultadoInformePatentibilidad: IResultadoInformePatentibilidad,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, resultadoInformePatentibilidad);
    if (resultadoInformePatentibilidad) {
      this.resultadoInformePatentibilidad = { ...resultadoInformePatentibilidad };
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

    if (this.resultadoInformePatentibilidad.nombre) {

      this.translate.get(
        RESULTADO_INFORME_PATENTABILIDAD_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
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

      this.textSaveOrUpdate = MSG_ANADIR;
    }

  }

  protected getDatosForm(): IResultadoInformePatentibilidad {
    this.resultadoInformePatentibilidad.nombre = this.formGroup.controls.nombre.value;
    this.resultadoInformePatentibilidad.descripcion = this.formGroup.controls.descripcion.value;
    return this.resultadoInformePatentibilidad;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.resultadoInformePatentibilidad?.nombre, [Validators.maxLength(50)]),
      descripcion: new FormControl(this.resultadoInformePatentibilidad?.descripcion, [Validators.maxLength(250)]),
    });
    return formGroup;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }
}

