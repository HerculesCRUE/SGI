import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { IProyectoPeriodoSeguimientoWithTituloProyecto } from '../../ejecucion-economica-formulario/seguimiento-justificacion-resumen/seguimiento-justificacion-resumen.fragment';

const FECHA_PRESENTACION_DOCUMENTACION_KEY =
  marker('csp.presentacion-documentacion.fecha-presentacion-documentacion');

@Component({
  selector: 'sgi-presentacion-documentacion-modal',
  templateUrl: './presentacion-documentacion-modal.component.html',
  styleUrls: ['./presentacion-documentacion-modal.component.scss']
})
export class PresentacionDocumentacionModalComponent extends
  DialogFormComponent<StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>> implements OnInit {

  msgParamFechaPresentacionEntity = {};

  constructor(
    matDialogRef: MatDialogRef<PresentacionDocumentacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, true);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      FECHA_PRESENTACION_DOCUMENTACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaPresentacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup(
      {
        fechaPresentacionDocumentacion: new FormControl(this.data.value.fechaPresentacionDocumentacion, [Validators.required]),
      }
    );
  }

  protected getValue(): StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto> {
    this.data.value.fechaPresentacionDocumentacion = this.formGroup.controls.fechaPresentacionDocumentacion.value;

    return this.data;
  }
}
