import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { TranslateService } from '@ngx-translate/core';

const ALEGACION_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.incidencia-documento.alegacion');

@Component({
  selector: 'sgi-incidencia-documentacion-requerimiento-alegacion-modal',
  templateUrl: './incidencia-documentacion-requerimiento-alegacion-modal.component.html',
  styleUrls: ['./incidencia-documentacion-requerimiento-alegacion-modal.component.scss']
})
export class IncidenciaDocumentacionRequerimientoAlegacionModalComponent
  extends DialogFormComponent<IIncidenciaDocumentacionRequerimiento> implements OnInit {

  msgParamAlegacionEntity = {};

  title: string;

  constructor(
    matDialogRef: MatDialogRef<IncidenciaDocumentacionRequerimientoAlegacionModalComponent>,
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
      nombreDocumento: new FormControl({ value: this.data?.nombreDocumento, disabled: true }),
      incidencia: new FormControl({ value: this.data.incidencia, disabled: true }),
      alegacion: new FormControl(this.data.alegacion, Validators.maxLength(2000)),
    });
  }

  protected getValue(): IIncidenciaDocumentacionRequerimiento {
    this.data.alegacion = this.formGroup.controls.alegacion.value;

    return this.data;
  }

  private setupI18N(): void {
    this.translate.get(
      ALEGACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamAlegacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
  }
}
