import { FormControl, FormGroup } from '@angular/forms';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { CardinalidadRelacionSgiSge, IConfiguracion, ValidacionClasificacionGastos } from '@core/models/csp/configuracion';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { TipoEntidad } from '@core/models/csp/relacion-ejecucion-economica';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoConceptoGastoCodigoEcService } from '@core/services/csp/proyecto-concepto-gasto-codigo-ec.service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable, from, of } from 'rxjs';
import { combineAll, concatMap, filter, map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaWithResponsables } from '../ejecucion-economica.action.service';
import { DesgloseEconomicoFragment, IColumnDefinition, IDesgloseEconomicoExportData, RowTreeDesglose } from './desglose-economico.fragment';

export interface IDesglose extends IDatoEconomico {
  proyecto: IProyecto;
  agrupacionGasto: IProyectoAgrupacionGasto;
  conceptoGasto: IConceptoGasto;
  clasificadoAutomaticamente: boolean;
  gastoProyectoId: number;
}

export enum GastosClasficadosSgiEnum {
  TODOS = "TODOS",
  SI = "SI",
  NO = "NO"
}

export abstract class FacturasJustificantesFragment extends DesgloseEconomicoFragment<IDesglose> {

  readonly gastosClasficadosSgiControl = new FormControl();

  protected updatedGastosProyectos: Map<string, IGastoProyecto> = new Map();

  private proyectosMap = new Map<string, IProyecto>();
  private proyectoConceptoGastosMap = new Map<string, IProyectoConceptoGasto>();

  readonly formGroupFechas = new FormGroup({
    devengoDesde: new FormControl(),
    devengoHasta: new FormControl(),
    contabilizacionDesde: new FormControl(),
    contabilizacionHasta: new FormControl(),
    pagoDesde: new FormControl(),
    pagoHasta: new FormControl()
  });

  get configuracionValidacionClasificacionGastos(): ValidacionClasificacionGastos {
    return this.configuracion.validacionClasificacionGastos;
  }

  get isClasificacionGastosEnabled(): boolean {
    return this.configuracion.validacionClasificacionGastos === ValidacionClasificacionGastos.CLASIFICACION;
  }

  get disableProyectoSgi(): boolean {
    return this.config.cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_1_SGE_1
      || this.config.cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_1_SGE_N;
  }

  constructor(
    key: number,
    protected proyectoSge: IProyectoSge,
    protected relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    protected proyectoService: ProyectoService,
    proyectoAnualidadService: ProyectoAnualidadService,
    protected gastoProyectoService: GastoProyectoService,
    private proyectoConceptoGastoCodigoEcService: ProyectoConceptoGastoCodigoEcService,
    private proyectoConceptoGastoService: ProyectoConceptoGastoService,
    private configuracion: IConfiguracion,
  ) {
    super(key, proyectoSge, relaciones, proyectoService, proyectoAnualidadService, configuracion);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    super.onInitialize();
  }

  protected abstract getColumns(reducida?: boolean): Observable<IColumnDefinition[]>;

  protected abstract getDatosEconomicos(
    anualidades: string[],
    devengosRang?: any,
    contabilizacionRange?: any,
    pagosRang?: any,
    reducida?: boolean
  ): Observable<IDatoEconomico[]>;

