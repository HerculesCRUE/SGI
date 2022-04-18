import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaWithResponsables } from '../../ejecucion-economica.action.service';
import { IColumnDefinition } from '../desglose-economico.fragment';
import { EjecucionPresupuestariaFragment } from '../ejecucion-presupuestaria.fragment';

export class EjecucionPresupuestariaEstadoActualFragment extends EjecucionPresupuestariaFragment {

  constructor(
    key: number,
    proyectoSge: IProyectoSge,
    relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    proyectoService: ProyectoService,
    proyectoAnualidadService: ProyectoAnualidadService,
    private ejecucionEconomicaService: EjecucionEconomicaService
  ) {
    super(key, proyectoSge, relaciones, proyectoService, proyectoAnualidadService);
  }

  protected onInitialize(): void {
    super.onInitialize();

    this.subscriptions.push(this.getColumns().subscribe(
      (columns) => {
        this.columns = columns;
        this.displayColumns = ['anualidad', 'tipo', 'aplicacionPresupuestaria', ...columns.map(column => column.id)];
      }
    ));
  }

  protected getColumns(): Observable<IColumnDefinition[]> {
    return this.ejecucionEconomicaService.getColumnasEjecucionPresupuestariaEstadoActual(this.proyectoSge.id)
      .pipe(
        map(response => this.toColumnDefinition(response))
      );
  }

  protected getDatosEconomicos(anualidades: string[]): Observable<IDatoEconomico[]> {
    return this.ejecucionEconomicaService.getEjecucionPresupuestariaEstadoActual(this.proyectoSge.id, anualidades);
  }

}
