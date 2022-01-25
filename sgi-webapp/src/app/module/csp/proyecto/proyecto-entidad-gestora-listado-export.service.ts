import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoEntidadGestora } from '@core/models/csp/proyecto-entidad-gestora';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const ENTIDAD_GESTORA_KEY = 'csp.proyecto-entidad-gestora.entidad-gestora';
const ENTIDAD_GESTORA_NOMBRE_KEY = 'csp.proyecto-entidad-gestora.entidad-principal.nombre';
const ENTIDAD_GESTORA_CIF_KEY = marker('csp.proyecto-entidad-financiadora.cif');
const ENTIDAD_GESTORA_FIELD = 'entidadGestora';
const ENTIDAD_GESTORA_CIF_FIELD = 'cifEntidadGestora';

@Injectable()
export class ProyectoEntidadGestoraListadoExportService extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private empresaService: EmpresaService
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    return this.proyectoService.findEntidadGestora(proyectoData.id).pipe(
      switchMap(responseEntidadesGestoras => {
        if (responseEntidadesGestoras.total === 0) {
          return of(proyectoData);
        }
        const entidadGestora = responseEntidadesGestoras.items[0];
        proyectoData.entidadGestora = entidadGestora;
        return this.empresaService.findById(entidadGestora.empresa.id).pipe(
          map((result) => {
            proyectoData.entidadGestora.empresa = result;
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
      return this.getColumnsEntidadGestoraNotExcel();
    } else {
      return this.getColumnsEntidadGestoraExcel();
    }
  }

  private getColumnsEntidadGestoraNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: ENTIDAD_GESTORA_FIELD,
      title: this.translate.instant(ENTIDAD_GESTORA_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(ENTIDAD_GESTORA_KEY) +
      ' (' + this.translate.instant(ENTIDAD_GESTORA_NOMBRE_KEY) +
      ' - ' + this.translate.instant(ENTIDAD_GESTORA_CIF_KEY) + ')';
    const columnEntidad: ISgiColumnReport = {
      name: ENTIDAD_GESTORA_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsEntidadGestoraExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const titleEntidadGestora = this.translate.instant(ENTIDAD_GESTORA_KEY);

    const columnEntidadGestora: ISgiColumnReport = {
      name: ENTIDAD_GESTORA_FIELD,
      title: titleEntidadGestora + ': ' + this.translate.instant(ENTIDAD_GESTORA_NOMBRE_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnEntidadGestora);
    const columnCifEntidadGestora: ISgiColumnReport = {
      name: ENTIDAD_GESTORA_CIF_FIELD,
      title: titleEntidadGestora + ': ' + this.translate.instant(ENTIDAD_GESTORA_CIF_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnCifEntidadGestora);

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = proyectos[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsEntidadGestoraNotExcel(proyecto, elementsRow);
    } else {
      this.fillRowsEntidadExcel(elementsRow, proyecto.entidadGestora);
    }
    return elementsRow;
  }

  private fillRowsEntidadGestoraNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    if (proyecto.entidadGestora) {
      const entidadGestora = proyecto.entidadGestora;
      const entidadGestoraElementsRow: any[] = [];

      let entidadTable = entidadGestora.empresa?.nombre ?? '';
      entidadTable += '\n';
      entidadTable += entidadGestora.empresa?.numeroIdentificacion ?? '';

      entidadGestoraElementsRow.push(entidadTable);

      const rowReport: ISgiRowReport = {
        elements: entidadGestoraElementsRow
      };
      rowsReport.push(rowReport);
    }

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], entidadGestora: IProyectoEntidadGestora) {
    if (entidadGestora) {
      elementsRow.push(entidadGestora.empresa?.nombre ?? '');
      elementsRow.push(entidadGestora.empresa?.numeroIdentificacion ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
