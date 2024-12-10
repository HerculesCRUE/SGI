import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { SgiError } from '@core/errors/sgi-error';
import { IEstadoValidacionIP, TIPO_ESTADO_VALIDACION_MAP } from '@core/models/csp/estado-validacion-ip';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IProyectoFacturacion } from '@core/models/csp/proyecto-facturacion';
import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IFacturaPrevistaPendiente } from '@core/models/sge/factura-prevista-pendiente';
import { ConfigService as ConfigCnfService } from '@core/services/cnf/config.service';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DialogService } from '@core/services/dialog.service';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { FacturaPrevistaPendienteService } from '@core/services/sge/factura-prevista-pendiente/factura-prevista-pendiente.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, forkJoin, from, Observable, of, Subscription } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { FacturasPrevistasPendientesListadoExportService } from '../facturas-previstas-pendientes-listado-export.service';

const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const WARN_EXPORT_EXCEL_KEY = marker('msg.export.max-registros-warning');

export interface IFacturaPrevistaPendienteListadoData extends IFacturaPrevistaPendiente {
  fechaEmision: DateTime;
  importeBase: number;
  porcentajeIVA: number;
  importeTotal: number;
  tipoFacturacion: ITipoFacturacion;
  fechaConformidad: DateTime;
  estadoValidacionIP: IEstadoValidacionIP;
  comentario: string;
  entidadesFinanciadoras: IProyectoEntidadFinanciadora[];
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
  private limiteRegistrosExportacionExcel: number;

  get TIPO_ESTADO_VALIDACION_MAP() {
    return TIPO_ESTADO_VALIDACION_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    private readonly configService: ConfigService,
    private readonly configCnfService: ConfigCnfService,
    private readonly empresaService: EmpresaService,
    private readonly facturaPrevistaPendienteService: FacturaPrevistaPendienteService,
    private readonly proyectoService: ProyectoService,
    private readonly exportService: FacturasPrevistasPendientesListadoExportService,
    private readonly dialogService: DialogService,
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
        this.limiteRegistrosExportacionExcel = value ? +value : null;
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
              return this.proyectoService.visible(+facturaPrevistaPendiente.proyectoIdSGI).pipe(
                filter(isVisibleByCurrentUser => isVisibleByCurrentUser),
                switchMap(() =>
                  of(facturaPrevistaPendiente).pipe(
                    switchMap(facturaPrevistaPendiente =>
                      forkJoin({
                        proyectoFacturacion: this.getProyectoFacturacion(
                          +facturaPrevistaPendiente.proyectoIdSGI,
                          isCalendarioFacturacionSgeEnabled ? facturaPrevistaPendiente.proyectoIdSGE : null,
                          facturaPrevistaPendiente.numeroPrevision
                        ),
                        entidadesFinanciadoras: this.getEntidadesFinanciadoras(+facturaPrevistaPendiente.proyectoIdSGI)
                      }).pipe(
                        map(({ entidadesFinanciadoras, proyectoFacturacion }) => {
                          if (entidadesFinanciadoras) {
                            facturaPrevistaPendiente.entidadesFinanciadoras = entidadesFinanciadoras;
                          }

                          if (proyectoFacturacion) {
                            facturaPrevistaPendiente.fechaEmision = proyectoFacturacion.fechaEmision;
                            facturaPrevistaPendiente.importeBase = proyectoFacturacion.importeBase;
                            facturaPrevistaPendiente.porcentajeIVA = proyectoFacturacion.porcentajeIVA;
                            facturaPrevistaPendiente.importeTotal = (facturaPrevistaPendiente.importeBase * (100 + facturaPrevistaPendiente.porcentajeIVA)) / 100;
                            facturaPrevistaPendiente.tipoFacturacion = proyectoFacturacion.tipoFacturacion;
                            facturaPrevistaPendiente.fechaConformidad = proyectoFacturacion.fechaConformidad;
                            facturaPrevistaPendiente.estadoValidacionIP = proyectoFacturacion.estadoValidacionIP;
                            facturaPrevistaPendiente.comentario = proyectoFacturacion.comentario;
                          }

                          return facturaPrevistaPendiente;
                        })
                      )
                    )
                  )
                ),
                catchError((error) => {
                  this.logger.error(error);
                  return of(facturaPrevistaPendiente);
                })
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
   * Clean filters
   */
  onClearFilters(): void {
    this.resetFilters();
    this.clearProblems();
    this.dataSource.data = [];
  }

  exportCSV(): void {
    this.export(OutputReport.CSV);
  }

  exportXLSX(): void {
    this.export(OutputReport.XLSX);
  }

  private export(outputType: OutputReport): void {
    if (this.isTotalRegistosGreatherThanLimite(this.dataSource.data.length)) {
      this.dialogService.showInfoDialog(WARN_EXPORT_EXCEL_KEY, { max: this.limiteRegistrosExportacionExcel });
      return;
    }

    this.problems$.next([]);
    this.subscriptions.push(this.exportService.export(this.getReportOptions(outputType)).subscribe(
      () => { },
      ((error: Error) => {
        if (error instanceof SgiError) {
          this.problems$.next([error]);
        } else {
          this.problems$.next([new SgiError(MSG_DOWNLOAD_ERROR)]);
        }
      })
    ));
  }

  private getReportOptions(outputType: OutputReport): IReportConfig<IReportOptions> {
    const reportModalData: IReportConfig<IReportOptions> = {
      outputType,
      reportOptions: {
        findOptions: this.getFindOptions()
      }
    };
    return reportModalData;
  }

  private isTotalRegistosGreatherThanLimite(totalRegistrosExportacion: number): boolean {
    return totalRegistrosExportacion && this.limiteRegistrosExportacionExcel && totalRegistrosExportacion > this.limiteRegistrosExportacionExcel;
  }

  private initColumns(): void {
    this.columnas = [
      'proyectoIdSGI',
      'proyectoIdSGE',
      'entidadesFinanciadoras',
      'numeroPrevision',
      'fechaEmision',
      'importeBase',
      'porcentajeIVA',
      'importeTotal',
      'comentario',
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

  private getEntidadesFinanciadoras(proyectoId: number): Observable<IProyectoEntidadFinanciadora[]> {
    return this.proyectoService.findEntidadesFinanciadoras(proyectoId).pipe(
      map(response => response.items),
      switchMap(entidades => from(entidades).pipe(
        mergeMap(entidad => {
          return this.empresaService.findById(entidad.empresa.id).pipe(
            map((empresa) => {
              entidad.empresa = empresa;
              return entidad;
            })
          );
        }),
        toArray()
      ))
    )
  }

}
