import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProcedimientoDocumento } from '@core/models/pii/procedimiento-documento';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { map, switchMap } from 'rxjs/operators';

const PROCEDIMIENTO_DOCUMENTO_KEY = marker('pii.solicitud-proteccion.procedimiento-documento');
const PROCEDIMIENTO_DOCUMENTO_NOMBRE_KEY = marker('pii.solicitud-proteccion.procedimiento-documento.nombre');
const PROCEDIMIENTO_DOCUMENTO_FICHERO_KEY = marker('pii.solicitud-proteccion.procedimiento-documento.fichero');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const MSG_UPLOAD_SUCCESS = marker('msg.file.upload.success');
const MSG_UPLOAD_ERROR = marker('error.file.upload');
const MSG_ERROR_FORM_GROUP = marker('error.form-group');

@Component({
  selector: 'sgi-solicitud-proteccion-procedimiento-documento-modal',
  templateUrl: './solicitud-proteccion-procedimiento-documento-modal.component.html',
  styleUrls: ['./solicitud-proteccion-procedimiento-documento-modal.component.scss']
})
export class SolicitudProteccionProcedimientoDocumentoModalComponent extends
  BaseModalComponent<
  StatusWrapper<IProcedimientoDocumento>,
  SolicitudProteccionProcedimientoDocumentoModalComponent
  > implements OnInit, OnDestroy {

  uploading = false;
  @ViewChild('uploader') private uploader: SgiFileUploadComponent;


  public fxLayoutProperties: FxLayoutProperties;
  public procedimientoDocumento: IProcedimientoDocumento;
  public msgParamNombreEntity = {};
  public msgParamFicheroEntity = {};
  public title: string;
  public textSaveOrUpdate: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<SolicitudProteccionProcedimientoDocumentoModalComponent>,
    @Inject(MAT_DIALOG_DATA) procedimientoDocumento: StatusWrapper<IProcedimientoDocumento>,
    private translate: TranslateService,
    private documentoService: DocumentoService) {

    super(snackBarService, matDialogRef, procedimientoDocumento);

    this.initLayoutProperties();

    this.initProcedimientoDocumento(procedimientoDocumento);
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

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  protected getDatosForm(): StatusWrapper<IProcedimientoDocumento> {
    this.entity.value.nombre = this.formGroup.controls.nombre.value;
    this.entity.value.documento = this.formGroup.controls.fichero.value;
    return this.entity;
  }

  protected getFormGroup(): FormGroup {

    return new FormGroup({
      nombre: new FormControl(this.procedimientoDocumento?.nombre, [Validators.maxLength(250), Validators.required]),
      fichero: new FormControl(this.procedimientoDocumento?.documento, [Validators.required]),
    });
  }

  saveOrUpdate(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      this.uploader.uploadSelection().subscribe(
        () => {
          this.matDialogRef.close(this.getDatosForm());
        });
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
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

  private initLayoutProperties() {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
  }

  onUploadProgress(event: UploadEvent) {
    switch (event.status) {
      case 'start':
        this.uploading = true;
        break;
      case 'end':
        this.snackBarService.showSuccess(MSG_UPLOAD_SUCCESS);
        this.uploading = false;
        break;
      case 'error':
        this.snackBarService.showError(MSG_UPLOAD_ERROR);
        this.uploading = false;
        break;
    }
  }

}
