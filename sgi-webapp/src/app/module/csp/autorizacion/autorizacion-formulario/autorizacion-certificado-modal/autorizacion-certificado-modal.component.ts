import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { ICertificadoAutorizacion } from '@core/models/csp/certificado-autorizacion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { SgiFileUploadComponent, UploadEvent } from '@shared/file-upload/file-upload.component';
import { switchMap } from 'rxjs/operators';

const MSG_ACEPTAR = marker('btn.ok');
const MGS_ANADIR = marker('btn.add');
const CERTIFICADO_AUTORIZACION_KEY = marker('csp.certificado-autorizacion');
const MSG_ERROR_FORM_GROUP = marker('error.form-group');
const CERTIFICADO_AUTORIZACION_DOCUMENTO_KEY = marker('csp.certificado-autorizacion.documento');
const CERTIFICADO_AUTORIZACION_PUBLICO_KEY = marker('csp.certificado-autorizacion.publico');

export interface ICertificadoAutorizacionModalData {
  certificado: ICertificadoAutorizacion;
  hasSomeOtherCertificadoAutorizacionVisible: boolean;
  generadoAutomatico: boolean;
}

@Component({
  selector: 'sgi-autorizacion-certificado-modal',
  templateUrl: './autorizacion-certificado-modal.component.html',
  styleUrls: ['./autorizacion-certificado-modal.component.scss']
})
export class AutorizacionCertificadoModalComponent extends DialogFormComponent<ICertificadoAutorizacionModalData> implements OnInit {

  showTipoRelacion: boolean;

  textSaveOrUpdate: string;
  title: string;

  uploading = false;
  documentoAutorizacion: IDocumento;

  msgParamTipoEntidadRelacionada = {};
  msgParamEntidadRelacionada = {};
  msgParamTipoRelacion = {};
  msgParamObservaciones = {};
  msgParamFicheroEntity = {};
  msgParamPublicoEntity = {};
  msgParamDocumentoEntity = {};

  @ViewChild('uploader') private uploader: SgiFileUploadComponent;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<AutorizacionCertificadoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ICertificadoAutorizacionModalData,
    private readonly translate: TranslateService,
    private autorizacionService: AutorizacionService,
    readonly sgiAuthService: SgiAuthService,
    private readonly documentoService: DocumentoService
  ) {
    super(matDialogRef, !!data?.certificado?.id);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    if (!this.data.certificado) {
      this.data.certificado = {} as ICertificadoAutorizacion;
    }

    if (this.data?.certificado?.id) {
      this.formGroup.controls.documento.disable();
      this.formGroup.controls.documentoAuto.disable();
    }
  }

  doAction(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      if (this.formGroup.controls.generadoAutomatico.value) {
        if (this.formGroup.controls.documentoAuto.value) {
          this.close(this.getValue());
        } else {
          this.pushProblems(new SgiError(MSG_ERROR_FORM_GROUP));
        }
      } else {
        this.uploader.uploadSelection().subscribe(
          () => this.close(this.getValue()),
          this.processError
        );
      }
    }
  }

  protected getValue(): ICertificadoAutorizacionModalData {
    this.data.certificado.nombre = this.formGroup.controls.nombre.value;
    this.data.certificado.visible = this.formGroup.controls.publico.value;
    this.data.certificado.documento = this.formGroup.controls.generadoAutomatico.value
      ? this.getDocumentoAuto() : this.formGroup.controls.documento.value;
    this.data.generadoAutomatico = this.formGroup.controls.generadoAutomatico.value;
    return this.data;
  }

  private getDocumentoAuto(): IDocumento {
    return this.documentoAutorizacion ?? this.data.certificado.documento;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      nombre: new FormControl(this.data?.certificado?.nombre),
      publico: new FormControl(this.data?.certificado?.visible, [Validators.required,
      this.buildValidadorHasSomeOtherCertificadoAutorizacionVisible(this.data?.hasSomeOtherCertificadoAutorizacionVisible)]),
      documento: new FormControl(this.data?.certificado?.documento, Validators.required),
      documentoAuto: new FormControl(this.data?.certificado?.documento?.nombre, Validators.required),
      generadoAutomatico: new FormControl(null),
    });
    form.controls.generadoAutomatico.setValue(this.data.generadoAutomatico, { emitEvent: false });
    if (this.data.generadoAutomatico) {
      form.controls.documento.disable();
    } else {
      form.controls.documentoAuto.disable();
    }

    this.subscriptions.push(
      form.controls.generadoAutomatico.valueChanges.subscribe(
        (value) => {
          form.controls.documento.setValue(null);
          if (value) {
            form.controls.documentoAuto.enable();
            form.controls.documento.disable();
            if (!this.documentoAutorizacion?.documentoRef) {
              this.generarInforme(this.data?.certificado?.autorizacion?.id);
            }
          } else {
            form.controls.documento.enable();
            form.controls.documentoAuto.disable();
          }
        }
      )
    );

    return form;
  }

  private setupI18N(): void {
    this.translate.get(
      CERTIFICADO_AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.title = value);

    this.translate.get(
      CERTIFICADO_AUTORIZACION_DOCUMENTO_KEY,
    ).subscribe((value) => this.msgParamDocumentoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CERTIFICADO_AUTORIZACION_PUBLICO_KEY,
    ).subscribe((value) => this.msgParamPublicoEntity = { field: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data?.certificado?.id) {
      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.textSaveOrUpdate = MGS_ANADIR;
    }
  }

  private buildValidadorHasSomeOtherCertificadoAutorizacionVisible(hasSomeOtherCertificadoAutorizacionVisible: boolean): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (hasSomeOtherCertificadoAutorizacionVisible && control.value) {
        return { hasCertificadoPublico: true };
      } else {
        return null;
      }
    };
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

  private generarInforme(idAutorizacion: number): void {
    this.autorizacionService.getInformeAutorizacion(idAutorizacion).subscribe(
      (documentoInfo: IDocumento) => {
        this.documentoAutorizacion = documentoInfo;
        this.formGroup.controls.documentoAuto.setValue(documentoInfo?.nombre);
      });
  }

  /**
   * Visualiza el informe de autorización.
   * @param documentoRef referencia del documento
   */
  visualizarInforme(): void {
    const documento = this.getDocumentoAuto();
    if (documento?.documentoRef) {
      this.documentoService.getInfoFichero(documento.documentoRef).pipe(
        switchMap((documentoInfo: IDocumento) => {
          return this.documentoService.downloadFichero(documentoInfo.documentoRef);
        })
      ).subscribe(response => {
        triggerDownloadToUser(response, documento.nombre);
      });
    }
  }
}
