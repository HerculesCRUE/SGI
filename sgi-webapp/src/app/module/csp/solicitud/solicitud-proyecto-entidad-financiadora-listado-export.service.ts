import { DecimalPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { ISolicitudProyectoEntidadFinanciadoraAjena } from '@core/models/csp/solicitud-proyecto-entidad-financiadora-ajena';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeAll, switchMap, takeLast } from 'rxjs/operators';
import { ISolicitudReportData, ISolicitudReportOptions } from './solicitud-listado-export.service';

const ENTIDAD_FINANCIADORA_KEY = 'csp.proyecto-entidad-financiadora';
const ENTIDAD_FINANCIADORA_NOMBRE_KEY = 'csp.proyecto-entidad-financiadora.nombre';
const ENTIDAD_FINANCIADORA_FIELD = 'entidadFinanciadora';
const ENTIDAD_FINANCIADORA_CIF_KEY = marker('csp.convocatoria-entidad-convocante.numeroIdentificacion');
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
const ENTIDAD_FINANCIADORA_AJENA_CONVOCATORIA_KEY = marker('csp.proyecto-entidad-financiadora-ajenas-convocatoria');
const ENTIDAD_FINANCIADORA_AJENA_FIELD = 'ajenaEntidadFinanciadora';
const PROYECTO_KEY = marker('menu.csp.solicitudes.datos-proyecto');

