import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoProrroga, TIPO_MAP } from '@core/models/csp/proyecto-prorroga';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const PRORROGAS_KEY = 'menu.csp.proyectos.prorrogas';
const PRORROGA_KEY = 'csp.proyecto-prorroga';
const PRORROGA_NUMERO_KEY = 'csp.prorroga.numero-prorroga';
const PRORROGA_FIELD = 'prorroga';
const PRORROGA_TIPO_KEY = marker('csp.prorroga.tipo');
const PRORROGA_TIPO_FIELD = 'tipoProrroga';
const PRORROGA_FECHA_CONCESION_KEY = marker('csp.prorroga.fecha-concesion');
const PRORROGA_FECHA_CONCESION_FIELD = 'fechaConcesionProrroga';
const PRORROGA_FECHA_FIN_KEY = marker('csp.prorroga.fecha-fin');
const PRORROGA_FECHA_FIN_FIELD = 'fechaFinProrroga';
const PRORROGA_IMPORTE_KEY = marker('csp.prorroga.importe');
const PRORROGA_IMPORTE_FIELD = 'importeProrroga';

@Injectable()
export class ProyectoProrrogaListadoExportService extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private readonly proyectoService: ProyectoService
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.proyectoService.findAllProyectoProrrogaProyecto(proyectoData.id, findOptions).pipe(
      map((responseProrroga) => {
        proyectoData.prorrogas = [];
        return responseProrroga;
      }),
      switchMap(responseProrroga => {
        if (responseProrroga.total === 0) {
          return of(proyectoData);
        }

        proyectoData.prorrogas = responseProrroga.items;
        return of(proyectoData);
      })
    );
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {

    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsProrrogaNotExcel();
    } else {
      return this.getColumnsProrrogaExcel(proyectos);
    }
  }

  private getColumnsProrrogaNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: PRORROGA_FIELD,
      title: this.translate.instant(PRORROGA_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(PRORROGAS_KEY) +
      ' (' + this.translate.instant(PRORROGA_NUMERO_KEY) +
      ' - ' + this.translate.instant(PRORROGA_TIPO_KEY) +
      ' - ' + this.translate.instant(PRORROGA_FECHA_CONCESION_KEY) +
      ' - ' + this.translate.instant(PRORROGA_FECHA_FIN_KEY) +
      ' - ' + this.translate.instant(PRORROGA_IMPORTE_KEY) +
      ')';
    const columnProrroga: ISgiColumnReport = {
      name: PRORROGA_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnProrroga];
  }

  private getColumnsProrrogaExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumProrrogas = Math.max(...proyectos.map(p => p.prorrogas?.length));
    const titleProrroga = this.translate.instant(PRORROGA_KEY);
    for (let i = 0; i < maxNumProrrogas; i++) {
      const idProrroga: string = String(i + 1);
      const columnNombreProrroga: ISgiColumnReport = {
        name: PRORROGA_FIELD + idProrroga,
        title: titleProrroga + idProrroga + ': ' + this.translate.instant(PRORROGA_NUMERO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombreProrroga);

      const columnNumeroProrroga: ISgiColumnReport = {
        name: PRORROGA_TIPO_FIELD + idProrroga,
        title: titleProrroga + idProrroga + ': ' + this.translate.instant(PRORROGA_TIPO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNumeroProrroga);

      const columnFechaConcesionProrroga: ISgiColumnReport = {
        name: PRORROGA_FECHA_CONCESION_FIELD + idProrroga,
        title: titleProrroga + idProrroga + ': ' + this.translate.instant(PRORROGA_FECHA_CONCESION_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaConcesionProrroga);

      const columnFechaFinProrroga: ISgiColumnReport = {
        name: PRORROGA_FECHA_FIN_FIELD + idProrroga,
        title: titleProrroga + idProrroga + ': ' + this.translate.instant(PRORROGA_FECHA_FIN_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinProrroga);

      const columnImporteProrroga: ISgiColumnReport = {
        name: PRORROGA_IMPORTE_FIELD + idProrroga,
        title: titleProrroga + idProrroga + ': ' + this.translate.instant(PRORROGA_IMPORTE_KEY),
        type: ColumnType.NUMBER,
      };
      columns.push(columnImporteProrroga);
    }
    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {

    const proyecto = proyectos[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsProrrogaNotExcel(proyecto, elementsRow);
    } else {
      const maxNumProrroga = Math.max(...proyectos.map(p => p.prorrogas?.length));
      for (let i = 0; i < maxNumProrroga; i++) {
        const prorroga = proyecto.prorrogas[i] ?? null;
        this.fillRowsProrrogaExcel(elementsRow, prorroga);
      }
    }
    return elementsRow;
  }

  private fillRowsProrrogaNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.prorrogas?.forEach(prorroga => {
      const prorrogaElementsRow: any[] = [];

      let prorrogaTable = String(prorroga?.numProrroga);
      prorrogaTable += '\n';
      prorrogaTable += prorroga?.tipo ? this.translate.instant(TIPO_MAP.get(prorroga?.tipo)) : '';
      prorrogaTable += '\n';
      prorrogaTable += this.luxonDatePipe.transform(LuxonUtils.toBackend(prorroga?.fechaConcesion, true), 'shortDate') ?? '';
      prorrogaTable += '\n';
      prorrogaTable += this.luxonDatePipe.transform(LuxonUtils.toBackend(prorroga?.fechaFin, true), 'shortDate') ?? '';
      prorrogaTable += '\n';
      prorrogaTable += prorroga?.importe ?? '';
      prorrogaElementsRow.push(prorrogaTable);

      const rowReport: ISgiRowReport = {
        elements: prorrogaElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsProrrogaExcel(elementsRow: any[], prorroga: IProyectoProrroga) {
    if (prorroga) {
      elementsRow.push(prorroga.numProrroga ?? '');
      elementsRow.push(prorroga?.tipo ? this.translate.instant(TIPO_MAP.get(prorroga?.tipo)) : '');
      elementsRow.push(LuxonUtils.toBackend(prorroga.fechaConcesion) ?? '');
      elementsRow.push(LuxonUtils.toBackend(prorroga.fechaFin) ?? '');
      elementsRow.push(prorroga.importe ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
