import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { TIPO_PARTIDA_MAP } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

export interface IConvocatoriaPartidaPresupuestariaModalData {
  convocatoriaPartidaPresupuestaria: IConvocatoriaPartidaPresupuestaria;
  convocatoriaPartidaPresupuestariaList: StatusWrapper<IConvocatoriaPartidaPresupuestaria>[];
  readonly: boolean;
  canEdit: boolean;
}

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_PARTIDAS_PRESUPUESTARIAS_KEY = marker('csp.convocatoria-partida-presupuestaria');
const CONVOCATORIA_PARTIDAS_PRESUPUESTARIAS_CODIGO_KEY = marker('csp.convocatoria-partida-presupuestaria.codigo');
const CONVOCATORIA_PARTIDAS_PRESUPUESTARIAS_DESCRIPCION_KEY = marker('csp.convocatoria-partida-presupuestaria.descripcion');
const CONVOCATORIA_PARTIDAS_PRESUPUESTARIAS_TIPO_PARTIDA_KEY = marker('csp.convocatoria-partida-presupuestaria.tipo-partida');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './convocatoria-partidas-presupuestarias-modal.component.html',
  styleUrls: ['./convocatoria-partidas-presupuestarias-modal.component.scss']
})
export class ConvocatoriaPartidaPresupuestariaModalComponent
  extends BaseModalComponent<IConvocatoriaPartidaPresupuestaria, ConvocatoriaPartidaPresupuestariaModalComponent> implements OnInit {

  textSaveOrUpdate: string;
  title: string;

  msgParamCodigoEntity = {};
  msgParamDescripcionEntity = {};
  msgParamTipoPartida = {};

  get TIPO_PARTIDA_MAP() {
    return TIPO_PARTIDA_MAP;
  }

  constructor(
    protected snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: IConvocatoriaPartidaPresupuestariaModalData,
    public matDialogRef: MatDialogRef<ConvocatoriaPartidaPresupuestariaModalComponent>,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data.convocatoriaPartidaPresupuestaria);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.textSaveOrUpdate = this.data?.convocatoriaPartidaPresupuestaria?.codigo ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_PARTIDAS_PRESUPUESTARIAS_CODIGO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_PARTIDAS_PRESUPUESTARIAS_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDescripcionEntity = {
      entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      CONVOCATORIA_PARTIDAS_PRESUPUESTARIAS_TIPO_PARTIDA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoPartida = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data.convocatoriaPartidaPresupuestaria.codigo) {
      this.translate.get(
        CONVOCATORIA_PARTIDAS_PRESUPUESTARIAS_KEY,
        MSG_PARAMS.CARDINALIRY.PLURAL
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        CONVOCATORIA_PARTIDAS_PRESUPUESTARIAS_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      codigo: new FormControl(this.data.convocatoriaPartidaPresupuestaria?.codigo, [Validators.required]),
      tipoPartida: new FormControl(this.data.convocatoriaPartidaPresupuestaria?.tipoPartida, [Validators.required]),
      descripcion: new FormControl(this.data.convocatoriaPartidaPresupuestaria?.descripcion, [Validators.maxLength(50)])
    });

    if (!this.data.canEdit) {
      formGroup.disable();
    }
    return formGroup;
  }

  protected getDatosForm(): IConvocatoriaPartidaPresupuestaria {
    const convocatoriaPartidaPresupuestaria = this.data.convocatoriaPartidaPresupuestaria;
    convocatoriaPartidaPresupuestaria.codigo = this.formGroup.get('codigo').value;
    convocatoriaPartidaPresupuestaria.descripcion = this.formGroup.get('descripcion').value;
    convocatoriaPartidaPresupuestaria.tipoPartida = this.formGroup.get('tipoPartida').value;

    return convocatoriaPartidaPresupuestaria;
  }

}
