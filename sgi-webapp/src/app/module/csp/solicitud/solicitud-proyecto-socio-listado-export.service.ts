import { DecimalPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ISolicitudReportData, ISolicitudReportOptions } from './solicitud-listado-export.service';

const SOCIO_KEY = 'csp.solicitud-proyecto-socio';
const SOCIO_NOMBRE_KEY = 'info.csp.proyecto-socio.nombre';
const SOCIO_FIELD = 'socio';
const SOCIO_CIF_KEY = marker('csp.convocatoria-entidad-convocante.numeroIdentificacion');
const SOCIO_CIF_FIELD = 'cifSocio';
const SOCIO_ROL_KEY = marker('info.csp.proyecto-socio.rol');
const SOCIO_ROL_FIELD = 'rolSocio';
const SOCIO_NUM_INVESTIGADORES_KEY = marker('info.csp.proyecto-socio.numero-investigadores');
const SOCIO_NUM_INVESTIGADORES_FIELD = 'numInvestigadoresSocio';
const SOCIO_FECHA_INICIO_KEY = marker('csp.proyecto-socio.mes-inicio');
const SOCIO_FECHA_INICIO_FIELD = 'fechaInicioSocio';
const SOCIO_FECHA_FIN_KEY = marker('csp.proyecto-socio.mes-fin');
const SOCIO_FECHA_FIN_FIELD = 'fechaFinSocio';
const SOCIO_IMPORTE_PRESUPUESTADO_KEY = marker('csp.solicitud-proyecto-presupuesto.importe-presupuestado');
const SOCIO_IMPORTE_PRESUPUESTADO_FIELD = 'importePresupuestadoSocio';
const SOCIO_IMPORTE_SOLICITADO_KEY = marker('csp.solicitud-proyecto-presupuesto.importe-solicitado');
const SOCIO_IMPORTE_SOLICITADO_FIELD = 'importeSolicitadoSocio';
const PROYECTO_KEY = marker('menu.csp.solicitudes.datos-proyecto');

