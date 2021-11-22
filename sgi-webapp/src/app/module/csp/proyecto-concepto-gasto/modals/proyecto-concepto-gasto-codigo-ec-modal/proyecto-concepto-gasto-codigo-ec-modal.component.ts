import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DateValidator } from '@core/validators/date-validator';
import { SelectValidator } from '@core/validators/select-validator';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { compareConceptoGastoCodigoEc } from '../../proyecto-concepto-gasto.utils';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY = marker('csp.proyecto-concepto-gasto-codigo-economico');
const PROYECTO_CONCEPTO_GASTO_CODIGO_ECONOMICO_SGE_KEY = marker('csp.proyecto-concepto-gasto-codigo-economico.codigo-concepto-sge');
const PROYECTO_CONCEPTO_GASTO_CODIGO_ECONOMICO_PERMITIDO_KEY = marker('csp.proyecto-concepto-gasto-codigo-economico.permitido');
const PROYECTO_CONCEPTO_GASTO_CODIGO_ECONOMICO_NO_PERMITIDO_KEY = marker('csp.proyecto-concepto-gasto-codigo-economico.no-permitido');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoConceptoGastoCodigoEcDataModal {
  convocatoriaConceptoGastoCodigoEc: IConvocatoriaConceptoGastoCodigoEc;
  proyectoConceptoGastoCodigoEc: IProyectoConceptoGastoCodigoEc;
  proyectoConceptoGastoCodigoEcsTabla: IProyectoConceptoGastoCodigoEc[];
  permitido: boolean;
  editModal: boolean;
  readonly: boolean;
}

