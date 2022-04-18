import { FormControl, FormGroup } from '@angular/forms';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IConfiguracion } from '@core/models/csp/configuracion';
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
import { from, Observable, of } from 'rxjs';
import { combineAll, concatMap, filter, map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaWithResponsables } from '../ejecucion-economica.action.service';
import { DesgloseEconomicoFragment, IColumnDefinition, IDesgloseEconomicoExportData, RowTreeDesglose } from './desglose-economico.fragment';

export interface IDesglose extends IDatoEconomico {
  proyecto: IProyecto;
  agrupacionGasto: IProyectoAgrupacionGasto;
  conceptoGasto: IConceptoGasto;
}

export abstract class FacturasJustificantesFragment extends DesgloseEconomicoFragment<IDesglose> {

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
    super(key, proyectoSge, relaciones, proyectoService, proyectoAnualidadService);
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

  protected buildRows(datosEconomicos: IDatoEconomico[]): Observable<RowTreeDesglose<IDesglose>[]> {
    return this.fillDatosEconomicos(datosEconomicos).pipe(
      map(values => {
        const root: RowTreeDesglose<IDesglose>[] = [];
        const mapTree = new Map<string, RowTreeDesglose<IDesglose>>();
        values.forEach(element => {
          const keyAnualidad = `${element.anualidad}-${element.proyecto?.id ?? 0}`;
          const keyConcepto = `${keyAnualidad}-${element.conceptoGasto?.id ?? 0}`;
          const keyClasificacionSGE = `${keyConcepto}-${element.clasificacionSGE?.id ?? 0}`;
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
          clasificacionSGE.addChild(new RowTreeDesglose(
            {
              id: element.id,
              anualidad: '',
              proyecto: {},
              conceptoGasto: {},
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
            if (this.configuracion?.validacionGastos) {
              return this.fillDatoEconomicoWhenValidacionGastosTrue(datoEconomico);
            } else {
              return this.fillDatoEconomicoWhenValidacionGastosFalse(datoEconomico);
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
    return of(exportData).pipe(
      switchMap((exportDataResult) => {
        return this.getDatosEconomicosToDesglose(anualidades, devengoRange, contabilizacionRange, pagoRange, false).pipe(
          map(data => {
            this.sortRowsDesglose(data);
            exportDataResult.data = data;
            return exportDataResult;
          })
        );
      }),
      switchMap((exportDataResult) => {
        return this.getColumns(false).pipe(
          map((columns) => {
            exportDataResult.columns = columns;
            return exportDataResult;
          })
        );
      })
    );
  }

  public getDatosEconomicosToDesglose(
    anualidades: string[],
    devengosRange?: any,
    contabilizacionRange?: any,
    pagosRange?: any,
    reducida?: boolean
  ): Observable<IDesglose[]> {
    return this.getDatosEconomicos(anualidades, devengosRange, contabilizacionRange, pagosRange, false).pipe(
      switchMap(response => this.fillDatosEconomicos(response)));
  }

  public loadDesglose(): void {
    const anualidades = this.aniosControl.value ?? [];
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
        switchMap(response => this.buildRows(response))
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
    if (!itemA || !itemB || itemA.level < 2 || itemB.level < 2) {
      return 0;
    }

    const nombreConceptoGastoItemA = this.getItemLevel(itemA, 2)?.item?.conceptoGasto?.nombre ?? '';
    const nombreConceptoGastoItemB = this.getItemLevel(itemB, 2)?.item?.conceptoGasto?.nombre ?? '';
    return nombreConceptoGastoItemA.localeCompare(nombreConceptoGastoItemB);
  }

  protected compareConceptoGastoNombreDesglose(itemA: IDesglose, itemB: IDesglose): number {
    const nombreConceptoGastoItemA = itemA?.conceptoGasto?.nombre ?? '';
    const nombreConceptoGastoItemB = itemB?.conceptoGasto?.nombre ?? '';
    return nombreConceptoGastoItemA.localeCompare(nombreConceptoGastoItemB);
  }

  protected compareClasificacionSGENombreRowTree(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    if (!itemA || !itemB || itemA.level < 1 || itemB.level < 1) {
      return 0;
    }

    const nombreClasificacionSGEItemA = this.getItemLevel(itemA, 1)?.item?.clasificacionSGE?.nombre ?? '';
    const nombreClasificacionSGEItemB = this.getItemLevel(itemB, 1)?.item?.clasificacionSGE?.nombre ?? '';
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

  private fillDatoEconomicoWhenValidacionGastosTrue(datoEconomico: IDesglose): Observable<IDesglose> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('gastoRef', SgiRestFilterOperator.EQUALS, datoEconomico.id)
    };

    return this.gastoProyectoService.findAll(options).pipe(
      map(response => {
        if (response.items.length) {
          datoEconomico.proyecto = this.proyectosMap.get(response.items[0].proyectoId.toString());
          datoEconomico.conceptoGasto = response.items[0].conceptoGasto;
        }
        else {
          datoEconomico.proyecto = { titulo: 'Sin clasificar' } as IProyecto;
          datoEconomico.conceptoGasto = { nombre: 'Sin clasificar' } as IConceptoGasto;
        }
        return datoEconomico;
      }),
    );
  }

  private fillDatoEconomicoWhenValidacionGastosFalse(datoEconomico: IDesglose): Observable<IDesglose> {
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

}
