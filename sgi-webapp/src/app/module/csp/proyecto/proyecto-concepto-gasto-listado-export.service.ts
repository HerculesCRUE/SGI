import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConceptoGastoCodigoEc } from '@core/models/csp/concepto-gasto-codigo-ec';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const ELEGIBILIDAD_KEY = marker('csp.proyecto-elegibilidad');
const CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY = marker('csp.convocatoria-elegibilidad.codigo-economico');
const CONCEPTO_GASTO_CODIGO_ECONOMICO_FIELD = 'conceptoGastoCodigoEconomico';
const CONCEPTO_GASTO_KEY = marker('csp.convocatoria-elegibilidad.concepto-gasto.concepto-gasto');
const CONCEPTO_GASTO_FIELD = 'conceptoGasto';

const CONCEPTO_GASTO_PERMITIDO_KEY = marker('csp.convocatoria-concepto-gasto-permitido');
const CONCEPTO_GASTO_PERMITIDO_FIELD = 'conceptoGastoPermitido';

const CONCEPTO_GASTO_NOMBRE_KEY = marker('csp.concepto-gasto.nombre');
const CONCEPTO_GASTO_NOMBRE_FIELD = 'nombreConceptoGasto';

const CONCEPTO_GASTO_IMPORTE_KEY = marker('csp.convocatoria-concepto-gasto.importe-maximo');
const CONCEPTO_GASTO_IMPORTE_FIELD = 'importeConceptoGasto';

const CONCEPTO_GASTO_FECHA_INICIO_KEY = marker('csp.proyecto-concepto-gasto.fecha-inicio');
const CONCEPTO_GASTO_FECHA_INICIO_FIELD = 'fechaInicioConceptoGasto';

const CONCEPTO_GASTO_FECHA_FIN_KEY = marker('csp.proyecto-concepto-gasto.fecha-fin');
const CONCEPTO_GASTO_FECHA_FIN_FIELD = 'fechaFinConceptoGasto';


export interface IProyectoConceptoGastoListadoExport extends IProyectoConceptoGasto {
  codigosEconomicos?: IConceptoGastoCodigoEc[];
}

