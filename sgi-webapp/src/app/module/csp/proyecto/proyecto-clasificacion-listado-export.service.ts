import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoClasificacion } from '@core/models/csp/proyecto-clasificacion';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { ProyectoClasificacionListado } from './proyecto-formulario/proyecto-clasificaciones/proyecto-clasificaciones.fragment';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const CLASIFICACION_KEY = marker('csp.solicitud-proyecto-clasificacion');
const CLASIFICACION_FIELD = 'clasificacion';

@Injectable()
export class ProyectoClasificacionListadoExportService extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private clasificacionService: ClasificacionService
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    return this.proyectoService.findAllClasificacionesProyecto(proyectoData.id).pipe(
      map(response => response.items.map(proyectoClasificacion => {
        const clasificacionListado: ProyectoClasificacionListado = {
          id: proyectoClasificacion.id,
          proyectoId: proyectoClasificacion.proyectoId,
          clasificacion: undefined,
          nivelSeleccionado: proyectoClasificacion.clasificacion,
          niveles: undefined,
          nivelesTexto: ''
        };
        return clasificacionListado;
      })),
      switchMap((responseClasificacion) => {
        if (responseClasificacion.length === 0) {
          return of(responseClasificacion);
        }
        return from(responseClasificacion).pipe(
          mergeMap((proyectoClasificacion) => {
            return this.getClasificacion(proyectoClasificacion);
          }),
          map(() => responseClasificacion)
        );
      }),
      map(responseClasificacion => {
        proyectoData.clasificaciones = responseClasificacion;
        return proyectoData;
      }),
      takeLast(1)
    );
  }

  private getClasificacion(proyectoClasificacion: ProyectoClasificacionListado): Observable<IProyectoClasificacion> {
    return this.clasificacionService.findById(proyectoClasificacion.nivelSeleccionado.id).pipe(
      map((clasificacion) => {
        proyectoClasificacion.nivelSeleccionado = clasificacion;
        proyectoClasificacion.niveles = [clasificacion];
      }),
      switchMap(() => {
        return this.getNiveles(proyectoClasificacion);
      }),
      switchMap(() => {
        proyectoClasificacion.clasificacion =
          proyectoClasificacion.niveles[proyectoClasificacion.niveles.length - 1];

        proyectoClasificacion.nivelesTexto = proyectoClasificacion.niveles
          .slice(1, proyectoClasificacion.niveles.length - 1)
          .reverse()
          .map(clasificacion => clasificacion.nombre).join(' - ');
        return of(proyectoClasificacion);
      })
    );
  }

  private getNiveles(proyectoClasificacion: ProyectoClasificacionListado):
    Observable<ProyectoClasificacionListado> {
    const lastLevel = proyectoClasificacion.niveles[proyectoClasificacion.niveles.length - 1];
    if (!lastLevel.padreId) {
      return of(proyectoClasificacion);
    }

    return this.clasificacionService.findById(lastLevel.padreId).pipe(
      switchMap(clasificacion => {
        proyectoClasificacion.niveles.push(clasificacion);
        return this.getNiveles(proyectoClasificacion);
      })
    );
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsClasificacionNotExcel();
    } else {
      return this.getColumnsClasificacionExcel(proyectos);
    }
  }

  private getColumnsClasificacionNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: CLASIFICACION_FIELD,
      title: this.translate.instant(CLASIFICACION_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(CLASIFICACION_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);
    const columnEntidad: ISgiColumnReport = {
      name: CLASIFICACION_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsClasificacionExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumClasificaciones = Math.max(...proyectos.map(p => p.clasificaciones?.length));
    const titleClasificacion = this.translate.instant(CLASIFICACION_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);

    for (let i = 0; i < maxNumClasificaciones; i++) {
      const idClasificacion: string = String(i + 1);
      const columnClasificacion: ISgiColumnReport = {
        name: CLASIFICACION_FIELD + idClasificacion,
        title: titleClasificacion + idClasificacion,
        type: ColumnType.STRING,
      };
      columns.push(columnClasificacion);
    }

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {

    const proyecto = proyectos[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsClasificacionNotExcel(proyecto, elementsRow);
    } else {
      const maxNumClasificaciones = Math.max(...proyectos.map(p => p.clasificaciones?.length));
      for (let i = 0; i < maxNumClasificaciones; i++) {
        const clasificacion = proyecto.clasificaciones[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, clasificacion);
      }
    }
    return elementsRow;
  }

  private fillRowsClasificacionNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.clasificaciones?.forEach(proyectoClasificacion => {
      const clasificacionElementsRow: any[] = [];

      let clasificacionContent = proyectoClasificacion?.clasificacion?.nombre ?? '';
      clasificacionContent += ' - ';
      clasificacionContent += proyectoClasificacion?.nivelesTexto ?? '';
      clasificacionContent += ' - ';
      clasificacionContent += proyectoClasificacion?.nivelSeleccionado?.nombre ?? '';

      clasificacionElementsRow.push(clasificacionContent);

      const rowReport: ISgiRowReport = {
        elements: clasificacionElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], proyectoClasificacion: ProyectoClasificacionListado) {
    if (proyectoClasificacion) {
      let clasificacionContent = proyectoClasificacion.clasificacion?.nombre ?? '';
      clasificacionContent += '\n';
      clasificacionContent += proyectoClasificacion.nivelesTexto ?? '';
      clasificacionContent += '\n';
      clasificacionContent += proyectoClasificacion.nivelSeleccionado?.nombre ?? '';
      elementsRow.push(clasificacionContent);
    } else {
      elementsRow.push('');
    }
  }
}
