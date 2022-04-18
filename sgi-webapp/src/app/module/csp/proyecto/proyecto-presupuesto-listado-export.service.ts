import { DecimalPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoPresupuestoTotales } from '@core/models/csp/proyecto-presupuesto-totales';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const PRESUPUESTO_KEY = marker('csp.proyecto-presupuesto');
const IMPORTE_PRESUPUESTO_UNIVERSIDAD_KEY = marker('csp.proyecto-presupuesto.importe-presupuesto-universidad');
const IMPORTE_PRESUPUESTO_UNIVERSIDAD_COSTES_INDIRECTOS_KEY = marker('csp.proyecto-presupuesto.importe-presupuesto-universidad-costes-indirectos');
const TOTAL_IMPORTE_PRESUPUESTO_UNIVERSIDAD_KEY = marker('csp.proyecto-presupuesto.total-importe-presupuesto-universidad');
const IMPORTE_CONCEDIDO_UNIVERSIDAD_KEY = marker('csp.proyecto-presupuesto.importe-concedido-universidad');
const IMPORTE_CONCEDIDO_UNIVERSIDAD_COSTES_INDIRECTOS_KEY = marker('csp.proyecto-presupuesto.importe-concedido-universidad-costes-indirectos');
const TOTAL_IMPORTE_CONCEDIDO_UNIVERSIDAD_KEY = marker('csp.proyecto-presupuesto.total-importe-concedido-universidad');
const IMPORTE_PRESUPUESTO_SOCIOS_KEY = marker('csp.proyecto-presupuesto.importe-presupuesto-socios');
const IMPORTE_CONCEDIDO_SOCIOS_KEY = marker('csp.proyecto-presupuesto.importe-concedido-socios');
const TOTAL_IMPORTE_PRESUPUESTO_KEY = marker('csp.proyecto-presupuesto.total-importe-presupuesto');
const TOTAL_IMPORTE_CONCEDIDO_KEY = marker('csp.proyecto-presupuesto.total-importe-concedido');

const PRESUPUESTO_FIELD = 'presupuesto';
const IMPORTE_PRESUPUESTO_UNIVERSIDAD_FIELD = 'importePresupuestoUniversidad';
const IMPORTE_PRESUPUESTO_UNIVERSIDAD_COSTES_INDIRECTOS_FIELD = 'importePresupuestoUniversidadCostesIndirectos';
const TOTAL_IMPORTE_PRESUPUESTO_UNIVERSIDAD_FIELD = 'totalImportePresupuestoUniversidad';
const IMPORTE_CONCEDIDO_UNIVERSIDAD_FIELD = 'importeConcedidoUniversidad';
const IMPORTE_CONCEDIDO_UNIVERSIDAD_COSTES_INDIRECTOS_FIELD = 'importeConcedidoUniversidadCostesIndirectos';
const TOTAL_IMPORTE_CONCEDIDO_UNIVERSIDAD_FIELD = 'totalImporteConcedidoUniversidad';
const IMPORTE_PRESUPUESTO_SOCIOS_FIELD = 'importePresupuestoSocios';
const IMPORTE_CONCEDIDO_SOCIOS_FIELD = 'importeConcedidoSocios';
const TOTAL_IMPORTE_PRESUPUESTO_FIELD = 'importePresupuesto';
const TOTAL_IMPORTE_CONCEDIDO_FIELD = 'importeConcedido';

