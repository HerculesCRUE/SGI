import { DecimalPipe, PercentPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { FacturaPrevistaEmitidaService } from '@core/services/sge/factura-prevista-emitida/factura-prevista-emitida.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator } from '@sgi/framework/http';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { IProyectoFacturacionData } from './proyecto-formulario/proyecto-calendario-facturacion/proyecto-calendario-facturacion.fragment';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const CALENDARIO_FACTURACION_KEY = marker('menu.csp.proyectos.calendario-facturacion');
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

const CALENDARIO_FACTURACION_FIELD = 'calendarioFacturacion';
const CALENDARIO_FACTURACION_TIPO_FIELD = 'tipoCalendarioFacturacion';

@Injectable()
export class ProyectoCalendarioFacturacionListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private readonly decimalPipe: DecimalPipe,
    private readonly percentPipe: PercentPipe,
    private readonly proyectoService: ProyectoService,
    private readonly facturaPrevistaEmitidaService: FacturaPrevistaEmitidaService
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
          }),
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
          if (facturasEmitidas.items.length === 1) {
            proyectoCalendarioFacturacion.numeroFacturaEmitida = facturasEmitidas.items[0]?.numeroFactura;
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
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsCalendarioFacturacionNotExcel();
    } else {
      return this.getColumnsCalendarioFacturacionExcel(proyectos);
    }
  }

  private getColumnsCalendarioFacturacionNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: CALENDARIO_FACTURACION_FIELD,
      title: this.translate.instant(CALENDARIO_FACTURACION_ITEM_KEY),
      type: ColumnType.STRING
    });

    const titleI18n = this.translate.instant(CALENDARIO_FACTURACION_KEY) +
      ' (' + this.translate.instant(CALENDARIO_FACTURACION_NUM_PREVISION_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_FACTURACION_FECHA_EMISION_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_FACTURACION_IMPORTE_BASE_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_FACTURACION_IVA_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_FACTURACION_IMPORTE_TOTAL_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_FACTURACION_TIPO_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_FACTURACION_ESTADO_VALIDACION_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_FACTURACION_FECHA_CONFORMIDAD_KEY) +
      ' - ' + this.translate.instant(CALENDARIO_FACTURACION_NUM_FACTURA_EMITIDA_KEY) +
      ')';
    const columnEntidad: ISgiColumnReport = {
      name: CALENDARIO_FACTURACION_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsCalendarioFacturacionExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumCalendarioFacturaciones = Math.max(...proyectos.map(p => p.calendarioFacturacion?.length));
    const titleCalendarioFacturacion = this.translate.instant(CALENDARIO_FACTURACION_ITEM_KEY);

    for (let i = 0; i < maxNumCalendarioFacturaciones; i++) {
      const idCalendarioFacturacion: string = String(i + 1);
      const columnCalendarioFacturacion: ISgiColumnReport = {
        name: CALENDARIO_FACTURACION_TIPO_FIELD + idCalendarioFacturacion,
        title: titleCalendarioFacturacion + idCalendarioFacturacion + ': ' + this.translate.instant(CALENDARIO_FACTURACION_TIPO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnCalendarioFacturacion);
    }

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {

    const proyecto = proyectos[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsCalendarioFacturacionNotExcel(proyecto, elementsRow);
    } else {
      const maxNumCalendarioFacturaciones = Math.max(...proyectos.map(p => p.calendarioFacturacion?.length));
      for (let i = 0; i < maxNumCalendarioFacturaciones; i++) {
        const calendarioFacturacion = proyecto.calendarioFacturacion[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, calendarioFacturacion);
      }
    }
    return elementsRow;
  }

  private fillRowsCalendarioFacturacionNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.calendarioFacturacion?.forEach(proyectoCalendarioFacturacion => {
      const calendarioFacturacionElementsRow: any[] = [];

      let calendarioFacturacionContent = proyectoCalendarioFacturacion?.numeroPrevision ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += this.luxonDatePipe.transform(LuxonUtils.toBackend(proyectoCalendarioFacturacion?.fechaEmision, true), 'shortDate') ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += this.decimalPipe.transform(proyectoCalendarioFacturacion.importeBase, '.2-2') ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += proyectoCalendarioFacturacion.porcentajeIVA ?
        this.percentPipe.transform(proyectoCalendarioFacturacion.porcentajeIVA / 100) : '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += this.decimalPipe.transform(
        this.getImporteTotal(proyectoCalendarioFacturacion.importeBase, proyectoCalendarioFacturacion.porcentajeIVA), '.2-2'
      ) ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += proyectoCalendarioFacturacion.tipoFacturacion?.nombre ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += proyectoCalendarioFacturacion.estadoValidacionIP?.estado ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += this.luxonDatePipe.transform(
        LuxonUtils.toBackend(proyectoCalendarioFacturacion?.fechaConformidad, true), 'shortDate') ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += proyectoCalendarioFacturacion.numeroFacturaEmitida ?? '';

      calendarioFacturacionElementsRow.push(calendarioFacturacionContent);

      const rowReport: ISgiRowReport = {
        elements: calendarioFacturacionElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], proyectoCalendarioFacturacion: IProyectoFacturacionData) {
    if (proyectoCalendarioFacturacion) {
      let calendarioFacturacionContent = proyectoCalendarioFacturacion.numeroPrevision ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += LuxonUtils.toBackend(proyectoCalendarioFacturacion?.fechaEmision) ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += proyectoCalendarioFacturacion.importeBase.toString() ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += proyectoCalendarioFacturacion.porcentajeIVA ? proyectoCalendarioFacturacion.porcentajeIVA.toString() + '%' : '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += this.getImporteTotal(
        proyectoCalendarioFacturacion.importeBase, proyectoCalendarioFacturacion.porcentajeIVA
      ).toString() ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += proyectoCalendarioFacturacion.tipoFacturacion?.nombre ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += proyectoCalendarioFacturacion.estadoValidacionIP?.estado ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += LuxonUtils.toBackend(proyectoCalendarioFacturacion?.fechaConformidad) ?? '';
      calendarioFacturacionContent += '\n';
      calendarioFacturacionContent += proyectoCalendarioFacturacion.numeroFacturaEmitida ?? '';

      elementsRow.push(calendarioFacturacionContent);
    } else {
      elementsRow.push('');
    }
  }

  private getImporteTotal(importeBase: number, porcentajeIVA: number): number {
    return importeBase + (importeBase * porcentajeIVA / 100);
  }
}
