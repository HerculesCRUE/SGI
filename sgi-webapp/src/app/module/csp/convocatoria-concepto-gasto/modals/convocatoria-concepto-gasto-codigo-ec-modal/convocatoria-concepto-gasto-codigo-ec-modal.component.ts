import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { DateValidator } from '@core/validators/date-validator';
import { SelectValidator } from '@core/validators/select-validator';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY = marker('csp.convocatoria-elegibilidad.codigo-economico');
const CONVOCATORIA_CONCEPTO_GASTO_CODIGO_ECONOMICO_SGE_KEY = marker('csp.convocatoria-elegibilidad.codigo-economico.sge');
const CONVOCATORIA_ELEGIBILIDAD_CODIGO_ECONOMICO_PERMITIDO_KEY = marker('csp.convocatoria-elegibilidad.codigo-economico.permitido');
const CONVOCATORIA_ELEGIBILIDAD_CODIGO_ECONOMICO_NO_PERMITIDO_KEY = marker('csp.convocatoria-elegibilidad.codigo-economico.no-permitido');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ConvocatoriaConceptoGastoCodigoEc extends IConvocatoriaConceptoGastoCodigoEc {
  convocatoriaConceptoGasto: IConvocatoriaConceptoGasto;
}

export interface IConvocatoriaConceptoGastoCodigoEcModalComponent {
  convocatoriaConceptoGastoCodigoEc: IConvocatoriaConceptoGastoCodigoEc;
  convocatoriaConceptoGastoCodigoEcsTabla: IConvocatoriaConceptoGastoCodigoEc[];
  convocatoriaConceptoGastoCodigoEcsConvocatoria: ConvocatoriaConceptoGastoCodigoEc[];
  convocatoriaConceptoGasto: IConvocatoriaConceptoGasto;
  permitido: boolean;
  editModal: boolean;
  readonly: boolean;
}

