import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { resolveFormularioByTipoEvaluacionAndComite } from '@core/models/eti/formulario';
import { IMemoria } from '@core/models/eti/memoria';
import { ITipoDocumento } from '@core/models/eti/tipo-documento';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { TipoDocumentoService } from '@core/services/eti/tipo-documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { Observable, of, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TITLE_NEW_ENTITY = marker('title.new.entity');
const DOCUMENTO_KEY = marker('eti.memoria.documento');
const DOCUMENTO_TIPO_KEY = marker('eti.memoria.documento.tipo');
const PROYECTO_DOCUMENTO_FICHERO_KEY = marker('csp.proyecto-documento.fichero');
const PROYECTO_DOCUMENTO_NOMBRE_KEY = marker('csp.documento.nombre');
const MSG_UPLOAD_SUCCESS = marker('msg.file.upload.success');
const MSG_UPLOAD_ERROR = marker('error.file.upload');

export interface MemoriaDocumentacionMemoriaModalData {
  memoriaId: number;
  tipoEvaluacion: TIPO_EVALUACION;
}

@Component({
  templateUrl: './memoria-documentacion-memoria-modal.component.html',
  styleUrls: ['./memoria-documentacion-memoria-modal.component.scss']
})
export class MemoriaDocumentacionMemoriaModalComponent implements OnInit {

  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;

  readonly tiposDocumento$: Observable<ITipoDocumento[]>;
  private subscriptions: Subscription[] = [];
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
    public readonly matDialogRef: MatDialogRef<MemoriaDocumentacionMemoriaModalComponent>,
    private readonly snackBarService: SnackBarService,
    memoriaService: MemoriaService,
    @Inject(MAT_DIALOG_DATA) public readonly data: MemoriaDocumentacionMemoriaModalData,
    private readonly translate: TranslateService,
    private readonly tipoDocumentoService: TipoDocumentoService
  ) {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    if (data.tipoEvaluacion === TIPO_EVALUACION.MEMORIA) {
      this.tiposDocumento$ = memoriaService.getTiposDocumentoRespuestasFormulario(this.data.memoriaId);
    }
    else {
      this.tiposDocumento$ = of([]);
    }
  }

  ngOnInit(): void {
    this.initFormGroup();
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
  private initFormGroup() {
    this.formGroup = new FormGroup({
      tipoDocumento: new FormControl(null, Validators.required),
      nombre: new FormControl(null, Validators.required),
      fichero: new FormControl(null, Validators.required),
    });
  }

  saveOrUpdate(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      this.uploader.uploadSelection().subscribe(
        () => {
          const documentacionMemoria: IDocumentacionMemoria = {} as IDocumentacionMemoria;
          documentacionMemoria.tipoDocumento = this.formGroup.controls.tipoDocumento.value;
          documentacionMemoria.nombre = this.formGroup.controls.nombre.value;
          documentacionMemoria.documento = this.formGroup.controls.fichero.value;
          documentacionMemoria.memoria = { id: this.data.memoriaId } as IMemoria;
          this.matDialogRef.close(documentacionMemoria);
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
