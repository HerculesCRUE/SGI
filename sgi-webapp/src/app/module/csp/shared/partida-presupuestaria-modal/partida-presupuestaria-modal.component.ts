import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { TIPO_PARTIDA_MAP } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { IPartidaPresupuestaria } from '@core/models/csp/partida-presupuestaria';
import { ConfiguracionService } from '@core/services/csp/configuracion.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';
import { comparePartidaPresupuestaria } from '../../proyecto/proyecto-formulario/proyecto-partidas-presupuestarias/proyecto-partida-presupuestaria.utils';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_PARTIDA_PRESUPUESTARIA_CODIGO_KEY = marker('csp.proyecto-partida-presupuestaria.codigo');
const PROYECTO_PARTIDA_PRESUPUESTARIA_TIPO_KEY = marker('csp.proyecto-partida-presupuestaria.tipo-partida');
const PROYECTO_PARTIDA_PRESUPUESTARIA_DESCRIPCION_KEY = marker('csp.proyecto-partida-presupuestaria.descripcion');
const PROYECTO_PARTIDA_PRESUPUESTARIA_KEY = marker('csp.proyecto-partida-presupuestaria');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const PROYECTO_PARTIDA_PRESUPUESTARIA_CODIGO_TOOLTIP_KEY = marker('csp.partida-presupuestaria.codigo-tooltip');

export interface PartidaPresupuestariaModalComponentData {
  partidasPresupuestarias: IPartidaPresupuestaria[];
  partidaPresupuestaria: IPartidaPresupuestaria;
  convocatoriaPartidaPresupuestaria: IConvocatoriaPartidaPresupuestaria;
  readonly: boolean;
  canEdit: boolean;
}

@Component({
  templateUrl: './partida-presupuestaria-modal.component.html',
  styleUrls: ['./partida-presupuestaria-modal.component.scss']
})
export class PartidaPresupuestariaModalComponent extends DialogFormComponent<IPartidaPresupuestaria> implements OnInit, OnDestroy {

  textSaveOrUpdate: string;

  msgParamCodigoEntity = {};
  msgParamTipoEntity = {};
  msgParamDescripcionEntity = {};
  msgTooltip = {};
  title: string;

  get TIPO_PARTIDA_MAP() {
    return TIPO_PARTIDA_MAP;
  }

  showDatosPartidaPresupuestaria = false;
  disabledCopy = false;
  disabledSave = false;

  constructor(
    matDialogRef: MatDialogRef<PartidaPresupuestariaModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PartidaPresupuestariaModalComponentData,
    private configuracionService: ConfiguracionService,
    private readonly translate: TranslateService) {
    super(matDialogRef, !!data?.partidaPresupuestaria);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.loadConfiguracion();
    this.setupI18N();
    this.textSaveOrUpdate = this.data?.partidaPresupuestaria?.codigo ? MSG_ACEPTAR : MSG_ANADIR;

    this.checkShowDatosConvocatoriaPartidaPresupuestaria(this.data.convocatoriaPartidaPresupuestaria);

    if (this.data.convocatoriaPartidaPresupuestaria) {
      this.subscriptions.push(this.formGroup.valueChanges.subscribe(
        () => {
          this.disabledCopy = !comparePartidaPresupuestaria(this.data.convocatoriaPartidaPresupuestaria, this.getValue());
        }
      ));
    }
  }

