import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IPrograma } from '@core/models/csp/programa';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
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

const ENTIDAD_CONVOCANTE_KEY = marker('title.csp.solicitud-entidad-convocante');
const ENTIDAD_CONVOCANTE_NOMBRE_KEY = marker('csp.convocatoria-entidad-convocante.nombre');
const ENTIDAD_CONVOCANTE_IDENTIFICACION_KEY = marker('csp.convocatoria-entidad-convocante.numeroIdentificacion');
const ENTIDAD_CONVOCANTE_PLAN_KEY = marker('csp.convocatoria-entidad-convocante.plan');
const ENTIDAD_CONVOCANTE_PROGRAMA_KEY = marker('csp.proyecto-entidad-convocante.programa.programa-convocatoria');
const ENTIDAD_CONVOCANTE_MODALIDAD_KEY = marker('csp.solicitud-modalidad');
const ENTIDAD_CONVOCANTE_FIELD = 'entidadConvocante';
const ENTIDAD_CONVOCANTE_IDENTIFICACION_FIELD = 'identificacionEntidadConvocante';
const ENTIDAD_CONVOCANTE_PROGRAMA_FIELD = 'programaEntidadConvocante';
const ENTIDAD_CONVOCANTE_PLAN_FIELD = 'planEntidadConvocante';
const ENTIDAD_CONVOCANTE_MODALIDAD_FIELD = 'modalidadEntidadConvocante';

