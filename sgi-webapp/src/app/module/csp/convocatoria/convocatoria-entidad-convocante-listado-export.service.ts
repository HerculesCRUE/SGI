import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IPrograma } from '@core/models/csp/programa';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

const ENTIDAD_CONVOCANTE_KEY = marker('csp.convocatoria-entidad-convocante');
const ENTIDAD_CONVOCANTE_NOMBRE_KEY = marker('csp.convocatoria-entidad-convocante.nombre');
const ENTIDAD_CONVOCANTE_CIF_KEY = marker('csp.convocatoria-entidad-convocante.cif');
const ENTIDAD_CONVOCANTE_PLAN_KEY = marker('csp.convocatoria-entidad-convocante.plan');
const ENTIDAD_CONVOCANTE_PROGRAMA_MODALIDAD_NIVEL_KEY = marker('csp.convocatoria-entidad-convocante.programa-modalidad-nivel');
const ENTIDAD_CONVOCANTE_FIELD = 'entidadConvocante';
const ENTIDAD_CONVOCANTE_CIF_FIELD = 'cifEntidadConvocante';
const ENTIDAD_CONVOCANTE_PLAN_FIELD = 'planEntidadConvocante';
const ENTIDAD_CONVOCANTE_NIVEL_PROGRAMA_FIELD = 'nivelProgramaEntidadConvocante';

@Injectable()
export class ConvocatoriaEntidadConvocanteListadoExportService
  extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly convocatoriaService: ConvocatoriaService,
    private empresaService: EmpresaService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.convocatoriaService.findAllConvocatoriaEntidadConvocantes(convocatoriaData?.convocatoria.id, findOptions).pipe(
      map((responseEntidadesConvocantes) => {
        convocatoriaData.entidadesConvocantes = [];
        return responseEntidadesConvocantes;
      }),
      switchMap(responseEntidadesConvocantes => {
        if (responseEntidadesConvocantes.total === 0) {
          return of(convocatoriaData);
        }
        const entidadesConvocantes = responseEntidadesConvocantes.items;

        const entidadesConvocantesIds = new Set<string>(responseEntidadesConvocantes.items.map(
          (entidadConvocante) => entidadConvocante.entidad.id)
        );
        return this.empresaService.findAllByIdIn([...entidadesConvocantesIds]).pipe(
          map((result) => {
            const entidades = result.items;

            entidadesConvocantes.forEach((entidadConvocatoria) => {
              entidadConvocatoria.entidad = entidades.find((entidad) => entidadConvocatoria.entidad.id === entidad.id);
            });

            convocatoriaData.entidadesConvocantes = entidadesConvocantes;
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
    return this.getColumnsEntidadConvocanteExcel(convocatorias, reportConfig.reportOptions.showPlanesInvestigacion);
  }

  private getColumnsEntidadConvocanteExcel(convocatorias: IConvocatoriaReportData[], showPlanesInvestigacion: boolean): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEntidasConvocantes = Math.max(...convocatorias.map(c => c.entidadesConvocantes ? c.entidadesConvocantes?.length : 0));
    const titleEntidadConvocante = this.translate.instant(ENTIDAD_CONVOCANTE_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);
    const titlePlanEntidadConvocante = this.translate.instant(ENTIDAD_CONVOCANTE_PLAN_KEY);
    const titleNivelPrograma = this.translate.instant(ENTIDAD_CONVOCANTE_PROGRAMA_MODALIDAD_NIVEL_KEY);

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
        const maxNivelProgramaEntidadConvocanteX = Math.max(...convocatorias.map(convocatoria => {
          return (convocatoria.entidadesConvocantes && convocatoria.entidadesConvocantes.length > i) ? this.getNivelPrograma(convocatoria.entidadesConvocantes[i].programa) : 0;
        }));

        for (let j = 0; j <= maxNivelProgramaEntidadConvocanteX; j++) {
          if (j === 0) {
            const columnPlanEntidadConvocante: ISgiColumnReport = {
              name: ENTIDAD_CONVOCANTE_PLAN_FIELD + idEntidadConvocante,
              title: titleEntidadConvocante + ' ' + idEntidadConvocante + ': ' + titlePlanEntidadConvocante,
              type: ColumnType.STRING,
            };
            columns.push(columnPlanEntidadConvocante);
          } else {
            const columnProgramaEntidadConvocante: ISgiColumnReport = {
              name: ENTIDAD_CONVOCANTE_NIVEL_PROGRAMA_FIELD + idEntidadConvocante + '_' + j,
              title: titleEntidadConvocante + ' ' + idEntidadConvocante + ': ' + titleNivelPrograma + ' ' + j,
              type: ColumnType.STRING,
            };
            columns.push(columnProgramaEntidadConvocante);
          }
        }
      }
    }

    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    const maxNumEntidasConvocantes = Math.max(...convocatorias.map(c => c.entidadesConvocantes ? c.entidadesConvocantes?.length : 0));

    for (let i = 0; i < maxNumEntidasConvocantes; i++) {
      const entidadConvocante = convocatoria.entidadesConvocantes ? convocatoria.entidadesConvocantes[i] ?? null : null;

      const maxNivelProgramaEntidadConvocanteX = Math.max(...convocatorias.map(convocatoria => {
        return (convocatoria.entidadesConvocantes && convocatoria.entidadesConvocantes.length > i) ? this.getNivelPrograma(convocatoria.entidadesConvocantes[i].programa) : 0;
      }));

      this.fillRowsEntidadExcel(elementsRow, entidadConvocante, maxNivelProgramaEntidadConvocanteX, reportConfig.reportOptions.showPlanesInvestigacion);
    }

    return elementsRow;
  }

  private fillRowsEntidadExcel(
    elementsRow: any[],
    entidadConvocante: IConvocatoriaEntidadConvocante,
    maxNivelProgramaEntidadConvocante: number,
    showPlanesInvestigacion: boolean
  ) {
    if (entidadConvocante) {
      elementsRow.push(entidadConvocante.entidad?.nombre ?? '');
      elementsRow.push(entidadConvocante.entidad?.numeroIdentificacion ?? '');

      if (showPlanesInvestigacion) {
        const nombresNivelesPrograma = this.getNombresNivelesPrograma(entidadConvocante.programa);
        for (let i = 0; i <= maxNivelProgramaEntidadConvocante; i++) {
          const nivel = nombresNivelesPrograma.length > i ? nombresNivelesPrograma[i] : '';
          elementsRow.push(nivel);
        }
      }
    } else {
      elementsRow.push('');
      elementsRow.push('');

      if (showPlanesInvestigacion) {
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
