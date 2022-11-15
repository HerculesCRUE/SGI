import { FormControl, FormGroup } from '@angular/forms';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { CalendarioFacturacionService } from '@core/services/sge/calendario-facturacion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaWithResponsables } from '../../ejecucion-economica.action.service';
import {
  DesgloseEconomicoFragment, IColumnDefinition,
  IDesgloseEconomicoExportData, RowTreeDesglose
} from '../desglose-economico.fragment';

export class FacturasEmitidasFragment extends DesgloseEconomicoFragment<IDatoEconomico> {

  readonly formGroupFechas = new FormGroup({
    facturaDesde: new FormControl(),
    facturaHasta: new FormControl()
  });

  constructor(
    key: number,
    proyectoSge: IProyectoSge,
    relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    proyectoService: ProyectoService,
    proyectoAnualidadService: ProyectoAnualidadService,
    private calendarioFacturacionService: CalendarioFacturacionService
  ) {
    super(key, proyectoSge, relaciones, proyectoService, proyectoAnualidadService);
  }

  protected onInitialize(): void {
    super.onInitialize();

    this.subscriptions.push(this.getColumns().subscribe(
      (columns) => {
        this.columns = columns;
        this.displayColumns = ['anualidad', 'factura', ...columns.map(column => column.id), 'acciones'];
      }
    ));
  }

  protected getColumns(): Observable<IColumnDefinition[]> {
    return this.calendarioFacturacionService.getColumnasFacturasEmitidas(this.proyectoSge.id)
      .pipe(
        map(response => this.toColumnDefinition(response))
      );
  }

  protected getDatosEconomicos(facturaRange?: any): Observable<IDatoEconomico[]> {
    return this.calendarioFacturacionService.getFacturasEmitidas(this.proyectoSge.id, facturaRange);
  }

  public loadDataExport(): Observable<IDesgloseEconomicoExportData> {
    const fechas = this.formGroupFechas.controls;
    const facturaRange = {
      desde: LuxonUtils.toBackend(fechas.facturaDesde.value, true),
      hasta: LuxonUtils.toBackend(fechas.facturaHasta.value, true)
    };
    const exportData: IDesgloseEconomicoExportData = {
      data: [],
      columns: []
    };
    return of(exportData).pipe(
      switchMap((exportDataResult) => {
        return this.getDatosEconomicos(facturaRange).pipe(
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
      desde: LuxonUtils.toBackend(fechas.facturaDesde.value, true),
      hasta: LuxonUtils.toBackend(fechas.facturaHasta.value, true)
    };
    this.getDatosEconomicos(facturaRange)
      .pipe(
        switchMap(response => this.buildRows(response))
      ).subscribe(
        (root) => {
          const regs: RowTreeDesglose<any>[] = [];
          root.forEach(r => {
            r.compute(this.columns);
            regs.push(...this.addChilds(r));
          });
          this.desglose$.next(regs);
        }
      );
  }

  protected buildRows(datosEconomicos: IDatoEconomico[]): Observable<RowTreeDesglose<IDatoEconomico>[]> {
    const root: RowTreeDesglose<IDatoEconomico>[] = [];
    const mapTree = new Map<string, RowTreeDesglose<IDatoEconomico>>();
    datosEconomicos.forEach(element => {
      const keyAnualidad = `${element.anualidad}`;
      const keyFactura = `${keyAnualidad}-${element.id}`;
      let anualidad = mapTree.get(keyAnualidad);
      if (!anualidad) {
        anualidad = new RowTreeDesglose(
          {
            anualidad: element.anualidad,
            id: '',
            columnas: this.processColumnsValues(element.columnas, this.columns, true)
          } as IDatoEconomico
        );
        mapTree.set(keyAnualidad, anualidad);
        root.push(anualidad);
      }
      let factura = mapTree.get(keyFactura);
      if (!factura) {
        factura = new RowTreeDesglose(
          {
            anualidad: '',
            id: element.id,
            columnas: this.processColumnsValues(element.columnas, this.columns, false)
          } as IDatoEconomico
        );
        mapTree.set(keyFactura, factura);
        anualidad.addChild(factura);
      }
    });
    return of(root);
  }

}