@Injectable()
export class SolicitudProyectoSocioListadoExportService extends AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
    private empresaService: EmpresaService,
    private readonly decimalPipe: DecimalPipe,
  ) {
    super(translate);
  }

  public getData(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.solicitudService.findAllSolicitudProyectoSocio(solicitudData.id, findOptions).pipe(
      switchMap(responseSocio => {
        if (responseSocio.total === 0) {
          return of(solicitudData);
        }
        const socios = responseSocio.items;

        const sociosIds = new Set<string>(responseSocio.items.map(
          (socio) => socio.empresa.id)
        );
        return this.empresaService.findAllByIdIn([...sociosIds]).pipe(
          map((result) => {
            const empresasSocios = result.items;

            socios.forEach((socio) => {
              socio.empresa = empresasSocios.find((entidad) => socio.empresa.id === entidad.id);
            });

            solicitudData.socios = socios;
            return solicitudData;

          })
        );
      })
    );
  }

  public fillColumns(
    solicitudes: ISolicitudReportData[],
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): ISgiColumnReport[] {

    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsSocioNotExcel();
    } else {
      return this.getColumnsSocioExcel(solicitudes);
    }
  }

  private getColumnsSocioNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: SOCIO_FIELD,
      title: this.translate.instant(SOCIO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(PROYECTO_KEY) + ': ' + this.translate.instant(SOCIO_KEY, MSG_PARAMS.CARDINALIRY.PLURAL) +
      ' (' + this.translate.instant(SOCIO_NOMBRE_KEY) +
      ' - ' + this.translate.instant(SOCIO_CIF_KEY) +
      ' - ' + this.translate.instant(SOCIO_ROL_KEY) +
      ' - ' + this.translate.instant(SOCIO_NUM_INVESTIGADORES_KEY) +
      ' - ' + this.translate.instant(SOCIO_FECHA_INICIO_KEY) +
      ' - ' + this.translate.instant(SOCIO_FECHA_FIN_KEY) +
      ' - ' + this.translate.instant(SOCIO_IMPORTE_PRESUPUESTADO_KEY) +
      ' - ' + this.translate.instant(SOCIO_IMPORTE_SOLICITADO_KEY) +
      ')';
    const columnSocio: ISgiColumnReport = {
      name: SOCIO_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnSocio];
  }

  private getColumnsSocioExcel(solicitudes: ISolicitudReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumSocios = Math.max(...solicitudes.map(s => s.socios ? s.socios?.length : 0));
    const titleSocio = this.translate.instant(PROYECTO_KEY) + ': ' + this.translate.instant(SOCIO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);
    for (let i = 0; i < maxNumSocios; i++) {
      const idSocio: string = String(i + 1);
      const columnNombreSocio: ISgiColumnReport = {
        name: SOCIO_FIELD + idSocio,
        title: titleSocio + idSocio + ': ' + this.translate.instant(SOCIO_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombreSocio);

      const columnCifSocio: ISgiColumnReport = {
        name: SOCIO_CIF_FIELD + idSocio,
        title: titleSocio + idSocio + ': ' + this.translate.instant(SOCIO_CIF_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnCifSocio);

      const columnRolSocio: ISgiColumnReport = {
        name: SOCIO_ROL_FIELD + idSocio,
        title: titleSocio + idSocio + ': ' + this.translate.instant(SOCIO_ROL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnRolSocio);

      const columnNumInvestigadoresSocio: ISgiColumnReport = {
        name: SOCIO_NUM_INVESTIGADORES_FIELD + idSocio,
        title: titleSocio + idSocio + ': ' + this.translate.instant(SOCIO_NUM_INVESTIGADORES_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNumInvestigadoresSocio);

      const columnFechaInicioSocio: ISgiColumnReport = {
        name: SOCIO_FECHA_INICIO_FIELD + idSocio,
        title: titleSocio + idSocio + ': ' + this.translate.instant(SOCIO_FECHA_INICIO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaInicioSocio);

      const columnFechaFinSocio: ISgiColumnReport = {
        name: SOCIO_FECHA_FIN_FIELD + idSocio,
        title: titleSocio + idSocio + ': ' + this.translate.instant(SOCIO_FECHA_FIN_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaFinSocio);

      const columnImportePresupuestoSocio: ISgiColumnReport = {
        name: SOCIO_IMPORTE_PRESUPUESTADO_FIELD + idSocio,
        title: titleSocio + idSocio + ': ' + this.translate.instant(SOCIO_IMPORTE_PRESUPUESTADO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnImportePresupuestoSocio);

      const columnImporteConcedidoSocio: ISgiColumnReport = {
        name: SOCIO_IMPORTE_SOLICITADO_FIELD + idSocio,
        title: titleSocio + idSocio + ': ' + this.translate.instant(SOCIO_IMPORTE_SOLICITADO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnImporteConcedidoSocio);
    }
    return columns;
  }

  public fillRows(solicitudes: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {

    const solicitud = solicitudes[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsSocioNotExcel(solicitud, elementsRow);
    } else {
      const maxNumSocio = Math.max(...solicitudes.map(s => s.socios ? s.socios?.length : 0));
      for (let i = 0; i < maxNumSocio; i++) {
        const socio = solicitud.socios && solicitud.socios.length > 0 ? solicitud.socios[i] : null;
        this.fillRowsSocioExcel(elementsRow, socio);
      }
    }
    return elementsRow;
  }

  private fillRowsSocioNotExcel(solicitud: ISolicitudReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    solicitud.socios?.forEach(socio => {
      const socioElementsRow: any[] = [];

      let socioTable = socio?.empresa?.nombre;
      socioTable += '\n';
      socioTable += socio?.empresa?.numeroIdentificacion ?? '';
      socioTable += '\n';
      socioTable += socio?.rolSocio?.nombre ?? '';
      socioTable += '\n';
      socioTable += socio?.numInvestigadores ?? '';
      socioTable += '\n';
      socioTable += socio?.mesInicio ?? '';
      socioTable += '\n';
      socioTable += socio?.mesFin ?? '';
      socioTable += '\n';
      socioTable += socio?.importePresupuestado ?? '';
      socioTable += '\n';
      socioTable += socio?.importeSolicitado ?? '';

      socioElementsRow.push(socioTable);

      const rowReport: ISgiRowReport = {
        elements: socioElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsSocioExcel(elementsRow: any[], socio: ISolicitudProyectoSocio) {
    if (socio) {
      elementsRow.push(socio.empresa?.nombre ?? '');
      elementsRow.push(socio.empresa?.numeroIdentificacion ?? '');
      elementsRow.push(socio.rolSocio?.nombre ?? '');
      elementsRow.push(socio.numInvestigadores ? socio.numInvestigadores.toString() : '');
      elementsRow.push(socio.mesInicio?.toString() ?? '');
      elementsRow.push(socio.mesFin?.toString() ?? '');
      elementsRow.push(this.decimalPipe.transform(socio.importePresupuestado, '.2-2') ?? '');
      elementsRow.push(this.decimalPipe.transform(socio.importeSolicitado, '.2-2') ?? '');
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
