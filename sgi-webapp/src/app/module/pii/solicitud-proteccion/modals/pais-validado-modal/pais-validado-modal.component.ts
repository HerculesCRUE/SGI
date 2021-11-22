import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPaisValidado } from '@core/models/pii/pais-validado';
import { IPais } from '@core/models/sgo/pais';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';

export interface PaisValidadoModalData {
  paisValidado: StatusWrapper<IPaisValidado>;
  paises: IPais[];
}

const PAIS_VALIDADO_TITULO_KEY = marker('pii.solicitud-proteccion.pais-validado.titulo');
const CODIGO_INVENCION_KEY = marker('pii.solicitud-proteccion.pais-validado.codigo-invencion');
const FECHA_VALIDACION_KEY = marker('pii.solicitud-proteccion.pais-validado.fecha-validacion');
const PAIS_KEY = marker('pii.solicitud-proteccion.pais-validado.pais');

@Component({
  selector: 'sgi-solicitud-proteccion-pais-validado',
  templateUrl: './pais-validado-modal.component.html',
  styleUrls: ['./pais-validado-modal.component.scss']
})
export class PaisValidadoModalComponent
  extends BaseModalComponent<StatusWrapper<IPaisValidado>, PaisValidadoModalComponent> implements OnInit {

  paisValidado: StatusWrapper<IPaisValidado>;
  msgParamTituloEntity = {};
  msgParamPaisEntity = {};
  msgParamCodigoInvencionEntity = {};
  msgParamFechaValidacionEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  protected getDatosForm(): StatusWrapper<IPaisValidado> {

    this.paisValidado.value.codigoInvencion = this.formGroup.controls.codigoInvencion.value;
    this.paisValidado.value.fechaValidacion = this.formGroup.controls.fechaValidacion.value;
    this.paisValidado.value.pais = this.formGroup.controls.pais.value;

    return this.paisValidado;
  }

  protected getFormGroup(): FormGroup {

    const formGroup = new FormGroup({
      codigoInvencion: new FormControl(this.paisValidado?.value?.codigoInvencion, Validators.required),
      fechaValidacion: new FormControl(this.paisValidado?.value?.fechaValidacion, [Validators.required]),
      pais: new FormControl(this.paisValidado?.value?.pais, [Validators.required]),
    });

    return formGroup;
  }

  constructor(
    private readonly logger: NGXLogger,
    public matDialogRef: MatDialogRef<PaisValidadoModalComponent>,
    protected readonly snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: PaisValidadoModalData,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data.paisValidado);
    this.paisValidado = data.paisValidado;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {

    this.translate.get(
      PAIS_VALIDADO_TITULO_KEY,
      this.MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CODIGO_INVENCION_KEY,
      this.MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamCodigoInvencionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      FECHA_VALIDACION_KEY,
      this.MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaValidacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PAIS_KEY,
      this.MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamPaisEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

  }

}
