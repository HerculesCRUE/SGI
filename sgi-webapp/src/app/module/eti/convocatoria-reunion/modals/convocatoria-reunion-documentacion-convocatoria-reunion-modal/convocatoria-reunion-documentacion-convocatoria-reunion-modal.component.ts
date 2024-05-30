import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IDocumentacionConvocatoriaReunion } from '@core/models/eti/documentacion-convocatoria-reunion';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { switchMap } from 'rxjs/operators';

const TITLE_NEW_ENTITY = marker('title.new.entity');
const DOCUMENTO_KEY = marker('eti.convocatoria-reunion.documento');
const CONVOCATORIA_REUNION_DOCUMENTO_FICHERO_KEY = marker('eti.convocatoria-reunion.documento.fichero');
const CONVOCATORIA_REUNION_DOCUMENTO_NOMBRE_KEY = marker('eti.convocatoria-reunion.documento.nombre');
const MSG_UPLOAD_ERROR = marker('error.file.upload');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

export interface ConvocatoriaReunionDocumentacionConvocatoriaReunionModalData {
  convocatoriaReunionId: number;
  readonly: boolean;
  documentacion: IDocumentacionConvocatoriaReunion;
  nuevo: boolean;
}

@Component({
  templateUrl: './convocatoria-reunion-documentacion-convocatoria-reunion-modal.component.html',
  styleUrls: ['./convocatoria-reunion-documentacion-convocatoria-reunion-modal.component.scss']
})
export class ConvocatoriaReunionDocumentacionConvocatoriaReunionModalComponent extends DialogFormComponent<IDocumentacionConvocatoriaReunion> implements OnInit {

  uploading = false;
  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  title: string;
  textSaveOrUpdate: string;
  msgParamTipoEntity = {};
  msgParamFicheroEntity = {};
  msgParamNombreEntity = {};

  constructor(
    matDialogRef: MatDialogRef<ConvocatoriaReunionDocumentacionConvocatoriaReunionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: ConvocatoriaReunionDocumentacionConvocatoriaReunionModalData,
    private readonly translate: TranslateService,
    private readonly documentoService: DocumentoService
  ) {
    super(matDialogRef, false);

    this.textSaveOrUpdate = this.data?.nuevo ? MSG_ANADIR : MSG_ACEPTAR;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    if (this.data?.nuevo) {
      this.translate.get(
        DOCUMENTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        DOCUMENTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    }

    this.translate.get(
      CONVOCATORIA_REUNION_DOCUMENTO_FICHERO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFicheroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_REUNION_DOCUMENTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  /**
   * Inicializa formulario de añadir documentació.
   */
  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.data?.documentacion?.nombre, Validators.required),
      fichero: new FormControl(null, Validators.required),
    });

    if (this.data?.documentacion?.documento?.documentoRef) {
      this.getFichero(this.data?.documentacion?.documento?.documentoRef, formGroup);
    }

    if (this.data?.documentacion?.id) {
      formGroup.controls.fichero.disable();
    }

    return formGroup;
  }

  protected getValue(): IDocumentacionConvocatoriaReunion {
    const documentacionConvocatoriaReunion: IDocumentacionConvocatoriaReunion = {} as IDocumentacionConvocatoriaReunion;
    documentacionConvocatoriaReunion.nombre = this.formGroup.controls.nombre.value;
    if (this.data?.documentacion?.id) {
      documentacionConvocatoriaReunion.id = this.data?.documentacion?.id;
    }
    documentacionConvocatoriaReunion.documento = this.formGroup.controls.fichero.value;
    documentacionConvocatoriaReunion.convocatoriaReunion = { id: this.data.convocatoriaReunionId } as IConvocatoriaReunion;
    return documentacionConvocatoriaReunion;
  }

  doAction(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      this.clearProblems();
      this.uploader.uploadSelection().subscribe(
        () => {
          this.close(this.getValue());
        }
      );
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

  private getFichero(documentoRef: string, formGroup: FormGroup): void {
    this.subscriptions.push(this.documentoService.getInfoFichero(documentoRef).subscribe((info) => {
      formGroup.controls.fichero.setValue(info);
    }));
  }
}
