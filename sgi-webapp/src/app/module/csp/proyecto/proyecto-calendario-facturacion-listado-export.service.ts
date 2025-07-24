import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TIPO_ESTADO_VALIDACION_MAP } from '@core/models/csp/estado-validacion-ip';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { LanguageService } from '@core/services/language.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { FacturaPrevistaEmitidaService } from '@core/services/sge/factura-prevista-emitida/factura-prevista-emitida.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, from, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { IProyectoFacturacionData } from './proyecto-formulario/proyecto-calendario-facturacion/proyecto-calendario-facturacion.fragment';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const CALENDARIO_FACTURACION_ITEM_KEY = marker('csp.proyecto-calendario-facturacion.item');
const CALENDARIO_FACTURACION_NUM_PREVISION_KEY = marker('csp.proyecto-calendario-facturacion.numero-prevision');
const CALENDARIO_FACTURACION_FECHA_EMISION_KEY = marker('csp.proyecto-calendario-facturacion.fecha-emision');
const CALENDARIO_FACTURACION_IMPORTE_BASE_KEY = marker('csp.proyecto-calendario-facturacion.importe-base');
const CALENDARIO_FACTURACION_IVA_KEY = marker('csp.proyecto-calendario-facturacion.iva');
const CALENDARIO_FACTURACION_IMPORTE_TOTAL_KEY = marker('csp.proyecto-calendario-facturacion.importe-total');
const CALENDARIO_FACTURACION_TIPO_KEY = marker('csp.proyecto-calendario-facturacion.tipo-facturacion');
const CALENDARIO_FACTURACION_ESTADO_VALIDACION_KEY = marker('csp.proyecto-calendario-facturacion.estado-validacion');
const CALENDARIO_FACTURACION_FECHA_CONFORMIDAD_KEY = marker('csp.proyecto-calendario-facturacion.fecha-conformidad');
const CALENDARIO_FACTURACION_NUM_FACTURA_EMITIDA_KEY = marker('csp.proyecto-calendario-facturacion.numero-factura-emitida');
const CALENDARIO_FACTURACION_PRORROGA_KEY = marker('csp.proyecto-calendario-facturacion.prorroga');

const CALENDARIO_FACTURACION_TIPO_FIELD = 'tipoCalendarioFacturacion';
const CALENDARIO_FACTURACION_NUM_PREVISION_FIELD = 'numPrevisionFacturacion';
const CALENDARIO_FACTURACION_FECHA_EMISION_FIELD = 'fechaEmisionFacturacion';
const CALENDARIO_FACTURACION_IMPORTE_BASE_FIELD = 'importeBaseFacturacion';
const CALENDARIO_FACTURACION_IVA_FIELD = 'ivaFacturacion';
const CALENDARIO_FACTURACION_IMPORTE_TOTAL_FIELD = 'importeTotalFacturacion';
const CALENDARIO_FACTURACION_ESTADO_VALIDACION_FIELD = 'estadoValidacionFacturacion';
const CALENDARIO_FACTURACION_FECHA_CONFORMIDAD_FIELD = 'fechaConformidadFacturacion';
const CALENDARIO_FACTURACION_NUM_FACTURA_EMITIDA_FIELD = 'numFacturaEmitidaFacturacion';
const CALENDARIO_FACTURACION_PRORROGA_FIELD = 'prorroga';