@Component({
  templateUrl: './convocatoria-concepto-gasto-codigo-ec-modal.component.html',
  styleUrls: ['./convocatoria-concepto-gasto-codigo-ec-modal.component.scss']
})
export class ConvocatoriaConceptoGastoCodigoEcModalComponent
  extends DialogFormComponent<IConvocatoriaConceptoGastoCodigoEcModalComponent> implements OnInit, OnDestroy {

  convocatoriaConceptoGastosFiltered: IConvocatoriaConceptoGasto[];
  convocatoriaConceptoGastos$: Observable<IConvocatoriaConceptoGasto[]>;

  codigosEconomicos$: Observable<ICodigoEconomicoGasto[]>;
  textSaveOrUpdate: string;

  msgParamEntity = {};
  msgParamCodigoEconomigoSgeEntity = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<ConvocatoriaConceptoGastoCodigoEcModalComponent>,
    codigoEconomicoGastoService: CodigoEconomicoGastoService,
    @Inject(MAT_DIALOG_DATA) public data: IConvocatoriaConceptoGastoCodigoEcModalComponent,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data.convocatoriaConceptoGastoCodigoEc.codigoEconomico);

    this.codigosEconomicos$ = codigoEconomicoGastoService.findAll().pipe(
      map(response => response.items)
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.textSaveOrUpdate = this.data.convocatoriaConceptoGastoCodigoEc.codigoEconomico ? MSG_ACEPTAR : MSG_ANADIR;

    this.subscriptions.push(this.codigosEconomicos$.subscribe(
      (codigosEconomicos) => this.formGroup.controls.codigoEconomico.setValidators([
        SelectValidator.isSelectOption(codigosEconomicos.map(cod => cod.id), true),
        Validators.required,
        this.notOverlapsSameConceptoGastoAndCodigoEconomico(this.data)
      ])
    ));

    this.subscriptions.push(this.formGroup.controls.codigoEconomico.valueChanges.subscribe(
      (value) => {
        if (!this.formGroup.controls.codigoEconomico.disabled) {
          this.formGroup.controls.fechaInicio.setValue(value.fechaInicio);
          this.formGroup.controls.fechaFin.setValue(value.fechaFin);
        }
      }
    ));
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_CODIGO_ECONOMICO_SGE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoEconomigoSgeEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data.convocatoriaConceptoGastoCodigoEc.codigoEconomico && this.data.permitido) {
      this.translate.get(
        CONVOCATORIA_ELEGIBILIDAD_CODIGO_ECONOMICO_PERMITIDO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else if (this.data.convocatoriaConceptoGastoCodigoEc.codigoEconomico && !this.data.permitido) {
      this.translate.get(
        CONVOCATORIA_ELEGIBILIDAD_CODIGO_ECONOMICO_NO_PERMITIDO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else if (!this.data.convocatoriaConceptoGastoCodigoEc.codigoEconomico && this.data.permitido) {
      this.translate.get(
        CONVOCATORIA_ELEGIBILIDAD_CODIGO_ECONOMICO_PERMITIDO_KEY,
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
        CONVOCATORIA_ELEGIBILIDAD_CODIGO_ECONOMICO_NO_PERMITIDO_KEY,
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

  protected getValue(): IConvocatoriaConceptoGastoCodigoEcModalComponent {
    this.data.convocatoriaConceptoGastoCodigoEc.codigoEconomico = this.formGroup.controls.codigoEconomico.value;
    this.data.convocatoriaConceptoGastoCodigoEc.observaciones = this.formGroup.controls.observaciones.value;
    this.data.convocatoriaConceptoGastoCodigoEc.fechaInicio = this.formGroup.controls.fechaInicio.value;
    this.data.convocatoriaConceptoGastoCodigoEc.fechaFin = this.formGroup.controls.fechaFin.value;
    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    const codigoEconomico = this.data.convocatoriaConceptoGastoCodigoEc?.codigoEconomico ?? null;
    const formGroup = new FormGroup(
      {
        codigoEconomico: new FormControl(codigoEconomico, [Validators.required, this.notOverlapsSameConceptoGastoAndCodigoEconomico(this.data)]),
        fechaInicio: new FormControl(this.data.convocatoriaConceptoGastoCodigoEc?.fechaInicio),
        fechaFin: new FormControl(this.data.convocatoriaConceptoGastoCodigoEc?.fechaFin),
        observaciones: new FormControl(this.data.convocatoriaConceptoGastoCodigoEc?.observaciones),
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
    if (this.data.editModal) {
      formGroup.controls.codigoEconomico.disable();
      formGroup.controls.observaciones.disable();
    }

    return formGroup;
  }

  private notOverlapsSameConceptoGastoAndCodigoEconomico(data: IConvocatoriaConceptoGastoCodigoEcModalComponent): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }

      const codigoEconomico: ICodigoEconomicoGasto = control.value;
      const mesIncialConceptoGasto = data.convocatoriaConceptoGasto.mesInicial ? data.convocatoriaConceptoGasto.mesInicial : Number.MIN_VALUE;
      const mesFinalConceptoGasto = data.convocatoriaConceptoGasto.mesFinal ? data.convocatoriaConceptoGasto.mesFinal : Number.MAX_VALUE;

      const ranges = data.convocatoriaConceptoGastoCodigoEcsConvocatoria
        .filter(c => c.codigoEconomico.id === codigoEconomico.id && c.convocatoriaConceptoGasto.conceptoGasto.id === data.convocatoriaConceptoGasto.conceptoGasto.id)
        .map(c => {
          return {
            inicio: c.convocatoriaConceptoGasto.mesInicial ? c.convocatoriaConceptoGasto.mesInicial : Number.MIN_VALUE,
            fin: c.convocatoriaConceptoGasto.mesFinal ? c.convocatoriaConceptoGasto.mesFinal : Number.MAX_VALUE
          };
        });

      if (ranges.some(r => mesIncialConceptoGasto <= r.fin && r.inicio <= mesFinalConceptoGasto)) {
        return { overlappedConceptoAndCodigo: true };
      }

      return null;
    };
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

        const ranges = this.data.convocatoriaConceptoGastoCodigoEcsTabla.filter(
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
}
