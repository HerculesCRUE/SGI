import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const SOCIO_KEY = 'csp.solicitud-proyecto-socio';
const SOCIO_NOMBRE_KEY = 'info.csp.proyecto-socio.nombre';
const SOCIO_FIELD = 'socio';
const SOCIO_CIF_KEY = marker('csp.proyecto-socio.indentificador-fiscal');
const SOCIO_CIF_FIELD = 'cifSocio';
const SOCIO_ROL_KEY = marker('info.csp.proyecto-socio.rol');
const SOCIO_ROL_FIELD = 'rolSocio';
const SOCIO_NUM_INVESTIGADORES_KEY = marker('info.csp.proyecto-socio.numero-investigadores');
const SOCIO_NUM_INVESTIGADORES_FIELD = 'numInvestigadoresSocio';
const SOCIO_FECHA_INICIO_KEY = marker('info.csp.proyecto-socio.fecha-inicio');
const SOCIO_FECHA_INICIO_FIELD = 'fechaInicioSocio';
const SOCIO_FECHA_FIN_KEY = marker('info.csp.proyecto-socio.fecha-fin');
const SOCIO_FECHA_FIN_FIELD = 'fechaFinSocio';
const SOCIO_IMPORTE_PRESUPUESTO_KEY = marker('info.csp.proyecto-socio.importe-presupuesto');
const SOCIO_IMPORTE_PRESUPUESTO_FIELD = 'importePresupuestoSocio';
const SOCIO_IMPORTE_CONCEDIDO_KEY = marker('info.csp.proyecto-socio.importe-concedido');
const SOCIO_IMPORTE_CONCEDIDO_FIELD = 'importeConcedidoSocio';

@Injectable()
export class ProyectoSocioListadoExportService extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private readonly proyectoService: ProyectoService,
    private empresaService: EmpresaService,
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.proyectoService.findAllProyectoSocioProyecto(proyectoData.id, findOptions).pipe(
      switchMap(responseSocio => {
        if (responseSocio.total === 0) {
          return of(proyectoData);
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

            proyectoData.socios = socios;
            return proyectoData;

          })
        );
      })
    );
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {

    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsSocioNotExcel();
    } else {
      return this.getColumnsSocioExcel(proyectos);
    }
  }

  private getColumnsSocioNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: SOCIO_FIELD,
      title: this.translate.instant(SOCIO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(SOCIO_KEY, MSG_PARAMS.CARDINALIRY.PLURAL) +
      ' (' + this.translate.instant(SOCIO_NOMBRE_KEY) +
      ' - ' + this.translate.instant(SOCIO_CIF_KEY) +
      ' - ' + this.translate.instant(SOCIO_ROL_KEY) +
      ' - ' + this.translate.instant(SOCIO_NUM_INVESTIGADORES_KEY) +
      ' - ' + this.translate.instant(SOCIO_FECHA_INICIO_KEY) +
      ' - ' + this.translate.instant(SOCIO_FECHA_FIN_KEY) +
      ' - ' + this.translate.instant(SOCIO_IMPORTE_PRESUPUESTO_KEY) +
      ' - ' + this.translate.instant(SOCIO_IMPORTE_CONCEDIDO_KEY) +
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

  private getColumnsSocioExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumSocios = Math.max(...proyectos.map(p => p.socios?.length));
    const titleSocio = this.translate.instant(SOCIO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);
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
        type: ColumnType.NUMBER,
      };
      columns.push(columnNumInvestigadoresSocio);

      const columnFechaInicioSocio: ISgiColumnReport = {
        name: SOCIO_FECHA_INICIO_FIELD + idSocio,
        title: titleSocio + idSocio + ': ' + this.translate.instant(SOCIO_FECHA_INICIO_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaInicioSocio);

      const columnFechaFinSocio: ISgiColumnReport = {
        name: SOCIO_FECHA_FIN_FIELD + idSocio,
        title: titleSocio + idSocio + ': ' + this.translate.instant(SOCIO_FECHA_FIN_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinSocio);

      const columnImportePresupuestoSocio: ISgiColumnReport = {
        name: SOCIO_IMPORTE_PRESUPUESTO_FIELD + idSocio,
        title: titleSocio + idSocio + ': ' + this.translate.instant(SOCIO_IMPORTE_PRESUPUESTO_KEY),
        type: ColumnType.NUMBER,
      };
      columns.push(columnImportePresupuestoSocio);

      const columnImporteConcedidoSocio: ISgiColumnReport = {
        name: SOCIO_IMPORTE_CONCEDIDO_FIELD + idSocio,
        title: titleSocio + idSocio + ': ' + this.translate.instant(SOCIO_IMPORTE_CONCEDIDO_KEY),
        type: ColumnType.NUMBER,
      };
      columns.push(columnImporteConcedidoSocio);
    }
    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {

    const proyecto = proyectos[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsSocioNotExcel(proyecto, elementsRow);
    } else {
      const maxNumSocio = Math.max(...proyectos.map(p => p.socios?.length));
      for (let i = 0; i < maxNumSocio; i++) {
        const socio = proyecto.socios[i] ?? null;
        this.fillRowsSocioExcel(elementsRow, socio);
      }
    }
    return elementsRow;
  }

  private fillRowsSocioNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.socios?.forEach(socio => {
      const socioElementsRow: any[] = [];

      let socioTable = socio?.empresa?.nombre;
      socioTable += '\n';
      socioTable += socio?.empresa?.numeroIdentificacion ?? '';
      socioTable += '\n';
      socioTable += socio?.rolSocio?.nombre ?? '';
      socioTable += '\n';
      socioTable += socio?.numInvestigadores ?? '';
      socioTable += '\n';
      socioTable += this.luxonDatePipe.transform(LuxonUtils.toBackend(socio?.fechaInicio, true), 'shortDate') ?? '';
      socioTable += '\n';
      socioTable += this.luxonDatePipe.transform(LuxonUtils.toBackend(socio?.fechaFin, true), 'shortDate') ?? '';
      socioTable += '\n';
      socioTable += socio?.importePresupuesto ?? '';
      socioTable += '\n';
      socioTable += socio?.importeConcedido ?? '';

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

  private fillRowsSocioExcel(elementsRow: any[], socio: IProyectoSocio) {
    if (socio) {
      elementsRow.push(socio.empresa?.nombre ?? '');
      elementsRow.push(socio.empresa?.numeroIdentificacion ?? '');
      elementsRow.push(socio.rolSocio?.nombre ?? '');
      elementsRow.push(socio.numInvestigadores ?? '');
      elementsRow.push(LuxonUtils.toBackend(socio.fechaInicio) ?? '');
      elementsRow.push(LuxonUtils.toBackend(socio.fechaFin) ?? '');
      elementsRow.push(socio.importePresupuesto ?? '');
      elementsRow.push(socio.importeConcedido ?? '');
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
