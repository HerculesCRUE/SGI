import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { TipoPartida } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { ProyectoConceptoGastoCodigoEcService } from '@core/services/csp/proyecto-concepto-gasto-codigo-ec.service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { BehaviorSubject, Observable, of, zip } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CODIGO_ECONOMICO_KEY = marker('csp.proyecto-anualidad.partida-gasto.concepto-gasto.codigo-economico');
const CODIGO_ECONOMICO_NO_PERMITIDO_KEY = marker('csp.proyecto-anualidad.partida-gasto.concepto-gasto.codigo-economico.no-permitido');
const CODIGO_ECONOMICO_PERMITIDO_KEY = marker('csp.proyecto-anualidad.partida-gasto.concepto-gasto.codigo-economico.permitido');
const CONCEPTO_GASTO_KEY = marker('csp.proyecto-anualidad.partida-gasto.concepto-gasto');
const PARTIDA_PRESUPUESTARIA_KEY = marker('csp.proyecto-anualidad.partida.partida-presupuestaria');
const PROYECTO_PARTIDA_GASTO_KEY = marker('csp.proyecto-anualidad.partida-gasto');
const PROYECTO_SGE_KEY = marker('csp.proyecto-anualidad.partida-gasto.identificador-sge');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoAnualidadGastoModalData {
  anualidadGasto: IAnualidadGasto;
  proyectoId: number;
  fechaInicioAnualidad: DateTime;
  fechaFinAnualidad: DateTime;
  isEdit: boolean;
  readonly: boolean;
}

export enum ConceptoGastoTipo {
  PERMITIDO = 'PERMITIDO',
  TODOS = 'TODOS'
}

export const CONCEPTO_GASTO_TIPO_MAP: Map<ConceptoGastoTipo, string> = new Map([
  [ConceptoGastoTipo.PERMITIDO, marker(`csp.proyecto-anualidad.anualidad-gasto.PERMITIDO`)],
  [ConceptoGastoTipo.TODOS, marker(`csp.proyecto-anualidad.anualidad-gasto.TODOS`)]
]);

enum CodigoEconomicoTipo {
  PERMITIDO = 'PERMITIDO',
  TODOS = 'TODOS'
}

const CODIGO_ECONOMICO_TIPO_MAP: Map<CodigoEconomicoTipo, string> = new Map([
  [CodigoEconomicoTipo.PERMITIDO, marker(`csp.proyecto-anualidad.anualidad-gasto.codigo-economico.PERMITIDO`)],
  [CodigoEconomicoTipo.TODOS, marker(`csp.proyecto-anualidad.anualidad-gasto.codigo-economico.TODOS`)]
]);

@Component({
  templateUrl: './proyecto-anualidad-gasto-modal.component.html',
  styleUrls: ['./proyecto-anualidad-gasto-modal.component.scss']
})
export class ProyectoAnualidadGastoModalComponent extends DialogFormComponent<ProyectoAnualidadGastoModalData> implements OnInit {

  textSaveOrUpdate: string;
  msgParamCodigosEconomicosPermitidos = {};
  msgParamCodigosEconomicosNoPermitidos = {};
  msgParamCodigoEconomicoEntity = {};
  msgParamConceptoGastoEntity = {};
  msgParamPartidaPresupuestariaEntity = {};
  msgParamImporteEntity = {};
  msgParamFechaPrevistaEntity = {};
  msgParamProyectoSgeEntity = {};
  title: string;

  readonly optionsConceptoGasto: ConceptoGastoTipo[];
  readonly optionsCodigoEconomico: CodigoEconomicoTipo[];

  proyectosSge$ = new BehaviorSubject<IProyectoProyectoSge[]>([]);
  proyectosPartida$ = new BehaviorSubject<IProyectoPartida[]>([]);

