import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP } from '@core/models/csp/estado-solicitud';
import { ISolicitud } from '@core/models/csp/solicitud';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const SOLICITUD_KEY = 'csp.solicitud';
const SOLICITUD_TITULO_KEY = 'csp.solicitud.titulo';
const SOLICITUD_FIELD = 'solicitud';
const SOLICITUD_CODIGO_KEY = marker('csp.solicitud.codigo-registro');
const SOLICITUD_CODIGO_FIELD = 'codigoSolicitud';
const SOLICITUD_CODIGO_EXTERNO_KEY = marker('csp.proyecto.ref-codigo-externo');
const SOLICITUD_CODIGO_EXTERNO_FIELD = 'codigoExternoSolicitud';
const SOLICITUD_FECHA_ESTADO_KEY = marker('csp.estado-validacion-ip.fecha-estado');
const SOLICITUD_FECHA_ESTADO_FIELD = 'fechaEstadoSolicitud';
const SOLICITUD_ESTADO_KEY = marker('csp.proyecto.estado');
const SOLICITUD_ESTADO_FIELD = 'estadoSolicitud';

const COLUMN_VALUE_PREFIX = ': ';

@Injectable()
export class ProyectoSolicitudListadoExportService extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private readonly proyectoService: ProyectoService,
    private solicitudService: SolicitudService,
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    if (proyectoData.solicitudId) {
      return this.solicitudService.findById(proyectoData.solicitudId).pipe(
        map(responseSolicitud => {
          proyectoData.solicitud = responseSolicitud;
          return proyectoData;
        })
      );
    } else {
      return of(proyectoData);
    }
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {

    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsSolicitudNotExcel();
    } else {
      const prefixTitleSolicitud = this.translate.instant(SOLICITUD_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) + ': ';
      return this.getColumnsSolicitud(prefixTitleSolicitud, false);
    }
  }

  private getColumnsSolicitudNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = this.getColumnsSolicitud('', true);

    const titleI18n = this.translate.instant(SOLICITUD_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);

    const columnSolicitud: ISgiColumnReport = {
      name: SOLICITUD_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnSolicitud];
  }

  private getColumnsSolicitud(prefix: string, allString: boolean): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const columnCodigoSolicitud: ISgiColumnReport = {
      name: SOLICITUD_CODIGO_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_CODIGO_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnCodigoSolicitud);

    const columnNombreSolicitud: ISgiColumnReport = {
      name: SOLICITUD_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_TITULO_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnNombreSolicitud);

    const columnCodigoExternoSolicitud: ISgiColumnReport = {
      name: SOLICITUD_CODIGO_EXTERNO_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_CODIGO_EXTERNO_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnCodigoExternoSolicitud);

    const columnEstadoSolicitud: ISgiColumnReport = {
      name: SOLICITUD_ESTADO_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_ESTADO_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnEstadoSolicitud);

    const columnFechaPublicacionSolicitud: ISgiColumnReport = {
      name: SOLICITUD_FECHA_ESTADO_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_FECHA_ESTADO_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.DATE,
    };
    columns.push(columnFechaPublicacionSolicitud);

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {

    const proyecto = proyectos[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsSolicitudNotExcel(proyecto, elementsRow);
    } else {
      this.fillRowsSolicitudExcel(elementsRow, proyecto.solicitud);
    }
    return elementsRow;
  }

  private fillRowsSolicitudNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];
    if (proyecto.solicitud) {
      const solicitud = proyecto.solicitud;

      const solicitudElementsRow: any[] = [];

      solicitudElementsRow.push(solicitud?.codigoRegistroInterno ?? '');
      solicitudElementsRow.push(solicitud?.titulo ?? '');
      solicitudElementsRow.push(solicitud?.codigoExterno ?? '');
      solicitudElementsRow.push(solicitud?.estado?.estado ? this.translate.instant(ESTADO_MAP.get(solicitud.estado.estado)) : '');
      solicitudElementsRow.push(this.luxonDatePipe.transform(LuxonUtils.toBackend(solicitud?.estado?.fechaEstado,
        true), 'shortDate') ?? '');

      const rowReport: ISgiRowReport = {
        elements: solicitudElementsRow
      };
      rowsReport.push(rowReport);
    }
    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsSolicitudExcel(elementsRow: any[], solicitud: ISolicitud) {
    if (solicitud) {
      elementsRow.push(solicitud.codigoRegistroInterno ?? '');
      elementsRow.push(solicitud.titulo ?? '');
      elementsRow.push(solicitud.codigoExterno ?? '');
      elementsRow.push(solicitud?.estado?.estado ? this.translate.instant(ESTADO_MAP.get(solicitud.estado.estado)) : '');
      elementsRow.push(LuxonUtils.toBackend(solicitud.estado?.fechaEstado) ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private getValuePrefix(prefix: string): string {
    if (!prefix) {
      return COLUMN_VALUE_PREFIX;
    }
    return '';
  }
}
