import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDocumento } from '@core/models/sgdoc/documento';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { DateTime } from 'luxon';

const MSG_ACEPTAR = marker('btn.ok');
const DOCUMENTO_KEY = marker('csp.solicitud.solicitud-rrhh.requisitos.documento');

export interface SolicitudRrhhAcreditarNivelAcademicoModalData {
  nivelAcademico: INivelAcademico;
  fechaMaximaObtencion: DateTime;
  fechaMinimaObtencion: DateTime;
  documento: IDocumento;
}

@Component({
  templateUrl: './solicitud-rrhh-acreditar-nivel-academico-modal.component.html',
  styleUrls: ['./solicitud-rrhh-acreditar-nivel-academico-modal.component.scss']
})
export class SolicitudRrhhAcreditarNivelAcademicoModalComponent
  extends DialogFormComponent<SolicitudRrhhAcreditarNivelAcademicoModalData>
  implements OnInit, OnDestroy {

  textSaveOrUpdate: string;

  msgParamDocumentoEntity = {};

  uploading = false;

  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<SolicitudRrhhAcreditarNivelAcademicoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudRrhhAcreditarNivelAcademicoModalData,
    private readonly translate: TranslateService,
  ) {
    super(matDialogRef, true);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.textSaveOrUpdate = MSG_ACEPTAR;
  }

  doAction(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      this.uploader.uploadSelection().subscribe(
        () => this.close(this.getValue()),
        this.processError
      );
    }
  }


  private setupI18N(): void {
    this.translate.get(
      DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDocumentoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }


  protected getValue(): SolicitudRrhhAcreditarNivelAcademicoModalData {
    this.data.documento = this.formGroup.controls.documento.value;

    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      nivelAcademico: new FormControl({ value: this.data.nivelAcademico.nombre, disabled: true }),
      fechaMinima: new FormControl({ value: this.data.fechaMinimaObtencion, disabled: true }),
      fechaMaxima: new FormControl({ value: this.data.fechaMaximaObtencion, disabled: true }),
      documento: new FormControl(this.data.documento, Validators.required)
    });

  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  onUploadProgress(event: UploadEvent) {
    switch (event.status) {
      case 'start':
        this.uploading = true;
        break;
      case 'end':
        this.uploading = false;
        break;
      case 'error':
        this.uploading = false;
        break;
    }
  }

}
