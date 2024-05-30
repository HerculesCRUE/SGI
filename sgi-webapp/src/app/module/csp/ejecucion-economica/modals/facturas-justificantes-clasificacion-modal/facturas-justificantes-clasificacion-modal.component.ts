import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { BehaviorSubject, Observable, from, of } from 'rxjs';
import { distinctUntilChanged, filter, map, mergeMap, tap, toArray } from 'rxjs/operators';

const IMPORTE_INSCRIPCION_KEY = marker('csp.ejecucion-economica.facturas-justificantes.importe-inscripcion');
const PROYECTO_KEY = marker('csp.ejecucion-economica.facturas-justificantes.proyecto-sgi');
const PROYECTO_CONCEPTO_GASTO_KEY = marker('csp.proyecto-concepto-gasto');

export interface DatoEconomicoDetalleClasificacionModalData extends IDatoEconomicoDetalle {
  proyectosSgiIds: number[];
  proyecto: IProyecto;
  gastoProyecto: IGastoProyecto;
  vinculacion: string;
  tituloModal: string;
  showDatosCongreso: boolean;
  disableProyectoSgi: boolean;
}

enum ConceptoGastoTipo {
  PERMITIDO = 'PERMITIDO',
  TODOS = 'TODOS'
}

const CONCEPTO_GASTO_TIPO_MAP: Map<ConceptoGastoTipo, string> = new Map([
  [ConceptoGastoTipo.PERMITIDO, marker(`csp.proyecto-anualidad.anualidad-gasto.PERMITIDO`)],
  [ConceptoGastoTipo.TODOS, marker(`csp.proyecto-anualidad.anualidad-gasto.TODOS`)]
]);

@Component({
  templateUrl: './facturas-justificantes-clasificacion-modal.component.html',
  styleUrls: ['./facturas-justificantes-clasificacion-modal.component.scss']
})
export class FacturasJustificantesClasificacionModal extends DialogFormComponent<DatoEconomicoDetalleClasificacionModalData> implements OnInit {

  msgParamImporteInscripcion = {};
  msgParamProyecto = {};
  msgParamConceptoGastoEntity = {};

  proyectos$: Observable<IProyecto[]>;
  onProyectoOrConceptoGastoTipoChange$: Observable<any>;

  conceptosGasto$ = new BehaviorSubject<IConceptoGasto[]>([]);
  private conceptosGastoTodos: IConceptoGasto[];

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get CONCEPTO_GASTO_TIPO_MAP() {
    return CONCEPTO_GASTO_TIPO_MAP;
  }

  get ConceptoGastoTipo() {
    return ConceptoGastoTipo;
  }

