import { FacturasJustificantesColumnasFijasConfigurables, IConfiguracion } from '@core/models/csp/configuracion';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoConceptoGastoCodigoEcService } from '@core/services/csp/proyecto-concepto-gasto-codigo-ec.service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaWithResponsables } from '../../ejecucion-economica.action.service';
import { IColumnDefinition, IRowConfig, RowTreeDesglose } from '../desglose-economico.fragment';
import { FacturasJustificantesFragment, IDesglose } from '../facturas-justificantes.fragment';

export class FacturasGastosFragment extends FacturasJustificantesFragment {

  constructor(
    key: number,
    proyectoSge: IProyectoSge,
    relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    proyectoService: ProyectoService,
    proyectoAnualidadService: ProyectoAnualidadService,
    gastoProyectoService: GastoProyectoService,
    private ejecucionEconomicaService: EjecucionEconomicaService,
    proyectoConceptoGastoCodigoEcService: ProyectoConceptoGastoCodigoEcService,
    proyectoConceptoGastoService: ProyectoConceptoGastoService,
    configuracion: IConfiguracion,
  ) {
    super(key, proyectoSge, relaciones, proyectoService, proyectoAnualidadService,
      gastoProyectoService, proyectoConceptoGastoCodigoEcService, proyectoConceptoGastoService, configuracion);
  }

  protected onInitialize(): void {
    super.onInitialize();

    this.subscriptions.push(this.getColumns(true).subscribe(
      (columns) => {
        this.columns = columns;
        this.displayColumns = this.getDisplayColumns(this.getRowConfig(), columns);
      }
    ));
  }

  protected getColumns(reducida?: boolean): Observable<IColumnDefinition[]> {
    return this.ejecucionEconomicaService.getColumnasFacturasGastos(this.proyectoSge.id, reducida)
      .pipe(
        map(response => this.toColumnDefinition(response))
      );
  }

  protected getRowConfig(): IRowConfig {
    return {
      anualidadGroupBy: true,
      anualidadShow: true,
      aplicacionPresupuestariaGroupBy: this.config.facturasGastosColumnasFijasVisibles?.some(c => c === FacturasJustificantesColumnasFijasConfigurables.APLICACION_PRESUPUESTARIA),
      aplicacionPresupuestariaShow: this.config.facturasGastosColumnasFijasVisibles?.some(c => c === FacturasJustificantesColumnasFijasConfigurables.APLICACION_PRESUPUESTARIA),
      clasificacionSgeGroupBy: this.config.facturasGastosColumnasFijasVisibles?.some(c => c === FacturasJustificantesColumnasFijasConfigurables.CLASIFICACION_SGE),
      clasificacionSgeShow: this.config.facturasGastosColumnasFijasVisibles?.some(c => c === FacturasJustificantesColumnasFijasConfigurables.CLASIFICACION_SGE),
      clasificadoAutomaticamenteShow: this.isClasificacionGastosEnabled,
      proyectoGroupBy: !this.disableProyectoSgi,
      proyectoShow: !this.disableProyectoSgi,
      tipoGroupBy: false,
      tipoShow: false
    };
  }

  protected getDatosEconomicos(
    anualidades: string[],
    devengosRange?: any,
    contabilizacionRange?: any,
    pagosRange?: any,
    reducida?: boolean
  ): Observable<IDatoEconomico[]> {
    return this.ejecucionEconomicaService.getFacturasGastos(
      this.proyectoSge.id,
      anualidades,
      reducida,
      pagosRange,
      devengosRange,
      contabilizacionRange
    );
  }

  public clearRangos(): void {
    this.formGroupFechas.reset();
  }

  protected sortRowsTree(rows: RowTreeDesglose<IDesglose>[]): void {
    const rowConfig = this.getRowConfig();
    rows.sort((a, b) => {
      return this.compareAnualidadRowTree(b, a, rowConfig)
        || this.compareProyectoTituloRowTree(a, b, rowConfig)
        || this.compareConceptoGastoNombreRowTree(a, b)
        || this.compareClasificacionSGENombreRowTree(a, b, rowConfig)
        || this.comparePartidaPresupuestariaRowTree(a, b, rowConfig)
        || this.compareCodigoEconomicoRowTree(a, b)
        || this.compareFechaDevengoRowTree(a, b);
    });
  }

  protected sortRowsDesglose(rows: IDesglose[]): void {
    const rowConfig = this.getRowConfig();
    rows.sort((a, b) => {
      return this.compareAnualidadDesglose(b, a, rowConfig)
        || this.compareProyectoTituloDesglose(a, b, rowConfig)
        || this.compareConceptoGastoNombreDesglose(a, b)
        || this.compareClasificacionSGENombreDesglose(a, b, rowConfig)
        || this.comparePartidaPresupuestariaDesglose(a, b, rowConfig)
        || this.compareCodigoEconomicoDesglose(a, b)
        || this.compareFechaDevengoDesglose(a, b);
    });
  }

  private getDisplayColumns(rowConfig: IRowConfig, columns: IColumnDefinition[]): string[] {
    const displayColumns = [];

    if (rowConfig?.anualidadShow) {
      displayColumns.push('anualidad');
    }

    if (rowConfig?.proyectoShow) {
      displayColumns.push('proyecto');
    }

    displayColumns.push('conceptoGasto');

    if (rowConfig?.clasificacionSgeShow) {
      displayColumns.push('clasificacionSGE');
    }

    if (rowConfig?.aplicacionPresupuestariaShow) {
      displayColumns.push('aplicacionPresupuestaria');
    }

    displayColumns.push('codigoEconomico');
    displayColumns.push('fechaDevengo');
    displayColumns.push(...columns.map(column => column.id));
    displayColumns.push('acciones');

    return displayColumns;
  }

}
