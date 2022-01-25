import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IPrograma } from '@core/models/csp/programa';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
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
const ENTIDAD_CONVOCANTE_PROGRAMA_KEY = marker('csp.convocatoria-entidad-convocante.programa');
const ENTIDAD_CONVOCANTE_PROGRAMA_CONVOCATORIA_KEY = marker('csp.proyecto-entidad-convocante.programa.programa-convocatoria');
const ENTIDAD_CONVOCANTE_MODALIDAD_KEY = marker('csp.proyecto-entidad-convocante.programa.programa');
const ENTIDAD_CONVOCANTE_FIELD = 'entidadConvocante';
const ENTIDAD_CONVOCANTE_CIF_FIELD = 'cifEntidadConvocante';
const ENTIDAD_CONVOCANTE_PROGRAMA_FIELD = 'programaEntidadConvocante';
const ENTIDAD_CONVOCANTE_PLAN_FIELD = 'planEntidadConvocante';
const ENTIDAD_CONVOCANTE_MODALIDAD_FIELD = 'modalidadEntidadConvocante';

@Injectable()
export class ProyectoEntidadConvocanteListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

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
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsEntidadConvocanteNotExcel();
    } else {
      return this.getColumnsEntidadConvocanteExcel(proyectos);
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
      ' - ' + this.translate.instant(ENTIDAD_CONVOCANTE_CIF_KEY) +
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

  private getColumnsEntidadConvocanteExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEntidasConvocantes = Math.max(...proyectos.map(p => p.entidadesConvocantes?.length));
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
        name: ENTIDAD_CONVOCANTE_CIF_FIELD + idEntidadConvocante,
        title: titleEntidadConvocante + idEntidadConvocante + ': ' + this.translate.instant(ENTIDAD_CONVOCANTE_CIF_KEY),
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
        title: titlePlanPlusEntidadConvocante + this.translate.instant(ENTIDAD_CONVOCANTE_PROGRAMA_CONVOCATORIA_KEY),
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

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = proyectos[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsEntidadConvocanteNotExcel(proyecto, elementsRow);
    } else {

      const maxNumEntidasConvocantes = Math.max(...proyectos.map(p => p.entidadesConvocantes?.length));
      for (let i = 0; i < maxNumEntidasConvocantes; i++) {
        const entidadConvocante = proyecto.entidadesConvocantes[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, entidadConvocante);
      }
    }
    return elementsRow;
  }

  private fillRowsEntidadConvocanteNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.entidadesConvocantes?.forEach(entidadConvocante => {
      const entidadConvocanteElementsRow: any[] = [];

      let entidadTable = entidadConvocante.entidad?.nombre ?? '';
      entidadTable += '\n';
      entidadTable += entidadConvocante.entidad?.numeroIdentificacion ?? '';
      entidadTable += '\n';
      entidadTable += this.getPlan(entidadConvocante)?.nombre ?? '';
      entidadTable += '\n';
      entidadTable += entidadConvocante.programaConvocatoria?.nombre ?? '';
      entidadTable += '\n';
      entidadTable += entidadConvocante.programa?.nombre ?? '';

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

  private fillRowsEntidadExcel(elementsRow: any[], entidadConvocante: IProyectoEntidadConvocante) {
    if (entidadConvocante) {
      elementsRow.push(entidadConvocante.entidad?.nombre ?? '');
      elementsRow.push(entidadConvocante.entidad?.numeroIdentificacion ?? '');
      elementsRow.push(this.getPlan(entidadConvocante)?.nombre ?? '');
      elementsRow.push(entidadConvocante?.programaConvocatoria?.nombre ?? '');
      elementsRow.push(entidadConvocante?.programa?.nombre ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private getPlan(value: IProyectoEntidadConvocante): IPrograma {
    if (value.programa != null) {
      return this.getTopLevel(value.programa);
    }
    if (value.programaConvocatoria != null) {
      return this.getTopLevel(value.programaConvocatoria);
    }
    return null;
  }

  private getTopLevel(programa: IPrograma): IPrograma {
    if (programa.padre == null) {
      return programa;
    }
    return this.getTopLevel(programa.padre);
  }
}