  /**
   * Crea la fila para mostrar el dato economico en la tabla y los agrupadores de  Anualidad - Proyecto SGI, Concepto gasto y Clasificacion SGE
   * que no existan ya de añadir otros datos economicos
   */
  protected buildRows(datosEconomicos: IDatoEconomico[]): Observable<RowTreeDesglose<IDesglose>[]> {
    return this.fillDatosEconomicos(datosEconomicos).pipe(
      map(values => {
        const root: RowTreeDesglose<IDesglose>[] = [];
        const mapTree = new Map<string, RowTreeDesglose<IDesglose>>();
        values.forEach(element => {
          const keyAnualidad = `${element.anualidad}-${this.disableProyectoSgi ? 0 : (element.proyecto?.id ?? 0)}`;
          const keyConcepto = `${keyAnualidad}-${element.conceptoGasto?.id ?? 0}`;
          const keyClasificacionSGE = `${keyConcepto}-${element.clasificacionSGE?.id ?? 0}`;

          // Agrupador Anualidad - Proyecto SGI
          let anualidad = mapTree.get(keyAnualidad);
          if (!anualidad) {
            anualidad = new RowTreeDesglose(
              {
                anualidad: element.anualidad,
                proyecto: element.proyecto,
                conceptoGasto: {},
                partidaPresupuestaria: '',
                codigoEconomico: {},
                columnas: this.processColumnsValues(element.columnas, this.columns, true)
              } as IDesglose
            );
            mapTree.set(keyAnualidad, anualidad);
            root.push(anualidad);
          }

          // Agrupador Concepto gasto
          let conceptoGasto = mapTree.get(keyConcepto);
          if (!conceptoGasto) {
            conceptoGasto = new RowTreeDesglose(
              {
                anualidad: '',
                proyecto: {},
                conceptoGasto: element.conceptoGasto,
                partidaPresupuestaria: '',
                codigoEconomico: {},
                columnas: this.processColumnsValues(element.columnas, this.columns, true)
              } as IDesglose
            );
            mapTree.set(keyConcepto, conceptoGasto);
            anualidad.addChild(conceptoGasto);
          }

          // Agrupador clasificacion SGE
          let clasificacionSGE = mapTree.get(keyClasificacionSGE);
          if (!clasificacionSGE) {
            clasificacionSGE = new RowTreeDesglose(
              {
                anualidad: '',
                proyecto: {},
                conceptoGasto: {},
                clasificacionSGE: element.clasificacionSGE,
                partidaPresupuestaria: '',
                codigoEconomico: {},
                columnas: this.processColumnsValues(element.columnas, this.columns, true)
              } as IDesglose
            );
            mapTree.set(keyClasificacionSGE, clasificacionSGE);
            conceptoGasto.addChild(clasificacionSGE);
          }

          // Dato economico
          clasificacionSGE.addChild(new RowTreeDesglose(
            {
              id: element.id,
              anualidad: '',
              proyecto: {},
              conceptoGasto: {},
              clasificadoAutomaticamente: element.clasificadoAutomaticamente,
              partidaPresupuestaria: element.partidaPresupuestaria,
              codigoEconomico: element.codigoEconomico,
              fechaDevengo: element.fechaDevengo,
              columnas: this.processColumnsValues(element.columnas, this.columns, false)
            } as IDesglose
          ));
        });
        return root;
      })
    );
  }

  protected fillDatosEconomicos(datosEconomicos: IDatoEconomico[]): Observable<IDesglose[]> {
    return this.fillProyectosMap().pipe(
      switchMap(() =>
        from(datosEconomicos).pipe(
          concatMap((datoEconomico: IDesglose) => {
            switch (this.configuracion?.validacionClasificacionGastos) {
              case ValidacionClasificacionGastos.VALIDACION:
                return this.fillDatoEconomicoClasificacionWithGastoProyecto(datoEconomico);
              case ValidacionClasificacionGastos.CLASIFICACION:
                return this.fillDatoEconomicoWithGastoProyectoOrElegibilidad(datoEconomico);
              case ValidacionClasificacionGastos.ELEGIBILIDAD:
                return this.fillDatoEconomicoClasificacionWithElegibilidad(datoEconomico);
              default:
                return of(datoEconomico);
            }
          }),
          map(final => of(final))
        ).pipe(
          combineAll()
        ))
    );
  }

