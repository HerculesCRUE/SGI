import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IComite } from '@core/models/eti/comite';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { resolveFormularioByTipoEvaluacionAndComite } from '@core/models/eti/formulario';
import { IMemoria } from '@core/models/eti/memoria';
import { ITipoDocumento } from '@core/models/eti/tipo-documento';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { TipoDocumentoService } from '@core/services/eti/tipo-documento.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TITLE_NEW_ENTITY = marker('title.new.entity');
const DOCUMENTO_KEY = marker('eti.memoria.documento');
const DOCUMENTO_TIPO_KEY = marker('eti.memoria.documento.tipo');
const PROYECTO_DOCUMENTO_FICHERO_KEY = marker('csp.proyecto-documento.fichero');
const PROYECTO_DOCUMENTO_NOMBRE_KEY = marker('csp.documento.nombre');
const MSG_UPLOAD_ERROR = marker('error.file.upload');

export interface MemoriaDocumentacionMemoriaModalData {
  memoriaId: number;
  tipoEvaluacion: TIPO_EVALUACION;
  showTipoDocumentos: boolean;
  comite: IComite;
}

@Component({
  templateUrl: './memoria-documentacion-memoria-modal.component.html',
  styleUrls: ['./memoria-documentacion-memoria-modal.component.scss']
})
export class MemoriaDocumentacionMemoriaModalComponent extends DialogFormComponent<IDocumentacionMemoria> implements OnInit {

  readonly tiposDocumento$: Observable<ITipoDocumento[]>;

  uploading = false;
  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  title: string;
  msgParamTipoEntity = {};
  msgParamFicheroEntity = {};
  msgParamNombreEntity = {};

  get TIPO_EVALUACION() {
    return TIPO_EVALUACION;
  }

  constructor(
    matDialogRef: MatDialogRef<MemoriaDocumentacionMemoriaModalComponent>,
    memoriaService: MemoriaService,
    @Inject(MAT_DIALOG_DATA) public readonly data: MemoriaDocumentacionMemoriaModalData,
    private readonly translate: TranslateService,
    private readonly tipoDocumentoService: TipoDocumentoService
  ) {
    super(matDialogRef, false);

    if (data.tipoEvaluacion === TIPO_EVALUACION.MEMORIA) {
      this.tiposDocumento$ = memoriaService.getTiposDocumentoRespuestasFormulario(this.data.memoriaId);
      if (!data.showTipoDocumentos) {
        // Se setea solo el tipo de documento adicional
        this.subscriptions.push(this.tipoDocumentoService.findByFormulario(
          resolveFormularioByTipoEvaluacionAndComite(this.data.tipoEvaluacion, this.data.comite)
        ).subscribe(
          (docs) => {
            docs.filter(doc => doc.formulario.id === 1 ? doc.id === 11 : (doc.formulario.id === 2 ? doc.id === 16 : (doc.formulario.id === 3 ? doc.id === 21 : doc))).forEach(doc => this.formGroup.controls.tipoDocumento.setValue(doc));
          }
        ));
      }
    } else {
      this.tiposDocumento$ = of([]);
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.data.tipoEvaluacion !== TIPO_EVALUACION.MEMORIA) {
      this.subscriptions.push(this.tipoDocumentoService.findByFormulario(
        resolveFormularioByTipoEvaluacionAndComite(this.data.tipoEvaluacion, null)
      ).subscribe(
        (tiposDocumento) => {
          this.formGroup.controls.tipoDocumento.setValue(tiposDocumento[0]);
        }
      ));
    }
    this.setupI18N();
  }

  private setupI18N(): void {
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

    this.translate.get(
      DOCUMENTO_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_DOCUMENTO_FICHERO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFicheroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_DOCUMENTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  /**
   * Inicializa formulario de añadir documentació.
   */
  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoDocumento: new FormControl(null, Validators.required),
      nombre: new FormControl(null, Validators.required),
      fichero: new FormControl(null, Validators.required),
    });
    return formGroup;
  }

  protected getValue(): IDocumentacionMemoria {
    const documentacionMemoria: IDocumentacionMemoria = {} as IDocumentacionMemoria;
    documentacionMemoria.tipoDocumento = this.formGroup.controls.tipoDocumento.value;
    documentacionMemoria.nombre = this.formGroup.controls.nombre.value;
    documentacionMemoria.documento = this.formGroup.controls.fichero.value;
    documentacionMemoria.memoria = { id: this.data.memoriaId } as IMemoria;

    return documentacionMemoria;
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

}
