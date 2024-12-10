import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TIPO_ESTADO_VALIDACION_MAP, TipoEstadoValidacion } from '@core/models/csp/estado-validacion-ip';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IProyectoFacturacion } from '@core/models/csp/proyecto-facturacion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IFacturaPrevistaPendiente } from '@core/models/sge/factura-prevista-pendiente';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { FacturaPrevistaPendienteService } from '@core/services/sge/factura-prevista-pendiente/factura-prevista-pendiente.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, from, Observable, of } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, toArray } from 'rxjs/operators';

const CODIGO_SGE_KEY = marker('csp.facturas-previstas-pendientes.proyecto-id-sge');
const FECHA_CONFORMIDAD_KEY = marker('csp.facturas-previstas-pendientes.fecha-conformidad');
const FECHA_EMISION_KEY = marker('csp.facturas-previstas-pendientes.fecha-emision');
const IDENTIFICADOR_INTERNO_KEY = marker('csp.facturas-previstas-pendientes.proyecto-id-sgi');
const IMPORTE_BASE_KEY = marker('csp.facturas-previstas-pendientes.importe-base');
const IMPORTE_TOTAL_KEY = marker('csp.facturas-previstas-pendientes.importe-total');
const IVA_KEY = marker('csp.facturas-previstas-pendientes.porcentaje-iva');
const NUMERO_PREVISION_KEY = marker('csp.facturas-previstas-pendientes.numero-prevision');
const TIPO_FACTURACION_KEY = marker('csp.facturas-previstas-pendientes.tipo-facturacion');
const COMENTARIO_KEY = marker('csp.facturas-previstas-pendientes.comentario');
const VALIDACION_IP_KEY = marker('csp.facturas-previstas-pendientes.estado-validacion-ip');
const ENTIDAD_FINANCIADORA_KEY = marker('csp.facturas-previstas-pendientes.entidad-financiadora');

export interface IFacturaPrevistaPendienteReportData extends IFacturaPrevistaPendiente {
  fechaEmision: DateTime;
  importeBase: number;
  porcentajeIVA: number;
  importeTotal: number;
  tipoFacturacion: string;
  fechaConformidad: DateTime;
  estadoValidacionIP: TipoEstadoValidacion;
  entidadesFinanciadoras: IProyectoEntidadFinanciadora[];
  comentario: string;
}

