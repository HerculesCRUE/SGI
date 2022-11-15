import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDocumento } from '@core/models/sgdoc/documento';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { DateTime } from 'luxon';

const MSG_ACEPTAR = marker('btn.ok');
const DOCUMENTO_KEY = marker('csp.solicitud.solicitud-rrhh.requisitos.documento');

export interface SolicitudRrhhAcreditarCategoriaProfesionalPublicModalData {
  categoriaProfesional: ICategoriaProfesional;
  fechaMaximaObtencion: DateTime;
  fechaMinimaObtencion: DateTime;
  documento: IDocumento;
}

@Component({
  templateUrl: './solicitud-rrhh-acreditar-categoria-profesional-public-modal.component.html',
  styleUrls: ['./solicitud-rrhh-acreditar-categoria-profesional-public-modal.component.scss']
})
export class SolicitudRrhhAcreditarCategoriaProfesionalPublicModalComponent
  extends DialogFormComponent<SolicitudRrhhAcreditarCategoriaProfesionalPublicModalData>
  implements OnInit, OnDestroy {

  textSaveOrUpdate: string;

  msgParamDocumentoEntity = {};

  uploading = false;

  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<SolicitudRrhhAcreditarCategoriaProfesionalPublicModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudRrhhAcreditarCategoriaProfesionalPublicModalData,
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


  protected getValue(): SolicitudRrhhAcreditarCategoriaProfesionalPublicModalData {
    this.data.documento = this.formGroup.controls.documento.value;

    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      categoriaProfesional: new FormControl({ value: this.data.categoriaProfesional.nombre, disabled: true }),
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
