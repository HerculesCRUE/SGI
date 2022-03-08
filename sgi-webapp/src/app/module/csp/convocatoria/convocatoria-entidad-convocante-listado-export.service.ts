import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IPrograma } from '@core/models/csp/programa';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
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
import { MSG_PARAMS } from '@core/i18n';

const ENTIDAD_CONVOCANTE_KEY = marker('csp.convocatoria-entidad-convocante');
const ENTIDAD_CONVOCANTE_NOMBRE_KEY = marker('csp.convocatoria-entidad-convocante.nombre');
const ENTIDAD_CONVOCANTE_CIF_KEY = marker('csp.convocatoria-entidad-convocante.cif');
const ENTIDAD_CONVOCANTE_PLAN_KEY = marker('csp.convocatoria-entidad-convocante.plan');
const ENTIDAD_CONVOCANTE_MODALIDAD_KEY = marker('csp.convocatoria-entidad-convocante.modalidad');
const ENTIDAD_CONVOCANTE_FIELD = 'entidadConvocante';
const ENTIDAD_CONVOCANTE_CIF_FIELD = 'cifEntidadConvocante';
const ENTIDAD_CONVOCANTE_PLAN_FIELD = 'planEntidadConvocante';
const ENTIDAD_CONVOCANTE_MODALIDAD_FIELD = 'modalidadEntidadConvocante';

@Injectable()
export class ConvocatoriaEntidadConvocanteListadoExportService
  extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

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
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsEntidadConvocanteNotExcel();
    } else {
      return this.getColumnsEntidadConvocanteExcel(convocatorias);
    }
  }

  private getColumnsEntidadConvocanteNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: ENTIDAD_CONVOCANTE_FIELD,
      title: this.translate.instant(ENTIDAD_CONVOCANTE_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(ENTIDAD_CONVOCANTE_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) +
      ' (' + this.translate.instant(ENTIDAD_CONVOCANTE_NOMBRE_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_CONVOCANTE_CIF_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_CONVOCANTE_PLAN_KEY) +
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

  private getColumnsEntidadConvocanteExcel(convocatorias: IConvocatoriaReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumEntidasConvocantes = Math.max(...convocatorias.map(c => c.entidadesConvocantes ? c.entidadesConvocantes?.length : 0));
    const titleEntidadConvocante = this.translate.instant(ENTIDAD_CONVOCANTE_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);
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

      const columnModalidadEntidadConvocante: ISgiColumnReport = {
        name: ENTIDAD_CONVOCANTE_MODALIDAD_FIELD + idEntidadConvocante,
        title: titleEntidadConvocante + idEntidadConvocante + ': ' + this.translate.instant(ENTIDAD_CONVOCANTE_MODALIDAD_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnModalidadEntidadConvocante);
    }

    return columns;
  }

  public fillRows(convocatorias: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = convocatorias[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsEntidadConvocanteNotExcel(convocatoria, elementsRow);
    } else {

      const maxNumEntidasConvocantes = Math.max(...convocatorias.map(c => c.entidadesConvocantes ? c.entidadesConvocantes?.length : 0));
      for (let i = 0; i < maxNumEntidasConvocantes; i++) {
        const entidadConvocante = convocatoria.entidadesConvocantes ? convocatoria.entidadesConvocantes[i] ?? null : null;
        this.fillRowsEntidadExcel(elementsRow, entidadConvocante);
      }
    }
    return elementsRow;
  }

  private fillRowsEntidadConvocanteNotExcel(convocatoria: IConvocatoriaReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    convocatoria.entidadesConvocantes?.forEach(entidadConvocante => {
      const entidadConvocanteElementsRow: any[] = [];

      const modalidad = entidadConvocante.programa;
      const programa = this.getSecondLevelPrograma(modalidad);
      const plan = programa?.padre ? programa.padre : modalidad;

      let entidadTable = entidadConvocante.entidad?.nombre ?? '';
      entidadTable += '\n';
      entidadTable += entidadConvocante.entidad?.numeroIdentificacion ?? '';
      entidadTable += '\n';
      entidadTable += plan ? plan.nombre ?? '' : '';
      entidadTable += '\n';
      entidadTable += modalidad ? modalidad.nombre ?? '' : '';

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

  private fillRowsEntidadExcel(elementsRow: any[], entidadConvocante: IConvocatoriaEntidadConvocante) {
    if (entidadConvocante) {
      const modalidad = entidadConvocante.programa;
      const programa = this.getSecondLevelPrograma(modalidad);
      const plan = programa?.padre ? programa.padre : modalidad;
      elementsRow.push(entidadConvocante.entidad?.nombre ?? '');
      elementsRow.push(entidadConvocante.entidad?.numeroIdentificacion ?? '');
      elementsRow.push(plan ? plan.nombre ?? '' : '');
      elementsRow.push(modalidad ? modalidad.nombre ?? '' : '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private getSecondLevelPrograma(programa: IPrograma): IPrograma {
    if (programa?.padre?.padre) {
      return this.getSecondLevelPrograma(programa.padre);
    }
    return programa;
  }
}
