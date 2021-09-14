import { IProyecto } from '@core/models/csp/proyecto';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { ProyectoAgrupacionGastoService } from '@core/services/csp/proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IColumnDefinition } from '../desglose-economico.fragment';
import { FacturasJustificantesFragment } from '../facturas-justificantes.fragment';

export class PersonalContratadoFragment extends FacturasJustificantesFragment {

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
    super(key, proyectoSge, proyectosRelacionados, proyectoService, personaService, proyectoAnualidadService, proyectoAgrupacionGastoService, gastoProyectoService);
  }

  protected onInitialize(): void {
    super.onInitialize();

    this.subscriptions.push(this.getColumns().subscribe(
      (columns) => {
        this.columns = columns;
        this.displayColumns = ['anualidad', 'proyecto', 'agrupacionGasto', 'conceptoGasto', 'aplicacionPresupuestaria', 'codigoEconomico', ...columns.map(column => column.id), 'acciones'];
      }
    ));
  }

  protected getColumns(): Observable<IColumnDefinition[]> {
    return this.ejecucionEconomicaService.getColumnasPersonalContratado(this.proyectoSge.id)
      .pipe(
        map(response => this.toColumnDefinition(response))
      );
  }

  protected getDatosEconomicos(anualidades: string[], devengosRange?: any, contabilizacionRange?: any, pagosRange?: any): Observable<IDatoEconomico[]> {
    return this.ejecucionEconomicaService.getPersonalContratado(this.proyectoSge.id, anualidades, pagosRange, devengosRange, contabilizacionRange);
  }

}
