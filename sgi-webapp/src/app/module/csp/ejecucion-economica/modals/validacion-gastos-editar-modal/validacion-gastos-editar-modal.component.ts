import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { Estado, ESTADO_MAP, IEstadoGastoProyecto } from '@core/models/csp/estado-gasto-proyecto';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { GastoService } from '@core/services/sge/gasto/gasto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { ConceptoGastoTipo, CONCEPTO_GASTO_TIPO_MAP } from '../../../proyecto-anualidad/modals/proyecto-anualidad-gasto-modal/proyecto-anualidad-gasto-modal.component';

const PROYECTO_SGI_KEY = marker('csp.ejecucion-economica.validacion-gastos.proyecto-sgi');
const CONCEPTO_GASTO_KEY = marker('csp.ejecucion-economica.validacion-gastos.concepto-gasto');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const MSG_REJECT = marker('reject.entity');


export interface GastoDetalleModalData extends IDatoEconomicoDetalle {
  estado: IEstadoGastoProyecto;
  proyectosSgi: IProyecto[];
  proyecto: IProyecto;
  gastoProyecto: IGastoProyecto;
  vinculacion: string;
  conceptoGasto: IConceptoGasto;
}

export interface GastoDetalleModalData extends IDatoEconomicoDetalle {
  estado: IEstadoGastoProyecto;
  gastoProyecto: IGastoProyecto;
}