  public loadDataExport(): Observable<IDesgloseEconomicoExportData> {
    const anualidades = this.aniosControl.value ?? [];
    const gastosClasficadosSgiFilter = this.gastosClasficadosSgiControl.value;
    const fechas = this.formGroupFechas.controls;
    const devengoRange = {
      desde: LuxonUtils.toBackend(fechas.devengoDesde.value, true),
      hasta: LuxonUtils.toBackend(fechas.devengoHasta.value, true)
    };
    const contabilizacionRange = {
      desde: LuxonUtils.toBackend(fechas.contabilizacionDesde.value, true),
      hasta: LuxonUtils.toBackend(fechas.contabilizacionHasta.value, true)
    };
    const pagoRange = {
      desde: LuxonUtils.toBackend(fechas.pagoDesde.value, true),
      hasta: LuxonUtils.toBackend(fechas.pagoHasta.value, true)
    };
    const exportData: IDesgloseEconomicoExportData = {
      data: [],
      columns: []
    };
    const columnasReducidas = false;
    return of(exportData).pipe(
      switchMap((exportDataResult) => {
        return this.getDatosEconomicosToDesglose(anualidades, devengoRange, contabilizacionRange, pagoRange, columnasReducidas).pipe(
          map(data => {
            data = data.filter(element => this.desgloseMatchFilterGastosClasficadosSgi(element, gastosClasficadosSgiFilter));
            this.sortRowsDesglose(data);
            exportDataResult.data = data;
            return exportDataResult;
          })
        );
      }),
      switchMap((exportDataResult) => {
        return this.getColumns(columnasReducidas).pipe(
          map((columns) => {
            exportDataResult.columns = columns;
            return exportDataResult;
          })
        );
      })
    );
  }

  public getGastoProyectoUpdated(gastoRef: string): IGastoProyecto {
    return this.updatedGastosProyectos.get(gastoRef);
  }

  public isGastoProyectoUpdated(gastoRef: string): boolean {
    return this.updatedGastosProyectos.has(gastoRef);
  }

  public updateGastoProyecto(gastoProyecto: IGastoProyecto): void {
    this.updatedGastosProyectos.set(gastoProyecto.gastoRef, gastoProyecto);
    this.setChanges(true);
  }

  public acceptClasificacionGastosProyectos(desglose: RowTreeDesglose<IDesglose>): void {
    let gastoProyecto = this.updatedGastosProyectos.get(desglose.item.id);
    if (!gastoProyecto) {
      gastoProyecto = {
        conceptoGasto: this.getItemLevel(desglose, 1).item.conceptoGasto,
        proyectoId: this.getItemLevel(desglose, 0).item.proyecto?.id,
        gastoRef: desglose.item.id
      } as IGastoProyecto;
    }

    this.updateGastoProyecto(gastoProyecto);
  }

