import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const INCIDENCIA_DOCUMENTO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.incidencia-documento');
const NOMBRE_DOCUMENTO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.incidencia-documento.documento');
const INCIDENCIA_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.incidencia-documento.incidencia');

@Component({
  selector: 'sgi-incidencia-documento-requerimiento-modal',
  templateUrl: './incidencia-documento-requerimiento-modal.component.html',
  styleUrls: ['./incidencia-documento-requerimiento-modal.component.scss']
})
export class IncidenciaDocumentoRequerimientoModalComponent
  extends DialogFormComponent<IIncidenciaDocumentacionRequerimiento> implements OnInit {

  msgParamDocumentoJustificacionEntity = {};
  msgParamIncidenciaEntity = {};

  textSaveOrUpdate: string;
  title: string;

  constructor(
    matDialogRef: MatDialogRef<IncidenciaDocumentoRequerimientoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IIncidenciaDocumentacionRequerimiento,
    private readonly translate: TranslateService,
  ) {
    super(matDialogRef, !!data?.nombreDocumento);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      nombreDocumento: new FormControl(this.data?.nombreDocumento, Validators.required),
      incidencia: new FormControl(this.data.incidencia, Validators.maxLength(2000)),
    });
  }

  protected getValue(): IIncidenciaDocumentacionRequerimiento {
    this.data.nombreDocumento = this.formGroup.controls.nombreDocumento.value;
    this.data.incidencia = this.formGroup.controls.incidencia.value;

    return this.data;
  }

  private setupI18N(): void {
    this.translate.get(
      NOMBRE_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamDocumentoJustificacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      INCIDENCIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamIncidenciaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    if (this.data?.nombreDocumento) {
      this.translate.get(
        INCIDENCIA_DOCUMENTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.translate.get(
        INCIDENCIA_DOCUMENTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ANADIR;
    }
  }
}
