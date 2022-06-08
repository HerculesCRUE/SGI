import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IInvencionDocumento } from '@core/models/pii/invencion-documento';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { map, switchMap } from 'rxjs/operators';

const INVENCION_DOCUMENTO_KEY = marker('pii.invencion-documento');
const INVENCION_DOCUMENTO_NOMBRE_KEY = marker('pii.invencion-documento.nombre');
const INVENCION_DOCUMENTO_FICHERO_KEY = marker('pii.invencion-documento.fichero');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const MSG_UPLOAD_ERROR = marker('error.file.upload');

@Component({
  selector: 'sgi-invencion-documento-modal',
  templateUrl: './invencion-documento-modal.component.html',
  styleUrls: ['./invencion-documento-modal.component.scss']
})
export class InvencionDocumentoModalComponent extends DialogFormComponent<IInvencionDocumento> implements OnInit {

  uploading = false;
  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  public invencionDocumento: IInvencionDocumento;
  public msgParamNombreEntity = {};
  public msgParamFicheroEntity = {};
  public title: string;
  public textSaveOrUpdate: string;

  constructor(
    matDialogRef: MatDialogRef<InvencionDocumentoModalComponent>,
    @Inject(MAT_DIALOG_DATA) invencionDocumento: IInvencionDocumento,
    private translate: TranslateService,
    private documentoService: DocumentoService) {

    super(matDialogRef, !!invencionDocumento?.nombre);

    this.initInvencionDocumento(invencionDocumento);
  }

  private initInvencionDocumento(invencionDocumento: IInvencionDocumento): void {
    this.invencionDocumento = invencionDocumento ? { ...invencionDocumento } : {} as IInvencionDocumento;

    if (this.invencionDocumento.documento?.documentoRef) {
      this.subscriptions.push(
        this.documentoService.getInfoFichero(this.invencionDocumento.documento.documentoRef).pipe(
          map(docInfo => this.invencionDocumento.documento = docInfo)
        ).subscribe()
      );
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.disableFileUpload();
  }

  private disableFileUpload(): void {
    if (this.invencionDocumento?.documento?.documentoRef) {
      this.formGroup.controls?.fichero.disable();
    }
  }

  protected getValue(): IInvencionDocumento {
    return {
      ...this.invencionDocumento,
      nombre: this.formGroup.controls.nombre.value,
      documento: this.formGroup.controls.fichero.value
    };
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(this.invencionDocumento?.nombre, [Validators.maxLength(250), Validators.required]),
      fichero: new FormControl(this.invencionDocumento?.documento, [Validators.required]),
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
      INVENCION_DOCUMENTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INVENCION_DOCUMENTO_FICHERO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFicheroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.invencionDocumento.nombre) {

      this.translate.get(
        INVENCION_DOCUMENTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.translate.get(
        INVENCION_DOCUMENTO_KEY,
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