  conceptosGasto$ = new BehaviorSubject<IConceptoGasto[]>([]);
  codigosEconomicos$ = new BehaviorSubject<ICodigoEconomicoGasto[]>([]);
  private conceptosGastoTodos: IConceptoGasto[];
  private codigosEconomicosTodos: ICodigoEconomicoGasto[];

  conceptosGastoCodigoEcPermitidos = new MatTableDataSource<IProyectoConceptoGastoCodigoEc>();
  conceptosGastoCodigoEcNoPermitidos = new MatTableDataSource<IProyectoConceptoGastoCodigoEc>();
  @ViewChild(MatPaginator, { static: true }) paginatorCodigoEcPermitidos: MatPaginator;
  @ViewChild(MatSort, { static: true }) sortcodigoEcPermitidos: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginatorCodigoEcNoPermitidos: MatPaginator;
  @ViewChild(MatSort, { static: true }) sortcodigoEcNoPermitidos: MatSort;

  columnsCodigosEcPermitidos = ['conceptoGasto', 'importeMaximo', 'permitidoDesde', 'permitidoHasta', 'codigoEconomico'];
  columnsCodigosEcNoPermitidos = ['conceptoGasto', 'noPermitidoDesde', 'noPermitidoHasta', 'codigoEconomico'];

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get CONCEPTO_GASTO_TIPO_MAP() {
    return CONCEPTO_GASTO_TIPO_MAP;
  }

  get CODIGO_ECONOMICO_TIPO_MAP() {
    return CODIGO_ECONOMICO_TIPO_MAP;
  }

