import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { IEstadoValidacionIP, TIPO_ESTADO_VALIDACION_MAP } from '@core/models/csp/estado-validacion-ip';
import { IProyectoFacturacion } from '@core/models/csp/proyecto-facturacion';
import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';
import { IFacturaPrevistaPendiente } from '@core/models/sge/factura-prevista-pendiente';
import { ConfigService as ConfigCnfService } from '@core/services/cnf/config.service';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { FacturaPrevistaPendienteService } from '@core/services/sge/factura-prevista-pendiente/factura-prevista-pendiente.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, forkJoin, from, Observable, of, Subscription } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { FacturasPrevistasPendientesListadoExportModalComponent } from '../modals/facturas-previstas-pendientes-listado-export-modal/facturas-previstas-pendientes-listado-export-modal.component';

export interface IFacturaPrevistaPendienteListadoData extends IFacturaPrevistaPendiente {
  tituloProyecto: string;
  fechaEmision: DateTime;
  importeBase: number;
  porcentajeIVA: number;
  importeTotal: number;
  tipoFacturacion: ITipoFacturacion;
  fechaConformidad: DateTime;
  estadoValidacionIP: IEstadoValidacionIP;
}

@Component({
  selector: 'sgi-facturas-previstas-pendientes-listado',
  templateUrl: './facturas-previstas-pendientes-listado.component.html',
  styleUrls: ['./facturas-previstas-pendientes-listado.component.scss']
})
export class FacturasPrevistasPendientesListadoComponent extends AbstractMenuContentComponent implements OnInit, OnDestroy {

  columnas: string[];
  elementosPagina: number[];
  formGroup: FormGroup;
  dataSource = new MatTableDataSource<IFacturaPrevistaPendienteListadoData>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  private subscriptions: Subscription[] = [];
  private limiteRegistrosExportacionExcel: string;

