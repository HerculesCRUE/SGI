import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_CONFIGURACION_SOLICITUD_TIPO_DOCUMENTO_KEY = marker('csp.documento.tipo');
const CONVOCATORIA_CONFIGURACION_SOLICITUD_DOCUMENTO_REQUERIDO_KEY = marker('csp.convocatoria-configuracion-solicitud-documento-requerido');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ConvocatoriaConfiguracionSolicitudesModalData {
  documentoRequerido: IDocumentoRequeridoSolicitud;
  tipoFaseId: number;
  modeloEjecucionId: number;
  isConvocatoriaVinculada: boolean;
}

@Component({
  templateUrl: './convocatoria-configuracion-solicitudes-modal.component.html',
  styleUrls: ['./convocatoria-configuracion-solicitudes-modal.component.scss']
})
export class ConvocatoriaConfiguracionSolicitudesModalComponent
  extends DialogFormComponent<ConvocatoriaConfiguracionSolicitudesModalData> implements OnInit {

  textSaveOrUpdate: string;
  title: string;

  msgParamTipoDocumentoEntity = {};

  constructor(
    matDialogRef: MatDialogRef<ConvocatoriaConfiguracionSolicitudesModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConvocatoriaConfiguracionSolicitudesModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data.documentoRequerido.tipoDocumento);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.textSaveOrUpdate = this.data.documentoRequerido.tipoDocumento ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_CONFIGURACION_SOLICITUD_TIPO_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoDocumentoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data?.documentoRequerido?.tipoDocumento) {
      this.translate.get(
        CONVOCATORIA_CONFIGURACION_SOLICITUD_DOCUMENTO_REQUERIDO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        CONVOCATORIA_CONFIGURACION_SOLICITUD_DOCUMENTO_REQUERIDO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  protected getValue(): ConvocatoriaConfiguracionSolicitudesModalData {
    this.data.documentoRequerido.tipoDocumento = this.formGroup.controls.tipoDocumento.value;
    this.data.documentoRequerido.observaciones = this.formGroup.controls.observaciones.value;
    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoDocumento: new FormControl(this.data.documentoRequerido.tipoDocumento, Validators.required),
      observaciones: new FormControl(this.data.documentoRequerido.observaciones),
    });
    if (this.data.isConvocatoriaVinculada) {
      formGroup.disable();
    }
    return formGroup;
  }
}
