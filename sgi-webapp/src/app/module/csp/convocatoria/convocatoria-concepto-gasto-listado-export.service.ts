import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConceptoGastoCodigoEc } from '@core/models/csp/concepto-gasto-codigo-ec';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { TranslateService } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

const ELEGIBILIDAD_KEY = marker('csp.convocatoria-elegibilidad');
const CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY = marker('csp.convocatoria-elegibilidad.codigo-economico');
const CONCEPTO_GASTO_CODIGO_ECONOMICO_FIELD = 'conceptoGastoCodigoEconomico';
const CONCEPTO_GASTO_NO_PERMITIDO_CODIGO_ECONOMICO_FIELD = 'conceptoGastoCodigoEconomicoNoPermitido';
const CONCEPTO_GASTO_KEY = marker('csp.convocatoria-elegibilidad.concepto-gasto.concepto-gasto');
const CONCEPTO_GASTO_FIELD = 'conceptoGasto';

const CONCEPTO_GASTO_PERMITIDO_KEY = marker('csp.convocatoria-concepto-gasto-permitido');
const CONCEPTO_GASTO_NO_PERMITIDO_KEY = marker('csp.convocatoria-concepto-gasto-no-permitido');

const CONCEPTO_GASTO_NOMBRE_KEY = marker('csp.concepto-gasto.nombre');
const CONCEPTO_GASTO_NOMBRE_FIELD = 'nombreConceptoGasto';
const CONCEPTO_GASTO_NO_PERMITIDO_NOMBRE_FIELD = 'nombreConceptoGastoNoPermitido';

const CONCEPTO_GASTO_IMPORTE_KEY = marker('csp.convocatoria-concepto-gasto.importe-maximo');
const CONCEPTO_GASTO_IMPORTE_FIELD = 'importeConceptoGasto';
const CONCEPTO_GASTO_NO_PERMITIDO_IMPORTE_FIELD = 'importeConceptoGastoNoPermitido';

const CONCEPTO_GASTO_FECHA_INICIO_KEY = marker('csp.convocatoria-concepto-gasto.mes-inicial');
const CONCEPTO_GASTO_FECHA_INICIO_FIELD = 'fechaInicioConceptoGasto';
const CONCEPTO_GASTO_NO_PERMITIDO_FECHA_INICIO_FIELD = 'fechaInicioConceptoGastoNoPermitido';

const CONCEPTO_GASTO_FECHA_FIN_KEY = marker('csp.convocatoria-concepto-gasto.mes-final');
const CONCEPTO_GASTO_FECHA_FIN_FIELD = 'fechaFinConceptoGasto';
const CONCEPTO_GASTO_NO_PERMITIDO_FECHA_FIN_FIELD = 'fechaFinConceptoGastoNoPermitido';


export interface IConvocatoriaConceptoGastoListadoExport extends IConvocatoriaConceptoGasto {
  codigosEconomicos?: IConceptoGastoCodigoEc[];
}

