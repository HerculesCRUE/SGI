import { FormControl, FormGroup } from '@angular/forms';
import { IConfiguracion } from '@core/models/csp/configuracion';
import { IFacturaEmitida } from '@core/models/sge/factura-emitida';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { LanguageService } from '@core/services/language.service';
import { CalendarioFacturacionService } from '@core/services/sge/calendario-facturacion.service';
import { DateTime } from 'luxon';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaWithResponsables } from '../../ejecucion-economica.action.service';
import { IRowConfig } from '../desglose-economico.fragment';
import { DesgloseFacturaEmitidaFragment, IColumnDefinitionFacturaEmitida, IDesgloseFacturaEmitidaExportData, RowTreeDesgloseFacturaEmitida } from '../desglose-facturas.fragment';

export class FacturasEmitidasFragment extends DesgloseFacturaEmitidaFragment<IFacturaEmitida> {

  readonly formGroupFechas = new FormGroup({
    facturaDesde: new FormControl(),
    facturaHasta: new FormControl()
  });

  constructor(
    key: number,
    proyectoSge: IProyectoSge,
    relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    proyectoService: ProyectoService,
    private calendarioFacturacionService: CalendarioFacturacionService,
    protected readonly languageService: LanguageService,
    protected readonly config: IConfiguracion

  ) {
    super(key, proyectoSge, relaciones, proyectoService, languageService, config);
  }

  protected onInitialize(): void {
    super.onInitialize();
  }

  protected getColumns(): Observable<IColumnDefinitionFacturaEmitida[]> {
    return this.calendarioFacturacionService.getColumnasFacturasEmitidas(this.proyectoSge.id)
      .pipe(
        map(response => this.toColumnDefinition(response))
      );
  }

  protected getFacturasEmitidas(fechaFacturaRange?: { desde: DateTime, hasta: DateTime }): Observable<IFacturaEmitida[]> {
    return this.calendarioFacturacionService.getFacturasEmitidas(this.proyectoSge.id, fechaFacturaRange);
  }

  public loadDataExport(): Observable<IDesgloseFacturaEmitidaExportData> {
    const fechas = this.formGroupFechas.controls;
    const facturaRange = {
      desde: fechas.facturaDesde.value,
      hasta: fechas.facturaHasta.value
    };
    const exportData: IDesgloseFacturaEmitidaExportData = {
      data: [],
      columns: []
    };
    return of(exportData).pipe(
      switchMap((exportDataResult) => {
        return this.getFacturasEmitidas(facturaRange).pipe(
          map(data => {
            exportDataResult.data = data;
            return exportDataResult;
          })
        );
      }),
      switchMap((exportDataResult) => {
        return this.getColumns().pipe(
          map((columns) => {
            exportDataResult.columns = columns;
            return exportDataResult;
          })
        );
      })
    );
  }

  public loadDesglose(): void {
    const fechas = this.formGroupFechas.controls;
    const facturaRange = {
      desde: fechas.facturaDesde.value,
      hasta: fechas.facturaHasta.value
    };
    this.getFacturasEmitidas(facturaRange)
      .pipe(
        switchMap(response => this.buildRows(response))
      ).subscribe(
        (root) => {
          const regs: RowTreeDesgloseFacturaEmitida<any>[] = [];
          root.forEach(r => {
            r.compute(this.columns);
            regs.push(...this.addChilds(r));
          });
          this.desglose$.next(regs);
        }
      );
  }

  protected getRowConfig(): IRowConfig {
    return null;
  }

  protected buildRows(facturasEmitidas: IFacturaEmitida[]): Observable<RowTreeDesgloseFacturaEmitida<IFacturaEmitida>[]> {
    const root: RowTreeDesgloseFacturaEmitida<IFacturaEmitida>[] = [];
    const mapTree = new Map<string, RowTreeDesgloseFacturaEmitida<IFacturaEmitida>>();

    facturasEmitidas.forEach(element => {
      const keyAnualidad = `${element.anualidad}`;
      const keyFactura = `${keyAnualidad}-${element.id}`;

      let anualidad = mapTree.get(keyAnualidad);
      if (!anualidad) {
        anualidad = new RowTreeDesgloseFacturaEmitida(
          {
            anualidad: element.anualidad,
            numeroFactura: '',
            columnas: this.processColumnsValues(element.columnas, this.columns, true)
          } as IFacturaEmitida
        );
        mapTree.set(keyAnualidad, anualidad);
        root.push(anualidad);
      }

      let factura = mapTree.get(keyFactura);
      if (!factura) {
        factura = new RowTreeDesgloseFacturaEmitida(
          {
            id: element.id,
            anualidad: '',
            numeroFactura: element.numeroFactura,
            columnas: this.processColumnsValues(element.columnas, this.columns, false)
          } as IFacturaEmitida
        );
        mapTree.set(keyFactura, factura);
        anualidad.addChild(factura);
      }
    });

    return of(root);
  }

  protected getDisplayColumns(columns: IColumnDefinitionFacturaEmitida[]): string[] {
    return [
      'anualidad',
      'factura',
      ...columns.map(column => column.id),
      'acciones'
    ];
  }

}