@Injectable()
export class ProyectoPresupuestoListadoExportService extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly decimalPipe: DecimalPipe,
    private readonly proyectoService: ProyectoService
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    if (proyectoData.id && !proyectoData.importeConcedido && !proyectoData.importeConcedidoCostesIndirectos
      && !proyectoData.importeConcedidoSocios && !proyectoData.importePresupuesto && !proyectoData.importePresupuestoCostesIndirectos
      && !proyectoData.importePresupuestoSocios) {
      return this.proyectoService.getProyectoPresupuestoTotales(proyectoData.id).pipe(
        map((response) => {
          proyectoData.presupuesto = response;
          return proyectoData;
        }));
    } else {
      proyectoData.presupuesto = {
        importeTotalPresupuestoUniversidadSinCosteIndirecto: proyectoData.importePresupuesto,
        importeTotalPresupuestoUniversidadCostesIndirectos: proyectoData.importePresupuestoCostesIndirectos,
        importeTotalPresupuesto: proyectoData.totalImportePresupuesto,
        importeTotalConcedidoUniversidadSinCosteIndirecto: proyectoData.importeConcedido,
        importeTotalConcedidoUniversidadCostesIndirectos: proyectoData.importeConcedidoCostesIndirectos,
        importeTotalConcedido: proyectoData.totalImporteConcedido,
        importeTotalConcedidoSocios: proyectoData.importeConcedidoSocios,
        importeTotalPresupuestoSocios: proyectoData.importePresupuestoSocios
      } as IProyectoPresupuestoTotales;
      return of(proyectoData);
    }
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {

    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsPresupuestoNotExcel();
    } else {
      const prefixTitlePresupuesto = this.translate.instant(PRESUPUESTO_KEY) + ': ';

      return this.getColumnsPresupuesto(prefixTitlePresupuesto, false);
    }
  }

  private getColumnsPresupuestoNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = this.getColumnsPresupuesto('', true);

    const titleI18n = this.translate.instant(PRESUPUESTO_KEY);

    const columnPresupuesto: ISgiColumnReport = {
      name: PRESUPUESTO_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnPresupuesto];
  }

  private getColumnsPresupuesto(prefix: string, allString: boolean): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const columnImportePresupuestoUniversidad: ISgiColumnReport = {
      name: IMPORTE_PRESUPUESTO_UNIVERSIDAD_FIELD,
      title: prefix + this.translate.instant(IMPORTE_PRESUPUESTO_UNIVERSIDAD_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnImportePresupuestoUniversidad);

    const columnImportePresupuestoUniversidadCostesIndirectos: ISgiColumnReport = {
      name: IMPORTE_PRESUPUESTO_UNIVERSIDAD_COSTES_INDIRECTOS_FIELD,
      title: prefix + this.translate.instant(IMPORTE_PRESUPUESTO_UNIVERSIDAD_COSTES_INDIRECTOS_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnImportePresupuestoUniversidadCostesIndirectos);

    const columnTotalImportePresupuestoUniversidad: ISgiColumnReport = {
      name: TOTAL_IMPORTE_PRESUPUESTO_UNIVERSIDAD_FIELD,
      title: prefix + this.translate.instant(TOTAL_IMPORTE_PRESUPUESTO_UNIVERSIDAD_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnTotalImportePresupuestoUniversidad);

    const columnImporteConcedidoUniversidad: ISgiColumnReport = {
      name: IMPORTE_CONCEDIDO_UNIVERSIDAD_FIELD,
      title: prefix + this.translate.instant(IMPORTE_CONCEDIDO_UNIVERSIDAD_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnImporteConcedidoUniversidad);

    const columnImporteConcedidoUniversidadCostesIndirectos: ISgiColumnReport = {
      name: IMPORTE_CONCEDIDO_UNIVERSIDAD_COSTES_INDIRECTOS_FIELD,
      title: prefix + this.translate.instant(IMPORTE_CONCEDIDO_UNIVERSIDAD_COSTES_INDIRECTOS_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnImporteConcedidoUniversidadCostesIndirectos);

    const columnTotalImporteConcedidoUniversidad: ISgiColumnReport = {
      name: TOTAL_IMPORTE_CONCEDIDO_UNIVERSIDAD_FIELD,
      title: prefix + this.translate.instant(TOTAL_IMPORTE_CONCEDIDO_UNIVERSIDAD_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnTotalImporteConcedidoUniversidad);

    const columnImportePresupuestoSocios: ISgiColumnReport = {
      name: IMPORTE_PRESUPUESTO_SOCIOS_FIELD,
      title: prefix + this.translate.instant(IMPORTE_PRESUPUESTO_SOCIOS_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnImportePresupuestoSocios);

    const columnImporteConcedidoSocios: ISgiColumnReport = {
      name: IMPORTE_CONCEDIDO_SOCIOS_FIELD,
      title: prefix + this.translate.instant(IMPORTE_CONCEDIDO_SOCIOS_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnImporteConcedidoSocios);

    const columnTotalImportePresupuesto: ISgiColumnReport = {
      name: TOTAL_IMPORTE_PRESUPUESTO_FIELD,
      title: prefix + this.translate.instant(TOTAL_IMPORTE_PRESUPUESTO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnTotalImportePresupuesto);

    const columnTotalImporteConcedido: ISgiColumnReport = {
      name: TOTAL_IMPORTE_CONCEDIDO_FIELD,
      title: prefix + this.translate.instant(TOTAL_IMPORTE_CONCEDIDO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnTotalImporteConcedido);

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {

    const proyecto = proyectos[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsPresupuestoNotExcel(proyecto, elementsRow);
    } else {
      this.fillRowsPresupuestoExcel(elementsRow, proyecto);
    }
    return elementsRow;
  }

  private fillRowsPresupuestoNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];
    if (proyecto) {
      const presupuesto = proyecto.presupuesto;

      const presupuestoElementsRow: any[] = [];

      presupuestoElementsRow.push(this.decimalPipe.transform(presupuesto?.importeTotalPresupuestoUniversidadSinCosteIndirecto,
        '.2-2') ?? '');
      presupuestoElementsRow.push(this.decimalPipe.transform(presupuesto?.importeTotalPresupuestoUniversidadCostesIndirectos,
        '.2-2') ?? '');
      presupuestoElementsRow.push(this.decimalPipe.transform((presupuesto?.importeTotalPresupuestoUniversidadSinCosteIndirecto +
        presupuesto?.importeTotalPresupuestoUniversidadCostesIndirectos), '.2-2') ?? '');
      presupuestoElementsRow.push(this.decimalPipe.transform(presupuesto?.importeTotalConcedidoUniversidadSinCosteIndirecto,
        '.2-2') ?? '');
      presupuestoElementsRow.push(this.decimalPipe.transform(presupuesto?.importeTotalConcedidoUniversidadCostesIndirectos,
        '.2-2') ?? '');
      presupuestoElementsRow.push(this.decimalPipe.transform((presupuesto?.importeTotalConcedidoUniversidadSinCosteIndirecto +
        presupuesto?.importeTotalConcedidoUniversidadCostesIndirectos), '.2-2') ?? '');
      presupuestoElementsRow.push(this.decimalPipe.transform(presupuesto?.importeTotalPresupuestoSocios, '.2-2') ?? '');
      presupuestoElementsRow.push(this.decimalPipe.transform(presupuesto?.importeTotalConcedidoSocios, '.2-2') ?? '');
      presupuestoElementsRow.push(this.decimalPipe.transform(presupuesto?.importeTotalPresupuesto, '.2-2') ?? '');
      presupuestoElementsRow.push(this.decimalPipe.transform(presupuesto?.importeTotalConcedido, '.2-2') ?? '');

      const rowReport: ISgiRowReport = {
        elements: presupuestoElementsRow
      };
      rowsReport.push(rowReport);
    }
    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsPresupuestoExcel(elementsRow: any[], proyecto: IProyectoReportData) {
    if (proyecto) {
      const presupuesto = proyecto.presupuesto;
      elementsRow.push(this.notIsNullAndNotUndefined(presupuesto?.importeTotalPresupuestoUniversidadSinCosteIndirecto) ?
        presupuesto?.importeTotalPresupuestoUniversidadSinCosteIndirecto.toString() : '');
      elementsRow.push(this.notIsNullAndNotUndefined(presupuesto?.importeTotalPresupuestoUniversidadCostesIndirectos) ?
        presupuesto?.importeTotalPresupuestoUniversidadCostesIndirectos.toString() : '');
      elementsRow.push(this.notIsNullAndNotUndefined(presupuesto?.importeTotalPresupuestoUniversidadSinCosteIndirecto) &&
        this.notIsNullAndNotUndefined(presupuesto?.importeTotalPresupuestoUniversidadCostesIndirectos) ?
        (presupuesto?.importeTotalPresupuestoUniversidadSinCosteIndirecto
          + presupuesto?.importeTotalPresupuestoUniversidadCostesIndirectos).toString() : '');
      elementsRow.push(this.notIsNullAndNotUndefined(presupuesto?.importeTotalConcedidoUniversidadSinCosteIndirecto) ?
        presupuesto?.importeTotalConcedidoUniversidadSinCosteIndirecto.toString() : '');
      elementsRow.push(this.notIsNullAndNotUndefined(presupuesto?.importeTotalConcedidoUniversidadCostesIndirectos) ?
        presupuesto?.importeTotalConcedidoUniversidadCostesIndirectos.toString() : '');
      elementsRow.push(this.notIsNullAndNotUndefined(presupuesto?.importeTotalConcedidoUniversidadSinCosteIndirecto) &&
        this.notIsNullAndNotUndefined(presupuesto?.importeTotalConcedidoUniversidadCostesIndirectos) ?
        (presupuesto?.importeTotalConcedidoUniversidadSinCosteIndirecto
          + presupuesto?.importeTotalConcedidoUniversidadCostesIndirectos).toString() : '');
      elementsRow.push(this.notIsNullAndNotUndefined(presupuesto?.importeTotalPresupuestoSocios) ?
        presupuesto?.importeTotalPresupuestoSocios.toString() : '');
      elementsRow.push(this.notIsNullAndNotUndefined(presupuesto?.importeTotalConcedidoSocios) ?
        presupuesto?.importeTotalConcedidoSocios.toString() : '');
      elementsRow.push(this.notIsNullAndNotUndefined(presupuesto?.importeTotalPresupuesto) ?
        presupuesto?.importeTotalPresupuesto.toString() : '');
      elementsRow.push(this.notIsNullAndNotUndefined(presupuesto?.importeTotalConcedido) ?
        presupuesto?.importeTotalConcedido.toString() : '');
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
}
