import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEstadoProduccionCientificaRequest } from '@core/services/prc/estado-produccion-cientifica/estado-produccion-cientifica-input';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';

const PRODUCCION_CIENTIFICA_COMENTARIO_KEY = marker('prc.produccion-cientifica.motivo-rechazo');

@Component({
  selector: 'sgi-rechazar-produccion-cientifica-modal',
  templateUrl: './rechazar-produccion-cientifica-modal.component.html',
  styleUrls: ['./rechazar-produccion-cientifica-modal.component.scss']
})
export class RechazarProduccionCientificaModalComponent
  extends BaseModalComponent<IEstadoProduccionCientificaRequest, IEstadoProduccionCientificaRequest> {

  msgParamComentarioEntity = {};

  constructor(
    matDialogRef: MatDialogRef<IEstadoProduccionCientificaRequest>,
    snackBarService: SnackBarService,
    private readonly translate: TranslateService
  ) {
    super(
      snackBarService,
      matDialogRef,
      null);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      PRODUCCION_CIENTIFICA_COMENTARIO_KEY,
    ).subscribe((value) =>
      this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
  }

  protected getDatosForm(): IEstadoProduccionCientificaRequest {
    return { comentario: this.formGroup.controls.comentario.value } as IEstadoProduccionCientificaRequest;
  }
  protected getFormGroup(): FormGroup {
    return new FormGroup({
      comentario: new FormControl('', Validators.required)
    });
  }
}
