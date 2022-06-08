import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IProcedimientoDocumento } from '@core/models/pii/procedimiento-documento';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { switchMap } from 'rxjs/operators';

const PROCEDIMIENTO_DOCUMENTO_KEY = marker('pii.solicitud-proteccion.procedimiento-documento');
const PROCEDIMIENTO_DOCUMENTO_NOMBRE_KEY = marker('pii.solicitud-proteccion.procedimiento-documento.nombre');
const PROCEDIMIENTO_DOCUMENTO_FICHERO_KEY = marker('pii.solicitud-proteccion.procedimiento-documento.fichero');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const MSG_UPLOAD_ERROR = marker('error.file.upload');

@Component({
  selector: 'sgi-solicitud-proteccion-procedimiento-documento-modal',
  templateUrl: './solicitud-proteccion-procedimiento-documento-modal.component.html',
  styleUrls: ['./solicitud-proteccion-procedimiento-documento-modal.component.scss']
})
export class SolicitudProteccionProcedimientoDocumentoModalComponent
  extends DialogFormComponent<StatusWrapper<IProcedimientoDocumento>> implements OnInit {

  uploading = false;
  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  public procedimientoDocumento: IProcedimientoDocumento;
  public msgParamNombreEntity = {};
  public msgParamFicheroEntity = {};
  public title: string;
  public textSaveOrUpdate: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<SolicitudProteccionProcedimientoDocumentoModalComponent>,
    @Inject(MAT_DIALOG_DATA) private data: StatusWrapper<IProcedimientoDocumento>,
    private translate: TranslateService,
    private documentoService: DocumentoService) {

    super(matDialogRef, !!data.value.nombre);

    this.initProcedimientoDocumento(data);
  }

  private initProcedimientoDocumento(procedimientoDocumento: StatusWrapper<IProcedimientoDocumento>): void {
    this.procedimientoDocumento = procedimientoDocumento.value;

    if (this.procedimientoDocumento.documento?.documentoRef) {
      this.subscriptions.push(
        this.documentoService
          .getInfoFichero(this.procedimientoDocumento.documento.documentoRef)
          .subscribe(docInfo => this.procedimientoDocumento.documento = docInfo)
      );
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.disableFileUpload();
  }

  private disableFileUpload(): void {
    if (this.procedimientoDocumento?.documento?.documentoRef) {
      this.formGroup.controls?.fichero.disable();
    }
  }

  protected getValue(): StatusWrapper<IProcedimientoDocumento> {
    this.data.value.nombre = this.formGroup.controls.nombre.value;
    this.data.value.documento = this.formGroup.controls.fichero.value;
    return this.data;
  }

  protected buildFormGroup(): FormGroup {

    return new FormGroup({
      nombre: new FormControl(this.procedimientoDocumento?.nombre, [Validators.maxLength(250), Validators.required]),
      fichero: new FormControl(this.procedimientoDocumento?.documento, [Validators.required]),
    });
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

  private setupI18N(): void {
    this.translate.get(
      PROCEDIMIENTO_DOCUMENTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROCEDIMIENTO_DOCUMENTO_FICHERO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFicheroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.procedimientoDocumento.nombre) {

      this.translate.get(
        PROCEDIMIENTO_DOCUMENTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.translate.get(
        PROCEDIMIENTO_DOCUMENTO_KEY,
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

  onUploadProgress(event: UploadEvent) {
    switch (event.status) {
      case 'start':
        this.uploading = true;
        break;
      case 'end':
        this.uploading = false;
        break;
      case 'error':
        this.pushProblems(new SgiError(MSG_UPLOAD_ERROR));
        this.uploading = false;
        break;
    }
  }

}
