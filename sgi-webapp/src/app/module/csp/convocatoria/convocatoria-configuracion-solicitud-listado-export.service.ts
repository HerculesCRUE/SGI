import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConfiguracionSolicitud } from '@core/models/csp/configuracion-solicitud';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

const CONFIGURACION_SOLICITUD_KEY = marker('csp.convocatoria-configuracion-solicitud');
const CONFIGURACION_SOLICITUD_ITEM_KEY = marker('csp.convocatoria-configuracion-solicitud.informe-title');
const CONFIGURACION_SOLICITUD_PRESENTACION_SGI_KEY = marker('csp.convocatoria-configuracion-solicitud.presentacion-sgi');
const CONFIGURACION_SOLICITUD_INICIO_PRESENTACION_KEY = marker('csp.convocatoria-configuracion-solicitud.inicio-presentacion');
const CONFIGURACION_SOLICITUD_FIN_PRESENTACION_KEY = marker('csp.convocatoria-configuracion-solicitud.fin-presentacion');
const CONFIGURACION_SOLICITUD_IMPORTE_MAXIMO_KEY = marker('csp.convocatoria-configuracion-solicitud.importe-maximo');
const DOCUMENTOS_REQUERIDOS_KEY = marker('csp.convocatoria-configuracion-solicitud-documento-requerido');

const CONFIGURACION_SOLICITUD_FIELD = 'configuracionSolicitud';
const CONFIGURACION_SOLICITUD_PRESENTACION_SGI_FIELD = 'presentacionSGIconfiguracionSolicitud';
const CONFIGURACION_SOLICITUD_INICIO_PRESENTACION_FIELD = 'inicioPresentacionConfiguracionSolicitud';
const CONFIGURACION_SOLICITUD_FIN_PRESENTACION_FIELD = 'finPresentacionConfiguracionSolicitud';
const CONFIGURACION_SOLICITUD_IMPORTE_MAXIMO_FIELD = 'importeMaximoConfiguracionSolicitud';
const DOCUMENTOS_REQUERIDOS_FIELD = 'documentosRequeridos';