@Injectable()
export class SolicitudProyectoEntidadFinanciadoraListadoExportService
  extends AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
    private empresaService: EmpresaService,
    private readonly decimalPipe: DecimalPipe
  ) {
    super(translate);
  }

  public getData(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    return this.solicitudService.findAllSolicitudProyectoEntidadFinanciadora(solicitudData.id).pipe(
      switchMap(responseEntidadesFinanciadorasPropias => {
        return this.fillEntidadFinanciadora(solicitudData, responseEntidadesFinanciadorasPropias);
      }));
  }

  public getDataConv(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    return this.solicitudService
      .findEntidadesFinanciadorasConvocatoriaSolicitud(solicitudData.id)
      .pipe(
        map(result => result.items),
        switchMap((entidadesFinanciadoras) => {
          return from(entidadesFinanciadoras)
            .pipe(
              map((entidadesFinanciadora) => {
                return this.empresaService.findById(entidadesFinanciadora.empresa.id)
                  .pipe(
                    map(empresa => {
                      entidadesFinanciadora.empresa = empresa;
                      solicitudData.entidadesFinanciadorasConvocatoria = entidadesFinanciadoras;
                      return solicitudData;
                    }),
                  );

              }),
              mergeAll(this.DEFAULT_CONCURRENT),
              map(() => {
                solicitudData.entidadesFinanciadorasConvocatoria = entidadesFinanciadoras;
                return solicitudData;
              })
            );
        }),
        takeLast(1)
      )
  }

  private fillEntidadFinanciadora(
    solicitudData: ISolicitudReportData,
    responseEntidadesFinanciadoras: SgiRestListResult<ISolicitudProyectoEntidadFinanciadoraAjena>
  ): Observable<ISolicitudReportData> {
    if (responseEntidadesFinanciadoras.total === 0) {
      return of(solicitudData);
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
        if (!solicitudData.entidadesFinanciadoras) {
          solicitudData.entidadesFinanciadoras = [];
        }
        solicitudData.entidadesFinanciadoras.push(...entidadesFinanciadoras);
        return solicitudData;
      })
    );
  }

  public fillColumns(
    solicitudes: ISolicitudReportData[],
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsEntidadFinanciadoraNotExcel();
    } else {
      return this.getColumnsEntidadFinanciadoraExcel(solicitudes);
    }
  }

  private getColumnsEntidadFinanciadoraNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: ENTIDAD_FINANCIADORA_FIELD,
      title: this.translate.instant(ENTIDAD_FINANCIADORA_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(PROYECTO_KEY) + ': ' + this.translate.instant(ENTIDAD_FINANCIADORA_KEY) +
      ' (' + this.translate.instant(ENTIDAD_FINANCIADORA_NOMBRE_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_CIF_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_FUENTE_FINANCIACION_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_AMBITO_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_TIPO_FINANCIACION_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_IMPORTE_FINANCIACION_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_FINANCIADORA_AJENA_CONVOCATORIA_KEY) + ')';
    const columnEntidad: ISgiColumnReport = {
      name: ENTIDAD_FINANCIADORA_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsEntidadFinanciadoraExcel(solicitudes: ISolicitudReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEntidasFinanciadoras = Math.max(...solicitudes.map(s => s.entidadesFinanciadoras ? s.entidadesFinanciadoras?.length : 0));
    const maxNumEntidasFinanciadorasConv = Math.max(...solicitudes.map(s => s.entidadesFinanciadorasConvocatoria ? s.entidadesFinanciadorasConvocatoria?.length : 0));
    const titleEntidadFinanciadora = this.translate.instant(PROYECTO_KEY) + ': ' + this.translate.instant(ENTIDAD_FINANCIADORA_KEY);
    const numMax = maxNumEntidasFinanciadoras + maxNumEntidasFinanciadorasConv;

    for (let i = 0; i < numMax; i++) {
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
        type: ColumnType.STRING,
      };
      columns.push(columnImporteFinanciacionEntidadFinanciadora);

      const columnPorcentajeFinanciacionEntidadFinanciadora: ISgiColumnReport = {
        name: ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_FIELD + idEntidadFinanciadora,
        title: titleEntidadFinanciadora
          + idEntidadFinanciadora + ': '
          + this.translate.instant(ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_KEY),
        type: ColumnType.STRING,
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

  public fillRows(solicitudes: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {
    const proyecto = solicitudes[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsEntidadFinanciadoraNotExcel(proyecto, elementsRow);
    } else {

      const maxNumEntidasFinanciadorasConv = Math.max(...solicitudes.map(s => s.entidadesFinanciadorasConvocatoria ? s.entidadesFinanciadorasConvocatoria?.length : 0));
      for (let i = 0; i < maxNumEntidasFinanciadorasConv; i++) {
        const entidadFinanciadora = proyecto.entidadesFinanciadorasConvocatoria && proyecto.entidadesFinanciadorasConvocatoria.length > 0 ? proyecto.entidadesFinanciadorasConvocatoria[i] : null;
        this.fillRowsEntidadExcelConv(elementsRow, entidadFinanciadora);
      }

      const maxNumEntidasFinanciadoras = Math.max(...solicitudes.map(s => s.entidadesFinanciadoras ? s.entidadesFinanciadoras?.length : 0));
      for (let i = 0; i < maxNumEntidasFinanciadoras; i++) {
        const entidadFinanciadora = proyecto.entidadesFinanciadoras && proyecto.entidadesFinanciadoras.length > 0 ? proyecto.entidadesFinanciadoras[i] : null;
        this.fillRowsEntidadExcel(elementsRow, entidadFinanciadora);
      }
    }
    return elementsRow;
  }

  private fillRowsEntidadFinanciadoraNotExcel(proyecto: ISolicitudReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.entidadesFinanciadorasConvocatoria?.forEach(entidadFinanciadora => {
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
      entidadTable += this.getI18nBooleanYesNo(false);

      entidadFinanciadoraElementsRow.push(entidadTable);

      const rowReport: ISgiRowReport = {
        elements: entidadFinanciadoraElementsRow
      };
      rowsReport.push(rowReport);
    });

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
      entidadTable += this.getI18nBooleanYesNo(true);

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

  private fillRowsEntidadExcel(elementsRow: any[], entidadFinanciadora: ISolicitudProyectoEntidadFinanciadoraAjena) {
    if (entidadFinanciadora) {
      elementsRow.push(entidadFinanciadora.empresa?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.empresa?.numeroIdentificacion ?? '');
      elementsRow.push(entidadFinanciadora.fuenteFinanciacion?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.fuenteFinanciacion?.tipoAmbitoGeografico?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.tipoFinanciacion?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.importeFinanciacion ? entidadFinanciadora.importeFinanciacion.toString() : '');
      elementsRow.push(this.decimalPipe.transform(entidadFinanciadora.porcentajeFinanciacion, '2.2-2') ?? '');
      elementsRow.push(this.getI18nBooleanYesNo(true));
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

  private fillRowsEntidadExcelConv(elementsRow: any[], entidadFinanciadora: IConvocatoriaEntidadFinanciadora) {
    if (entidadFinanciadora) {
      elementsRow.push(entidadFinanciadora.empresa?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.empresa?.numeroIdentificacion ?? '');
      elementsRow.push(entidadFinanciadora.fuenteFinanciacion?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.fuenteFinanciacion?.tipoAmbitoGeografico?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.tipoFinanciacion?.nombre ?? '');
      elementsRow.push(entidadFinanciadora.importeFinanciacion ? entidadFinanciadora.importeFinanciacion.toString() : '');
      elementsRow.push(this.decimalPipe.transform(entidadFinanciadora.porcentajeFinanciacion, '2.2-2') ?? '');
      elementsRow.push(this.getI18nBooleanYesNo(false));
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
}