  public saveOrUpdate(): Observable<void> {
    if (this.updatedGastosProyectos.size === 0) {
      return of(void 0);
    }
    return from(this.updatedGastosProyectos.values()).pipe(
      mergeMap((gastoProyecto) => {
        return (gastoProyecto.id
          ? this.gastoProyectoService.update(gastoProyecto.id, gastoProyecto) : this.gastoProyectoService.create(gastoProyecto))
          .pipe(
            map(() => {
              this.updatedGastosProyectos.delete(gastoProyecto.gastoRef);
            })
          );
      }),
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
          this.loadDesglose();
        }
      })
    );
  }

  private getDatosEconomicosToDesglose(
    anualidades: string[],
    devengosRange?: any,
    contabilizacionRange?: any,
    pagosRange?: any,
    reducida?: boolean
  ): Observable<IDesglose[]> {
    return this.getDatosEconomicos(anualidades, devengosRange, contabilizacionRange, pagosRange, reducida).pipe(
      switchMap(response => this.fillDatosEconomicos(response)));
  }

  public loadDesglose(): void {
    const anualidades = this.aniosControl.value ?? [];
    const gastosClasficadosSgiFilter = this.gastosClasficadosSgiControl.value;
    const fechas = this.formGroupFechas.controls;
    const devengoRange = {
      desde: LuxonUtils.toBackend(fechas.devengoDesde.value, true),
      hasta: LuxonUtils.toBackend(fechas.devengoHasta.value, true)
    };
    const contabilizacionRange = {
      desde: LuxonUtils.toBackend(fechas.contabilizacionDesde.value, true),
      hasta: LuxonUtils.toBackend(fechas.contabilizacionHasta.value, true)
    };
    const pagoRange = {
      desde: LuxonUtils.toBackend(fechas.pagoDesde.value, true),
      hasta: LuxonUtils.toBackend(fechas.pagoHasta.value, true)
    };

    this.getDatosEconomicos(anualidades, devengoRange, contabilizacionRange, pagoRange, true)
      .pipe(
        switchMap(response => this.buildRows(response)),
        map(rows => this.applyFilterGastosClasficadosSgi(rows, gastosClasficadosSgiFilter))
      ).subscribe(
        (root) => {
          const regs: RowTreeDesglose<IDesglose>[] = [];
          root.forEach(r => {
            r.compute(this.columns);
            regs.push(...this.addChilds(r));
          });
          this.sortRowsTree(regs);
          this.desglose$.next(regs);
        }
      );
  }

  protected abstract sortRowsTree(rows: RowTreeDesglose<IDesglose>[]): void;
  protected abstract sortRowsDesglose(rows: IDesglose[]): void;

  protected getItemLevel(item: RowTreeDesglose<IDesglose>, level: number): RowTreeDesglose<IDesglose> {
    if (item.level === level) {
      return item;
    } else if (item.level < level) {
      return null;
    } else {
      return this.getItemLevel(item.parent, level);
    }
  }

  protected compareAnualidadRowTree(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    const anualidadItemA = this.getItemLevel(itemA, 0)?.item?.anualidad ?? '';
    const anualidadItemB = this.getItemLevel(itemB, 0)?.item?.anualidad ?? '';
    return anualidadItemA.localeCompare(anualidadItemB);
  }

  protected compareAnualidadDesglose(itemA: IDesglose, itemB: IDesglose): number {
    const anualidadItemA = itemA?.anualidad ?? '';
    const anualidadItemB = itemB?.anualidad ?? '';
    return anualidadItemA.localeCompare(anualidadItemB);
  }

  protected compareProyectoTituloRowTree(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    const tituloProyectoItemA = this.getItemLevel(itemA, 0)?.item?.proyecto?.titulo ?? '';
    const tituloProyectoItemB = this.getItemLevel(itemB, 0)?.item?.proyecto?.titulo ?? '';
    return tituloProyectoItemA.localeCompare(tituloProyectoItemB);
  }

  protected compareProyectoTituloDesglose(itemA: IDesglose, itemB: IDesglose): number {
    const tituloProyectoItemA = itemA?.proyecto?.titulo ?? '';
    const tituloProyectoItemB = itemB?.proyecto?.titulo ?? '';
    return tituloProyectoItemA.localeCompare(tituloProyectoItemB);
  }

  protected compareConceptoGastoNombreRowTree(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    if (!itemA || !itemB || itemA.level < 1 || itemB.level < 1) {
      return 0;
    }

    const nombreConceptoGastoItemA = this.getItemLevel(itemA, 1)?.item?.conceptoGasto?.nombre ?? '';
    const nombreConceptoGastoItemB = this.getItemLevel(itemB, 1)?.item?.conceptoGasto?.nombre ?? '';
    return nombreConceptoGastoItemA.localeCompare(nombreConceptoGastoItemB);
  }

  protected compareConceptoGastoNombreDesglose(itemA: IDesglose, itemB: IDesglose): number {
    const nombreConceptoGastoItemA = itemA?.conceptoGasto?.nombre ?? '';
    const nombreConceptoGastoItemB = itemB?.conceptoGasto?.nombre ?? '';
    return nombreConceptoGastoItemA.localeCompare(nombreConceptoGastoItemB);
  }

  protected compareClasificacionSGENombreRowTree(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    if (!itemA || !itemB || itemA.level < 2 || itemB.level < 2) {
      return 0;
    }

    const nombreClasificacionSGEItemA = this.getItemLevel(itemA, 2)?.item?.clasificacionSGE?.nombre ?? '';
    const nombreClasificacionSGEItemB = this.getItemLevel(itemB, 2)?.item?.clasificacionSGE?.nombre ?? '';
    return nombreClasificacionSGEItemA.localeCompare(nombreClasificacionSGEItemB);
  }

  protected compareClasificacionSGENombreDesglose(itemA: IDesglose, itemB: IDesglose): number {
    const nombreClasificacionSGEItemA = itemA?.clasificacionSGE?.nombre ?? '';
    const nombreClasificacionSGEItemB = itemB?.clasificacionSGE?.nombre ?? '';
    return nombreClasificacionSGEItemA.localeCompare(nombreClasificacionSGEItemB);
  }

  protected comparePartidaPresupuestariaRowTree(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    if (!itemA || !itemB || itemA.level < 3 || itemB.level < 3) {
      return 0;
    }

    const partidaPresupuestariaItemA = this.getItemLevel(itemA, 2)?.item?.partidaPresupuestaria ?? '';
    const partidaPresupuestariaItemB = this.getItemLevel(itemB, 2)?.item?.partidaPresupuestaria ?? '';
    return partidaPresupuestariaItemA.localeCompare(partidaPresupuestariaItemB);
  }

  protected comparePartidaPresupuestariaDesglose(itemA: IDesglose, itemB: IDesglose): number {
    const partidaPresupuestariaItemA = itemA?.partidaPresupuestaria ?? '';
    const partidaPresupuestariaItemB = itemB?.partidaPresupuestaria ?? '';
    return partidaPresupuestariaItemA.localeCompare(partidaPresupuestariaItemB);
  }

  protected compareCodigoEconomicoRowTree(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    if (!itemA || !itemB || itemA.level < 3 || itemB.level < 3) {
      return 0;
    }

    const codigoEconomicoItemA = this.getItemLevel(itemA, 3)?.item?.codigoEconomico?.id ?? '';
    const codigoEconomicoItemB = this.getItemLevel(itemB, 3)?.item?.codigoEconomico?.id ?? '';
    return codigoEconomicoItemA.localeCompare(codigoEconomicoItemB);
  }

  protected compareCodigoEconomicoDesglose(itemA: IDesglose, itemB: IDesglose): number {
    const codigoEconomicoItemA = itemA?.codigoEconomico?.id ?? '';
    const codigoEconomicoItemB = itemB?.codigoEconomico?.id ?? '';
    return codigoEconomicoItemA.localeCompare(codigoEconomicoItemB);
  }

  protected compareFechaDevengoRowTree(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    if (!itemA || !itemB || itemA.level < 3 || itemB.level < 3) {
      return 0;
    }

    const fechaDevengoInMillisItemA = this.getItemLevel(itemA, 3)?.item?.fechaDevengo?.toMillis() ?? 0;
    const fechaDevengoInMillisItemB = this.getItemLevel(itemB, 3)?.item?.fechaDevengo?.toMillis() ?? 0;
    return fechaDevengoInMillisItemB - fechaDevengoInMillisItemA;
  }

  protected compareFechaDevengoDesglose(itemA: IDesglose, itemB: IDesglose): number {
    const fechaDevengoInMillisItemA = itemA?.fechaDevengo.toMillis() ?? 0;
    const fechaDevengoInMillisItemB = itemB?.fechaDevengo.toMillis() ?? 0;
    return fechaDevengoInMillisItemB - fechaDevengoInMillisItemA;
  }

  private fillProyectosMap(): Observable<Map<string, IProyecto>> {
    return from(this.relaciones).pipe(
      filter(relacion => relacion.tipoEntidad === TipoEntidad.PROYECTO),
      concatMap(relacion => {
        const proyectoInMap = this.proyectosMap.get(relacion.id.toString());
        if (!proyectoInMap) {
          return this.proyectoService.findById(relacion.id).pipe(
            tap(proyecto => this.proyectosMap.set(proyecto.id.toString(), proyecto))
          )
        }

        return of(proyectoInMap);
      }),
      toArray(),
      map(() => this.proyectosMap)
    );
  }

  /**
   * Rellena el proyecto y el concepto de gasto a partir de los datos de gastoProyecto y si no esta clasificado se intenta hacer la clasificacion automatica
   * con los datos de elegibilidad del proyecto.
   * 
   * @param datoEconomico un dato economico
   * @returns el datoEconomico con el proyecto y el conceptoGasto rellenos con los datos obtenidos de la elegibilidad del proyecto y si no se pueden establecer 
   * ambos como Sin clasificar.
   */
  private fillDatoEconomicoWithGastoProyectoOrElegibilidad(datoEconomico: IDesglose): Observable<IDesglose> {
    return this.fillDatoEconomicoClasificacionWithGastoProyecto(datoEconomico).pipe(
      switchMap(datoEconomico => {
        if (!!datoEconomico?.gastoProyectoId) {
          datoEconomico.clasificadoAutomaticamente = false;
          return of(datoEconomico);
        }

        return this.fillDatoEconomicoClasificacionWithElegibilidad(datoEconomico).pipe(
          map(datoEconomico => {
            datoEconomico.clasificadoAutomaticamente = !!datoEconomico.proyecto?.id;
            return datoEconomico;
          })
        );
      })
    );
  }

  /**
   * Rellena el proyecto y el concepto de gasto a partir de los datos de gastoProyecto.
   * 
   * Si el gasto ya esta clasificado o validado se recuperan el proyecto y el concepto de gasto con los que esta asociado, 
   * si no es gasto no esta clasificado se dejan el proyecto y el concepto de gasto como 'Sin clasificar'.
   * 
   * @param datoEconomico un dato economico
   * @returns el datoEconomico con el proyecto y el conceptoGasto rellenos con los datos obtenidos de gastoProyecto y si no se dejan 
   * ambos como 'Sin clasificar'.
   */
  private fillDatoEconomicoClasificacionWithGastoProyecto(datoEconomico: IDesglose): Observable<IDesglose> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('gastoRef', SgiRestFilterOperator.EQUALS, datoEconomico.id)
    };

    return this.gastoProyectoService.findAll(options).pipe(
      map(response => {
        if (response.items.length) {
          const gastoProyecto = response.items[0];
          datoEconomico.gastoProyectoId = gastoProyecto.id
          datoEconomico.proyecto = this.proyectosMap.get(gastoProyecto.proyectoId?.toString());
          datoEconomico.conceptoGasto = gastoProyecto.conceptoGasto;
        }
        else {
          datoEconomico.proyecto = { titulo: 'Sin clasificar' } as IProyecto;
          datoEconomico.conceptoGasto = { nombre: 'Sin clasificar' } as IConceptoGasto;
        }
        return datoEconomico;
      }),
    );
  }

  /**
   * Rellena el proyecto y el concepto de gasto a partir de los datos de elegibilidad del proyecto.
   * 
   * Si el codigo economico del datoEconomico esta incluido como proyectoConceptoGastoCodigoEc de uno solo de los proyectos del sgi   
   * relacionados rellena el conceptoGasto al que esta asociado el codigo economico y el proyecto en el que esta el concepto de gasto,
   * si no esta o esta en varios proyectos se dejan ambos como 'Sin clasificar'
   * 
   * @param datoEconomico un dato economico
   * @returns el datoEconomico con el proyecto y el conceptoGasto rellenos con los datos obtenidos de la elegibilidad del proyecto y si no se pueden establecer 
   * ambos como 'Sin clasificar'.
   */
  private fillDatoEconomicoClasificacionWithElegibilidad(datoEconomico: IDesglose): Observable<IDesglose> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('codigoEconomicoRef', SgiRestFilterOperator.EQUALS, datoEconomico.codigoEconomico.id)
        .and(
          new RSQLSgiRestFilter(
            new RSQLSgiRestFilter(
              'fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(datoEconomico.fechaDevengo)
            ).and('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(datoEconomico.fechaDevengo))
          ).or(
            new RSQLSgiRestFilter(
              'fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(datoEconomico.fechaDevengo)
            )
              .and('fechaFin', SgiRestFilterOperator.IS_NULL, '')
          ).or(
            new RSQLSgiRestFilter('fechaInicio', SgiRestFilterOperator.IS_NULL, '')
              .and('fechaFin', SgiRestFilterOperator.IS_NULL, '')
          )
        )
        .and(
          'proyectoConceptoGasto.proyectoId',
          SgiRestFilterOperator.IN,
          this.relaciones
            .filter(relacion => relacion.tipoEntidad === TipoEntidad.PROYECTO)
            .map(proyecto => proyecto.id.toString()))
    };
    return this.proyectoConceptoGastoCodigoEcService.findAll(options).pipe(
      mergeMap(({ items }) => {
        if (items.length === 1) {
          const proyectoConceptoGasto = this.proyectoConceptoGastosMap.get(items[0].proyectoConceptoGasto.id.toString());
          if (!proyectoConceptoGasto) {
            return this.proyectoConceptoGastoService.findById(items[0].proyectoConceptoGasto.id).pipe(
              map(proyectoConceptoGastoFetched => {
                this.proyectoConceptoGastosMap.set(proyectoConceptoGastoFetched.id.toString(), proyectoConceptoGastoFetched);
                datoEconomico.proyecto = this.proyectosMap.get(proyectoConceptoGastoFetched.proyectoId.toString());
                datoEconomico.conceptoGasto = proyectoConceptoGastoFetched.conceptoGasto;
                return datoEconomico;
              })
            );
          }
          datoEconomico.proyecto = this.proyectosMap.get(proyectoConceptoGasto.proyectoId.toString());
          datoEconomico.conceptoGasto = proyectoConceptoGasto.conceptoGasto;
          return of(datoEconomico);
        } else {
          datoEconomico.proyecto = { titulo: 'Sin clasificar' } as IProyecto;
          datoEconomico.conceptoGasto = { nombre: 'Sin clasificar' } as IConceptoGasto;
          return of(datoEconomico);
        }
      })
    );
  }

  /**
   * Elimina las filas que no cumplen con el filtro directamente y los agrupadores que quedan vacios al aplicar el filtro
   * 
   * @param rows la lista de filas a filtrar
   * @param filter el filtro
   * @returns la lista de filas filtradas
   */
  private applyFilterGastosClasficadosSgi(rows: RowTreeDesglose<IDesglose>[], filter: GastosClasficadosSgiEnum): RowTreeDesglose<IDesglose>[] {
    return rows.filter(row => {
      if (row.level < 3) {
        row.childs = this.applyFilterGastosClasficadosSgi(row.childs, filter);
      }

      return (row.level < 3 && row.childs.length > 0)
        || (row.level === 3 && this.desgloseMatchFilterGastosClasficadosSgi({
          ...row.item,
          conceptoGasto: this.getItemLevel(row, 1).item.conceptoGasto
        } as IDesglose, filter));
    });
  }

  /**
   * Comprueba si el elemento cumple con el filtro
   * 
   * @param desglose el elemento sobre el que se aplica el filtro
   * @param filter el filtro
   * @returns si el elemento cumple o no con el filtro 
   */
  private desgloseMatchFilterGastosClasficadosSgi(desglose: IDesglose, filter: GastosClasficadosSgiEnum): boolean {
    return !filter
      || filter === GastosClasficadosSgiEnum.TODOS
      || (filter === GastosClasficadosSgiEnum.SI && !!desglose.conceptoGasto?.id && !desglose.clasificadoAutomaticamente)
      || (filter === GastosClasficadosSgiEnum.NO && (!desglose.conceptoGasto?.id || !!desglose.clasificadoAutomaticamente));
  }

  private isSaveOrUpdateComplete(): boolean {
    return this.updatedGastosProyectos.size === 0;
  }

}