@Injectable()
export class ConvocatoriaConfiguracionSolicitudListadoExportService extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private configuracionSolicitudService: ConfiguracionSolicitudService,
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return this.configuracionSolicitudService.findById(convocatoriaData?.convocatoria?.id).pipe(
      switchMap((configuracionSolicitud) => {
        convocatoriaData.configuracionSolicitudes = configuracionSolicitud;
        return this.configuracionSolicitudService.findAllConvocatoriaDocumentoRequeridoSolicitud(convocatoriaData?.convocatoria?.id).pipe(
          map((documentosRequeridos) => {
            convocatoriaData.documentosRequeridos = documentosRequeridos.items;
            return convocatoriaData;
          })
        );
      })
    );
  }

  public fillColumns(
    convocatorias: IConvocatoriaReportData[],
    reportConfig: IReportConfig<IConvocatoriaReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsConfiguracionSolicitudNotExcel();
    } else {
      return this.getColumnsConfiguracionSolicitudExcel(convocatorias);
    }
  }

  private getColumnsConfiguracionSolicitudNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: CONFIGURACION_SOLICITUD_FIELD,
      title: this.translate.instant(CONFIGURACION_SOLICITUD_ITEM_KEY),
      type: ColumnType.STRING
    });

    const titleI18n = this.translate.instant(CONFIGURACION_SOLICITUD_KEY) +
      ' (' + this.translate.instant(CONFIGURACION_SOLICITUD_PRESENTACION_SGI_KEY) +
      ' - ' + this.translate.instant(CONFIGURACION_SOLICITUD_INICIO_PRESENTACION_KEY) +
      ' - ' + this.translate.instant(CONFIGURACION_SOLICITUD_FIN_PRESENTACION_KEY) +
      ' - ' + this.translate.instant(CONFIGURACION_SOLICITUD_IMPORTE_MAXIMO_KEY) +
      ' - ' + this.translate.instant(DOCUMENTOS_REQUERIDOS_KEY, MSG_PARAMS.CARDINALIRY.PLURAL) +
      ')';

    const columnEntidad: ISgiColumnReport = {
      name: CONFIGURACION_SOLICITUD_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsConfiguracionSolicitudExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumDocumentos = Math.max(...convocatorias.map(c => c.documentosRequeridos ? c.documentosRequeridos?.length : 0));
    const titleConfiguracionSolicitud = this.translate.instant(CONFIGURACION_SOLICITUD_KEY);
    const titleDocumentosRequeridos = this.translate.instant(DOCUMENTOS_REQUERIDOS_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);

    const columnPresentacionSGI: ISgiColumnReport = {
      name: CONFIGURACION_SOLICITUD_PRESENTACION_SGI_FIELD,
      title: titleConfiguracionSolicitud + ': ' + this.translate.instant(CONFIGURACION_SOLICITUD_PRESENTACION_SGI_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnPresentacionSGI);

    const columnInicioPresentacion: ISgiColumnReport = {
      name: CONFIGURACION_SOLICITUD_INICIO_PRESENTACION_FIELD,
      title: titleConfiguracionSolicitud + ': ' + this.translate.instant(CONFIGURACION_SOLICITUD_INICIO_PRESENTACION_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnInicioPresentacion);

    const columnFinPresentacion: ISgiColumnReport = {
      name: CONFIGURACION_SOLICITUD_FIN_PRESENTACION_FIELD,
      title: titleConfiguracionSolicitud + ': ' + this.translate.instant(CONFIGURACION_SOLICITUD_FIN_PRESENTACION_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnFinPresentacion);

    const columnImporteMaximo: ISgiColumnReport = {
      name: CONFIGURACION_SOLICITUD_IMPORTE_MAXIMO_FIELD,
      title: titleConfiguracionSolicitud + ': ' + this.translate.instant(CONFIGURACION_SOLICITUD_IMPORTE_MAXIMO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnImporteMaximo);

    for (let i = 0; i < maxNumDocumentos; i++) {
      const idDocumento: string = String(i + 1);

      const columnDocumento: ISgiColumnReport = {
        name: DOCUMENTOS_REQUERIDOS_FIELD + idDocumento,
        title: titleDocumentosRequeridos + idDocumento + ': ',
        type: ColumnType.STRING,
      };
      columns.push(columnDocumento);
    }

    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsConfiguracionSolicitudNotExcel(convocatoria, elementsRow);
    } else {
      const maxNumDocumentosRequeridos = Math.max(...convocatorias.map(c => c.documentosRequeridos ? c.documentosRequeridos?.length : 0));
      this.fillRowsEntidadExcel(elementsRow, convocatoria.configuracionSolicitudes);
      for (let i = 0; i < maxNumDocumentosRequeridos; i++) {
        const documento = convocatoria.documentosRequeridos ? convocatoria.documentosRequeridos[i] ?? null : null;
        this.fillRowsEntidadExcelDocumento(elementsRow, documento);
      }
    }
    return elementsRow;
  }

  private fillRowsConfiguracionSolicitudNotExcel(convocatoria: IConvocatoriaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    if (convocatoria.configuracionSolicitudes) {
      let configuracionSolicitudContent = this.notIsNullAndNotUndefined(convocatoria.configuracionSolicitudes?.tramitacionSGI)
        ? this.getI18nBooleanYesNo(convocatoria.configuracionSolicitudes?.tramitacionSGI) : '';
      configuracionSolicitudContent += '\n';
      configuracionSolicitudContent += convocatoria.configuracionSolicitudes?.fasePresentacionSolicitudes ?
        this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoria.configuracionSolicitudes?.fasePresentacionSolicitudes.fechaInicio, false), 'short') ?? '' : '';
      configuracionSolicitudContent += '\n';
      configuracionSolicitudContent += convocatoria.configuracionSolicitudes?.fasePresentacionSolicitudes ?
        this.luxonDatePipe.transform(LuxonUtils.toBackend(convocatoria.configuracionSolicitudes?.fasePresentacionSolicitudes.fechaFin, false), 'short') ?? '' : '';
      configuracionSolicitudContent += '\n';
      configuracionSolicitudContent += convocatoria.configuracionSolicitudes?.importeMaximoSolicitud ? convocatoria.configuracionSolicitudes?.importeMaximoSolicitud.toString() : '';
      configuracionSolicitudContent += '\n';

      convocatoria.documentosRequeridos?.forEach((documento, index) => {
        configuracionSolicitudContent += documento?.tipoDocumento ? documento?.tipoDocumento.nombre ?? '' : '';
        if (convocatoria.documentosRequeridos.length - 1 > index) {
          configuracionSolicitudContent += ', ';
        }
      });
      const configuracionSolicitudElementsRow: any[] = [];
      configuracionSolicitudElementsRow.push(configuracionSolicitudContent);
      const rowReport: ISgiRowReport = {
        elements: configuracionSolicitudElementsRow
      };
      rowsReport.push(rowReport);

    }

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], configuracionSolicitud: IConfiguracionSolicitud) {
    if (configuracionSolicitud) {
      elementsRow.push(this.notIsNullAndNotUndefined(configuracionSolicitud?.tramitacionSGI)
        ? this.getI18nBooleanYesNo(configuracionSolicitud?.tramitacionSGI) : '');
      elementsRow.push(configuracionSolicitud.fasePresentacionSolicitudes ?
        this.luxonDatePipe.transform(LuxonUtils.toBackend(configuracionSolicitud?.fasePresentacionSolicitudes.fechaInicio, false), 'short') ?? '' : '');
      elementsRow.push(configuracionSolicitud.fasePresentacionSolicitudes ?
        this.luxonDatePipe.transform(LuxonUtils.toBackend(configuracionSolicitud?.fasePresentacionSolicitudes.fechaFin, false), 'short') ?? '' : '');
      elementsRow.push(configuracionSolicitud.importeMaximoSolicitud ? configuracionSolicitud.importeMaximoSolicitud.toString() ?? '' : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private fillRowsEntidadExcelDocumento(elementsRow: any[], documento: IDocumentoRequeridoSolicitud) {
    if (documento) {
      elementsRow.push(documento.tipoDocumento ? documento.tipoDocumento.nombre ?? '' : '');
    } else {
      elementsRow.push('');
    }
  }
}