@Injectable()
export class SolicitudEntidadConvocanteListadoExportService
  extends AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
    private readonly convocatoriaService: ConvocatoriaService,
    private empresaService: EmpresaService
  ) {
    super(translate);
  }

  public getData(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    if (solicitudData.convocatoriaId) {
      const findOptions: SgiRestFindOptions = {
        sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
      };
      return this.convocatoriaService.findAllConvocatoriaEntidadConvocantes(solicitudData.convocatoriaId, findOptions).pipe(
        map((responseEntidadesConvocantes) => {
          solicitudData.entidadesConvocantes = [];
          return responseEntidadesConvocantes;
        }),
        switchMap(responseEntidadesConvocantes => {
          if (responseEntidadesConvocantes.total === 0) {
            return of(solicitudData);
          }
          const entidadesConvocantes = responseEntidadesConvocantes.items;

          const entidadesConvocantesIds = new Set<string>(responseEntidadesConvocantes.items.map(
            (entidadConvocante) => entidadConvocante.entidad.id)
          );
          return this.empresaService.findAllByIdIn([...entidadesConvocantesIds]).pipe(
            map((result) => {
              const entidades = result.items;

              entidadesConvocantes.forEach((entidadSolicitud) => {
                entidadSolicitud.entidad = entidades.find((entidad) => entidadSolicitud.entidad.id === entidad.id);
              });

              solicitudData.entidadesConvocantes = entidadesConvocantes;
              return solicitudData;
            })
          );
        }),
        switchMap(res => {
          return this.getSolicitudModalidades(solicitudData.id).pipe(
            map(solicitudModalidades => {
              solicitudData.modalidades = solicitudModalidades;
              return solicitudData;
            })
          );
        })
      );
    } else {
      return of(solicitudData);
    }
  }

  public fillColumns(
    solicitudes: ISolicitudReportData[],
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsEntidadConvocanteNotExcel();
    } else {
      return this.getColumnsEntidadConvocanteExcel(solicitudes);
    }
  }

  private getColumnsEntidadConvocanteNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: ENTIDAD_CONVOCANTE_FIELD,
      title: this.translate.instant(ENTIDAD_CONVOCANTE_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(ENTIDAD_CONVOCANTE_KEY) +
      ' (' + this.translate.instant(ENTIDAD_CONVOCANTE_NOMBRE_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_CONVOCANTE_IDENTIFICACION_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_CONVOCANTE_PLAN_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_CONVOCANTE_PROGRAMA_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_CONVOCANTE_MODALIDAD_KEY) +
      ')';
    const columnEntidad: ISgiColumnReport = {
      name: ENTIDAD_CONVOCANTE_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsEntidadConvocanteExcel(solicituds: ISolicitudReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEntidasConvocantes = Math.max(...solicituds.map(s => s.entidadesConvocantes ? s.entidadesConvocantes?.length : 0));
    const titleEntidadConvocante = this.translate.instant(ENTIDAD_CONVOCANTE_KEY);
    const titlePlanEntidadConvocante = this.translate.instant(ENTIDAD_CONVOCANTE_PLAN_KEY);

    for (let i = 0; i < maxNumEntidasConvocantes; i++) {
      const idEntidadConvocante: string = String(i + 1);
      const columnEntidadConvocante: ISgiColumnReport = {
        name: ENTIDAD_CONVOCANTE_FIELD + idEntidadConvocante,
        title: titleEntidadConvocante + idEntidadConvocante + ': ' + this.translate.instant(ENTIDAD_CONVOCANTE_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEntidadConvocante);

      const columnCifEntidadConvocante: ISgiColumnReport = {
        name: ENTIDAD_CONVOCANTE_IDENTIFICACION_FIELD + idEntidadConvocante,
        title: titleEntidadConvocante + idEntidadConvocante + ': ' + this.translate.instant(ENTIDAD_CONVOCANTE_IDENTIFICACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnCifEntidadConvocante);

      const columnPlanEntidadConvocante: ISgiColumnReport = {
        name: ENTIDAD_CONVOCANTE_PLAN_FIELD + idEntidadConvocante,
        title: titleEntidadConvocante + idEntidadConvocante + ': ' + titlePlanEntidadConvocante,
        type: ColumnType.STRING,
      };
      columns.push(columnPlanEntidadConvocante);

      const titlePlanPlusEntidadConvocante = titlePlanEntidadConvocante +
        ' ' +
        titleEntidadConvocante + idEntidadConvocante +
        ': ';
      const columnProgramaEntidadConvocante: ISgiColumnReport = {
        name: ENTIDAD_CONVOCANTE_PROGRAMA_FIELD + idEntidadConvocante,
        title: titlePlanPlusEntidadConvocante + this.translate.instant(ENTIDAD_CONVOCANTE_PROGRAMA_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnProgramaEntidadConvocante);

      const columnModalidadEntidadConvocante: ISgiColumnReport = {
        name: ENTIDAD_CONVOCANTE_MODALIDAD_FIELD + idEntidadConvocante,
        title: titlePlanPlusEntidadConvocante + this.translate.instant(ENTIDAD_CONVOCANTE_MODALIDAD_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnModalidadEntidadConvocante);
    }

    return columns;
  }

  public fillRows(solicitudes: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {
    const solicitud = solicitudes[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsEntidadConvocanteNotExcel(solicitud, elementsRow);
    } else {

      const maxNumEntidasConvocantes = Math.max(...solicitudes.map(s => s.entidadesConvocantes ? s.entidadesConvocantes?.length : 0));
      for (let i = 0; i < maxNumEntidasConvocantes; i++) {
        const entidadConvocante = solicitud.entidadesConvocantes && solicitud.entidadesConvocantes.length > 0 ? solicitud.entidadesConvocantes[i] : null;
        this.fillRowsEntidadExcel(elementsRow, entidadConvocante, solicitud);
      }
    }
    return elementsRow;
  }

  private fillRowsEntidadConvocanteNotExcel(solicitud: ISolicitudReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    solicitud.entidadesConvocantes?.forEach(entidadConvocante => {
      const entidadConvocanteElementsRow: any[] = [];

      const solicitudModalidad = solicitud.modalidades
        .find(modalidad => modalidad.entidad.id === entidadConvocante.entidad.id);

      let entidadTable = entidadConvocante.entidad?.nombre ?? '';
      entidadTable += '\n';
      entidadTable += entidadConvocante.entidad?.numeroIdentificacion ?? '';
      entidadTable += '\n';
      entidadTable += this.getPlan(entidadConvocante)?.nombre ?? '';
      entidadTable += '\n';
      entidadTable += entidadConvocante.programa?.nombre ?? '';
      entidadTable += '\n';
      entidadTable += solicitudModalidad?.programa?.nombre ?? '';

      entidadConvocanteElementsRow.push(entidadTable);

      const rowReport: ISgiRowReport = {
        elements: entidadConvocanteElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], entidadConvocante: IConvocatoriaEntidadConvocante, solicitud: ISolicitudReportData) {
    if (entidadConvocante) {
      const solicitudModalidad = solicitud.modalidades
        .find(modalidad => modalidad.entidad.id === entidadConvocante.entidad.id);

      elementsRow.push(entidadConvocante.entidad?.nombre ?? '');
      elementsRow.push(entidadConvocante.entidad?.numeroIdentificacion ?? '');
      elementsRow.push(this.getPlan(entidadConvocante)?.nombre ?? '');
      elementsRow.push(entidadConvocante?.programa?.nombre ?? '');
      elementsRow.push(solicitudModalidad?.programa?.nombre ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private getPlan(value: IConvocatoriaEntidadConvocante): IPrograma {
    if (value.programa != null) {
      return this.getTopLevel(value.programa);
    }
    return null;
  }

  private getTopLevel(programa: IPrograma): IPrograma {
    if (programa.padre == null) {
      return programa;
    }
    return this.getTopLevel(programa.padre);
  }

  private getModalidad(value: IConvocatoriaEntidadConvocante, solicitudId: number): Observable<string> {

    return this.getSolicitudModalidades(solicitudId).pipe(
      switchMap(solicitudModalidades => {
        const solicitudModalidad = solicitudModalidades
          .find(modalidad => modalidad.entidad.id === value.entidad.id);
        if (solicitudModalidad) {
          return solicitudModalidad.programa.nombre;
        }
      })
    );

  }

  /**
   * Recupera las modalidades de la solicitud
   *
   * @param solicitudId Identificador de la solicitud
   * @returns observable para recuperar los datos
   */
  private getSolicitudModalidades(solicitudId: number): Observable<ISolicitudModalidad[]> {
    return this.solicitudService.findAllSolicitudModalidades(solicitudId).pipe(
      switchMap(res => {
        return of(res.items);
      })
    );
  }
}
