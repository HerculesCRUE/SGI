import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IInformePatentabilidad } from '@core/models/pii/informe-patentabilidad';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const INFORME_FECHA_INFORME_KEY = marker('pii.invencion-informe-patentabilidad.fecha-informe');
const INFORME_NOMBRE_KEY = marker('pii.invencion-informe-patentabilidad.nombre');
const INFORME_FICHERO_KEY = marker('pii.invencion-informe-patentabilidad.fichero');
const INFORME_RESULTADO_KEY = marker('pii.invencion-informe-patentabilidad.resultado');
const INFORME_ENTIDAD_CREADORA_KEY = marker('pii.invencion-informe-patentabilidad.entidad-creadora');
const INFORME_CONTACTO_ENTIDAD_KEY = marker('pii.invencion-informe-patentabilidad.contacto-entidad');
const INFORME_CONTACTO_EXAMINADOR_KEY = marker('pii.invencion-informe-patentabilidad.contacto-examinador');
const INFORME_COMENTARIOS_KEY = marker('pii.invencion-informe-patentabilidad.comentarios');
const INFORME_KEY = marker('pii.invencion-informe-patentabilidad');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_UPLOAD_ERROR = marker('pii.invencion-informe-patentabilidad.file.upload.error');

export interface IInformePatentabilidadModalData {
  informePatentabilidad: IInformePatentabilidad;
  readonly: boolean;
}

@Component({
  selector: 'sgi-informe-patentabilidad-modal',
  templateUrl: './informe-patentabilidad-modal.component.html',
  styleUrls: ['./informe-patentabilidad-modal.component.scss']
})
export class InformePatentabilidadModalComponent extends DialogFormComponent<IInformePatentabilidad> implements OnInit {

  msgParamFechaInformeEntity = {};
  msgParamNombreEntity = {};
  msgParamFicheroEntity = {};
  msgParamResultadoEntity = {};
  msgParamEntidadCreaddoraEntity = {};
  msgParamContactoEntidadEntity = {};
  msgParamContactoExaminadorEntity = {};
  msgParamComentariosEntity = {};

  textSaveOrUpdate: string;
  title: string;

  @ViewChild(SgiFileUploadComponent) private uploader: SgiFileUploadComponent;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get informePatentabilidad() {
    return this.data.informePatentabilidad;
  }

  constructor(
    matDialogRef: MatDialogRef<InformePatentabilidadModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IInformePatentabilidadModalData,
    private readonly translate: TranslateService,
  ) {
    super(matDialogRef, !!data.informePatentabilidad?.nombre);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      fecha: new FormControl(this.informePatentabilidad?.fecha, Validators.required),
      nombre: new FormControl(this.informePatentabilidad?.nombre, [Validators.required, Validators.maxLength(50)]),
      documento: new FormControl(this.informePatentabilidad?.documento, Validators.required),
      resultadoInformePatentabilidad: new FormControl(this.informePatentabilidad?.resultadoInformePatentabilidad, Validators.required),
      entidadCreadora: new FormControl(this.informePatentabilidad?.entidadCreadora, Validators.required),
      contactoEntidadCreadora: new FormControl(this.informePatentabilidad?.contactoEntidadCreadora, Validators.maxLength(50)),
      contactoExaminador: new FormControl(this.informePatentabilidad?.contactoExaminador, [Validators.required, Validators.maxLength(50)]),
      comentarios: new FormControl(this.informePatentabilidad?.comentarios, Validators.maxLength(250)),
    });

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  protected getValue(): IInformePatentabilidad {
    this.informePatentabilidad.fecha = this.formGroup.controls.fecha.value;
    this.informePatentabilidad.nombre = this.formGroup.controls.nombre.value;
    this.informePatentabilidad.documento = this.formGroup.controls.documento.value;
    this.informePatentabilidad.resultadoInformePatentabilidad = this.formGroup.controls.resultadoInformePatentabilidad.value;
    this.informePatentabilidad.entidadCreadora = this.formGroup.controls.entidadCreadora.value;
    this.informePatentabilidad.contactoEntidadCreadora = this.formGroup.controls.contactoEntidadCreadora.value;
    this.informePatentabilidad.contactoExaminador = this.formGroup.controls.contactoExaminador.value;
    this.informePatentabilidad.comentarios = this.formGroup.controls.comentarios.value;

    return this.informePatentabilidad;
  }

  doAction(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      this.uploader.uploadSelection().subscribe(
        () => {
          this.close(this.getValue());
        });
    }
  }

  onUploadProgress(event: UploadEvent) {
    switch (event.status) {
      case 'end':
        break;
      case 'error':
        this.pushProblems(new SgiError(MSG_UPLOAD_ERROR));
        break;
    }
  }

  private setupI18N(): void {
    this.translate.get(
      INFORME_FECHA_INFORME_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInformeEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INFORME_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INFORME_FICHERO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFicheroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INFORME_RESULTADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamResultadoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INFORME_ENTIDAD_CREADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntidadCreaddoraEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INFORME_CONTACTO_ENTIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamContactoEntidadEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INFORME_CONTACTO_EXAMINADOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamContactoExaminadorEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INFORME_COMENTARIOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentariosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    if (this.data.informePatentabilidad?.nombre) {
      this.translate.get(
        INFORME_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.translate.get(
        INFORME_KEY,
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
}
