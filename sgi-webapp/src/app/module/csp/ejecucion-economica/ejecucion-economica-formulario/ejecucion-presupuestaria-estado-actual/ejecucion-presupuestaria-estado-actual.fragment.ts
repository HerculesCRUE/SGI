import { IConfiguracion } from '@core/models/csp/configuracion';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaWithResponsables } from '../../ejecucion-economica.action.service';
import { IColumnDefinition, IRowConfig } from '../desglose-economico.fragment';
import { EjecucionPresupuestariaFragment } from '../ejecucion-presupuestaria.fragment';

export class EjecucionPresupuestariaEstadoActualFragment extends EjecucionPresupuestariaFragment {

  constructor(
    key: number,
    proyectoSge: IProyectoSge,
    relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    proyectoService: ProyectoService,
    proyectoAnualidadService: ProyectoAnualidadService,
    private ejecucionEconomicaService: EjecucionEconomicaService,
    protected readonly config: IConfiguracion
  ) {
    super(key, proyectoSge, relaciones, proyectoService, proyectoAnualidadService, config);
  }

  protected onInitialize(): void {
    super.onInitialize();

    this.subscriptions.push(this.getColumns().subscribe(
      (columns) => {
        this.columns = columns;
        this.displayColumns = this.getDisplayColumns(this.getRowConfig(), columns);
      }
    ));
  }

  protected getColumns(): Observable<IColumnDefinition[]> {
    return this.ejecucionEconomicaService.getColumnasEjecucionPresupuestariaEstadoActual(this.proyectoSge.id)
      .pipe(
        map(response => this.toColumnDefinition(response))
      );
  }

  protected getRowConfig(): IRowConfig {
    return {
      anualidadGroupBy: true,
      anualidadShow: true,
      aplicacionPresupuestariaGroupBy: true,
      aplicacionPresupuestariaShow: true,
      clasificacionSgeGroupBy: false,
      clasificacionSgeShow: false,
      clasificadoAutomaticamenteShow: false,
      proyectoGroupBy: false,
      proyectoShow: false,
      tipoGroupBy: true,
      tipoShow: true
    };
  }

  protected getDatosEconomicos(anualidades: string[]): Observable<IDatoEconomico[]> {
    return this.ejecucionEconomicaService.getEjecucionPresupuestariaEstadoActual(this.proyectoSge.id, anualidades);
  }

  private getDisplayColumns(rowConfig: IRowConfig, columns: IColumnDefinition[]): string[] {
    const displayColumns = [];

    if (rowConfig?.anualidadShow) {
      displayColumns.push('anualidad');
    }

    if (rowConfig?.tipoShow) {
      displayColumns.push('tipo');
    }

    if (rowConfig?.aplicacionPresupuestariaShow) {
      displayColumns.push('aplicacionPresupuestaria');
    }

    displayColumns.push(...columns.map(column => column.id));

    return displayColumns;
  }

}
