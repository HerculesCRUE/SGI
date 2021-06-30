import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatRadioChange } from '@angular/material/radio';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { TipoPartida } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { ProyectoConceptoGastoCodigoEcService } from '@core/services/csp/proyecto-concepto-gasto-codigo-ec.service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { BehaviorSubject, Observable, of, zip } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CODIGO_ECONOMICO_KEY = marker('csp.proyecto-anualidad.partida-gasto.concepto-gasto.codigo-economico');
const CODIGO_ECONOMICO_NO_PERMITIDO_KEY = marker('csp.proyecto-anualidad.partida-gasto.concepto-gasto.codigo-economico.no-permitido');
const CODIGO_ECONOMICO_PERMITIDO_KEY = marker('csp.proyecto-anualidad.partida-gasto.concepto-gasto.codigo-economico.permitido');
const CONCEPTO_GASTO_KEY = marker('csp.proyecto-anualidad.partida-gasto.concepto-gasto');
const PARTIDA_PRESUPUESTARIA_KEY = marker('csp.proyecto-anualidad.partida.partida-presupuestaria');
const PROYECTO_PARTIDA_GASTO_KEY = marker('csp.proyecto-anualidad.partida-gasto');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoAnualidadGastoModalData {
  anualidadGasto: IAnualidadGasto;
  proyectoId: number;
  fechaInicioAnualidad: DateTime;
  fechaFinAnualidad: DateTime;
  isEdit: boolean;
}

export enum ConceptoGastoTipo {
  PERMITIDO = 'PERMITIDO',
  TODOS = 'TODOS'
}

export const CONCEPTO_GASTO_TIPO_MAP: Map<ConceptoGastoTipo, string> = new Map([
  [ConceptoGastoTipo.PERMITIDO, marker(`csp.proyecto-anualidad.anualidad-gasto.PERMITIDO`)],
  [ConceptoGastoTipo.TODOS, marker(`csp.proyecto-anualidad.anualidad-gasto.TODOS`)]
]);

export enum CodigoEconomicoTipo {
  PERMITIDO = 'PERMITIDO',
  TODOS = 'TODOS'
}

export const CODIGO_ECONOMICO_TIPO_MAP: Map<CodigoEconomicoTipo, string> = new Map([
  [CodigoEconomicoTipo.PERMITIDO, marker(`csp.proyecto-anualidad.anualidad-gasto.codigo-economico.PERMITIDO`)],
  [CodigoEconomicoTipo.TODOS, marker(`csp.proyecto-anualidad.anualidad-gasto.codigo-economico.TODOS`)]
]);

