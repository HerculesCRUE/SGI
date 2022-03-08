import { DecimalPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const ENTIDAD_FINANCIADORA_KEY = 'csp.proyecto-entidad-financiadora';
const ENTIDAD_FINANCIADORA_NOMBRE_KEY = 'csp.proyecto-entidad-financiadora.nombre';
const ENTIDAD_FINANCIADORA_FIELD = 'entidadFinanciadora';
const ENTIDAD_FINANCIADORA_CIF_KEY = marker('csp.proyecto-entidad-financiadora.cif');
const ENTIDAD_FINANCIADORA_CIF_FIELD = 'cifEntidadFinanciadora';
const ENTIDAD_FINANCIADORA_FUENTE_FINANCIACION_KEY = marker('csp.proyecto-entidad-financiadora.fuente-financiacion');
const ENTIDAD_FINANCIADORA_FUENTE_FINANCIACION_FIELD = 'fuenteFinanciacionEntidadFinanciadora';
const ENTIDAD_FINANCIADORA_AMBITO_KEY = marker('csp.fuente-financiacion.ambito-geografico');
const ENTIDAD_FINANCIADORA_AMBITO_FIELD = 'ambitoEntidadFinanciadora';
const ENTIDAD_FINANCIADORA_TIPO_FINANCIACION_KEY = marker('csp.proyecto-entidad-financiadora.tipo-financiacion');
const ENTIDAD_FINANCIADORA_TIPO_FINANCIACION_FIELD = 'tipoFinanciacionEntidadFinanciadora';
const ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_KEY = marker('csp.proyecto-entidad-financiadora.porcentaje-financiacion');
const ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_FIELD = 'porcentajeFinanciacionEntidadFinanciadora';
const ENTIDAD_FINANCIADORA_IMPORTE_FINANCIACION_KEY = marker('csp.proyecto-entidad-financiadora.importe-financiacion');
const ENTIDAD_FINANCIADORA_IMPORTE_FINANCIACION_FIELD = 'importeFinanciacionEntidadFinanciadora';
const ENTIDAD_FINANCIADORA_AJENA_KEY = marker('csp.proyecto-entidad-financiadora-ajena');
const ENTIDAD_FINANCIADORA_AJENA_CONVOCATORIA_KEY = marker('csp.proyecto-entidad-financiadora-ajenas-convocatoria');
const ENTIDAD_FINANCIADORA_AJENA_FIELD = 'ajenaEntidadFinanciadora';

@Injectable()
export class ProyectoEntidadFinanciadoraListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private empresaService: EmpresaService,
    private readonly decimalPipe: DecimalPipe
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    return merge(
      this.proyectoService.findEntidadesFinanciadorasPropias(proyectoData.id).pipe(
        mergeMap(responseEntidadesFinanciadorasPropias => {
          return this.fillEntidadFinanciadora(proyectoData, responseEntidadesFinanciadorasPropias);
        })),
      this.proyectoService.findEntidadesFinanciadorasAjenas(proyectoData.id).pipe(
        mergeMap(responseEntidadesFinanciadorasAjenas => {
          return this.fillEntidadFinanciadora(proyectoData, responseEntidadesFinanciadorasAjenas);
        }))
    ).pipe(
      takeLast(1)
    );
  }

  private fillEntidadFinanciadora(
    proyectoData: IProyectoReportData,
    responseEntidadesFinanciadoras: SgiRestListResult<IProyectoEntidadFinanciadora>
  ): Observable<IProyectoReportData> {
    if (responseEntidadesFinanciadoras.total === 0) {
      return of(proyectoData);
    }
    const entidadesFinanciadoras = responseEntidadesFinanciadoras.items;

    const entidadesFinanciadorasIds = new Set<string>(responseEntidadesFinanciadoras.items.map(
      (entidadFinanciadora) => entidadFinanciadora.empresa.id)
    );
    return this.empresaService.findAllByIdIn([...entidadesFinanciadorasIds]).pipe(
      map((result) => {
        const entidades = result.items;

        entidadesFinanciadoras.forEach((entidadProyecto) => {
          entidadProyecto.empresa = entidades.find((entidad) => entidadProyecto.empresa.id === entidad.id);
        });
        if (!proyectoData.entidadesFinanciadoras) {
          proyectoData.entidadesFinanciadoras = [];
        }
        proyectoData.entidadesFinanciadoras.push(...entidadesFinanciadoras);
        return proyectoData;
      })
    );
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsEntidadFinanciadoraNotExcel();
    } else {
      return this.getColumnsEntidadFinanciadoraExcel(proyectos);
    }
  }

  private getColumnsEntidadFinanciadoraNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: ENTIDAD_FINANCIADORA_FIELD,
      title: this.translate.instant(ENTIDAD_FINANCIADORA_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(ENTIDAD_FINANCIADORA_KEY) +
      ' (' + this.translate.instant(ENTIDAD_FINANCIADORA_NOMBRE_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_CIF_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_FUENTE_FINANCIACION_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_AMBITO_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_TIPO_FINANCIACION_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_IMPORTE_FINANCIACION_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_AJENA_KEY) + ')';
    const columnEntidad: ISgiColumnReport = {
      name: ENTIDAD_FINANCIADORA_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsEntidadFinanciadoraExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEntidasFinanciadoras = Math.max(...proyectos.map(p => p.entidadesFinanciadoras?.length));
    const titleEntidadFinanciadora = this.translate.instant(ENTIDAD_FINANCIADORA_KEY);

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
      };
      columns.push(columnImporteFinanciacionEntidadFinanciadora);

      const columnPorcentajeFinanciacionEntidadFinanciadora: ISgiColumnReport = {
        name: ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_FIELD + idEntidadFinanciadora,
        title: titleEntidadFinanciadora
          + idEntidadFinanciadora + ': '
          + this.translate.instant(ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_KEY),
        type: ColumnType.NUMBER,
      };
      columns.push(columnPorcentajeFinanciacionEntidadFinanciadora);

      const columnAjenaEntidadFinanciadora: ISgiColumnReport = {
        name: ENTIDAD_FINANCIADORA_AJENA_FIELD + idEntidadFinanciadora,
        title: titleEntidadFinanciadora + idEntidadFinanciadora + ': ' + this.translate.instant(ENTIDAD_FINANCIADORA_AJENA_CONVOCATORIA_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnAjenaEntidadFinanciadora);
    }
    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = proyectos[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsEntidadFinanciadoraNotExcel(proyecto, elementsRow);
    } else {

      const maxNumEntidasFinanciadoras = Math.max(...proyectos.map(p => p.entidadesFinanciadoras?.length));
      for (let i = 0; i < maxNumEntidasFinanciadoras; i++) {
        const entidadFinanciadora = proyecto.entidadesFinanciadoras[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, entidadFinanciadora);
      }
    }
    return elementsRow;
  }

  private fillRowsEntidadFinanciadoraNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.entidadesFinanciadoras?.forEach(entidadFinanciadora => {
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
      entidadTable += '\n';
      entidadTable += this.notIsNullAndNotUndefined(entidadFinanciadora.ajena) ? this.getI18nBooleanYesNo(entidadFinanciadora.ajena) : '';

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

  private fillRowsEntidadExcel(elementsRow: any[], entidadFinanciadora: IProyectoEntidadFinanciadora) {
    if (entidadFinanciadora) {
      elementsRow.push(entidadFinanciadora.empresa?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.empresa?.numeroIdentificacion ?? '');
      elementsRow.push(entidadFinanciadora.fuenteFinanciacion?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.fuenteFinanciacion?.tipoAmbitoGeografico?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.tipoFinanciacion?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.importeFinanciacion ?? '');
      elementsRow.push(entidadFinanciadora.porcentajeFinanciacion ?? '');
      elementsRow.push(this.notIsNullAndNotUndefined(entidadFinanciadora.ajena) ? this.getI18nBooleanYesNo(entidadFinanciadora.ajena) : '');
    } else {
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

  private notIsNullAndNotUndefined(value): boolean {
    return value !== null && value !== undefined;
  }
}