  copyToProyecto(): void {
    this.enableEditableControls();
    this.formGroup.controls.codigo.setValue(this.formGroup.controls.codigoConvocatoria.value);
    this.formGroup.controls.tipo.setValue(this.formGroup.controls.tipoConvocatoria.value);
    this.formGroup.controls.descripcion.setValue(this.formGroup.controls.descripcionConvocatoria.value);
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_PARTIDA_PRESUPUESTARIA_CODIGO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_PARTIDA_PRESUPUESTARIA_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDescripcionEntity = {
      entity: value, ...MSG_PARAMS.GENDER.FEMALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      PROYECTO_PARTIDA_PRESUPUESTARIA_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data?.partidaPresupuestaria?.codigo) {
      this.translate.get(
        PROYECTO_PARTIDA_PRESUPUESTARIA_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_PARTIDA_PRESUPUESTARIA_KEY,
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

  private checkShowDatosConvocatoriaPartidaPresupuestaria(convocatoriaPartidaPresupuestaria: IConvocatoriaPartidaPresupuestaria): void {
    this.showDatosPartidaPresupuestaria = !!convocatoriaPartidaPresupuestaria;
  }

  protected getValue(): IPartidaPresupuestaria {
    if (!this.data.partidaPresupuestaria) {
      this.data.partidaPresupuestaria = {} as IPartidaPresupuestaria;
    }

    this.data.partidaPresupuestaria.codigo = this.formGroup.controls.codigo.value;
    this.data.partidaPresupuestaria.tipoPartida = this.formGroup.controls.tipo.value;
    this.data.partidaPresupuestaria.descripcion = this.formGroup.controls.descripcion.value === ''
      ? null : this.formGroup.controls.descripcion.value;
    return this.data.partidaPresupuestaria;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      codigo: new FormControl(this.data?.partidaPresupuestaria?.codigo, [
        Validators.required,
        Validators.maxLength(50)
      ]),
      tipo: new FormControl(this.data?.partidaPresupuestaria?.tipoPartida, [
        Validators.required
      ]),
      descripcion: new FormControl(this.data?.partidaPresupuestaria?.descripcion, [
        Validators.maxLength(250)
      ]),
      codigoConvocatoria: new FormControl({ value: this.data?.convocatoriaPartidaPresupuestaria?.codigo, disabled: true }),
      tipoConvocatoria: new FormControl({ value: this.data?.convocatoriaPartidaPresupuestaria?.tipoPartida, disabled: true }),
      descripcionConvocatoria: new FormControl({ value: this.data?.convocatoriaPartidaPresupuestaria?.descripcion, disabled: true }),
    }, {
      validators: [
        this.uniqueCodigoTipo(),
      ]
    });

    if (!this.data.canEdit) {
      formGroup.disable();
    }

    this.subscriptions.push(
      this.configuracionService.getConfiguracion().subscribe(configuracion => {
        formGroup.controls.codigo.setValidators([
          formGroup.controls.codigo.validator,
          Validators.pattern(configuracion.formatoPartidaPresupuestaria)
        ]);
      })
    );

    if (this.data.convocatoriaPartidaPresupuestaria) {
      formGroup.controls.codigo.disable();
      formGroup.controls.tipo.disable();

      if (this.data.partidaPresupuestaria) {
        this.disabledCopy = !comparePartidaPresupuestaria(this.data.convocatoriaPartidaPresupuestaria, this.data.partidaPresupuestaria);
      } else {
        this.disabledSave = true;
        formGroup.controls.descripcion.disable();
      }
    }

    return formGroup;
  }

  /**
   * Comprueba si es unica la combinacion de codigo y tipo.
   */
  private uniqueCodigoTipo(): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {
      const codigoControl = formGroup.controls.codigo;
      const tipoControl = formGroup.controls.tipo;

      if ((codigoControl.errors || tipoControl.errors) && !codigoControl.errors?.duplicated) {
        return;
      }

      const codigoValue = codigoControl.value;
      const tipoValue = tipoControl.value;

      const duplicated = this.data.partidasPresupuestarias
        .some(partidaPresupuestaria => partidaPresupuestaria?.codigo === codigoValue && partidaPresupuestaria?.tipoPartida === tipoValue);

      if (duplicated) {
        codigoControl.setErrors({ duplicated: true });
        codigoControl.markAsTouched({ onlySelf: true });
      } else if (codigoControl.errors) {
        delete codigoControl.errors.duplicated;
        codigoControl.updateValueAndValidity({ onlySelf: true });
      }
    };
  }

  private loadConfiguracion() {
    const configuracionFindSubscription = this.configuracionService.getConfiguracion().subscribe(
      configuracion => {
        this.translate.get(
          PROYECTO_PARTIDA_PRESUPUESTARIA_CODIGO_TOOLTIP_KEY,
          MSG_PARAMS.CARDINALIRY.SINGULAR
        ).subscribe(() => this.msgTooltip = {
          mask: configuracion.plantillaFormatoPartidaPresupuestaria,
        });
      });
    this.subscriptions.push(configuracionFindSubscription);
  }

  private enableEditableControls(): void {
    this.disabledSave = false;
    this.formGroup.controls.descripcion.enable();
  }

}