@Component({
  templateUrl: './proyecto-anualidad-gasto-modal.component.html',
  styleUrls: ['./proyecto-anualidad-gasto-modal.component.scss']
})
export class ProyectoAnualidadGastoModalComponent extends
  BaseModalComponent<ProyectoAnualidadGastoModalData, ProyectoAnualidadGastoModalComponent>
  implements OnInit {

  textSaveOrUpdate: string;
  msgParamCodigosEconomicosPermitidos = {};
  msgParamCodigosEconomicosNoPermitidos = {};
  msgParamCodigoEconomicoEntity = {};
  msgParamConceptoGastoEntity = {};
  msgParamPartidaPresupuestariaEntity = {};
  msgParamImporteEntity = {};
  msgParamFechaPrevistaEntity = {};
  title: string;

  optionsConceptoGasto: string[];
  optionsCodigoEconomico: string[];

  proyectosSge$ = new BehaviorSubject<IProyectoProyectoSge[]>([]);
  proyectosPartida$ = new BehaviorSubject<IProyectoPartida[]>([]);

  conceptosGasto$: Observable<IConceptoGasto[] | IProyectoConceptoGasto[]>;
  codigosEconomicos$: Observable<IProyectoConceptoGastoCodigoEc[] | ICodigoEconomicoGasto[]>;

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

  get CONCEPTO_GASTO_PERMITIDO_MAP() {
    return CONCEPTO_GASTO_TIPO_MAP;
  }

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ProyectoAnualidadGastoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoAnualidadGastoModalData,
    private readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private readonly proyectoConceptoGastoService: ProyectoConceptoGastoService,
    private readonly conceptoGastoService: ConceptoGastoService,
    private readonly proyectoConceptoGastoCodigoEcService: ProyectoConceptoGastoCodigoEcService,
    private readonly codigoEconomicoGastoService: CodigoEconomicoGastoService
  ) {
    super(snackBarService, matDialogRef, data);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';

    this.textSaveOrUpdate = this.data.isEdit ? MSG_ACEPTAR : MSG_ANADIR;

    this.subscriptions.push(
      this.proyectoService.findAllProyectosSgeProyecto(data.proyectoId)
        .subscribe((response) => this.proyectosSge$.next(response.items)));

    let options = [];
    CONCEPTO_GASTO_TIPO_MAP.forEach((value) => options.push(value));
    this.optionsConceptoGasto = options;

    options = [];
    CODIGO_ECONOMICO_TIPO_MAP.forEach((value) => options.push(value));
    this.optionsCodigoEconomico = options;

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
    ).subscribe((value) => this.msgParamConceptoGastoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

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
    ).subscribe((value) => this.msgParamPartidaPresupuestariaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });
  }

  protected getDatosForm(): ProyectoAnualidadGastoModalData {
    this.data.anualidadGasto.proyectoSgeRef = this.formGroup.controls.identificadorSge.value.proyectoSge.id;
    this.data.anualidadGasto.codigoEconomicoRef =
      this.formGroup.controls.codigoEconomico.value?.id;
    this.data.anualidadGasto.importeConcedido = this.formGroup.controls.importeConcedido.value;
    this.data.anualidadGasto.importePresupuesto = this.formGroup.controls.importePresupuesto.value;
    this.data.anualidadGasto.proyectoPartida = this.formGroup.controls.partidaPresupuestaria.value;
    this.data.anualidadGasto.conceptoGasto =
      this.formGroup.controls.conceptoGasto.value?.conceptoGasto ? this.formGroup.controls.conceptoGasto.value?.conceptoGasto :
        this.formGroup.controls.conceptoGasto.value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const codigoEconomico = this.data.anualidadGasto?.codigoEconomicoRef
      ? { id: this.data.anualidadGasto?.codigoEconomicoRef } as ICodigoEconomicoGasto
      : null;
    const identificadorSge = this.data.anualidadGasto?.proyectoSgeRef
      ? {
        proyectoSge:
          {
            id: this.data.anualidadGasto?.proyectoSgeRef
          } as IProyectoSge
      } as IProyectoProyectoSge
      : null;
    const proyectoPartida = this.data.anualidadGasto?.proyectoPartida
      ? { id: this.data.anualidadGasto?.proyectoPartida.id } as IProyectoPartida
      : null;

    const conceptoGasto = this.data.anualidadGasto?.conceptoGasto
      ? {
        id: this.data.anualidadGasto?.conceptoGasto?.id,
        nombre: this.data.anualidadGasto?.conceptoGasto?.nombre
      } as IConceptoGasto
      : null;

    let conceptoGastoFiltro: string;
    if (conceptoGasto) {
      conceptoGastoFiltro = CONCEPTO_GASTO_TIPO_MAP.get(ConceptoGastoTipo.TODOS);

      this.loadCodigosEconomicosInfo(conceptoGasto);
    } else {
      conceptoGastoFiltro = CONCEPTO_GASTO_TIPO_MAP.get(ConceptoGastoTipo.PERMITIDO);
    }
    this.loadConceptoGasto(conceptoGastoFiltro);
    const codigoEconomicoFiltro = CODIGO_ECONOMICO_TIPO_MAP.get(CodigoEconomicoTipo.TODOS);
    this.loadCodigoEconomico(codigoEconomicoFiltro);

    const formGroup = new FormGroup(
      {
        identificadorSge: new FormControl(identificadorSge, Validators.required),
        conceptoGastoFiltro: new FormControl(conceptoGastoFiltro),
        conceptoGasto: new FormControl(conceptoGasto, Validators.required),
        codigoEconomicoFiltro: new FormControl(codigoEconomicoFiltro),
        codigoEconomico: new FormControl(codigoEconomico),
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
        .subscribe((response) => {
          if (response.items.length === 1 && !identificadorSge) {
            formGroup.controls.identificadorSge.setValue(response.items[0].id);
          }
          this.proyectosPartida$.next(response.items);
        }
        )
    );

    return formGroup;
  }

  displayerIdentificadorSge(proyectoSge: IProyectoProyectoSge): string {
    return proyectoSge?.proyectoSge?.id;
  }

  sorterIdentificadorSge(o1: SelectValue<IProyectoSge>, o2: SelectValue<IProyectoSge>): number {
    return o1?.displayText.toString().localeCompare(o2?.displayText.toString());
  }

  comparerIdentificadorSge(o1: IProyectoSge, o2: IProyectoSge): boolean {
    if (o1 && o2) {
      return o1?.id === o2?.id;
    }
    return o1 === o2;
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
      queryOptionsConceptoGasto.filter.and('fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL,
        LuxonUtils.toBackend(this.data.fechaInicioAnualidad))
        .and('fechaFin', SgiRestFilterOperator.LOWER_OR_EQUAL,
          LuxonUtils.toBackend(this.data.fechaFinAnualidad))
        .and('proyectoId', SgiRestFilterOperator.EQUALS, this.data.proyectoId.toString());

      this.conceptosGasto$ = this.proyectoConceptoGastoService.findAll(queryOptionsConceptoGasto).pipe(
        map(response => response.items)
      );
    } else {
      this.conceptosGasto$ = this.conceptoGastoService.findAll().pipe(
        map(response => response.items)
      );
    }

    this.loadCodigoEconomico(this.formGroup?.controls.codigoEconomicoFiltro.value
      ? this.formGroup?.controls.codigoEconomicoFiltro.value : CODIGO_ECONOMICO_TIPO_MAP.get(CodigoEconomicoTipo.TODOS));
  }

  loadCodigosEconomicosInfo(conceptoGastoSeleccionado: IConceptoGasto | IProyectoConceptoGasto) {
    let conceptoGastoId: number;
    if ('conceptoGasto' in conceptoGastoSeleccionado) {
      conceptoGastoId = conceptoGastoSeleccionado.conceptoGasto.id;
    } else {
      conceptoGastoId = conceptoGastoSeleccionado.id;
    }

    const queryOptionsConceptoGastoCodigoEcPermitidos: SgiRestFindOptions = {};
    queryOptionsConceptoGastoCodigoEcPermitidos.filter =
      new RSQLSgiRestFilter('proyectoConceptoGasto.permitido', SgiRestFilterOperator.EQUALS, 'true');
    queryOptionsConceptoGastoCodigoEcPermitidos.filter.and('inRangoProyectoAnualidadInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL,
      LuxonUtils.toBackend(this.data.fechaInicioAnualidad))
      .and('inRangoProyectoAnualidadFin', SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(this.data.fechaFinAnualidad))
      .and('proyectoConceptoGasto.conceptoGasto.id', SgiRestFilterOperator.EQUALS, conceptoGastoId.toString())
      .and('proyectoConceptoGasto.proyectoId', SgiRestFilterOperator.EQUALS, this.data.proyectoId.toString());

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
              );
            });

          return zip(...codigosEconomicosObservable);
        })
      ).subscribe(
        (codigosEconomicos) =>
          this.conceptosGastoCodigoEcPermitidos.data = codigosEconomicos
      )
    );

    const queryOptionsConceptoGastoCodigoEcNoPermitidos: SgiRestFindOptions = {};
    queryOptionsConceptoGastoCodigoEcNoPermitidos.filter =
      new RSQLSgiRestFilter('proyectoConceptoGasto.permitido', SgiRestFilterOperator.EQUALS, 'false');
    queryOptionsConceptoGastoCodigoEcNoPermitidos.filter.and('inRangoProyectoAnualidadInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL,
      LuxonUtils.toBackend(this.data.fechaInicioAnualidad))
      .and('inRangoProyectoAnualidadFin', SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(this.data.fechaFinAnualidad))
      .and('proyectoConceptoGasto.conceptoGasto.id', SgiRestFilterOperator.EQUALS, conceptoGastoId.toString())
      .and('proyectoConceptoGasto.proyectoId', SgiRestFilterOperator.EQUALS, this.data.proyectoId.toString());

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
              );
            });

          return zip(...codigosEconomicosObservable);
        })
      ).subscribe(
        (codigosEconomicos) =>
          this.conceptosGastoCodigoEcNoPermitidos.data = codigosEconomicos
      ));
  }

  displayerConceptoGasto(conceptoGasto: IConceptoGasto | IProyectoConceptoGasto): string {
    return 'conceptoGasto' in conceptoGasto ? conceptoGasto?.conceptoGasto.nombre : conceptoGasto.nombre;
  }

  loadCodigoEconomico(codigoEconomicoTipoSeleccionado: string) {
    let codigoEconomicoTipo: CodigoEconomicoTipo;

    CODIGO_ECONOMICO_TIPO_MAP.forEach((value: string, key: CodigoEconomicoTipo) => {
      if (value === codigoEconomicoTipoSeleccionado) {
        codigoEconomicoTipo = key;
      }
    });

    if (codigoEconomicoTipo === CodigoEconomicoTipo.PERMITIDO) {
      this.codigosEconomicos$ = of(this.conceptosGastoCodigoEcPermitidos.data);
    } else {
      this.codigosEconomicos$ = this.codigoEconomicoGastoService.findAll().pipe(
        map(response => response.items)
      );
    }
  }

  displayerCodigoEconomico(codigoEconomico: IProyectoConceptoGastoCodigoEc | ICodigoEconomicoGasto): string {
    return 'proyectoConceptoGasto' in codigoEconomico ?
      codigoEconomico?.codigoEconomicoRef : codigoEconomico.id.toString();
  }

  displayerProyectoPartida(proyectoPartida: IProyectoPartida): string {
    return proyectoPartida?.codigo;
  }

}
