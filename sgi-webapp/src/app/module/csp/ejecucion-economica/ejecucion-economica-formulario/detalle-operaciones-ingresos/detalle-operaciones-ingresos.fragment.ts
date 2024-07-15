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
import { DetalleOperacionFragment } from '../detalle-operacion.fragment';

export class DetalleOperacionesIngresosFragment extends DetalleOperacionFragment {

  constructor(
    key: number,
    proyectoSge: IProyectoSge,
    relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    proyectoService: ProyectoService,
    proyectoAnualidadService: ProyectoAnualidadService,
    private ejecucionEconomicaService: EjecucionEconomicaService,
    readonly config: IConfiguracion
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
    return this.ejecucionEconomicaService.getColumnasDetalleOperacionesIngresos(this.proyectoSge.id)
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
      tipoGroupBy: false,
      tipoShow: false
    };
  }

  protected getDatosEconomicos(anualidades: string[]): Observable<IDatoEconomico[]> {
    return this.ejecucionEconomicaService.getDetalleOperacionesIngresos(this.proyectoSge.id, anualidades);
  }

  private getDisplayColumns(rowConfig: IRowConfig, columns: IColumnDefinition[]): string[] {
    const displayColumns = [];

    if (rowConfig?.anualidadGroupBy) {
      displayColumns.push('anualidad');
    }

    if (rowConfig?.aplicacionPresupuestariaShow) {
      displayColumns.push('aplicacionPresupuestaria');
    }

    displayColumns.push('codigoEconomico');
    displayColumns.push(...columns.map(column => column.id));

    return displayColumns;
  }

}
