import { IConfiguracion } from '@core/models/csp/configuracion';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ConfigService } from '@core/services/cnf/config.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { LanguageService } from '@core/services/language.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ConfigCsp } from 'src/app/module/adm/config-csp/config-csp.component';
import { IRelacionEjecucionEconomicaWithResponsables } from '../../ejecucion-economica.action.service';
import { IColumnDefinition, IRowConfig } from '../desglose-economico.fragment';
import { DetalleOperacionFragment } from '../detalle-operacion.fragment';

export class DetalleOperacionesGastosFragment extends DetalleOperacionFragment {

  get rowConfig(): IRowConfig {
    return this.getRowConfig();
  }

  constructor(
    key: number,
    proyectoSge: IProyectoSge,
    relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    protected readonly languageService: LanguageService,
    proyectoService: ProyectoService,
    proyectoAnualidadService: ProyectoAnualidadService,
    private readonly cnfService: ConfigService,
    private ejecucionEconomicaService: EjecucionEconomicaService,
    protected readonly config: IConfiguracion
  ) {
    super(key, proyectoSge, relaciones, languageService, proyectoService, proyectoAnualidadService, config);
  }

  protected onInitialize(): void {
    super.onInitialize();
  }

  protected getColumns(reducida?: boolean): Observable<IColumnDefinition[]> {
    return this.ejecucionEconomicaService.getColumnasDetalleOperacionesGastos(this.proyectoSge.id, reducida)
      .pipe(
        map(response => this.toColumnDefinition(response))
      );
  }

  protected getRowConfig(): IRowConfig {
    return {
      actionsShow: !!this.config.sgeDetalleOperacionesGastosDetalleEnabled,
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

  protected getDatosEconomicos(anualidades: string[], reducida?: boolean): Observable<IDatoEconomico[]> {
    return this.ejecucionEconomicaService.getDetalleOperacionesGastos(this.proyectoSge.id, anualidades, reducida);
  }

  protected getDisplayColumns(rowConfig: IRowConfig, columns: IColumnDefinition[]): string[] {
    const displayColumns = [];

    if (rowConfig?.anualidadGroupBy) {
      displayColumns.push('anualidad');
    }

    if (rowConfig?.aplicacionPresupuestariaShow) {
      displayColumns.push('aplicacionPresupuestaria');
    }

    displayColumns.push('codigoEconomico');
    displayColumns.push(...columns.map(column => column.id));

    if (rowConfig?.actionsShow) {
      displayColumns.push('acciones');
    }

    return displayColumns;
  }

  protected getLimiteRegistrosExportacionExcel(): Observable<number> {
    return this.cnfService.getLimiteRegistrosExportacionExcel(ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_GASTOS).pipe(
      map(limite => Number(limite))
    );
  }

}
