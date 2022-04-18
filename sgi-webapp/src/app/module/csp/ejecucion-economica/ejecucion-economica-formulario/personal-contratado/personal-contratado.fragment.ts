import { IConfiguracion } from '@core/models/csp/configuracion';
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
import { IColumnDefinition, RowTreeDesglose } from '../desglose-economico.fragment';
import { FacturasJustificantesFragment, IDesglose } from '../facturas-justificantes.fragment';

export class PersonalContratadoFragment extends FacturasJustificantesFragment {

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
    super(
      key,
      proyectoSge,
      relaciones,
      proyectoService,
      proyectoAnualidadService,
      gastoProyectoService,
      proyectoConceptoGastoCodigoEcService,
      proyectoConceptoGastoService,
      configuracion
    );
  }

  protected onInitialize(): void {
    super.onInitialize();

    this.subscriptions.push(this.getColumns(true).subscribe(
      (columns) => {
        this.columns = columns;
        this.displayColumns = [
          'anualidad',
          'proyecto',
          'conceptoGasto',
          'clasificacionSGE',
          'aplicacionPresupuestaria',
          'codigoEconomico',
          'fechaDevengo',
          ...columns.map(column => column.id),
          'acciones'
        ];
      }
    ));
  }

  protected getColumns(reducida?: boolean): Observable<IColumnDefinition[]> {
    return this.ejecucionEconomicaService.getColumnasPersonalContratado(this.proyectoSge.id, reducida)
      .pipe(
        map(response => this.toColumnDefinition(response))
      );
  }

  protected getDatosEconomicos(
    anualidades: string[],
    devengosRange?: any,
    contabilizacionRange?: any,
    pagosRange?: any,
    reducida?: boolean): Observable<IDatoEconomico[]> {
    return this.ejecucionEconomicaService.getPersonalContratado(
      this.proyectoSge.id, anualidades, reducida, pagosRange, devengosRange, contabilizacionRange);
  }

  public clearRangos(): void {
    this.formGroupFechas.reset();
  }

  protected sortRowsTree(rows: RowTreeDesglose<IDesglose>[]): void {
    rows.sort((a, b) => {
      return this.compareAnualidadRowTree(b, a)
        || this.compareProyectoTituloRowTree(a, b)
        || this.compareConceptoGastoNombreRowTree(a, b)
        || this.compareClasificacionSGENombreRowTree(a, b)
        || this.comparePartidaPresupuestariaRowTree(a, b)
        || this.compareCodigoEconomicoRowTree(a, b);
    });
  }

  protected sortRowsDesglose(rows: IDesglose[]): void {
    rows.sort((a, b) => {
      return this.compareAnualidadDesglose(b, a)
        || this.compareProyectoTituloDesglose(a, b)
        || this.compareConceptoGastoNombreDesglose(a, b)
        || this.compareClasificacionSGENombreDesglose(a, b)
        || this.comparePartidaPresupuestariaDesglose(a, b)
        || this.compareCodigoEconomicoDesglose(a, b);
    });
  }

}