@Injectable()
export class FacturasPrevistasPendientesListadoExportService extends AbstractTableExportService<IFacturaPrevistaPendienteReportData, IReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly empresaService: EmpresaService,
    private readonly proyectoService: ProyectoService,
    protected reportService: ReportService,
    private readonly facturaPrevistaPendienteService: FacturaPrevistaPendienteService,
    private readonly configService: ConfigService
  ) {
    super(reportService);
  }

  protected getRows(facturasPrevistasPendientes: IFacturaPrevistaPendienteReportData[], reportConfig: IReportConfig<IReportOptions>): Observable<ISgiRowReport[]> {
    const rowsReport = facturasPrevistasPendientes.map(facturaPrevistaPendiente => {
      const rowReport: ISgiRowReport = {
        elements: [
          facturaPrevistaPendiente.proyectoIdSGI,
          facturaPrevistaPendiente.proyectoIdSGE,
          facturaPrevistaPendiente.numeroPrevision,
          LuxonUtils.toBackend(facturaPrevistaPendiente.fechaEmision),
          facturaPrevistaPendiente.importeBase ?? 0,
          facturaPrevistaPendiente.porcentajeIVA ?? 0,
          facturaPrevistaPendiente.importeTotal ?? 0,
          facturaPrevistaPendiente.comentario ?? '',
          facturaPrevistaPendiente.tipoFacturacion,
          LuxonUtils.toBackend(facturaPrevistaPendiente.fechaConformidad),
          facturaPrevistaPendiente.estadoValidacionIP ? this.translate.instant(TIPO_ESTADO_VALIDACION_MAP.get(facturaPrevistaPendiente.estadoValidacionIP)) : ''
        ]
      }

      const maxNumEntidasFinanciadoras = Math.max(...facturasPrevistasPendientes.map(f => f.entidadesFinanciadoras?.length ?? 0));

      for (let i = 0; i < maxNumEntidasFinanciadoras; i++) {
        rowReport.elements.push((facturaPrevistaPendiente.entidadesFinanciadoras?.length ?? 0) > i ? facturaPrevistaPendiente.entidadesFinanciadoras[i].empresa?.nombre : '');
      }

      return rowReport;
    })

    return of(rowsReport);
  }

  protected getDataReport(reportConfig: IReportConfig<IReportOptions>): Observable<IFacturaPrevistaPendienteReportData[]> {
    const findOptions = reportConfig.reportOptions?.findOptions;

    return forkJoin({
      facturasPrevistasPendientes: this.facturaPrevistaPendienteService.findAll(findOptions).pipe(
        map(response => response.items as IFacturaPrevistaPendienteReportData[])),
      isCalendarioFacturacionSgeEnabled: this.configService.isCalendarioFacturacionSgeEnabled()
    }).pipe(
      switchMap(({ facturasPrevistasPendientes, isCalendarioFacturacionSgeEnabled }) => {
        if (!facturasPrevistasPendientes?.length) {
          return of([]);
        }
        return from(facturasPrevistasPendientes).pipe(
          mergeMap(facturaPrevistaPendiente => {
            return this.proyectoService.visible(+facturaPrevistaPendiente.proyectoIdSGI).pipe(
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
                          facturaPrevistaPendiente.tipoFacturacion = proyectoFacturacion.tipoFacturacion?.nombre;
                          facturaPrevistaPendiente.fechaConformidad = proyectoFacturacion.fechaConformidad;
                          facturaPrevistaPendiente.estadoValidacionIP = proyectoFacturacion.estadoValidacionIP?.estado;
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
          toArray(),
          map(response => response.sort((a, b) => this.compareFechaEmision(b, a)))
        );
      })
    );
  }

  protected getColumns(facturasPrevistasPendientes: IFacturaPrevistaPendienteReportData[], reportConfig: IReportConfig<IReportOptions>): Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(IDENTIFICADOR_INTERNO_KEY),
        name: 'proyectoIdSGI',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(CODIGO_SGE_KEY),
        name: 'proyectoIdSGE',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(NUMERO_PREVISION_KEY),
        name: 'numeroPrevision',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(FECHA_EMISION_KEY),
        name: 'fechaEmision',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(IMPORTE_BASE_KEY),
        name: 'importeBase',
        type: ColumnType.NUMBER
      },
      {
        title: this.translate.instant(IVA_KEY),
        name: 'porcentajeIVA',
        type: ColumnType.NUMBER
      },
      {
        title: this.translate.instant(IMPORTE_TOTAL_KEY),
        name: 'importeTotal',
        type: ColumnType.NUMBER
      },
      {
        title: this.translate.instant(COMENTARIO_KEY),
        name: 'comentario',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(TIPO_FACTURACION_KEY),
        name: 'tipoFacturacion',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(FECHA_CONFORMIDAD_KEY),
        name: 'fechaConformidad',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(VALIDACION_IP_KEY),
        name: 'estadoValidacionIP',
        type: ColumnType.STRING
      }
    ];

    const maxNumEntidasFinanciadoras = Math.max(...facturasPrevistasPendientes.map(f => f.entidadesFinanciadoras?.length ?? 0));
    const titleEntidadConvocante = this.translate.instant(ENTIDAD_FINANCIADORA_KEY);

    for (let i = 0; i < maxNumEntidasFinanciadoras; i++) {
      const entidadFinanciadoraIndex: string = String(i + 1);
      const columnEntidadConvocante: ISgiColumnReport = {
        name: 'entidadFinanciadora' + entidadFinanciadoraIndex,
        title: titleEntidadConvocante + ' ' + entidadFinanciadoraIndex,
        type: ColumnType.STRING,
      };
      columns.push(columnEntidadConvocante);
    }

    return of(columns);
  }

  protected getGroupBy(): ISgiGroupReport {
    const groupBy: ISgiGroupReport = {
      name: 'header',
      visible: true
    };
    return groupBy;
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

  private compareFechaEmision(itemA: IFacturaPrevistaPendienteReportData, itemB: IFacturaPrevistaPendienteReportData): number {
    const fechaEmisionInMillisItemA = itemA?.fechaEmision?.toMillis() ?? 0;
    const fechaEmisionInMillisItemB = itemB?.fechaEmision?.toMillis() ?? 0;
    return fechaEmisionInMillisItemB - fechaEmisionInMillisItemA;
  }

}
