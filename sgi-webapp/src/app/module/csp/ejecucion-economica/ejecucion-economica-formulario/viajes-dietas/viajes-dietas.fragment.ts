import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { ProyectoAgrupacionGastoService } from '@core/services/csp/proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';
import { IColumnDefinition, RowTreeDesglose } from '../desglose-economico.fragment';
import { FacturasJustificantesFragment, IDesglose } from '../facturas-justificantes.fragment';

export class ViajesDietasFragment extends FacturasJustificantesFragment {

  updatedGastosProyectos: Map<string, IGastoProyecto> = new Map();

  constructor(
    key: number,
    proyectoSge: IProyectoSge,
    proyectosRelacionados: IProyecto[],
    proyectoService: ProyectoService,
    personaService: PersonaService,
    proyectoAnualidadService: ProyectoAnualidadService,
    proyectoAgrupacionGastoService: ProyectoAgrupacionGastoService,
    gastoProyectoService: GastoProyectoService,
    private ejecucionEconomicaService: EjecucionEconomicaService
  ) {
    super(key, proyectoSge, proyectosRelacionados, proyectoService, personaService, proyectoAnualidadService,
      proyectoAgrupacionGastoService, gastoProyectoService);
  }

  protected onInitialize(): void {
    super.onInitialize();

    this.subscriptions.push(this.getColumns(true).subscribe(
      (columns) => {
        this.columns = columns;
        this.displayColumns = ['anualidad', 'proyecto', 'agrupacionGasto', 'conceptoGasto', 'aplicacionPresupuestaria', 'codigoEconomico',
          ...columns.map(column => column.id), 'acciones'];
      }
    ));
  }

  protected getColumns(reducida?: boolean): Observable<IColumnDefinition[]> {
    return this.ejecucionEconomicaService.getColumnasViajesDietas(this.proyectoSge.id, reducida)
      .pipe(
        map(response => this.toColumnDefinition(response))
      );
  }

  protected getDatosEconomicos(
    anualidades: string[],
    devengosRange?: any,
    contabilizacionRange?: any,
    pagosRange?: any,
    reducida?: boolean
  ): Observable<IDatoEconomico[]> {
    return this.ejecucionEconomicaService
      .getViajesDietas(this.proyectoSge.id, anualidades, reducida, pagosRange, devengosRange, contabilizacionRange);
  }

  updateGastoProyecto(gastoProyecto: IGastoProyecto): void {
    this.updatedGastosProyectos.set(gastoProyecto.gastoRef, gastoProyecto);
    this.setChanges(true);
  }

  saveOrUpdate(): Observable<void> {
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
        }
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    return this.updatedGastosProyectos.size === 0;
  }

  public clearRangos(): void {
    this.formGroupFechas.reset();
  }

  protected sortRowsTree(rows: RowTreeDesglose<IDesglose>[]): void {
    rows.sort((a, b) => {
      return this.compareAnualidadRowTree(b, a)
        || this.compareProyectoTituloRowTree(a, b)
        || this.compareAgrupacionGastoNombreRowTree(a, b)
        || this.compareConceptoGastoNombreRowTree(a, b)
        || this.comparePartidaPresupuestariaRowTree(a, b)
        || this.compareCodigoEconomicoRowTree(a, b);
    });
  }

  protected sortRowsDesglose(rows: IDesglose[]): void {
    rows.sort((a, b) => {
      return this.compareAnualidadDesglose(b, a)
        || this.compareProyectoTituloDesglose(a, b)
        || this.compareAgrupacionGastoNombreDesglose(a, b)
        || this.compareConceptoGastoNombreDesglose(a, b)
        || this.comparePartidaPresupuestariaDesglose(a, b)
        || this.compareCodigoEconomicoDesglose(a, b);
    });
  }

}