@Injectable()
export class ProyectoCalendarioFacturacionListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private readonly proyectoProrrogaService: ProyectoProrrogaService,
    private readonly facturaPrevistaEmitidaService: FacturaPrevistaEmitidaService,
    private readonly languageService: LanguageService
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    return this.proyectoService.findProyectosFacturacionByProyectoId(proyectoData.id).pipe(
      map(response => response.items.map(item => item as IProyectoFacturacionData)),
      switchMap((responseCalendarioFacturacion) => {
        if (responseCalendarioFacturacion.length === 0) {
          return of(responseCalendarioFacturacion);
        }

        return from(responseCalendarioFacturacion).pipe(
          mergeMap((proyectoCalendarioFacturacion) => {
            return this.getNumeroFacturaEmitida(proyectoCalendarioFacturacion);
          }, this.DEFAULT_CONCURRENT),
          map(() => responseCalendarioFacturacion)
        );
      }),
      switchMap((responseCalendarioFacturacion) => {
        if (responseCalendarioFacturacion.length === 0) {
          return of(responseCalendarioFacturacion);
        }

        return from(responseCalendarioFacturacion).pipe(
          mergeMap((proyectoCalendarioFacturacion) => {
            if (!proyectoCalendarioFacturacion.proyectoProrroga?.id) {
              return of(proyectoCalendarioFacturacion);
            }

            return this.proyectoProrrogaService.findById(proyectoCalendarioFacturacion.proyectoProrroga.id).pipe(
              map(proyectoProrroga => {
                proyectoCalendarioFacturacion.proyectoProrroga = proyectoProrroga;
                return proyectoCalendarioFacturacion;
              })
            );
          }, this.DEFAULT_CONCURRENT),
          map(() => responseCalendarioFacturacion)
        );
      }),
      map(responseCalendarioFacturacion => {
        proyectoData.calendarioFacturacion = responseCalendarioFacturacion;
        return proyectoData;
      }),
      takeLast(1)
    );
  }

  private getNumeroFacturaEmitida(proyectoCalendarioFacturacion: IProyectoFacturacionData): Observable<IProyectoFacturacionData> {
    if (proyectoCalendarioFacturacion.fechaConformidad && proyectoCalendarioFacturacion.numeroPrevision) {

      const filter = new RSQLSgiRestFilter('proyectoIdSGI',
        SgiRestFilterOperator.EQUALS, proyectoCalendarioFacturacion.proyectoId?.toString())
        .and('numeroPrevision', SgiRestFilterOperator.EQUALS, proyectoCalendarioFacturacion.numeroPrevision.toString());

      return this.facturaPrevistaEmitidaService.findAll({ filter }).pipe(
        map((facturasEmitidas) => {
          if (facturasEmitidas.items.length > 0) {
            proyectoCalendarioFacturacion.numeroFacturaEmitida = facturasEmitidas.items.map(item => item.numeroFactura).join('*');
          }
          return proyectoCalendarioFacturacion;
        })
      );
    } else {
      return of(proyectoCalendarioFacturacion);
    }
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsCalendarioFacturacionExcel(proyectos);
    }
  }

  private getColumnsCalendarioFacturacionExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumCalendarioFacturaciones = Math.max(...proyectos.map(p => p.calendarioFacturacion?.length));
    const titleCalendarioFacturacion = this.translate.instant(CALENDARIO_FACTURACION_ITEM_KEY);

    for (let i = 0; i < maxNumCalendarioFacturaciones; i++) {
      const idCalendarioFacturacion: string = String(i + 1);
      const columnNumPrevisionCalendarioFacturacion: ISgiColumnReport = {
        name: CALENDARIO_FACTURACION_NUM_PREVISION_FIELD + idCalendarioFacturacion,
        title: titleCalendarioFacturacion + idCalendarioFacturacion + ': ' + this.translate.instant(CALENDARIO_FACTURACION_NUM_PREVISION_KEY),
        type: ColumnType.NUMBER,
      };
      columns.push(columnNumPrevisionCalendarioFacturacion);

      const columnFechaEmisionCalendarioFacturacion: ISgiColumnReport = {
        name: CALENDARIO_FACTURACION_FECHA_EMISION_FIELD + idCalendarioFacturacion,
        title: titleCalendarioFacturacion + idCalendarioFacturacion + ': ' + this.translate.instant(CALENDARIO_FACTURACION_FECHA_EMISION_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaEmisionCalendarioFacturacion);

      const columnImporteBaseCalendarioFacturacion: ISgiColumnReport = {
        name: CALENDARIO_FACTURACION_IMPORTE_BASE_FIELD + idCalendarioFacturacion,
        title: titleCalendarioFacturacion + idCalendarioFacturacion + ': ' + this.translate.instant(CALENDARIO_FACTURACION_IMPORTE_BASE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnImporteBaseCalendarioFacturacion);

      const columnIvaCalendarioFacturacion: ISgiColumnReport = {
        name: CALENDARIO_FACTURACION_IVA_FIELD + idCalendarioFacturacion,
        title: titleCalendarioFacturacion + idCalendarioFacturacion + ': ' + this.translate.instant(CALENDARIO_FACTURACION_IVA_KEY),
        type: ColumnType.NUMBER,
        format: '#,#" "%'
      };
      columns.push(columnIvaCalendarioFacturacion);

      const columnImporteTotalCalendarioFacturacion: ISgiColumnReport = {
        name: CALENDARIO_FACTURACION_IMPORTE_TOTAL_FIELD + idCalendarioFacturacion,
        title: titleCalendarioFacturacion + idCalendarioFacturacion + ': ' + this.translate.instant(CALENDARIO_FACTURACION_IMPORTE_TOTAL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnImporteTotalCalendarioFacturacion);

      const columnTipoCalendarioFacturacion: ISgiColumnReport = {
        name: CALENDARIO_FACTURACION_TIPO_FIELD + idCalendarioFacturacion,
        title: titleCalendarioFacturacion + idCalendarioFacturacion + ': ' + this.translate.instant(CALENDARIO_FACTURACION_TIPO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTipoCalendarioFacturacion);

      const columnProrogaCalendarioFacturacion: ISgiColumnReport = {
        name: CALENDARIO_FACTURACION_PRORROGA_FIELD + idCalendarioFacturacion,
        title: titleCalendarioFacturacion + idCalendarioFacturacion + ': ' + this.translate.instant(CALENDARIO_FACTURACION_PRORROGA_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnProrogaCalendarioFacturacion);

      const columnEstadoValidacionCalendarioFacturacion: ISgiColumnReport = {
        name: CALENDARIO_FACTURACION_ESTADO_VALIDACION_FIELD + idCalendarioFacturacion,
        title: titleCalendarioFacturacion + idCalendarioFacturacion + ': ' + this.translate.instant(CALENDARIO_FACTURACION_ESTADO_VALIDACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEstadoValidacionCalendarioFacturacion);

      const columnFechaConformidadCalendarioFacturacion: ISgiColumnReport = {
        name: CALENDARIO_FACTURACION_FECHA_CONFORMIDAD_FIELD + idCalendarioFacturacion,
        title: titleCalendarioFacturacion + idCalendarioFacturacion + ': ' + this.translate.instant(CALENDARIO_FACTURACION_FECHA_CONFORMIDAD_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaConformidadCalendarioFacturacion);

      const columnNumFacturaEmitidaCalendarioFacturacion: ISgiColumnReport = {
        name: CALENDARIO_FACTURACION_NUM_FACTURA_EMITIDA_FIELD + idCalendarioFacturacion,
        title: titleCalendarioFacturacion + idCalendarioFacturacion + ': ' + this.translate.instant(CALENDARIO_FACTURACION_NUM_FACTURA_EMITIDA_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNumFacturaEmitidaCalendarioFacturacion);
    }

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = proyectos[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumCalendarioFacturaciones = Math.max(...proyectos.map(p => p.calendarioFacturacion?.length));
      for (let i = 0; i < maxNumCalendarioFacturaciones; i++) {
        const calendarioFacturacion = proyecto.calendarioFacturacion[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, calendarioFacturacion);
      }
    }
    return elementsRow;
  }

  private fillRowsEntidadExcel(elementsRow: any[], proyectoCalendarioFacturacion: IProyectoFacturacionData) {
    if (proyectoCalendarioFacturacion) {
      elementsRow.push(proyectoCalendarioFacturacion.numeroPrevision ?? '');
      elementsRow.push(LuxonUtils.toBackend(proyectoCalendarioFacturacion?.fechaEmision) ?? '');
      elementsRow.push(proyectoCalendarioFacturacion.importeBase.toString() ?? '');
      elementsRow.push(proyectoCalendarioFacturacion.porcentajeIVA ? proyectoCalendarioFacturacion.porcentajeIVA / 100 : '');
      elementsRow.push(this.getImporteTotal(
        proyectoCalendarioFacturacion.importeBase, proyectoCalendarioFacturacion.porcentajeIVA
      ).toString() ?? '');
      elementsRow.push(this.languageService.getFieldValue(proyectoCalendarioFacturacion.tipoFacturacion?.nombre));
      elementsRow.push(proyectoCalendarioFacturacion.proyectoProrroga?.numProrroga ? `${proyectoCalendarioFacturacion.proyectoProrroga?.numProrroga} - ${LuxonUtils.toBackend(proyectoCalendarioFacturacion?.fechaEmision)}` : '');
      elementsRow.push(proyectoCalendarioFacturacion.estadoValidacionIP?.estado ? this.translate.instant(TIPO_ESTADO_VALIDACION_MAP.get(proyectoCalendarioFacturacion.estadoValidacionIP?.estado)) : '');
      elementsRow.push(LuxonUtils.toBackend(proyectoCalendarioFacturacion?.fechaConformidad) ?? '');
      elementsRow.push(proyectoCalendarioFacturacion.numeroFacturaEmitida ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private getImporteTotal(importeBase: number, porcentajeIVA: number): number {
    return importeBase + (importeBase * porcentajeIVA / 100);
  }
}
