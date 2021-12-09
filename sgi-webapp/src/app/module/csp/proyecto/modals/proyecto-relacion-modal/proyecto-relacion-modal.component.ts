import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { TipoEntidad, TIPO_ENTIDAD_MAP } from '@core/models/rel/relacion';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { ProyectoRelacionValidator } from '@core/validators/proyecto-relacion-validator';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { switchMap } from 'rxjs/operators';
import { IProyectoRelacionTableData } from '../../proyecto-formulario/proyecto-relaciones/proyecto-relaciones.fragment';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const PROYECTO_RELACION_KEY = marker('csp.proyecto-relacion');
const RELACION_TIPO_ENTIDAD_RELACIONADA = marker('csp.proyecto-relacion.tipo-entidad-relacionada');
const RELACION_ENTIDAD_RELACIONADA = marker('csp.proyecto-relacion.entidad-relacionada');
const RELACION_TIPO_RELACION = marker('csp.proyecto-relacion.tipo-relacion');
const RELACION_OBSERVACIONES = marker('csp.proyecto-relacion.observaciones');

export interface IProyectoRelacionModalData {
  relacion: IProyectoRelacionTableData;
  readonly: boolean;
  entitiesAlreadyRelated: IProyectoRelacionTableData[];
  miembrosEquipoProyecto: IPersona[];
}

@Component({
  selector: 'sgi-proyecto-relacion-modal',
  templateUrl: './proyecto-relacion-modal.component.html',
  styleUrls: ['./proyecto-relacion-modal.component.scss']
})
export class ProyectoRelacionModalComponent extends BaseModalComponent<IProyectoRelacionTableData, ProyectoRelacionModalComponent> implements OnInit {

  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  showTipoRelacion: boolean;

  textSaveOrUpdate: string;
  title: string;

  msgParamTipoEntidadRelacionada = {};
  msgParamEntidadRelacionada = {};
  msgParamTipoRelacion = {};
  msgParamObservaciones = {};

  readonly TIPO_ENTIDAD_MAP_FILTERED: Map<TipoEntidad, string>;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TIPO_ENTIDAD() {
    return TipoEntidad;
  }

  get relacion() {
    return this.data.relacion;
  }

  constructor(
    public matDialogRef: MatDialogRef<ProyectoRelacionModalComponent>,
    protected readonly snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: IProyectoRelacionModalData,
    private readonly translate: TranslateService,
    readonly sgiAuthService: SgiAuthService,
  ) {
    super(snackBarService, matDialogRef, null);
    this.showTipoRelacion = false;
    if (this.sgiAuthService.hasAuthority('PII-INV-MOD-V')) {
      this.TIPO_ENTIDAD_MAP_FILTERED = TIPO_ENTIDAD_MAP;
    } else {
      this.TIPO_ENTIDAD_MAP_FILTERED = new Map(
        Array.from(TIPO_ENTIDAD_MAP).filter(element => element[0] !== TipoEntidad.INVENCION)
      );
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initFlexProperties();
    this.setupI18N();
  }

  protected getDatosForm(): IProyectoRelacionTableData {
    this.relacion.tipoEntidadRelacionada = this.formGroup.controls.tipoEntidadRelacionada.value;
    this.relacion.entidadRelacionada = this.formGroup.controls.entidadRelacionada.value;
    this.relacion.observaciones = this.formGroup.controls.observaciones.value;

    return this.relacion;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoEntidadRelacionada: new FormControl(this.relacion?.tipoEntidadRelacionada, Validators.required),
      entidadRelacionada: new FormControl(this.relacion?.entidadRelacionada, Validators.required),
      observaciones: new FormControl(this.relacion?.observaciones, Validators.maxLength(2000)),
    }, ProyectoRelacionValidator.notRepeatedProyectoRelacion(this.data.entitiesAlreadyRelated));

    this.initFormConfiguration(formGroup);

    return formGroup;
  }

  private initFormConfiguration(formGroup: FormGroup): void {
    if (this.data.readonly) {
      formGroup.disable();
    }
    if (this.relacion?.tipoEntidadRelacionada) {
      formGroup.controls.tipoEntidadRelacionada.disable();
      formGroup.controls.entidadRelacionada.disable();
    }

    this.subscriptions.push(
      formGroup.controls.tipoEntidadRelacionada.valueChanges.subscribe((tipoEntidadSelected: TipoEntidad) => {
        formGroup.controls.entidadRelacionada.reset();
      })
    );
  }

  private setupI18N(): void {
    this.translate.get(
      RELACION_TIPO_ENTIDAD_RELACIONADA,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamTipoEntidadRelacionada = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      RELACION_ENTIDAD_RELACIONADA,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamEntidadRelacionada = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      RELACION_TIPO_RELACION,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoRelacion = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      RELACION_OBSERVACIONES,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamObservaciones = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data.relacion?.entidadRelacionada?.id) {
      this.translate.get(
        PROYECTO_RELACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.translate.get(
        PROYECTO_RELACION_KEY,
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

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'column';

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
  }
}