@Injectable()
export class ProyectoConceptoGastoListadoExportService extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private readonly proyectoConceptoGastoService: ProyectoConceptoGastoService,
    private readonly codigoEconomicoGastoService: CodigoEconomicoGastoService,

    private readonly luxonDatePipe: LuxonDatePipe
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    return merge(
      this.proyectoService.findAllProyectoConceptosGastoPermitidos(proyectoData.id).pipe(
        map(response => response.items.map(item => item as IProyectoConceptoGastoListadoExport)),
        mergeMap(responseConceptosGastoPermitidos => {
          return this.fillConceptoGasto(proyectoData, responseConceptosGastoPermitidos);
        })),
      this.proyectoService.findAllProyectoConceptosGastoNoPermitidos(proyectoData.id).pipe(
        map(response => response.items.map(item => item as IProyectoConceptoGastoListadoExport)),
        mergeMap(responseConceptosGastoNoPermitidos => {
          return this.fillConceptoGasto(proyectoData, responseConceptosGastoNoPermitidos);
        }))
    ).pipe(
      takeLast(1)
    );
  }

  private fillConceptoGasto(
    proyectoData: IProyectoReportData,
    responseConceptosGastos: IProyectoConceptoGastoListadoExport[]
  ): Observable<IProyectoReportData> {
    if (responseConceptosGastos.length === 0) {
      return of(proyectoData);
    }
    if (!proyectoData.conceptosGastos) {
      proyectoData.conceptosGastos = [];
    }

    return from(responseConceptosGastos).pipe(
      mergeMap(proyectoConceptoGasto => {
        return this.getCodigosEconomicos(proyectoConceptoGasto).pipe(
          map(proyectoConceptoGastoListado => {
            proyectoData.conceptosGastos.push(proyectoConceptoGastoListado);
            return proyectoData;
          })
        );
      }),
      takeLast(1)
    );
  }

  private getCodigosEconomicos(proyectoConceptoGasto: IProyectoConceptoGastoListadoExport):
    Observable<IProyectoConceptoGastoListadoExport> {
    return this.proyectoConceptoGastoService.findAllProyectoConceptoGastoCodigosEc(proyectoConceptoGasto.id).pipe(
      switchMap(responseCodigoEconomico => {
        return from(responseCodigoEconomico.items).pipe(
          mergeMap(proyectoCodigoEconomico => {
            return this.getCodigoEconomico(proyectoCodigoEconomico);
          }),
          map(() => responseCodigoEconomico)
        );
      }),
      map(responseCodigoEconomico => {
        proyectoConceptoGasto.codigosEconomicos = responseCodigoEconomico.items;
        return proyectoConceptoGasto;
      }),
      takeLast(1)
    );
  }

  private getCodigoEconomico(proyectoCodigoEconomico: IProyectoConceptoGastoCodigoEc): Observable<IProyectoConceptoGastoCodigoEc> {
    return this.codigoEconomicoGastoService.findById(proyectoCodigoEconomico.codigoEconomico.id).pipe(
      map(codigoEconomico => {
        proyectoCodigoEconomico.codigoEconomico = codigoEconomico;
        return proyectoCodigoEconomico;
      }),
      catchError((err) => {
        this.logger.error(err);
        return of(proyectoCodigoEconomico);
      })
    );
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsConceptoGastoNotExcel();
    } else {
      return this.getColumnsConceptoGastoExcel(proyectos);
    }
  }

  private getColumnsConceptoGastoNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: CONCEPTO_GASTO_FIELD,
      title: this.translate.instant(CONCEPTO_GASTO_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(ELEGIBILIDAD_KEY) +
      ' (' + this.translate.instant(CONCEPTO_GASTO_NOMBRE_KEY) +
      ' - ' + this.translate.instant(CONCEPTO_GASTO_IMPORTE_KEY) +
      ' - ' + this.translate.instant(CONCEPTO_GASTO_FECHA_INICIO_KEY) +
      ' - ' + this.translate.instant(CONCEPTO_GASTO_FECHA_FIN_KEY) +
      ' - ' + this.translate.instant(CONCEPTO_GASTO_PERMITIDO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) +
      ' - ' + this.translate.instant(CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY) +
      ')';
    const columnEntidad: ISgiColumnReport = {
      name: CONCEPTO_GASTO_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsConceptoGastoExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumConceptosGastos = Math.max(...proyectos.map(p => p.conceptosGastos?.length));

    const titleConceptoGasto = this.translate.instant(CONCEPTO_GASTO_KEY);
    const titleConceptoGastoPermitido = this.translate.instant(CONCEPTO_GASTO_PERMITIDO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);

    for (let i = 0; i < maxNumConceptosGastos; i++) {
      const idConceptoGasto: string = String(i + 1);
      const columnConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_NOMBRE_FIELD + idConceptoGasto,
        title: titleConceptoGasto + idConceptoGasto + ': ' + this.translate.instant(CONCEPTO_GASTO_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnConceptoGasto);

      const columnImporteConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_IMPORTE_FIELD + idConceptoGasto,
        title: titleConceptoGasto + idConceptoGasto + ': ' + this.translate.instant(CONCEPTO_GASTO_IMPORTE_KEY),
        type: ColumnType.NUMBER,
      };
      columns.push(columnImporteConceptoGasto);

      const columnFechaInicioConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_FECHA_INICIO_FIELD + idConceptoGasto,
        title: titleConceptoGasto + idConceptoGasto + ': ' + this.translate.instant(CONCEPTO_GASTO_FECHA_INICIO_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaInicioConceptoGasto);

      const columnFechaFinConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_FECHA_FIN_FIELD + idConceptoGasto,
        title: titleConceptoGasto + idConceptoGasto + ': ' + this.translate.instant(CONCEPTO_GASTO_FECHA_FIN_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinConceptoGasto);

      const columnPermitidoConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_PERMITIDO_FIELD + idConceptoGasto,
        title: titleConceptoGasto + idConceptoGasto + ': ' + titleConceptoGastoPermitido,
        type: ColumnType.STRING,
      };
      columns.push(columnPermitidoConceptoGasto);

      const maxNumCodigosConceptosGastos = Math.max(...proyectos.map(
        p => Math.max(p.conceptosGastos[i] ? p.conceptosGastos[i]?.codigosEconomicos?.length : 0))
      );
      for (let j = 0; j < maxNumCodigosConceptosGastos; j++) {
        const idCodigoConceptoGasto: string = String(j + 1);
        const columnCodigoEconomicoConceptoGasto: ISgiColumnReport = {
          name: CONCEPTO_GASTO_CODIGO_ECONOMICO_FIELD + idConceptoGasto + '_' + idCodigoConceptoGasto,
          title: titleConceptoGasto + idConceptoGasto + ': '
            + this.translate.instant(CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY) + idCodigoConceptoGasto,
          type: ColumnType.STRING,
        };
        columns.push(columnCodigoEconomicoConceptoGasto);
      }
    }

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = proyectos[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsConceptoGastoNotExcel(proyecto, elementsRow);
    } else {

      const maxNumConceptosGastos = Math.max(...proyectos.map(p => p.conceptosGastos?.length));

      for (let i = 0; i < maxNumConceptosGastos; i++) {
        const conceptoGasto = proyecto.conceptosGastos[i] ?? null;
        const maxNumCodigosConceptosGastos = Math.max(...proyectos.map(
          p => Math.max(p.conceptosGastos[i] ? p.conceptosGastos[i]?.codigosEconomicos?.length : 0))
        );
        this.fillRowsConceptoGastoExcel(elementsRow, conceptoGasto, maxNumCodigosConceptosGastos);
      }
    }
    return elementsRow;
  }

  private fillRowsConceptoGastoNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.conceptosGastos?.forEach(proyectoConceptoGasto => {
      const conceptoGastoElementsRow: any[] = [];

      let content = proyectoConceptoGasto.conceptoGasto?.nombre ?? '';
      content += '\n';
      content += proyectoConceptoGasto.importeMaximo ?? '';
      content += '\n';
      content += this.luxonDatePipe.transform(LuxonUtils.toBackend(proyectoConceptoGasto?.fechaInicio, true), 'shortDate') ?? '';
      content += '\n';
      content += this.luxonDatePipe.transform(LuxonUtils.toBackend(proyectoConceptoGasto?.fechaFin, true), 'shortDate') ?? '';
      content += '\n';
      content += this.getI18nBooleanYesNo(proyectoConceptoGasto.permitido);
      content += '\n';
      content += this.getStringCodigosEconomicos(proyectoConceptoGasto.codigosEconomicos);

      conceptoGastoElementsRow.push(content);

      const rowReport: ISgiRowReport = {
        elements: conceptoGastoElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private getStringCodigosEconomicos(codigosEconomicos: IConceptoGastoCodigoEc[]): string {
    let content = '';
    if (codigosEconomicos?.length) {
      content = codigosEconomicos.map((item) => item.codigoEconomico?.id + ' - ' + item.codigoEconomico?.nombre).join(',');
    }
    return content;
  }

  private fillRowsConceptoGastoExcel(
    elementsRow: any[],
    proyectoConceptoGasto: IProyectoConceptoGastoListadoExport,
    maxNumCodigosConceptosGastos: number) {
    if (proyectoConceptoGasto) {
      elementsRow.push(proyectoConceptoGasto.conceptoGasto?.nombre ?? '');
      elementsRow.push(proyectoConceptoGasto.importeMaximo ?? '');
      elementsRow.push(LuxonUtils.toBackend(proyectoConceptoGasto?.fechaInicio) ?? '');
      elementsRow.push(LuxonUtils.toBackend(proyectoConceptoGasto?.fechaFin) ?? '');
      elementsRow.push(this.getI18nBooleanYesNo(proyectoConceptoGasto.permitido));

      for (let i = 0; i < maxNumCodigosConceptosGastos; i++) {
        const codigoEconomicoConceptoGasto = proyectoConceptoGasto.codigosEconomicos[i] ?? null;
        if (codigoEconomicoConceptoGasto) {
          elementsRow.push(codigoEconomicoConceptoGasto.codigoEconomico?.id + '-' + codigoEconomicoConceptoGasto.codigoEconomico?.nombre);
        } else {
          elementsRow.push('');
        }
      }
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      for (let i = 0; i < maxNumCodigosConceptosGastos; i++) {
        elementsRow.push('');
      }
    }
  }
}
