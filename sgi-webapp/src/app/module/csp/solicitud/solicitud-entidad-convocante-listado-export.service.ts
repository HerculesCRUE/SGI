import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IPrograma } from '@core/models/csp/programa';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
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
const ENTIDAD_CONVOCANTE_PROGRAMA_CONVOCATORIA_KEY = marker('csp.proyecto-entidad-convocante.programa.programa-convocatoria');
const ENTIDAD_CONVOCANTE_NIVEL_KEY = marker('csp.solicitud-entidad-convocante.modalidad-nivel');
const ENTIDAD_CONVOCANTE_FIELD = 'entidadConvocante';
const ENTIDAD_CONVOCANTE_IDENTIFICACION_FIELD = 'identificacionEntidadConvocante';
const ENTIDAD_CONVOCANTE_PROGRAMA_FIELD = 'programaEntidadConvocante';
const ENTIDAD_CONVOCANTE_PLAN_FIELD = 'planEntidadConvocante';
const ENTIDAD_CONVOCANTE_NIVEL_PROGRAMA_FIELD = 'nivelProgramaEntidadConvocante';

@Injectable()
export class SolicitudEntidadConvocanteListadoExportService
  extends AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions> {

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
    return this.getColumnsEntidadConvocanteExcel(solicitudes, reportConfig.reportOptions.showPlanesInvestigacion);
  }

  private getColumnsEntidadConvocanteExcel(solicitudes: ISolicitudReportData[], showPlanesInvestigacion: boolean): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEntidasConvocantes = Math.max(...solicitudes.map(s => s.entidadesConvocantes ? s.entidadesConvocantes?.length : 0));
    const titleEntidadConvocante = this.translate.instant(ENTIDAD_CONVOCANTE_KEY);
    const titlePlanEntidadConvocante = this.translate.instant(ENTIDAD_CONVOCANTE_PLAN_KEY);
    const titleNivelPrograma = this.translate.instant(ENTIDAD_CONVOCANTE_NIVEL_KEY);

    for (let i = 0; i < maxNumEntidasConvocantes; i++) {
      const idEntidadConvocante: string = String(i + 1);
      const columnEntidadConvocante: ISgiColumnReport = {
        name: ENTIDAD_CONVOCANTE_FIELD + idEntidadConvocante,
        title: titleEntidadConvocante + ' ' + idEntidadConvocante + ': ' + this.translate.instant(ENTIDAD_CONVOCANTE_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEntidadConvocante);

      const columnCifEntidadConvocante: ISgiColumnReport = {
        name: ENTIDAD_CONVOCANTE_IDENTIFICACION_FIELD + idEntidadConvocante,
        title: titleEntidadConvocante + ' ' + idEntidadConvocante + ': ' + this.translate.instant(ENTIDAD_CONVOCANTE_IDENTIFICACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnCifEntidadConvocante);

      if (showPlanesInvestigacion) {
        const maxNivelProgramaEntidadConvocanteX = Math.max(...solicitudes.map(solicitud => {

          let nivelPrograma = 0;
          if (solicitud.entidadesConvocantes && solicitud.entidadesConvocantes.length > i) {
            const solicitudModalidad = solicitud.modalidades
              .find(modalidad => modalidad.entidad.id === solicitud.entidadesConvocantes[i].entidad.id);
            nivelPrograma = this.getNivelPrograma(solicitudModalidad?.programa ?? solicitud.entidadesConvocantes[i].programa);
          }

          return nivelPrograma;
        }));

        for (let j = 0; j <= maxNivelProgramaEntidadConvocanteX; j++) {
          if (j === 0) {
            const columnPlanEntidadConvocante: ISgiColumnReport = {
              name: ENTIDAD_CONVOCANTE_PLAN_FIELD + idEntidadConvocante,
              title: titleEntidadConvocante + ' ' + idEntidadConvocante + ': ' + titlePlanEntidadConvocante,
              type: ColumnType.STRING,
            };
            columns.push(columnPlanEntidadConvocante);

            const columnProgramaEntidadConvocanteConvocatoria: ISgiColumnReport = {
              name: ENTIDAD_CONVOCANTE_PROGRAMA_FIELD + idEntidadConvocante,
              title: titlePlanEntidadConvocante + ' ' + titleEntidadConvocante + ' ' + idEntidadConvocante + ': ' + this.translate.instant(ENTIDAD_CONVOCANTE_PROGRAMA_CONVOCATORIA_KEY),
              type: ColumnType.STRING,
            };
            columns.push(columnProgramaEntidadConvocanteConvocatoria);
          } else {
            const columnProgramaEntidadConvocante: ISgiColumnReport = {
              name: ENTIDAD_CONVOCANTE_NIVEL_PROGRAMA_FIELD + idEntidadConvocante + '_' + j,
              title: titlePlanEntidadConvocante + ' ' + titleEntidadConvocante + ' ' + idEntidadConvocante + ': ' + titleNivelPrograma + ' ' + j,
              type: ColumnType.STRING,
            };
            columns.push(columnProgramaEntidadConvocante);
          }
        }
      }
    }

    return columns;
  }

  public fillRows(solicitudes: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {
    const solicitud = solicitudes[index];

    const elementsRow: any[] = [];
    const maxNumEntidasConvocantes = Math.max(...solicitudes.map(s => s.entidadesConvocantes ? s.entidadesConvocantes?.length : 0));

    for (let i = 0; i < maxNumEntidasConvocantes; i++) {
      const entidadConvocante = solicitud.entidadesConvocantes && solicitud.entidadesConvocantes.length > 0 ? solicitud.entidadesConvocantes[i] : null;

      const maxNivelProgramaEntidadConvocanteX = Math.max(...solicitudes.map(solicitud => {

        let nivelPrograma = 0;
        if (solicitud.entidadesConvocantes && solicitud.entidadesConvocantes.length > i) {
          const solicitudModalidad = solicitud.modalidades
            .find(modalidad => modalidad.entidad.id === solicitud.entidadesConvocantes[i].entidad.id);
          nivelPrograma = this.getNivelPrograma(solicitudModalidad?.programa ?? solicitud.entidadesConvocantes[i].programa);
        }

        return nivelPrograma;
      }));

      this.fillRowsEntidadExcel(elementsRow, entidadConvocante, solicitud, maxNivelProgramaEntidadConvocanteX, reportConfig.reportOptions.showPlanesInvestigacion);
    }

    return elementsRow;
  }

  private fillRowsEntidadExcel(
    elementsRow: any[],
    entidadConvocanteConvocatoria: IConvocatoriaEntidadConvocante,
    solicitud: ISolicitudReportData,
    maxNivelProgramaEntidadConvocante: number,
    showPlanesInvestigacion: boolean
  ) {
    if (entidadConvocanteConvocatoria) {
      const solicitudModalidad = solicitud.modalidades
        .find(modalidad => modalidad.entidad.id === entidadConvocanteConvocatoria.entidad.id);

      elementsRow.push(entidadConvocanteConvocatoria.entidad?.nombre ?? '');
      elementsRow.push(entidadConvocanteConvocatoria.entidad?.numeroIdentificacion ?? '');

      if (showPlanesInvestigacion) {
        const nombresNivelesPrograma = this.getNombresNivelesPrograma(solicitudModalidad?.programa ?? entidadConvocanteConvocatoria.programa);
        for (let i = 0; i <= maxNivelProgramaEntidadConvocante; i++) {
          const nivel = nombresNivelesPrograma.length > i ? nombresNivelesPrograma[i] : '';
          elementsRow.push(nivel);

          if (i === 0) {
            elementsRow.push(entidadConvocanteConvocatoria?.programa?.nombre ?? '');
          }
        }
      }

    } else {
      elementsRow.push('');
      elementsRow.push('');

      if (showPlanesInvestigacion) {
        elementsRow.push('');

        for (let i = 0; i <= maxNivelProgramaEntidadConvocante; i++) {
          elementsRow.push('');
        }
      }
    }
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

  /**
   * Obtiene el nivel del programa en el arbol
   * 
   * @param programa un programa
   * @returns el nivel del programa en el arbol
   */
  private getNivelPrograma(programa: IPrograma): number {
    if (!programa) {
      return 0;
    }

    let level = 0;
    let currentPrograma = programa;

    while (currentPrograma.padre) {
      level++;
      currentPrograma = currentPrograma.padre;
    }

    return level;
  }

  /**
   * Lista con los nombres de todos los niveles desde el nodo raiz hasta el programa
   * 
   * @param programa un programa
   * @returns la lista de los nombres de todos los niveles desde el nodo raiz hasta el programa
   */
  private getNombresNivelesPrograma(programa: IPrograma): string[] {
    if (!programa) {
      return [];
    }

    let nombres = [];
    let currentPrograma = programa;

    while (currentPrograma.padre) {
      nombres.push(currentPrograma.nombre ?? '');
      currentPrograma = currentPrograma.padre;
    }

    nombres.push(currentPrograma.nombre ?? '');

    return nombres.reverse();
  }

}