@Component({
  templateUrl: './validacion-gastos-editar-modal.component.html',
  styleUrls: ['./validacion-gastos-editar-modal.component.scss']
})
export class ValidacionGastosEditarModalComponent
  extends BaseModalComponent<GastoDetalleModalData, ValidacionGastosEditarModalComponent>
  implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesInline: FxFlexProperties;

  msgParamImporteInscripcion = {};
  msgParamProyecto = {};
  msgParamConceptoGastoEntity = {};
  msgParamProyectoSgiEntity = {};

  textoCrearSuccess: string;
  textoCrearError: string;
  textoGastoRechazado: string;

  proyectosSgi$ = new BehaviorSubject<IProyectoProyectoSge[]>([]);
  conceptosGasto$: Observable<IConceptoGasto[] | IProyectoConceptoGasto[]>;

  optionsConceptoGasto: string[];
  optionsEstadoGasto: string[];

  showComment: boolean = false;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get CONCEPTO_GASTO_TIPO_MAP() {
    return CONCEPTO_GASTO_TIPO_MAP;
  }

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ValidacionGastosEditarModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GastoDetalleModalData,
    private readonly proyectoProyectoSGEService: ProyectoProyectoSgeService,
    private readonly proyectoConceptoGastoService: ProyectoConceptoGastoService,
    private readonly conceptoGastoService: ConceptoGastoService,
    private readonly gastoProyectoService: GastoProyectoService,
    private proyectoService: ProyectoService,
    private gastoService: GastoService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '3';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    const queryOptionsProyectosSGI: SgiRestFindOptions = {};
    queryOptionsProyectosSGI.filter = new RSQLSgiRestFilter('proyectoSgeRef', SgiRestFilterOperator.EQUALS, data.proyectoId);

    this.subscriptions.push(
      this.proyectoProyectoSGEService.findAll(queryOptionsProyectosSGI)
        .pipe(
          switchMap(responseA => {
            return from(responseA.items)
              .pipe(
                mergeMap(response =>
                  this.proyectoService.findById(response.proyecto.id)
                    .pipe(
                      map(proyecto => {
                        response.proyecto = proyecto;
                        return responseA.items;
                      })
                    )
                ),
                takeLast(1)
              )
          }
          )
        ).subscribe((responseC) => this.proyectosSgi$.next(responseC)));

    let options = [];
    CONCEPTO_GASTO_TIPO_MAP.forEach((value) => options.push(value));
    this.optionsConceptoGasto = options;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.subscriptions.push(this.formGroup.get('estadoGasto').valueChanges.subscribe(() => this.validarEstado()));
    this.validarEstado();
  }

  protected getFormGroup(): FormGroup {
    const identificadorSgi = this.data.gastoProyecto?.proyectoId
      ? {
        proyecto:
          {
            id: this.data.gastoProyecto?.proyectoId
          } as IProyecto
      } as IProyectoProyectoSge
      : null;

    let conceptoGasto;
    if (this.data.gastoProyecto?.conceptoGasto == undefined) {
      conceptoGasto = {
        id: this.data.gastoProyecto?.conceptoGasto?.id,
        nombre: this.data.gastoProyecto?.conceptoGasto?.nombre
      } as IConceptoGasto
    } else {
      conceptoGasto = null;
    }

    const conceptoGastoFiltro = CONCEPTO_GASTO_TIPO_MAP.get(ConceptoGastoTipo.PERMITIDO);
    this.loadConceptoGasto(conceptoGastoFiltro);

    const estadoGasto = this.data.gastoProyecto?.estado.estado ?? null;
    const fechaCongreso = this.data.gastoProyecto?.fechaCongreso ?? null;
    const importeInscripcion = this.data.gastoProyecto?.importeInscripcion ?? null;
    const observaciones = this.data.gastoProyecto?.observaciones ?? null;
    const comentarioEstado = null;

    const formGroup = new FormGroup(
      {
        identificadorSgi: new FormControl(identificadorSgi),
        conceptoGastoFiltro: new FormControl(conceptoGastoFiltro),
        conceptoGasto: new FormControl(conceptoGasto),
        estadoGasto:
          new FormControl({ value: estadoGasto, disabled: this.data.gastoProyecto?.estado?.estado == Estado.VALIDADO }, Validators.required),
        fechaCongreso: new FormControl(fechaCongreso),
        importeInscripcion: new FormControl(importeInscripcion,
          [
            Validators.min(0),
            Validators.max(2_147_483_647)
          ]),
        observaciones: new FormControl(observaciones,
          [
            Validators.maxLength(2000)
          ]),

        comentarioEstado: new FormControl(comentarioEstado,
          [
            Validators.maxLength(2000)
          ])
      }
    );

    if (!this.data.proyecto?.id) {
      this.subscriptions.push(
        this.proyectosSgi$.subscribe((values) => {
          if (values.length === 1) {
            this.formGroup.controls.identificadorSgi.setValue(values[0]);
          }
        })
      );
    }

    if (!this.data.conceptoGasto?.nombre) {
      this.subscriptions.push(
        this.conceptosGasto$.subscribe((values) => {
          if (values.length === 1) {
            this.formGroup.controls.conceptoGasto.setValue(values[0]);
          }
        })
      );
    }

    return formGroup;
  }

  protected getDatosForm(): GastoDetalleModalData {
    const gastoProyecto: IGastoProyecto = {
      id: this.data.gastoProyecto?.id ?? null,
      proyectoId: Number(this.formGroup.controls.identificadorSgi.value?.proyecto.id),
      gastoRef: this.data.id,
      conceptoGasto: this.formGroup.controls.conceptoGasto.value ?? null,
      estado: undefined,
      fechaCongreso: this.formGroup.controls.fechaCongreso.value,
      importeInscripcion: this.formGroup.controls.importeInscripcion.value,
      observaciones: this.formGroup.controls.observaciones.value
    };

    let comentario = this.formGroup.controls.comentarioEstado.value;
    let estado: IEstadoGastoProyecto = {
      comentario: comentario,
      estado: this.formGroup.controls.estadoGasto.value,
      id: undefined,
      fechaEstado: undefined,
      gastoProyectoId: undefined
    }

    gastoProyecto.estado = estado;
    let subscription = new Observable<IGastoProyecto>();

    if (this.formGroup.get('estadoGasto').value == Estado.RECHAZADO) {
      this.gastoService.rechazarGasto(gastoProyecto.gastoRef, comentario).pipe(
        tap(() => this.snackBarService.showSuccess(this.textoGastoRechazado))
      )
      return this.data;
    }

    if (gastoProyecto.id != null) {
      subscription = this.gastoProyectoService.update(gastoProyecto.id, gastoProyecto);
    } else {
      subscription = this.gastoProyectoService.create(gastoProyecto);
    }

    subscription.subscribe(
      () => {
        if (this.formGroup.get('estadoGasto').value == Estado.VALIDADO) {
          this.gastoService.validarGasto(gastoProyecto.gastoRef, comentario).pipe(
            tap(() => this.snackBarService.showSuccess(this.textoCrearSuccess))
          )
        }
        return this.data;
      },
      (error) => {
        console.log(error);
        this.snackBarService.showError(this.textoCrearError);
      }
    );
    return this.data;
  }

  private setupI18N(): void {

    this.translate.get(
      PROYECTO_SGI_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamProyectoSgiEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConceptoGastoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REJECT,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoGastoRechazado = value);

    this.translate.get(
      CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearError = value);
  }

  loadConceptoGasto(conceptoGastoTipo: string) {
    let conceptoGasto: ConceptoGastoTipo;
    CONCEPTO_GASTO_TIPO_MAP.forEach((value: string, key: ConceptoGastoTipo) => {
      if (value === conceptoGastoTipo) {
        conceptoGasto = key;
      }
    });

    if (conceptoGasto === ConceptoGastoTipo.PERMITIDO) {
      const queryOptionsConceptoGasto: SgiRestFindOptions = {};
      queryOptionsConceptoGasto.filter = new RSQLSgiRestFilter('permitido', SgiRestFilterOperator.EQUALS, 'true');

      this.conceptosGasto$ = this.proyectoConceptoGastoService.findAll(queryOptionsConceptoGasto).pipe(
        map(response => {
          var hash = {};
          let array: any[] = response.items.filter(function (current) {
            var exists = !hash[current?.conceptoGasto?.id];
            hash[current?.conceptoGasto?.id] = true;
            return exists;
          });
          return array
        })
      );
    } else
      this.conceptosGasto$ = this.conceptoGastoService.findAll().pipe(
        map(response => response.items)
      );
  }

  displayerConceptoGasto(conceptoGasto: IConceptoGasto | IProyectoConceptoGasto): string {
    return 'conceptoGasto' in conceptoGasto ? conceptoGasto?.conceptoGasto.nombre : conceptoGasto.nombre;
  }

  displayerIdentificadorSgi(proyectoProyectoSGE: IProyectoProyectoSge): string {
    return proyectoProyectoSGE.proyecto?.titulo?.toString() + ' (' + proyectoProyectoSGE.proyecto?.fechaInicio?.toLocaleString(DateTime.DATE_SHORT) + '-' + proyectoProyectoSGE.proyecto?.fechaFin?.toLocaleString(DateTime.DATE_SHORT) + ')';
  }

  sorterIdentificadorSgi(o1: SelectValue<IProyectoProyectoSge>, o2: SelectValue<IProyectoProyectoSge>): number {
    return o1?.displayText.toString().localeCompare(o2?.displayText.toString());
  }

  comparerIdentificadorSgi(o1: IProyectoProyectoSge, o2: IProyectoProyectoSge): boolean {
    if (o1 && o2) {
      return o1?.proyectoSge?.id === o2?.proyectoSge?.id;
    }
    return o1 === o2;
  }

  private validarEstado() {

    if (this.data.gastoProyecto?.estado?.estado != this.formGroup.get('estadoGasto').value) {
      this.showComment = true;
    } else {
      this.showComment = false;
    }
    if (this.data.gastoProyecto?.estado?.estado == Estado.VALIDADO) {
      this.formGroup.get('identificadorSgi').setValidators(Validators.required);
      this.formGroup.get('conceptoGasto').setValidators(Validators.required);
    } else {
      if (this.formGroup.get('estadoGasto').value === undefined) {
        this.formGroup.get('estadoGasto').enable();
      }

      if (this.formGroup.get('estadoGasto').value == 'VALIDADO') {
        this.formGroup.get('identificadorSgi').setValidators(Validators.required);
        this.formGroup.get('conceptoGasto').setValidators(Validators.required);

      } else {
        this.formGroup.get('identificadorSgi').clearValidators();
        this.formGroup.get('conceptoGasto').clearValidators();

      }
    }
  }

}

