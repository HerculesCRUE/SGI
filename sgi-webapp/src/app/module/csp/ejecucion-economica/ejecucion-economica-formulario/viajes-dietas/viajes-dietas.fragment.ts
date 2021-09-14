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
import { IColumnDefinition } from '../desglose-economico.fragment';
import { FacturasJustificantesFragment } from '../facturas-justificantes.fragment';

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

    this.subscriptions.push(this.getColumns().subscribe(
      (columns) => {
        this.columns = columns;
        this.displayColumns = ['anualidad', 'proyecto', 'agrupacionGasto', 'conceptoGasto', 'aplicacionPresupuestaria', 'codigoEconomico',
          ...columns.map(column => column.id), 'acciones'];
      }
    ));
  }

  protected getColumns(): Observable<IColumnDefinition[]> {
    return this.ejecucionEconomicaService.getColumnasViajesDietas(this.proyectoSge.id)
      .pipe(
        map(response => this.toColumnDefinition(response))
      );
  }

  protected getDatosEconomicos(
    anualidades: string[],
    devengosRange?: any,
    contabilizacionRange?: any,
    pagosRange?: any
  ): Observable<IDatoEconomico[]> {
    return this.ejecucionEconomicaService
      .getViajesDietas(this.proyectoSge.id, anualidades, pagosRange, devengosRange, contabilizacionRange);
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

}
