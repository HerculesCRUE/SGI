import { DecimalPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

const ENTIDAD_FINANCIADORA_KEY = 'csp.convocatoria-entidad-financiadora';
const ENTIDAD_FINANCIADORA_NOMBRE_KEY = 'csp.convocatoria-entidad-financiadora.nombre';
const ENTIDAD_FINANCIADORA_FIELD = 'entidadFinanciadora';
const ENTIDAD_FINANCIADORA_CIF_KEY = marker('csp.convocatoria-entidad-financiadora.cif');
const ENTIDAD_FINANCIADORA_CIF_FIELD = 'cifEntidadFinanciadora';
const ENTIDAD_FINANCIADORA_FUENTE_FINANCIACION_KEY = marker('csp.convocatoria-entidad-financiadora.fuente-financiacion');
const ENTIDAD_FINANCIADORA_FUENTE_FINANCIACION_FIELD = 'fuenteFinanciacionEntidadFinanciadora';
const ENTIDAD_FINANCIADORA_AMBITO_KEY = marker('csp.fuente-financiacion.ambito-geografico');
const ENTIDAD_FINANCIADORA_AMBITO_FIELD = 'ambitoEntidadFinanciadora';
const ENTIDAD_FINANCIADORA_TIPO_FINANCIACION_KEY = marker('csp.convocatoria-entidad-financiadora.tipo-financiacion');
const ENTIDAD_FINANCIADORA_TIPO_FINANCIACION_FIELD = 'tipoFinanciacionEntidadFinanciadora';
const ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_KEY = marker('csp.convocatoria-entidad-financiadora.porcentaje-financiacion');
const ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_FIELD = 'porcentajeFinanciacionEntidadFinanciadora';
const ENTIDAD_FINANCIADORA_IMPORTE_FINANCIACION_KEY = marker('csp.convocatoria-entidad-financiadora.importe-financiacion');
const ENTIDAD_FINANCIADORA_IMPORTE_FINANCIACION_FIELD = 'importeFinanciacionEntidadFinanciadora';

@Injectable()
export class ConvocatoriaEntidadFinanciadoraListadoExportService
  extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly convocatoriaService: ConvocatoriaService,
    private empresaService: EmpresaService,
    private readonly decimalPipe: DecimalPipe
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return this.convocatoriaService.findEntidadesFinanciadoras(convocatoriaData?.convocatoria.id).pipe(
      map(responseEntidadesFinanciadoras => {
        convocatoriaData.entidadesFinanciadoras = responseEntidadesFinanciadoras.items;
        return responseEntidadesFinanciadoras;
      }),
      switchMap(responseEntidadesFinanciadoras => {
        if (responseEntidadesFinanciadoras.total === 0) {
          return of(convocatoriaData);
        }
        const entidadesFinanciadoras = responseEntidadesFinanciadoras.items;

        const entidadesFinanciadorasIds = new Set<string>(responseEntidadesFinanciadoras.items.map(
          (entidadFinanciadora) => entidadFinanciadora.empresa.id)
        );
        return this.empresaService.findAllByIdIn([...entidadesFinanciadorasIds]).pipe(
          map((result) => {
            const empresas = result.items;

            entidadesFinanciadoras.forEach((entidadFinanciadora) => {
              entidadFinanciadora.empresa = empresas.find((empresa) => entidadFinanciadora.empresa.id === empresa.id);
            });

            convocatoriaData.entidadesFinanciadoras = entidadesFinanciadoras;
            return convocatoriaData;
          })
        );
      }));
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsEntidadFinanciadoraNotExcel();
    } else {
      return this.getColumnsEntidadFinanciadoraExcel(convocatorias);
    }
  }

  private getColumnsEntidadFinanciadoraNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: ENTIDAD_FINANCIADORA_FIELD,
      title: this.translate.instant(ENTIDAD_FINANCIADORA_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(ENTIDAD_FINANCIADORA_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) +
      ' (' + this.translate.instant(ENTIDAD_FINANCIADORA_NOMBRE_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_CIF_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_FUENTE_FINANCIACION_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_AMBITO_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_TIPO_FINANCIACION_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_IMPORTE_FINANCIACION_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_KEY) +
      ')';
    const columnEntidad: ISgiColumnReport = {
      name: ENTIDAD_FINANCIADORA_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsEntidadFinanciadoraExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEntidasFinanciadoras = Math.max(...convocatorias.map(p => p.entidadesFinanciadoras?.length));
    const titleEntidadFinanciadora = this.translate.instant(ENTIDAD_FINANCIADORA_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);

    for (let i = 0; i < maxNumEntidasFinanciadoras; i++) {
      const idEntidadFinanciadora: string = String(i + 1);
      const columnEntidadFinanciadora: ISgiColumnReport = {
        name: ENTIDAD_FINANCIADORA_FIELD + idEntidadFinanciadora,
        title: titleEntidadFinanciadora + idEntidadFinanciadora + ': ' + this.translate.instant(ENTIDAD_FINANCIADORA_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEntidadFinanciadora);

      const columnCifEntidadFinanciadora: ISgiColumnReport = {
        name: ENTIDAD_FINANCIADORA_CIF_FIELD + idEntidadFinanciadora,
        title: titleEntidadFinanciadora + idEntidadFinanciadora + ': ' + this.translate.instant(ENTIDAD_FINANCIADORA_CIF_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnCifEntidadFinanciadora);

      const columnFuenteFinanciacionEntidadFinanciadora: ISgiColumnReport = {
        name: ENTIDAD_FINANCIADORA_FUENTE_FINANCIACION_FIELD + idEntidadFinanciadora,
        title: titleEntidadFinanciadora
          + idEntidadFinanciadora + ': '
          + this.translate.instant(ENTIDAD_FINANCIADORA_FUENTE_FINANCIACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFuenteFinanciacionEntidadFinanciadora);

      const columnAmbitoEntidadFinanciadora: ISgiColumnReport = {
        name: ENTIDAD_FINANCIADORA_AMBITO_FIELD + idEntidadFinanciadora,
        title: titleEntidadFinanciadora + idEntidadFinanciadora + ': ' + this.translate.instant(ENTIDAD_FINANCIADORA_AMBITO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnAmbitoEntidadFinanciadora);

      const columnTipoFinanciacionEntidadFinanciadora: ISgiColumnReport = {
        name: ENTIDAD_FINANCIADORA_TIPO_FINANCIACION_FIELD + idEntidadFinanciadora,
        title: titleEntidadFinanciadora + idEntidadFinanciadora + ': ' + this.translate.instant(ENTIDAD_FINANCIADORA_TIPO_FINANCIACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTipoFinanciacionEntidadFinanciadora);

      const columnImporteFinanciacionEntidadFinanciadora: ISgiColumnReport = {
        name: ENTIDAD_FINANCIADORA_IMPORTE_FINANCIACION_FIELD + idEntidadFinanciadora,
        title: titleEntidadFinanciadora
          + idEntidadFinanciadora + ': '
          + this.translate.instant(ENTIDAD_FINANCIADORA_IMPORTE_FINANCIACION_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteFinanciacionEntidadFinanciadora);

      const columnPorcentajeFinanciacionEntidadFinanciadora: ISgiColumnReport = {
        name: ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_FIELD + idEntidadFinanciadora,
        title: titleEntidadFinanciadora
          + idEntidadFinanciadora + ': '
          + this.translate.instant(ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_KEY),
        type: ColumnType.STRING
      };
      columns.push(columnPorcentajeFinanciacionEntidadFinanciadora);
    }
    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsEntidadFinanciadoraNotExcel(convocatoria, elementsRow);
    } else {

      const maxNumEntidasFinanciadoras = Math.max(...convocatorias.map(p => p.entidadesFinanciadoras?.length));
      for (let i = 0; i < maxNumEntidasFinanciadoras; i++) {
        const entidadFinanciadora = convocatoria.entidadesFinanciadoras[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, entidadFinanciadora);
      }
    }
    return elementsRow;
  }

  private fillRowsEntidadFinanciadoraNotExcel(convocatoria: IConvocatoriaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    convocatoria.entidadesFinanciadoras?.forEach(entidadFinanciadora => {
      const entidadFinanciadoraElementsRow: any[] = [];

      let entidadTable = entidadFinanciadora.empresa?.nombre ?? '';
      entidadTable += '\n';
      entidadTable += entidadFinanciadora.empresa?.numeroIdentificacion ?? '';
      entidadTable += '\n';
      entidadTable += entidadFinanciadora.fuenteFinanciacion?.nombre ?? '';
      entidadTable += '\n';
      entidadTable += entidadFinanciadora.fuenteFinanciacion?.tipoAmbitoGeografico?.nombre ?? '';
      entidadTable += '\n';
      entidadTable += entidadFinanciadora.tipoFinanciacion?.nombre ?? '';
      entidadTable += '\n';
      entidadTable += entidadFinanciadora.importeFinanciacion ?? '';
      entidadTable += '\n';
      entidadTable += this.decimalPipe.transform(entidadFinanciadora.porcentajeFinanciacion, '2.2-2') ?? '';

      entidadFinanciadoraElementsRow.push(entidadTable);

      const rowReport: ISgiRowReport = {
        elements: entidadFinanciadoraElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], entidadFinanciadora: IConvocatoriaEntidadFinanciadora) {
    if (entidadFinanciadora) {
      elementsRow.push(entidadFinanciadora.empresa?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.empresa?.numeroIdentificacion ?? '');
      elementsRow.push(entidadFinanciadora.fuenteFinanciacion?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.fuenteFinanciacion?.tipoAmbitoGeografico?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.tipoFinanciacion?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.importeFinanciacion ?? '');
      elementsRow.push(entidadFinanciadora.porcentajeFinanciacion ?? '');
    } else {
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