@Component({
  templateUrl: './proyecto-concepto-gasto-codigo-ec-modal.component.html',
  styleUrls: ['./proyecto-concepto-gasto-codigo-ec-modal.component.scss']
})
export class ProyectoConceptoGastoCodigoEcModalComponent
  extends BaseModalComponent<ProyectoConceptoGastoCodigoEcDataModal, ProyectoConceptoGastoCodigoEcModalComponent>
  implements OnInit, OnDestroy {

  fxLayoutProperties: FxLayoutProperties;

  codigosEconomicos$: Observable<ICodigoEconomicoGasto[]>;
  private codigosEconomicos: ICodigoEconomicoGasto[];
  textSaveOrUpdate: string;

  showDatosConvocatoriaCodigoEconomico: boolean;
  disabledCopy = false;
  disabledSave = true;

  msgParamEntity = {};
  msgParamCodigoEconomigoSgeEntity = {};
  title: string;

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ProyectoConceptoGastoCodigoEcModalComponent>,
    codigoEconomicoGastoService: CodigoEconomicoGastoService,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoConceptoGastoCodigoEcDataModal,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.xs = 'column';

    this.codigosEconomicos$ = codigoEconomicoGastoService.findAll().pipe(
      map(response => response.items),
      tap(response => {
        this.codigosEconomicos = response;
        this.disabledSave = false;
      })
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.textSaveOrUpdate = this.data.proyectoConceptoGastoCodigoEc?.codigoEconomico ? MSG_ACEPTAR : MSG_ANADIR;

    this.subscriptions.push(this.codigosEconomicos$.subscribe(
      (codigosEconomicos) => this.formGroup.controls.codigoEconomico.setValidators(
        SelectValidator.isSelectOption(codigosEconomicos.map(cod => cod.id), true)
      )
    ));

    this.subscriptions.push(this.formGroup.controls.codigoEconomico.valueChanges.subscribe(
      (value) => {
        if (!this.formGroup.controls.codigoEconomico.disabled
          && !!!this.formGroup.controls.fechaInicio.value
          && !!!this.formGroup.controls.fechaInicio.value
        ) {
          this.formGroup.controls.fechaInicio.setValue(value.fechaInicio);
          this.formGroup.controls.fechaFin.setValue(value.fechaFin);
        }
      }
    ));

    this.subscriptions.push(this.formGroup.valueChanges.subscribe(
      () => {
        this.disabledCopy = !compareConceptoGastoCodigoEc(this.data.convocatoriaConceptoGastoCodigoEc,
          this.getDatosForm().proyectoConceptoGastoCodigoEc);
      }
    ));
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_CONCEPTO_GASTO_CODIGO_ECONOMICO_SGE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoEconomigoSgeEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      PROYECTO_CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data.proyectoConceptoGastoCodigoEc?.codigoEconomico && this.data.permitido) {
      this.translate.get(
        PROYECTO_CONCEPTO_GASTO_CODIGO_ECONOMICO_PERMITIDO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else if (this.data.proyectoConceptoGastoCodigoEc?.codigoEconomico && !this.data.permitido) {
      this.translate.get(
        PROYECTO_CONCEPTO_GASTO_CODIGO_ECONOMICO_NO_PERMITIDO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else if (!this.data.proyectoConceptoGastoCodigoEc?.codigoEconomico && this.data.permitido) {
      this.translate.get(
        PROYECTO_CONCEPTO_GASTO_CODIGO_ECONOMICO_PERMITIDO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_CONCEPTO_GASTO_CODIGO_ECONOMICO_NO_PERMITIDO_KEY,
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

  comparerCodigoEconomico(o1: ICodigoEconomicoGasto, o2: ICodigoEconomicoGasto): boolean {
    if (o1 && o2) {
      return o1?.id === o2?.id;
    }
    return o1 === o2;
  }

  displayerCodigoEconomico(codigoEconomico: ICodigoEconomicoGasto) {
    return `${codigoEconomico?.id} - ${codigoEconomico?.nombre}` ?? '';
  }

  sorterCodigoEconomico(o1: SelectValue<ICodigoEconomicoGasto>, o2: SelectValue<ICodigoEconomicoGasto>): number {
    return o1?.displayText.localeCompare(o2?.displayText);
  }

  protected getDatosForm(): ProyectoConceptoGastoCodigoEcDataModal {
    if (!this.data.proyectoConceptoGastoCodigoEc) {
      this.data.proyectoConceptoGastoCodigoEc = {} as IProyectoConceptoGastoCodigoEc;
    }
    const codigoEconomicoForm = this.codigosEconomicos?.find(codigoEconomico =>
      this.formGroup.controls.codigoEconomico.value && codigoEconomico.id === this.formGroup.controls.codigoEconomico.value.id);
    this.data.proyectoConceptoGastoCodigoEc.codigoEconomico = codigoEconomicoForm;
    this.data.proyectoConceptoGastoCodigoEc.observaciones = this.formGroup.controls.observaciones.value;
    this.data.proyectoConceptoGastoCodigoEc.fechaInicio = this.formGroup.controls.fechaInicio.value;
    this.data.proyectoConceptoGastoCodigoEc.fechaFin = this.formGroup.controls.fechaFin.value;
    this.data.proyectoConceptoGastoCodigoEc.convocatoriaConceptoGastoCodigoEcId = this.data.convocatoriaConceptoGastoCodigoEc?.id;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const codigoEconomico = this.data.proyectoConceptoGastoCodigoEc?.codigoEconomico ?? null;
    const formGroup = new FormGroup(
      {
        codigoEconomico: new FormControl(codigoEconomico),
        fechaInicio: new FormControl(this.data.proyectoConceptoGastoCodigoEc?.fechaInicio),
        fechaFin: new FormControl(this.data.proyectoConceptoGastoCodigoEc?.fechaFin),
        observaciones: new FormControl(this.data.proyectoConceptoGastoCodigoEc?.observaciones),
        codigoEconomicoConvocatoria: new FormControl({
          value: this.data.convocatoriaConceptoGastoCodigoEc?.codigoEconomico,
          disabled: true
        }),
        fechaInicioConvocatoria: new FormControl({ value: this.data.convocatoriaConceptoGastoCodigoEc?.fechaInicio, disabled: true }),
        fechaFinConvocatoria: new FormControl({ value: this.data.convocatoriaConceptoGastoCodigoEc?.fechaFin, disabled: true }),
        observacionesConvocatoria: new FormControl({ value: this.data.convocatoriaConceptoGastoCodigoEc?.observaciones, disabled: true })
      },
      {
        validators: [
          DateValidator.isBefore('fechaFin', 'fechaInicio'),
          DateValidator.isAfter('fechaInicio', 'fechaFin'),
          this.notOverlapsSameCodigoEconomico('fechaInicio', 'fechaFin', 'codigoEconomico')
        ]
      }
    );

    if (this.data.readonly) {
      formGroup.disable();
    }

    if (this.data.convocatoriaConceptoGastoCodigoEc?.codigoEconomico) {
      formGroup.controls.codigoEconomico.disable();
      if (!codigoEconomico) {
        formGroup.controls.fechaInicio.disable();
        formGroup.controls.fechaFin.disable();
        formGroup.controls.observaciones.disable();
      }
      this.showDatosConvocatoriaCodigoEconomico = true;
      this.disabledCopy = !compareConceptoGastoCodigoEc(this.data.convocatoriaConceptoGastoCodigoEc,
        this.data.proyectoConceptoGastoCodigoEc);
    } else {
      this.showDatosConvocatoriaCodigoEconomico = false;
    }

    if (this.data.editModal) {
      formGroup.controls.codigoEconomico.disable();
    }

    return formGroup;
  }

  copyToProyecto(): void {
    this.enableEditableControls();
    this.formGroup.controls.codigoEconomico.setValue(this.formGroup.controls.codigoEconomicoConvocatoria.value);
    this.formGroup.controls.fechaInicio.setValue(this.formGroup.controls.fechaInicioConvocatoria.value);
    this.formGroup.controls.fechaFin.setValue(this.formGroup.controls.fechaFinConvocatoria.value);
    this.formGroup.controls.observaciones.setValue(this.formGroup.controls.observacionesConvocatoria.value);
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  /**
   * Comprueba que el rango de fechas entre los 2 campos indicados no se superpone con ninguno de los rangos de la lista
   * filtrada por códigos económicos
   *
   * @param startRangeFieldName Nombre del campo que indica el inicio del rango.
   * @param endRangeFieldName Nombre del campo que indica el fin del rango.
   * @param filterFieldName Filtro para obtener la lista de rangos con los que se quiere comprobar.
   */
  private notOverlapsSameCodigoEconomico(startRangeFieldName: string, endRangeFieldName: string, filterFieldName: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const filterFieldControl = formGroup.controls[filterFieldName];
      const inicioRangoControl = formGroup.controls[startRangeFieldName];
      const finRangoControl = formGroup.controls[endRangeFieldName];

      if (filterFieldControl.value) {
        if ((inicioRangoControl.errors && !inicioRangoControl.errors.overlapped)
          || (finRangoControl.errors && !finRangoControl.errors.overlapped)) {
          return;
        }

        const inicioRangoNumber = inicioRangoControl.value ? inicioRangoControl.value.toMillis() : Number.MIN_VALUE;
        const finRangoNumber = finRangoControl.value ? finRangoControl.value.toMillis() : Number.MAX_VALUE;

        const ranges = this.data.proyectoConceptoGastoCodigoEcsTabla.filter(
          conceptoGasto => conceptoGasto.codigoEconomico.id === filterFieldControl.value.id
        ).map(conceptoGasto => {
          return {
            inicio: conceptoGasto.fechaInicio ? conceptoGasto.fechaInicio.toMillis() : Number.MIN_VALUE,
            fin: conceptoGasto.fechaFin ? conceptoGasto.fechaFin.toMillis() : Number.MAX_VALUE
          };
        });

        if (ranges.some(r => inicioRangoNumber <= r.fin && r.inicio <= finRangoNumber)) {
          inicioRangoControl.setErrors({ overlapped: true });
          inicioRangoControl.markAsTouched({ onlySelf: true });

          finRangoControl.setErrors({ overlapped: true });
          finRangoControl.markAsTouched({ onlySelf: true });
        } else {
          if (inicioRangoControl.errors) {
            delete inicioRangoControl.errors.overlapped;
            inicioRangoControl.updateValueAndValidity({ onlySelf: true });
          }

          if (finRangoControl.errors) {
            delete finRangoControl.errors.overlapped;
            finRangoControl.updateValueAndValidity({ onlySelf: true });
          }
        }
      }
    };
  }

  private enableEditableControls(): void {
    this.formGroup.controls.fechaInicio.enable();
    this.formGroup.controls.fechaFin.enable();
    this.formGroup.controls.observaciones.enable();
  }
}
