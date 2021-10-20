import { FormControl, FormGroup } from '@angular/forms';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { ProyectoAgrupacionGastoService } from '@core/services/csp/proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { from, Observable, of } from 'rxjs';
import { catchError, combineAll, concatMap, map, switchMap, tap } from 'rxjs/operators';
import { DesgloseEconomicoFragment, IColumnDefinition, RowTreeDesglose } from './desglose-economico.fragment';

export interface IDesglose extends IDatoEconomico {
  proyecto: IProyecto;
  agrupacionGasto: IProyectoAgrupacionGasto;
  conceptoGasto: IConceptoGasto;
}

export abstract class FacturasJustificantesFragment extends DesgloseEconomicoFragment<IDesglose> {

  private proyectosMap = new Map<string, IProyecto>();
  private agrupacionGastoMap = new Map<string, IProyectoAgrupacionGasto>();

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
    protected proyectosRelacionados: IProyecto[],
    protected proyectoService: ProyectoService,
    personaService: PersonaService,
    proyectoAnualidadService: ProyectoAnualidadService,
    private proyectoAgrupacionGasto: ProyectoAgrupacionGastoService,
    protected gastoProyectoService: GastoProyectoService
  ) {
    super(key, proyectoSge, proyectosRelacionados, proyectoService, personaService, proyectoAnualidadService);
    this.setComplete(true);
    proyectosRelacionados.forEach(proyecto => {
      this.proyectosMap.set(proyecto.id.toString(), proyecto);
    });
  }

  private getAgrupacionGasto(proyectoId: string, id: string): Observable<IProyectoAgrupacionGasto> {
    const key = `${proyectoId}-${id}`;
    const existing = this.agrupacionGastoMap.get(key);
    if (existing) {
      return of(existing);
    }
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoId)
        .and('conceptos.conceptoGasto.id', SgiRestFilterOperator.EQUALS, id)
    };
    return this.proyectoAgrupacionGasto.findAll(options).pipe(
      map(response => {
        if (response.items.length) {
          return response.items[0];
        }
        return { nombre: 'Sin clasificar' } as IProyectoAgrupacionGasto;
      }),
      catchError(error => {
        return of({ nombre: 'Sin clasificar' } as IProyectoAgrupacionGasto);
      }),
      tap((proyectoAgrupacionGasto) => {
        this.agrupacionGastoMap.set(key, proyectoAgrupacionGasto);
      })
    );
  }

  protected abstract getColumns(): Observable<IColumnDefinition[]>;

  protected abstract getDatosEconomicos(
    anualidades: string[],
    devengosRang?: any,
    contabilizacionRange?: any,
    pagosRang?: any
  ): Observable<IDatoEconomico[]>;

  protected buildRows(datosEconomicos: IDatoEconomico[]): Observable<RowTreeDesglose<IDesglose>[]> {
    return from(datosEconomicos).pipe(
      concatMap((datoEconomico: IDesglose) => {
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
      }),
      concatMap((datoEconomico: IDesglose) => {
        if (datoEconomico.proyecto.id && datoEconomico.conceptoGasto.id) {
          return this.getAgrupacionGasto(datoEconomico.proyecto.id.toString(), datoEconomico.conceptoGasto.id.toString()).pipe(
            map(agrupacion => {
              datoEconomico.agrupacionGasto = agrupacion;
              return datoEconomico;
            })
          );
        }
        datoEconomico.agrupacionGasto = { nombre: 'Sin clasificar' } as IProyectoAgrupacionGasto;
        return of(datoEconomico);
      }),
      map(final => of(final))
    ).pipe(
      combineAll(),
      map(values => {
        const root: RowTreeDesglose<IDesglose>[] = [];
        const mapTree = new Map<string, RowTreeDesglose<IDesglose>>();
        values.forEach(element => {
          const keyAnualidad = `${element.anualidad}-${element.proyecto?.id ?? 0}`;
          const keyAgrupacion = `${keyAnualidad}-${element.agrupacionGasto?.id ?? 0}`;
          const keyConcepto = `${keyAgrupacion}-${element.conceptoGasto?.id ?? 0}`;
          let anualidad = mapTree.get(keyAnualidad);
          if (!anualidad) {
            anualidad = new RowTreeDesglose(
              {
                anualidad: element.anualidad,
                proyecto: element.proyecto,
                agrupacionGasto: {},
                conceptoGasto: {},
                partidaPresupuestaria: '',
                codigoEconomico: {},
                columnas: this.processColumnsValues(element.columnas, this.columns, true)
              } as IDesglose
            );
            mapTree.set(keyAnualidad, anualidad);
            root.push(anualidad);
          }
          let agrupacion = mapTree.get(keyAgrupacion);
          if (!agrupacion) {
            agrupacion = new RowTreeDesglose(
              {
                anualidad: '',
                proyecto: {},
                agrupacionGasto: element.agrupacionGasto,
                conceptoGasto: {},
                partidaPresupuestaria: '',
                codigoEconomico: {},
                columnas: this.processColumnsValues(element.columnas, this.columns, true)
              } as IDesglose
            );
            mapTree.set(keyAgrupacion, agrupacion);
            anualidad.addChild(agrupacion);
          }
          let conceptoGasto = mapTree.get(keyConcepto);
          if (!conceptoGasto) {
            conceptoGasto = new RowTreeDesglose(
              {
                anualidad: '',
                proyecto: {},
                agrupacionGasto: {},
                conceptoGasto: element.conceptoGasto,
                partidaPresupuestaria: '',
                codigoEconomico: {},
                columnas: this.processColumnsValues(element.columnas, this.columns, true)
              } as IDesglose
            );
            mapTree.set(keyConcepto, conceptoGasto);
            agrupacion.addChild(conceptoGasto);
          }
          conceptoGasto.addChild(new RowTreeDesglose(
            {
              id: element.id,
              anualidad: '',
              proyecto: {},
              agrupacionGasto: {},
              conceptoGasto: {},
              partidaPresupuestaria: element.partidaPresupuestaria,
              codigoEconomico: element.codigoEconomico,
              columnas: this.processColumnsValues(element.columnas, this.columns, false)
            } as IDesglose
          ));
        });
        return root;
      })
    );

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
    this.getDatosEconomicos(anualidades, devengoRange, contabilizacionRange, pagoRange)
      .pipe(
        switchMap(response => this.buildRows(response))
      ).subscribe(
        (root) => {
          const regs: RowTreeDesglose<IDesglose>[] = [];
          root.forEach(r => {
            r.compute(this.columns);
            regs.push(...this.addChilds(r));
          });
          this.sortRows(regs);
          this.desglose$.next(regs);
        }
      );
  }

  protected abstract sortRows(rows: RowTreeDesglose<IDesglose>[]): void;

  protected getItemLevel(item: RowTreeDesglose<IDesglose>, level: number): RowTreeDesglose<IDesglose> {
    if (item.level === level) {
      return item;
    } else if (item.level < level) {
      return null;
    } else {
      return this.getItemLevel(item.parent, level);
    }
  }

  protected compareAnualidad(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    const anualidadItemA = this.getItemLevel(itemA, 0)?.item?.anualidad ?? '';
    const anualidadItemB = this.getItemLevel(itemB, 0)?.item?.anualidad ?? '';
    return anualidadItemA.localeCompare(anualidadItemB);
  }

  protected compareProyectoTitulo(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    const tituloProyectoItemA = this.getItemLevel(itemA, 0)?.item?.proyecto?.titulo ?? '';
    const tituloProyectoItemB = this.getItemLevel(itemB, 0)?.item?.proyecto?.titulo ?? '';
    return tituloProyectoItemA.localeCompare(tituloProyectoItemB);
  }

  protected compareAgrupacionGastoNombre(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    if (!itemA || !itemB || itemA.level < 1 || itemB.level < 1) {
      return 0;
    }

    const nombreAgrupacionGastoItemA = this.getItemLevel(itemA, 1)?.item?.agrupacionGasto?.nombre ?? '';
    const nombreAgrupacionGastoItemB = this.getItemLevel(itemB, 1)?.item?.agrupacionGasto?.nombre ?? '';
    return nombreAgrupacionGastoItemA.localeCompare(nombreAgrupacionGastoItemB);
  }

  protected compareConceptoGastoNombre(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    if (!itemA || !itemB || itemA.level < 2 || itemB.level < 2) {
      return 0;
    }

    const nombreConceptoGastoItemA = this.getItemLevel(itemA, 2)?.item?.conceptoGasto?.nombre ?? '';
    const nombreConceptoGastoItemB = this.getItemLevel(itemB, 2)?.item?.conceptoGasto?.nombre ?? '';
    return nombreConceptoGastoItemA.localeCompare(nombreConceptoGastoItemB);
  }

  protected comparePartidaPresupuestaria(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    if (!itemA || !itemB || itemA.level < 3 || itemB.level < 3) {
      return 0;
    }

    const partidaPresupuestariaItemA = this.getItemLevel(itemA, 2)?.item?.partidaPresupuestaria ?? '';
    const partidaPresupuestariaItemB = this.getItemLevel(itemB, 2)?.item?.partidaPresupuestaria ?? '';
    return partidaPresupuestariaItemA.localeCompare(partidaPresupuestariaItemB);
  }

  protected compareCodigoEconomico(itemA: RowTreeDesglose<IDesglose>, itemB: RowTreeDesglose<IDesglose>): number {
    if (!itemA || !itemB || itemA.level < 3 || itemB.level < 3) {
      return 0;
    }

    const codigoEconomicoItemA = this.getItemLevel(itemA, 3)?.item?.codigoEconomico?.id ?? '';
    const codigoEconomicoItemB = this.getItemLevel(itemB, 3)?.item?.codigoEconomico?.id ?? '';
    return codigoEconomicoItemA.localeCompare(codigoEconomicoItemB);
  }
}
