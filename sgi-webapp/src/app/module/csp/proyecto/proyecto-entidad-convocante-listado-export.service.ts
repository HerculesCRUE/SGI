import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IPrograma } from '@core/models/csp/programa';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const ENTIDAD_CONVOCANTE_KEY = 'csp.proyecto-entidad-convocante.programa';
const ENTIDAD_CONVOCANTE_NOMBRE_KEY = 'csp.convocatoria-entidad-convocante.nombre';
const ENTIDAD_CONVOCANTE_CIF_KEY = marker('csp.convocatoria-entidad-convocante.cif');
const ENTIDAD_CONVOCANTE_PLAN_KEY = marker('csp.proyecto-entidad-convocante.programa.plan');
const ENTIDAD_CONVOCANTE_PROGRAMA_CONVOCATORIA_KEY = marker('csp.proyecto-entidad-convocante.programa.programa-convocatoria');
const ENTIDAD_CONVOCANTE_NIVEL_KEY = marker('csp.proyecto-entidad-convocante.modalidad-nivel');
const ENTIDAD_CONVOCANTE_FIELD = 'entidadConvocante';
const ENTIDAD_CONVOCANTE_CIF_FIELD = 'cifEntidadConvocante';
const ENTIDAD_CONVOCANTE_PROGRAMA_FIELD = 'programaEntidadConvocante';
const ENTIDAD_CONVOCANTE_PLAN_FIELD = 'planEntidadConvocante';
const ENTIDAD_CONVOCANTE_NIVEL_PROGRAMA_FIELD = 'nivelProgramaEntidadConvocante';

@Injectable()
export class ProyectoEntidadConvocanteListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private empresaService: EmpresaService
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.proyectoService.findAllEntidadConvocantes(proyectoData.id, findOptions).pipe(
      map((responseEntidadesConvocantes) => {
        proyectoData.entidadesConvocantes = [];
        return responseEntidadesConvocantes;
      }),
      switchMap(responseEntidadesConvocantes => {
        if (responseEntidadesConvocantes.total === 0) {
          return of(proyectoData);
        }
        const entidadesConvocantes = responseEntidadesConvocantes.items;

        const entidadesConvocantesIds = new Set<string>(responseEntidadesConvocantes.items.map(
          (entidadConvocante) => entidadConvocante.entidad.id)
        );
        return this.empresaService.findAllByIdIn([...entidadesConvocantesIds]).pipe(
          map((result) => {
            const entidades = result.items;

            entidadesConvocantes.forEach((entidadProyecto) => {
              entidadProyecto.entidad = entidades.find((entidad) => entidadProyecto.entidad.id === entidad.id);
            });

            proyectoData.entidadesConvocantes = entidadesConvocantes;
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
    return this.getColumnsEntidadConvocanteExcel(proyectos, reportConfig.reportOptions.showPlanesInvestigacion);
  }

  private getColumnsEntidadConvocanteExcel(proyectos: IProyectoReportData[], showPlanesInvestigacion: boolean): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEntidasConvocantes = Math.max(...proyectos.map(p => p.entidadesConvocantes?.length));
    const titleEntidadConvocante = this.translate.instant(ENTIDAD_CONVOCANTE_KEY);
    const titlePlanEntidadConvocante = this.translate.instant(ENTIDAD_CONVOCANTE_PLAN_KEY);
    const titleNivelPrograma = this.translate.instant(ENTIDAD_CONVOCANTE_NIVEL_KEY);

    for (let i = 0; i < maxNumEntidasConvocantes; i++) {
      const idEntidadConvocante: string = String(i + 1);
      const columnEntidadConvocante: ISgiColumnReport = {
        name: ENTIDAD_CONVOCANTE_FIELD + idEntidadConvocante,
        title: titleEntidadConvocante + idEntidadConvocante + ': ' + this.translate.instant(ENTIDAD_CONVOCANTE_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEntidadConvocante);

      const columnCifEntidadConvocante: ISgiColumnReport = {
        name: ENTIDAD_CONVOCANTE_CIF_FIELD + idEntidadConvocante,
        title: titleEntidadConvocante + idEntidadConvocante + ': ' + this.translate.instant(ENTIDAD_CONVOCANTE_CIF_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnCifEntidadConvocante);

      if (showPlanesInvestigacion) {
        const maxNivelProgramaEntidadConvocanteX = Math.max(...proyectos.map(proyecto => {

          let nivelPrograma = 0;
          if (proyecto.entidadesConvocantes && proyecto.entidadesConvocantes.length > i) {
            nivelPrograma = this.getNivelPrograma(proyecto.entidadesConvocantes[i]?.programa ?? proyecto.entidadesConvocantes[i].programaConvocatoria);
          }

          return nivelPrograma;
        }));

        for (let j = 0; j <= maxNivelProgramaEntidadConvocanteX; j++) {
          if (j === 0) {
            const columnPlanEntidadConvocante: ISgiColumnReport = {
              name: ENTIDAD_CONVOCANTE_PLAN_FIELD + idEntidadConvocante,
              title: titlePlanEntidadConvocante + ' ' + titleEntidadConvocante + ' ' + idEntidadConvocante + ': ' + titlePlanEntidadConvocante,
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

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = proyectos[index];

    const elementsRow: any[] = [];
    const maxNumEntidasConvocantes = Math.max(...proyectos.map(p => p.entidadesConvocantes?.length));
    for (let i = 0; i < maxNumEntidasConvocantes; i++) {
      const entidadConvocante = proyecto.entidadesConvocantes && proyecto.entidadesConvocantes.length > 0 ? proyecto.entidadesConvocantes[i] : null;

      const maxNivelProgramaEntidadConvocanteX = Math.max(...proyectos.map(proyecto => {

        let nivelPrograma = 0;
        if (proyecto.entidadesConvocantes && proyecto.entidadesConvocantes.length > i) {

          nivelPrograma = this.getNivelPrograma(proyecto.entidadesConvocantes[i]?.programa ?? proyecto.entidadesConvocantes[i].programaConvocatoria);
        }

        return nivelPrograma;
      }));

      this.fillRowsEntidadExcel(elementsRow, entidadConvocante, maxNivelProgramaEntidadConvocanteX, reportConfig.reportOptions.showPlanesInvestigacion);
    }

    return elementsRow;
  }

  private fillRowsEntidadExcel(
    elementsRow: any[],
    entidadConvocante: IProyectoEntidadConvocante,
    maxNivelProgramaEntidadConvocante: number,
    showPlanesInvestigacion: boolean
  ) {
    if (entidadConvocante) {
      elementsRow.push(entidadConvocante.entidad?.nombre ?? '');
      elementsRow.push(entidadConvocante.entidad?.numeroIdentificacion ?? '');

      if (showPlanesInvestigacion) {
        const nombresNivelesPrograma = this.getNombresNivelesPrograma(entidadConvocante?.programa ?? entidadConvocante.programaConvocatoria);
        for (let i = 0; i <= maxNivelProgramaEntidadConvocante; i++) {
          const nivel = nombresNivelesPrograma.length > i ? nombresNivelesPrograma[i] : '';
          elementsRow.push(nivel);

          if (i === 0) {
            elementsRow.push(entidadConvocante.programaConvocatoria?.nombre ?? '');
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