  constructor(
    matDialogRef: MatDialogRef<FacturasJustificantesClasificacionModal>,
    @Inject(MAT_DIALOG_DATA) public data: DatoEconomicoDetalleClasificacionModalData,
    private conceptoGastoService: ConceptoGastoService,
    private proyectoService: ProyectoService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data.gastoProyecto?.id);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.loadProyectos();
  }

  protected buildFormGroup(): FormGroup {
    const proyecto = this.data.gastoProyecto?.proyectoId ? { id: this.data.gastoProyecto?.proyectoId } as IProyecto : undefined;

    const conceptoGasto = this.data.gastoProyecto?.conceptoGasto ?? null;
    const conceptoGastoFiltro = conceptoGasto ? ConceptoGastoTipo.TODOS : ConceptoGastoTipo.PERMITIDO;
    this.loadConceptosGasto(conceptoGastoFiltro, conceptoGasto, proyecto);

    const formGroup = new FormGroup(
      {
        proyecto: new FormControl({ value: proyecto, disabled: this.data.disableProyectoSgi }),
        conceptoGastoFiltro: new FormControl(conceptoGastoFiltro),
        conceptoGasto: new FormControl(conceptoGasto)
      }
    );

    if (this.data.showDatosCongreso) {
      formGroup.addControl('fechaCongreso', new FormControl(this.data.gastoProyecto?.fechaCongreso));
      formGroup.addControl('importeInscripcion', new FormControl(
        this.data.gastoProyecto?.importeInscripcion,
        [Validators.min(0), Validators.max(2_147_483_647)])
      );
      formGroup.addControl('observaciones', new FormControl(this.data.gastoProyecto?.observaciones, [Validators.maxLength(2000)]));
    }

    this.conceptosGasto$.pipe(
      filter(() => !!this.initialized$.value)
    ).subscribe(conceptosGasto => {
      if (conceptosGasto.length === 0) {
        formGroup.controls.conceptoGasto.disable();
      } else {
        formGroup.controls.conceptoGasto.enable();
      }
      formGroup.controls.conceptoGasto.setValue(null);
    });

    this.subscriptions.push(
      formGroup.controls?.conceptoGastoFiltro.valueChanges.pipe(
        distinctUntilChanged()
      ).subscribe(() => {
        this.loadConceptosGasto(formGroup.controls.conceptoGastoFiltro.value, formGroup.controls.conceptoGasto.value, formGroup.controls.proyecto.value);
      })
    );

    this.subscriptions.push(
      formGroup.controls?.proyecto.valueChanges.pipe(
        distinctUntilChanged()
      ).subscribe(() => {
        this.loadConceptosGasto(formGroup.controls.conceptoGastoFiltro.value, formGroup.controls.conceptoGasto.value, formGroup.controls.proyecto.value);
      })
    );

    return formGroup;
  }

  protected getValue(): DatoEconomicoDetalleClasificacionModalData {
    if (!this.data.gastoProyecto) {
      this.data.gastoProyecto = { gastoRef: this.data.id } as IGastoProyecto;
    }

    this.data.proyecto = this.formGroup.controls.proyecto.value;
    this.data.gastoProyecto.proyectoId = this.formGroup.controls.proyecto.value?.id;
    this.data.gastoProyecto.conceptoGasto = this.formGroup.controls.conceptoGasto.value;

    if (this.data.showDatosCongreso) {
      this.data.gastoProyecto.fechaCongreso = this.formGroup.controls.fechaCongreso.value;
      this.data.gastoProyecto.importeInscripcion = this.formGroup.controls.importeInscripcion.value;
      this.data.gastoProyecto.observaciones = this.formGroup.controls.observaciones.value;
    }

    return this.data;
  }

  public selectFirstProyectoIfOnlyOneOption(options: SelectValue<IProyecto>[]): void {
    if (options?.length === 1 && !this.formGroup.controls.proyecto.value) {
      this.formGroup.controls.proyecto.setValue(options[0].item);
    }
  }

  public selectFirstConceptoGastoIfOnlyOneOption(options: SelectValue<IConceptoGasto>[]): void {
    if (options?.length === 1 && !this.formGroup.controls.conceptoGasto.value) {
      this.formGroup.controls.conceptoGasto.setValue(options[0].item);
    }
  }

  displayerProyecto(proyecto: IProyecto): string {
    return proyecto?.titulo;
  }

  private loadProyectos(): void {
    if (this.data.proyectosSgiIds) {
      this.proyectos$ = from(this.data.proyectosSgiIds).pipe(
        mergeMap(proyectoId => this.proyectoService.findById(proyectoId)),
        toArray()
      )
    } else {
      this.proyectos$ = of([]);
    }
  }

  private loadConceptosGasto(
    conceptoGastoTipo: ConceptoGastoTipo,
    conceptoGastoSeleccionado?: IConceptoGasto,
    proyectoSeleccionado?: IProyecto
  ): void {
    let conceptosGasto$: Observable<IConceptoGasto[]>;

    if (conceptoGastoTipo === ConceptoGastoTipo.PERMITIDO && !proyectoSeleccionado?.id) {
      conceptosGasto$ = of([]);
    } else {
      let filter: RSQLSgiRestFilter;
      if (conceptoGastoTipo === ConceptoGastoTipo.PERMITIDO && !!proyectoSeleccionado?.id) {
        filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoSeleccionado.id.toString());
      }

      const queryOptionsConceptoGasto: SgiRestFindOptions = {
        filter
      };

      if (conceptoGastoTipo === ConceptoGastoTipo.TODOS && !!this.conceptosGastoTodos?.length) {
        conceptosGasto$ = of(this.conceptosGastoTodos);
      } else {
        conceptosGasto$ = this.conceptoGastoService.findAll(queryOptionsConceptoGasto).pipe(
          map(response => response.items),
          tap(conceptosGasto => {
            if (conceptoGastoTipo === ConceptoGastoTipo.TODOS) {
              this.conceptosGastoTodos = conceptosGasto;
            }
          })
        );
      }

    }

    this.subscriptions.push(
      conceptosGasto$.subscribe(conceptosGasto => {
        this.conceptosGasto$.next(conceptosGasto);

        if (!!conceptoGastoSeleccionado && conceptosGasto.some(conceptoGasto => conceptoGasto.id === conceptoGastoSeleccionado.id)) {
          this.formGroup.controls.conceptoGasto.setValue(conceptoGastoSeleccionado);
        }
      })
    );
  }

  private setupI18N(): void {
    this.translate.get(
      IMPORTE_INSCRIPCION_KEY
    ).subscribe((value) => this.msgParamImporteInscripcion = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      PROYECTO_KEY
    ).subscribe((value) => this.msgParamProyecto = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      PROYECTO_CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConceptoGastoEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE
    });
  }

}
