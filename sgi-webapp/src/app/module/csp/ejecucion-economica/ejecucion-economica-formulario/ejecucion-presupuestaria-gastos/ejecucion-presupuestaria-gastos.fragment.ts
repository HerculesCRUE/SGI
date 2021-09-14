import { IProyecto } from '@core/models/csp/proyecto';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IColumnDefinition } from '../desglose-economico.fragment';
import { EjecucionPresupuestariaFragment } from '../ejecucion-presupuestaria.fragment';

export class EjecucionPresupuestariaGastosFragment extends EjecucionPresupuestariaFragment {

  constructor(
    key: number,
    proyectoSge: IProyectoSge,
    proyectosRelacionados: IProyecto[],
    proyectoService: ProyectoService,
    personaService: PersonaService,
    proyectoAnualidadService: ProyectoAnualidadService,
    private ejecucionEconomicaService: EjecucionEconomicaService
  ) {
    super(key, proyectoSge, proyectosRelacionados, proyectoService, personaService, proyectoAnualidadService);
  }

  protected onInitialize(): void {
    super.onInitialize();

    this.subscriptions.push(this.getColumns().subscribe(
      (columns) => {
        this.columns = columns;
        this.displayColumns = ['anualidad', 'aplicacionPresupuestaria', ...columns.map(column => column.id)];
      }
    ));
  }

  protected getColumns(): Observable<IColumnDefinition[]> {
    return this.ejecucionEconomicaService.getColumnasEjecucionPresupuestariaGastos(this.proyectoSge.id)
      .pipe(
        map(response => this.toColumnDefinition(response))
      );
  }

  protected getDatosEconomicos(anualidades: string[]): Observable<IDatoEconomico[]> {
    return this.ejecucionEconomicaService.getEjecucionPresupuestariaGastos(this.proyectoSge.id, anualidades);
  }

}