@Injectable()
export class ConvocatoriaConceptoGastoListadoExportService extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly convocatoriaService: ConvocatoriaService,
    private readonly convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoService,
    private readonly codigoEconomicoGastoService: CodigoEconomicoGastoService,

    private readonly luxonDatePipe: LuxonDatePipe
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return merge(
      this.convocatoriaService.findAllConvocatoriaConceptoGastosPermitidos(convocatoriaData?.convocatoria?.id).pipe(
        map(response => response.items.map(item => item as IConvocatoriaConceptoGastoListadoExport)),
        mergeMap(responseConceptosGastoPermitidos => {
          return this.fillConceptoGasto(convocatoriaData, responseConceptosGastoPermitidos);
        })),
      this.convocatoriaService.findAllConvocatoriaConceptoGastosNoPermitidos(convocatoriaData?.convocatoria?.id).pipe(
        map(response => response.items.map(item => item as IConvocatoriaConceptoGastoListadoExport)),
        mergeMap(responseConceptosGastoNoPermitidos => {
          return this.fillConceptoGasto(convocatoriaData, responseConceptosGastoNoPermitidos);
        }))
    ).pipe(
      takeLast(1)
    );
  }

  private fillConceptoGasto(
    convocatoriaData: IConvocatoriaReportData,
    responseConceptosGastos: IConvocatoriaConceptoGastoListadoExport[]
  ): Observable<IConvocatoriaReportData> {
    if (responseConceptosGastos.length === 0) {
      return of(convocatoriaData);
    }
    if (!convocatoriaData.conceptosGastos) {
      convocatoriaData.conceptosGastos = [];
    }

    return from(responseConceptosGastos).pipe(
      mergeMap(convocatoriaConceptoGasto => {
        return this.getCodigosEconomicos(convocatoriaConceptoGasto).pipe(
          map(convocatoriaConceptoGastoListado => {
            convocatoriaData.conceptosGastos.push(convocatoriaConceptoGastoListado);
            return convocatoriaData;
          })
        );
      }),
      takeLast(1)
    );
  }

  private getCodigosEconomicos(convocatoriaConceptoGasto: IConvocatoriaConceptoGastoListadoExport):
    Observable<IConvocatoriaConceptoGastoListadoExport> {
    return this.convocatoriaConceptoGastoService.existsCodigosEconomicos(convocatoriaConceptoGasto.id).pipe(
      switchMap(res => {
        if (res) {
          return this.convocatoriaConceptoGastoService.findAllConvocatoriaConceptoGastoCodigoEcs(convocatoriaConceptoGasto.id).pipe(
            switchMap(responseCodigoEconomico => {
              return from(responseCodigoEconomico.items).pipe(
                mergeMap(convocatoriaCodigoEconomico => {
                  return this.getCodigoEconomico(convocatoriaCodigoEconomico);
                }),
                map(() => responseCodigoEconomico)
              );
            }),
            map(responseCodigoEconomico => {
              convocatoriaConceptoGasto.codigosEconomicos = responseCodigoEconomico.items;
              return convocatoriaConceptoGasto;
            }),
            takeLast(1)
          );
        } else {
          return of(convocatoriaConceptoGasto);
        }
      })
    );
  }

  private getCodigoEconomico(convocatoriaCodigoEconomico: IConvocatoriaConceptoGastoCodigoEc): Observable<IConvocatoriaConceptoGastoCodigoEc> {
    return this.codigoEconomicoGastoService.findById(convocatoriaCodigoEconomico.codigoEconomico.id).pipe(
      map(codigoEconomico => {
        convocatoriaCodigoEconomico.codigoEconomico = codigoEconomico;
        return convocatoriaCodigoEconomico;
      }),
      catchError((err) => {
        this.logger.error(err);
        return of(convocatoriaCodigoEconomico);
      })
    );
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsConceptoGastoNotExcel();
    } else {
      return this.getColumnsConceptoGastoExcel(convocatorias);
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
      ' - ' + this.translate.instant(CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY) +
      ' - ' + this.translate.instant(CONCEPTO_GASTO_PERMITIDO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) +
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

  private getColumnsConceptoGastoExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumConceptosGastosPermitidos = Math.max(...convocatorias.map(c => c.conceptosGastos ? c.conceptosGastos?.filter(g => g.permitido).length : 0));
    const maxNumConceptosGastosNoPermitidos = Math.max(...convocatorias.map(c => c.conceptosGastos ? c.conceptosGastos?.filter(g => !g.permitido).length : 0));

    const titleConceptoGastoPermitido = this.translate.instant(CONCEPTO_GASTO_PERMITIDO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);
    const titleConceptoGastoNoPermitido = this.translate.instant(CONCEPTO_GASTO_NO_PERMITIDO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);

    for (let i = 0; i < maxNumConceptosGastosPermitidos; i++) {
      const idConceptoGasto: string = String(i + 1);
      const columnConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_NOMBRE_FIELD + idConceptoGasto,
        title: titleConceptoGastoPermitido + idConceptoGasto + ': ' + this.translate.instant(CONCEPTO_GASTO_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnConceptoGasto);

      const columnImporteConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_IMPORTE_FIELD + idConceptoGasto,
        title: titleConceptoGastoPermitido + idConceptoGasto + ': ' + this.translate.instant(CONCEPTO_GASTO_IMPORTE_KEY),
        type: ColumnType.NUMBER,
      };
      columns.push(columnImporteConceptoGasto);

      const columnFechaInicioConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_FECHA_INICIO_FIELD + idConceptoGasto,
        title: titleConceptoGastoPermitido + idConceptoGasto + ': ' + this.translate.instant(CONCEPTO_GASTO_FECHA_INICIO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaInicioConceptoGasto);

      const columnFechaFinConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_FECHA_FIN_FIELD + idConceptoGasto,
        title: titleConceptoGastoPermitido + idConceptoGasto + ': ' + this.translate.instant(CONCEPTO_GASTO_FECHA_FIN_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaFinConceptoGasto);

      const maxNumCodigosConceptosGastosPermitidos = Math.max(...convocatorias.map(
        c => Math.max(c.conceptosGastos && c.conceptosGastos.filter(g => g.permitido)[i] ? (c.conceptosGastos.filter(g => g.permitido)[i]?.codigosEconomicos ? c.conceptosGastos.filter(g => g.permitido)[i]?.codigosEconomicos?.length : 0) : 0))
      );
      for (let j = 0; j < maxNumCodigosConceptosGastosPermitidos; j++) {
        const idCodigoConceptoGasto: string = String(j + 1);
        const columnCodigoEconomicoConceptoGasto: ISgiColumnReport = {
          name: CONCEPTO_GASTO_CODIGO_ECONOMICO_FIELD + idConceptoGasto + '_' + idCodigoConceptoGasto,
          title: titleConceptoGastoPermitido + idConceptoGasto + ': '
            + this.translate.instant(CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY) + idCodigoConceptoGasto,
          type: ColumnType.STRING,
        };
        columns.push(columnCodigoEconomicoConceptoGasto);
      }
    }

    for (let i = 0; i < maxNumConceptosGastosNoPermitidos; i++) {
      const idConceptoGasto: string = String(i + 1);
      const columnConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_NO_PERMITIDO_NOMBRE_FIELD + idConceptoGasto,
        title: titleConceptoGastoNoPermitido + idConceptoGasto + ': ' + this.translate.instant(CONCEPTO_GASTO_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnConceptoGasto);

      const columnImporteConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_NO_PERMITIDO_IMPORTE_FIELD + idConceptoGasto,
        title: titleConceptoGastoNoPermitido + idConceptoGasto + ': ' + this.translate.instant(CONCEPTO_GASTO_IMPORTE_KEY),
        type: ColumnType.NUMBER,
      };
      columns.push(columnImporteConceptoGasto);

      const columnFechaInicioConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_NO_PERMITIDO_FECHA_INICIO_FIELD + idConceptoGasto,
        title: titleConceptoGastoNoPermitido + idConceptoGasto + ': ' + this.translate.instant(CONCEPTO_GASTO_FECHA_INICIO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaInicioConceptoGasto);

      const columnFechaFinConceptoGasto: ISgiColumnReport = {
        name: CONCEPTO_GASTO_NO_PERMITIDO_FECHA_FIN_FIELD + idConceptoGasto,
        title: titleConceptoGastoNoPermitido + idConceptoGasto + ': ' + this.translate.instant(CONCEPTO_GASTO_FECHA_FIN_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaFinConceptoGasto);

      const maxNumCodigosConceptosGastosNoPermitidos = Math.max(...convocatorias.map(
        c => Math.max(c.conceptosGastos && c.conceptosGastos.filter(g => !g.permitido)[i] ? (c.conceptosGastos.filter(g => !g.permitido)[i]?.codigosEconomicos ? c.conceptosGastos.filter(g => !g.permitido)[i]?.codigosEconomicos?.length : 0) : 0))
      );
      for (let j = 0; j < maxNumCodigosConceptosGastosNoPermitidos; j++) {
        const idCodigoConceptoGasto: string = String(j + 1);
        const columnCodigoEconomicoConceptoGasto: ISgiColumnReport = {
          name: CONCEPTO_GASTO_NO_PERMITIDO_CODIGO_ECONOMICO_FIELD + idConceptoGasto + '_' + idCodigoConceptoGasto,
          title: titleConceptoGastoNoPermitido + idConceptoGasto + ': '
            + this.translate.instant(CONCEPTO_GASTO_CODIGO_ECONOMICO_KEY) + idCodigoConceptoGasto,
          type: ColumnType.STRING,
        };
        columns.push(columnCodigoEconomicoConceptoGasto);
      }
    }

    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsConceptoGastoNotExcel(convocatoria, elementsRow);
    } else {

      const maxNumConceptosGastosPermitidos = Math.max(...convocatorias.map(
        c => c.conceptosGastos && c.conceptosGastos.filter(g => g.permitido) ? c.conceptosGastos.filter(g => g.permitido).length : 0));

      for (let i = 0; i < maxNumConceptosGastosPermitidos; i++) {
        const conceptoGasto = convocatoria.conceptosGastos ? convocatoria.conceptosGastos.filter(g => g.permitido)[i] ?? null : null;
        const maxNumCodigosConceptosGastosPermitidos = Math.max(...convocatorias.map(
          c => Math.max(c.conceptosGastos && c.conceptosGastos.filter(g => g.permitido)[i] ? (c.conceptosGastos.filter(g => g.permitido)[i]?.codigosEconomicos ? c.conceptosGastos.filter(g => g.permitido)[i]?.codigosEconomicos?.length : 0) : 0))
        );
        this.fillRowsConceptoGastoExcel(elementsRow, conceptoGasto, maxNumCodigosConceptosGastosPermitidos);
      }

      const maxNumConceptosGastosNoPermitidos = Math.max(...convocatorias.map(c => c.conceptosGastos && c.conceptosGastos.filter(g => !g.permitido) ? c.conceptosGastos.filter(g => !g.permitido)?.length : 0));

      for (let i = 0; i < maxNumConceptosGastosNoPermitidos; i++) {
        const conceptoGasto = convocatoria.conceptosGastos ? convocatoria.conceptosGastos.filter(g => !g.permitido)[i] ?? null : null;
        const maxNumCodigosConceptosGastosNoPermitidos = Math.max(...convocatorias.map(
          c => Math.max(c.conceptosGastos && c.conceptosGastos.filter(g => !g.permitido)[i] ? (c.conceptosGastos.filter(g => !g.permitido)[i]?.codigosEconomicos ? c.conceptosGastos.filter(g => !g.permitido)[i]?.codigosEconomicos?.length : 0) : 0))
        );
        this.fillRowsConceptoGastoExcel(elementsRow, conceptoGasto, maxNumCodigosConceptosGastosNoPermitidos);
      }
    }
    return elementsRow;
  }

  private fillRowsConceptoGastoNotExcel(convocatoria: IConvocatoriaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    convocatoria.conceptosGastos?.sort((a, b) => {
      if (a.permitido && !b.permitido) {
        return -1;
      }
      if (!a.permitido && b.permitido) {
        return 1;
      }
      return 0;
    }).forEach(convocatoriaConceptoGasto => {
      const conceptoGastoElementsRow: any[] = [];

      let content = convocatoriaConceptoGasto.conceptoGasto?.nombre ?? '';
      content += '\n';
      content += convocatoriaConceptoGasto.importeMaximo ?? '';
      content += '\n';
      content += convocatoriaConceptoGasto?.mesInicial ? convocatoriaConceptoGasto?.mesInicial.toString() ?? '' : '';
      content += '\n';
      content += convocatoriaConceptoGasto?.mesFinal ? convocatoriaConceptoGasto?.mesFinal.toString() ?? '' : '';
      content += '\n';
      content += this.getStringCodigosEconomicos(convocatoriaConceptoGasto.codigosEconomicos);
      content += '\n';
      content += this.notIsNullAndNotUndefined(convocatoriaConceptoGasto.permitido) ? this.getI18nBooleanYesNo(convocatoriaConceptoGasto.permitido) : '';

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
    convocatoriaConceptoGasto: IConvocatoriaConceptoGastoListadoExport,
    maxNumCodigosConceptosGastos: number) {
    if (convocatoriaConceptoGasto) {
      elementsRow.push(convocatoriaConceptoGasto.conceptoGasto?.nombre ?? '');
      elementsRow.push(convocatoriaConceptoGasto.importeMaximo ?? '');
      elementsRow.push(convocatoriaConceptoGasto?.mesInicial ? convocatoriaConceptoGasto?.mesInicial.toString() ?? '' : '');
      elementsRow.push(convocatoriaConceptoGasto?.mesFinal ? convocatoriaConceptoGasto?.mesFinal.toString() ?? '' : '');

      for (let i = 0; i < maxNumCodigosConceptosGastos; i++) {
        const codigoEconomicoConceptoGasto = convocatoriaConceptoGasto.codigosEconomicos ?
          convocatoriaConceptoGasto.codigosEconomicos[i] ?? null : null;
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
      for (let i = 0; i < maxNumCodigosConceptosGastos; i++) {
        elementsRow.push('');
      }
    }
  }

  private notIsNullAndNotUndefined(value): boolean {
    return value !== null && value !== undefined;
  }
}