  get TIPO_ESTADO_VALIDACION_MAP() {
    return TIPO_ESTADO_VALIDACION_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    private readonly matDialog: MatDialog,
    private readonly configService: ConfigService,
    private readonly configCnfService: ConfigCnfService,
    private readonly facturaPrevistaPendienteService: FacturaPrevistaPendienteService,
    private readonly proyectoService: ProyectoService,
    private readonly translate: TranslateService
  ) {
    super();
    this.elementosPagina = [5, 10, 25, 100];
  }

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      fechaEmisionDesde: new FormControl(),
      fechaEmisionHasta: new FormControl()
    });

    this.initColumns();
    this.initDataSource();

    this.subscriptions.push(
      this.configCnfService.getLimiteRegistrosExportacionExcel('csp-exp-max-num-registros-excel-listado-facturas-previstas-pendientes').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
  * Load table data
  */
  onSearch(): void {
    this.clearProblems();

    this.subscriptions.push(
      forkJoin({
        facturasPrevistasPendientes: this.facturaPrevistaPendienteService.findAll(this.getFindOptions()).pipe(
          map(response => response.items as IFacturaPrevistaPendienteListadoData[])),
        isCalendarioFacturacionSgeEnabled: this.configService.isCalendarioFacturacionSgeEnabled()
      }).pipe(
        switchMap(({ facturasPrevistasPendientes, isCalendarioFacturacionSgeEnabled }) => {
          if (!facturasPrevistasPendientes?.length) {
            return of([]);
          }
          return from(facturasPrevistasPendientes).pipe(
            mergeMap(facturaPrevistaPendiente => {
              return this.proyectoService.modificable(+facturaPrevistaPendiente.proyectoIdSGI).pipe(
                filter(isModificableByCurrentUser => isModificableByCurrentUser),
                switchMap(() =>
                  of(facturaPrevistaPendiente).pipe(
                    switchMap(facturaPrevistaPendiente =>
                      forkJoin({
                        proyectoFacturacion: this.getProyectoFacturacion(
                          +facturaPrevistaPendiente.proyectoIdSGI,
                          isCalendarioFacturacionSgeEnabled ? facturaPrevistaPendiente.proyectoIdSGE : null,
                          facturaPrevistaPendiente.numeroPrevision
                        ),
                        proyecto: this.proyectoService.findById(+facturaPrevistaPendiente.proyectoIdSGI)
                      }).pipe(
                        map(({ proyecto, proyectoFacturacion }) => {
                          if (proyecto) {
                            facturaPrevistaPendiente.tituloProyecto = proyecto.titulo;
                          }

                          if (proyectoFacturacion) {
                            facturaPrevistaPendiente.fechaEmision = proyectoFacturacion.fechaEmision;
                            facturaPrevistaPendiente.importeBase = proyectoFacturacion.importeBase;
                            facturaPrevistaPendiente.porcentajeIVA = proyectoFacturacion.porcentajeIVA;
                            facturaPrevistaPendiente.importeTotal = (facturaPrevistaPendiente.importeBase * (100 + facturaPrevistaPendiente.porcentajeIVA)) / 100;
                            facturaPrevistaPendiente.tipoFacturacion = proyectoFacturacion.tipoFacturacion;
                            facturaPrevistaPendiente.fechaConformidad = proyectoFacturacion.fechaConformidad;
                            facturaPrevistaPendiente.estadoValidacionIP = proyectoFacturacion.estadoValidacionIP;
                          }

                          return facturaPrevistaPendiente;
                        }),
                        catchError((error) => {
                          this.logger.error(error);
                          this.processError(error);
                          return EMPTY;
                        })
                      )
                    )
                  )
                )
              )
            }, 10),
            toArray()
          );
        }),
        catchError((error) => {
          this.logger.error(error);
          this.processError(error);
          return EMPTY;
        })
      ).subscribe(facturasPrevistasPendientes => this.dataSource.data = facturasPrevistasPendientes)
    );
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    this.resetFilters();
    this.onSearch();
  }

  openExportModal(): void {
    const data: IBaseExportModalData = {
      findOptions: this.getFindOptions(),
      totalRegistrosExportacionExcel: this.dataSource.data.length,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(FacturasPrevistasPendientesListadoExportModalComponent, config);
  }

  private initColumns(): void {
    this.columnas = [
      'tituloProyecto',
      'proyectoIdSGI',
      'proyectoIdSGE',
      'numeroPrevision',
      'fechaEmision',
      'importeBase',
      'porcentajeIVA',
      'importeTotal',
      'tipoFacturacion.nombre',
      'fechaConformidad',
      'estadoValidacionIP.estado'
    ];
  }

  private initDataSource(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (facturaPrevistaPendiente: IFacturaPrevistaPendienteListadoData, property: string) => {
        switch (property) {
          case 'estadoValidacionIP.estado':
            return facturaPrevistaPendiente.estadoValidacionIP?.estado ? this.translate.instant(TIPO_ESTADO_VALIDACION_MAP.get(facturaPrevistaPendiente.estadoValidacionIP?.estado)) : '';
          case 'tipoFacturacion.nombre':
            return facturaPrevistaPendiente.tipoFacturacion?.nombre ?? '';
          default:
            return facturaPrevistaPendiente[property];
        }
      };
    this.dataSource.sort = this.sort;
  }

  /**
    * Crea las opciones para el listado que devuelve el servidor.
    * Hay que añadirlo al método del servicio que llamamos
    *
    */
  private getFindOptions(): SgiRestFindOptions {
    return {
      filter: this.getFilter()
    };
  }

  private getFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    return new RSQLSgiRestFilter('fechaDesde', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEmisionDesde.value))
      .and('fechaHasta', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEmisionHasta.value));
  }

  private resetFilters(): void {
    this.formGroup.controls.fechaEmisionDesde.setValue(null);
    this.formGroup.controls.fechaEmisionHasta.setValue(null);
  }

  private getProyectoFacturacion(proyectoId: number, proyectoSgeRef: string, numeroPrevision: string): Observable<IProyectoFacturacion> {
    const filter = new RSQLSgiRestFilter('numeroPrevision', SgiRestFilterOperator.EQUALS, numeroPrevision)
      .and('proyectoSgeRef', SgiRestFilterOperator.EQUALS, proyectoSgeRef);
    return this.proyectoService.findProyectosFacturacionByProyectoId(proyectoId, { filter }).pipe(map(response => response.items.length === 1 ? response.items[0] : null));
  }

}