  constructor(
    matDialogRef: MatDialogRef<ProyectoAnualidadGastoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoAnualidadGastoModalData,
    private readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private readonly proyectoConceptoGastoService: ProyectoConceptoGastoService,
    private readonly conceptoGastoService: ConceptoGastoService,
    private readonly proyectoConceptoGastoCodigoEcService: ProyectoConceptoGastoCodigoEcService,
    private readonly codigoEconomicoGastoService: CodigoEconomicoGastoService
  ) {
    super(matDialogRef, data.isEdit);

    this.textSaveOrUpdate = this.data.isEdit ? MSG_ACEPTAR : MSG_ANADIR;

    this.subscriptions.push(
      this.proyectoService.findAllProyectosSgeProyecto(data.proyectoId)
        .subscribe((response) => this.proyectosSge$.next(response.items)));

    this.optionsConceptoGasto = Object.values(ConceptoGastoTipo);
    this.optionsCodigoEconomico = Object.values(CodigoEconomicoTipo);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {

    if (this.data.isEdit) {
      this.translate.get(
        PROYECTO_PARTIDA_GASTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_PARTIDA_GASTO_KEY,
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

    this.translate.get(
      CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamConceptoGastoEntity = {
        entity: value,
        ...MSG_PARAMS.GENDER.MALE,
        ...MSG_PARAMS.CARDINALIRY.SINGULAR
      }
    );

    this.translate.get(
      CODIGO_ECONOMICO_PERMITIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigosEconomicosPermitidos = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      CODIGO_ECONOMICO_NO_PERMITIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigosEconomicosNoPermitidos = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      CODIGO_ECONOMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoEconomicoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PARTIDA_PRESUPUESTARIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPartidaPresupuestariaEntity = {
      entity: value,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR, ...MSG_PARAMS.GENDER.FEMALE
    });

    this.translate.get(
      PROYECTO_SGE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamProyectoSgeEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });
  }

  protected getValue(): ProyectoAnualidadGastoModalData {
    this.data.anualidadGasto.proyectoSgeRef = this.formGroup.controls.identificadorSge.value.proyectoSge.id;
    this.data.anualidadGasto.codigoEconomico = this.formGroup.controls.codigoEconomico.value;
    this.data.anualidadGasto.importeConcedido = this.formGroup.controls.importeConcedido.value;
    this.data.anualidadGasto.importePresupuesto = this.formGroup.controls.importePresupuesto.value;
    this.data.anualidadGasto.proyectoPartida = this.formGroup.controls.partidaPresupuestaria.value;
    this.data.anualidadGasto.conceptoGasto =
      this.formGroup.controls.conceptoGasto.value?.conceptoGasto ? this.formGroup.controls.conceptoGasto.value?.conceptoGasto :
        this.formGroup.controls.conceptoGasto.value;
    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    const identificadorSge = this.data.anualidadGasto?.proyectoSgeRef
      ? {
        proyectoSge:
          {
            id: this.data.anualidadGasto?.proyectoSgeRef
          } as IProyectoSge
      } as IProyectoProyectoSge
      : null;
    const proyectoPartida = this.data.anualidadGasto?.proyectoPartida ?? null;

    const conceptoGasto = this.data.anualidadGasto?.conceptoGasto
      ? {
        id: this.data.anualidadGasto?.conceptoGasto?.id,
        nombre: this.data.anualidadGasto?.conceptoGasto?.nombre
      } as IConceptoGasto
      : null;

    const conceptoGastoFiltro = conceptoGasto ? ConceptoGastoTipo.TODOS : ConceptoGastoTipo.PERMITIDO;
    const codigoEconomicoFiltro = this.data.anualidadGasto?.codigoEconomico ? CodigoEconomicoTipo.TODOS : CodigoEconomicoTipo.PERMITIDO;

    this.loadConceptoGasto(conceptoGastoFiltro, conceptoGasto);
    this.loadCodigosEconomicos(conceptoGasto, this.data.anualidadGasto?.codigoEconomico, codigoEconomicoFiltro);

    const formGroup = new FormGroup(
      {
        identificadorSge: new FormControl(identificadorSge, Validators.required),
        conceptoGastoFiltro: new FormControl(conceptoGastoFiltro),
        conceptoGasto: new FormControl(conceptoGasto, Validators.required),
        codigoEconomicoFiltro: new FormControl(codigoEconomicoFiltro),
        codigoEconomico: new FormControl(this.data.anualidadGasto?.codigoEconomico),
        partidaPresupuestaria: new FormControl(proyectoPartida, Validators.required),
        importePresupuesto: new FormControl(this.data.anualidadGasto.importePresupuesto, Validators.required),
        importeConcedido: new FormControl(this.data.anualidadGasto.importeConcedido, Validators.required),
      }
    );

    const optionsProyectoPartida: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('tipoPartida', SgiRestFilterOperator.EQUALS, TipoPartida.GASTO)
    };

    this.subscriptions.push(
      this.proyectoService.findAllProyectoPartidas(this.data.proyectoId, optionsProyectoPartida)
        .subscribe((response) => this.proyectosPartida$.next(response.items))
    );

    if (!this.data.anualidadGasto?.proyectoSgeRef) {
      this.subscriptions.push(
        this.proyectosSge$.subscribe((values) => {
          if (values.length === 1) {
            this.formGroup.controls.identificadorSge.setValue(values[0]);
          }
        })
      );
    }

    if (!this.data.anualidadGasto?.proyectoPartida) {
      this.subscriptions.push(
        this.proyectosPartida$.subscribe((values) => {
          if (values.length === 1) {
            this.formGroup.controls.partidaPresupuestaria.setValue(values[0]);
          }
        })
      );
    }

    if (this.data.readonly) {
      formGroup.disable();
    }

    formGroup.controls?.conceptoGastoFiltro.valueChanges.subscribe(conceptoGastoTipo => {
      this.loadConceptoGasto(conceptoGastoTipo, this.formGroup.controls.conceptoGasto.value);
    });

    formGroup.controls?.codigoEconomicoFiltro.valueChanges.subscribe(codigoEconomicoTipo => {
      this.loadCodigosEconomicosCombo(codigoEconomicoTipo, this.formGroup.controls.codigoEconomico.value);
    });

    this.conceptosGasto$.subscribe(conceptosGasto => {
      if (conceptosGasto.length === 0) {
        formGroup.controls.conceptoGasto.disable();
      } else {
        formGroup.controls.conceptoGasto.enable();
      }
      formGroup.controls.conceptoGasto.setValue(null);
    });

    this.codigosEconomicos$.subscribe(codigosEconomico => {
      if (codigosEconomico.length === 0) {
        formGroup.controls.codigoEconomico.disable();
        formGroup.controls.codigoEconomico.setValue(null);
      } else {
        formGroup.controls.codigoEconomico.enable();
        formGroup.controls.codigoEconomico.setValue(null);
      }
    });

    return formGroup;
  }

  displayerIdentificadorSge(proyectoSge: IProyectoProyectoSge): string {
    return proyectoSge?.proyectoSge?.id;
  }

  sorterIdentificadorSge(o1: SelectValue<IProyectoProyectoSge>, o2: SelectValue<IProyectoProyectoSge>): number {
    return o1?.displayText.toString().localeCompare(o2?.displayText.toString());
  }

  comparerIdentificadorSge(o1: IProyectoProyectoSge, o2: IProyectoProyectoSge): boolean {
    if (o1 && o2) {
      return o1?.proyectoSge?.id === o2?.proyectoSge?.id;
    }
    return o1 === o2;
  }

  loadConceptoGasto(conceptoGastoTipo: ConceptoGastoTipo, conceptoGastoSeleccionado?: IConceptoGasto): void {
    let conceptosGasto$: Observable<IConceptoGasto[]>;

    if (conceptoGastoTipo === ConceptoGastoTipo.PERMITIDO) {
      const filter = new RSQLSgiRestFilter('permitido', SgiRestFilterOperator.EQUALS, 'true')
        .and('proyectoId', SgiRestFilterOperator.EQUALS, this.data.proyectoId.toString());

      if (this.data.fechaInicioAnualidad && this.data.fechaFinAnualidad) {
        filter.and(
          'inRangoProyectoAnualidad',
          SgiRestFilterOperator.BETWEEN,
          [LuxonUtils.toBackend(this.data.fechaInicioAnualidad), LuxonUtils.toBackend(this.data.fechaFinAnualidad)]
        );
      } else {
        filter
          .and('fechaInicioAnualidadMin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(this.data.fechaInicioAnualidad))
          .and('fechaFinAnualidadMax', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(this.data.fechaFinAnualidad));
      }

      const queryOptionsConceptoGasto: SgiRestFindOptions = {
        filter
      };

      conceptosGasto$ = this.proyectoConceptoGastoService.findAll(queryOptionsConceptoGasto).pipe(
        map(response =>
          response.items
            .map(proyectoConceptoGasto => proyectoConceptoGasto.conceptoGasto)
            .filter((c, i, conceptosGasto) => conceptosGasto.findIndex(c2 => (c2.id === c.id)) === i)
        )
      );
    } else {
      if (!this.conceptosGastoTodos) {
        conceptosGasto$ = this.conceptoGastoService.findAll().pipe(
          map(response => response.items),
          tap(conceptosGasto => this.conceptosGastoTodos = conceptosGasto)
        );
      } else {
        conceptosGasto$ = of(this.conceptosGastoTodos);
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

  /**
   * Carga las tablas de codigos economicos permitidos y no permitidos y el combo de cosigos economicos
   * para el concepto de gasto seleccionado.
   *
   * @param conceptoGasto El concepto de gasto para el que se cargan los codigos economicos
   * @param codigoEconomicoSeleccionado El codigo economico seleccionado
   * @param codigoEconomicoTipo Tipo de codigos economicos que se mostraran en el combo
   */
  loadCodigosEconomicos(
    conceptoGasto: IConceptoGasto,
    codigoEconomicoSeleccionado?: ICodigoEconomicoGasto,
    codigoEconomicoTipo?: CodigoEconomicoTipo): void {
    if (!conceptoGasto) {
      return;
    }

    this.loadCodigosEconomicosPermitidos(conceptoGasto, codigoEconomicoSeleccionado, codigoEconomicoTipo);
    this.loadCodigosEconomicosNoPermitidos(conceptoGasto);
  }

  /**
   * Carga la tabla de codigos economicos permitidos y el combo de codigos economicos si codigoEconomicoTipo es PERMITIDO
   *
   * @param conceptoGasto El concepto de gasto para el que se cargan los codigos economicos
   * @param codigoEconomicoSeleccionado El codigo economico seleccionado
   * @param codigoEconomicoTipo Tipo de codigos economicos que se mostraran en el combo
   */
  private loadCodigosEconomicosPermitidos(
    conceptoGasto: IConceptoGasto,
    codigoEconomicoSeleccionado: ICodigoEconomicoGasto,
    codigoEconomicoTipo: CodigoEconomicoTipo,
  ) {
    const queryOptionsConceptoGastoCodigoEcPermitidos: SgiRestFindOptions = {
      filter: this.buildFilterCodigosEconomicos(conceptoGasto.id, true)
    };

    this.conceptosGastoCodigoEcPermitidos.paginator = this.paginatorCodigoEcPermitidos;
    this.conceptosGastoCodigoEcPermitidos.sort = this.sortcodigoEcPermitidos;

    this.subscriptions.push(
      this.proyectoConceptoGastoCodigoEcService.findAll(queryOptionsConceptoGastoCodigoEcPermitidos).pipe(
        switchMap((codigosEconomicos) => {
          if (codigosEconomicos.items.length === 0) {
            return of([]);
          }
          const codigosEconomicosObservable = codigosEconomicos.items.
            map(codigoEconomico => {

              return this.proyectoConceptoGastoService.findById(codigoEconomico.proyectoConceptoGasto.id).pipe(
                map(proyectoConceptoGasto => {
                  codigoEconomico.proyectoConceptoGasto = proyectoConceptoGasto;
                  return codigoEconomico;
                }),
                switchMap(proyectoConceptoGastoCodgioEc => {
                  return this.codigoEconomicoGastoService.findById(proyectoConceptoGastoCodgioEc.codigoEconomico.id)
                    .pipe(
                      map(response => {
                        proyectoConceptoGastoCodgioEc.codigoEconomico = response;
                        return proyectoConceptoGastoCodgioEc;
                      })
                    );
                })
              );
            });

          return zip(...codigosEconomicosObservable);
        })
      ).subscribe(codigosEconomicos => {
        this.conceptosGastoCodigoEcPermitidos.data = codigosEconomicos;
        this.loadCodigosEconomicosCombo(codigoEconomicoTipo, codigoEconomicoSeleccionado);
      })
    );
  }

  private loadCodigosEconomicosNoPermitidos(conceptoGasto: IConceptoGasto) {
    if (!conceptoGasto?.id) {
      return;
    }

    const queryOptionsConceptoGastoCodigoEcNoPermitidos: SgiRestFindOptions = {
      filter: this.buildFilterCodigosEconomicos(conceptoGasto.id, false)
    };

    this.conceptosGastoCodigoEcNoPermitidos.paginator = this.paginatorCodigoEcNoPermitidos;
    this.conceptosGastoCodigoEcNoPermitidos.sort = this.sortcodigoEcNoPermitidos;
    this.subscriptions.push(
      this.proyectoConceptoGastoCodigoEcService.findAll(queryOptionsConceptoGastoCodigoEcNoPermitidos).pipe(
        switchMap((codigosEconomicos) => {
          if (codigosEconomicos.items.length === 0) {
            return of([]);
          }
          const codigosEconomicosObservable = codigosEconomicos.items.
            map(codogoEconomico => {

              return this.proyectoConceptoGastoService.findById(codogoEconomico.proyectoConceptoGasto.id).pipe(
                map(proyectoConceptoGasto => {
                  codogoEconomico.proyectoConceptoGasto = proyectoConceptoGasto;
                  return codogoEconomico;
                }),
                switchMap(proyectoConceptoGastoCodigoEc => {
                  return this.codigoEconomicoGastoService.findById(proyectoConceptoGastoCodigoEc.codigoEconomico.id)
                    .pipe(
                      map(response => {
                        proyectoConceptoGastoCodigoEc.codigoEconomico = response;
                        return proyectoConceptoGastoCodigoEc;
                      })
                    );
                })
              );
            });

          return zip(...codigosEconomicosObservable);
        })
      ).subscribe(
        (codigosEconomicos) =>
          this.conceptosGastoCodigoEcNoPermitidos.data = codigosEconomicos
      ));
  }

  private buildFilterCodigosEconomicos(conceptoGastoId: number, permitido: boolean): SgiRestFilter {
    const filter = new RSQLSgiRestFilter('proyectoConceptoGasto.permitido', SgiRestFilterOperator.EQUALS, permitido.toString())
      .and('proyectoConceptoGasto.conceptoGasto.id', SgiRestFilterOperator.EQUALS, conceptoGastoId.toString())
      .and('proyectoConceptoGasto.proyectoId', SgiRestFilterOperator.EQUALS, this.data.proyectoId.toString());

    if (this.data.fechaInicioAnualidad && this.data.fechaFinAnualidad) {
      filter.and(
        'inRangoProyectoAnualidad',
        SgiRestFilterOperator.BETWEEN,
        [LuxonUtils.toBackend(this.data.fechaInicioAnualidad), LuxonUtils.toBackend(this.data.fechaFinAnualidad)]
      );
    } else {
      filter
        .and('fechaInicioAnualidadMin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(this.data.fechaInicioAnualidad))
        .and('fechaFinAnualidadMax', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(this.data.fechaFinAnualidad));
    }

    return filter;
  }

  loadCodigosEconomicosCombo(codigoEconomicoTipo?: CodigoEconomicoTipo, codigoEconomicoSeleccionado?: ICodigoEconomicoGasto) {
    let codigosEconomicos$: Observable<ICodigoEconomicoGasto[]>;

    if ((codigoEconomicoTipo ?? this.formGroup?.controls.codigoEconomicoFiltro.value) === CodigoEconomicoTipo.PERMITIDO) {
      codigosEconomicos$ = of(this.conceptosGastoCodigoEcPermitidos.data
        .map(proyectoConceptoGastoCodigoEcs =>
          proyectoConceptoGastoCodigoEcs.codigoEconomico
        ));
    } else {
      if (!this.codigosEconomicosTodos) {
        codigosEconomicos$ = this.codigoEconomicoGastoService.findAll().pipe(
          map(response => response.items),
          tap(codigosEconomicos => this.codigosEconomicosTodos = codigosEconomicos)
        );
      } else {
        codigosEconomicos$ = of(this.codigosEconomicosTodos);
      }
    }

    this.subscriptions.push(
      codigosEconomicos$.subscribe(codigosEconomicos => {
        this.codigosEconomicos$.next(codigosEconomicos);

        if (!!codigoEconomicoSeleccionado
          && codigosEconomicos.some(codigoEconomico => codigoEconomico.id === codigoEconomicoSeleccionado.id)) {
          this.formGroup.controls.codigoEconomico.setValue(codigoEconomicoSeleccionado);
        }
      })
    );

  }

  displayerCodigoEconomico(codigoEconomico: ICodigoEconomicoGasto): string {
    return `${codigoEconomico?.id} - ${codigoEconomico?.nombre ?? ''}` ?? '';
  }

  displayerProyectoPartida(proyectoPartida: IProyectoPartida): string {
    return proyectoPartida?.codigo;
  }

}
