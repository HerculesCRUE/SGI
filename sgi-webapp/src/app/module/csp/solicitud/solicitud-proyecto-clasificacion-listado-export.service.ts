import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoClasificacion } from '@core/models/csp/solicitud-proyecto-clasificacion';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { SolicitudProyectoClasificacionListado } from './solicitud-formulario/solicitud-proyecto-clasificaciones/solicitud-proyecto-clasificaciones.fragment';
import { ISolicitudReportData, ISolicitudReportOptions } from './solicitud-listado-export.service';

const CLASIFICACION_KEY = marker('csp.solicitud-proyecto-clasificacion');
const CLASIFICACION_FIELD = 'clasificacion';
const PROYECTO_KEY = marker('menu.csp.solicitudes.datos-proyecto');

@Injectable()
export class SolicitudProyectoClasificacionListadoExportService extends AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
    private clasificacionService: ClasificacionService
  ) {
    super(translate);
  }

  public getData(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    return this.solicitudService.findAllClasificacionesSolicitud(solicitudData.id).pipe(
      map(response => response.items.map(solicitudProyectoClasificacion => {
        const clasificacionListado: SolicitudProyectoClasificacionListado = {
          id: solicitudProyectoClasificacion.id,
          solicitudProyectoId: solicitudProyectoClasificacion.solicitudProyectoId,
          clasificacion: undefined,
          nivelSeleccionado: solicitudProyectoClasificacion.clasificacion,
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
          mergeMap((solicitudProyectoClasificacion) => {
            return this.getClasificacion(solicitudProyectoClasificacion);
          }),
          map(() => responseClasificacion)
        );
      }),
      map(responseClasificacion => {
        solicitudData.clasificaciones = responseClasificacion;
        return solicitudData;
      }),
      takeLast(1)
    );
  }

  private getClasificacion(solicitudProyectoClasificacion: SolicitudProyectoClasificacionListado): Observable<ISolicitudProyectoClasificacion> {
    return this.clasificacionService.findById(solicitudProyectoClasificacion.nivelSeleccionado.id).pipe(
      map((clasificacion) => {
        solicitudProyectoClasificacion.nivelSeleccionado = clasificacion;
        solicitudProyectoClasificacion.niveles = [clasificacion];
      }),
      switchMap(() => {
        return this.getNiveles(solicitudProyectoClasificacion);
      }),
      switchMap(() => {
        solicitudProyectoClasificacion.clasificacion =
          solicitudProyectoClasificacion.niveles[solicitudProyectoClasificacion.niveles.length - 1];

        solicitudProyectoClasificacion.nivelesTexto = solicitudProyectoClasificacion.niveles
          .slice(1, solicitudProyectoClasificacion.niveles.length - 1)
          .reverse()
          .map(clasificacion => clasificacion.nombre).join(' - ');
        return of(solicitudProyectoClasificacion);
      })
    );
  }

  private getNiveles(solicitudProyectoClasificacion: SolicitudProyectoClasificacionListado):
    Observable<SolicitudProyectoClasificacionListado> {
    const lastLevel = solicitudProyectoClasificacion.niveles[solicitudProyectoClasificacion.niveles.length - 1];
    if (!lastLevel.padreId) {
      return of(solicitudProyectoClasificacion);
    }

    return this.clasificacionService.findById(lastLevel.padreId).pipe(
      switchMap(clasificacion => {
        solicitudProyectoClasificacion.niveles.push(clasificacion);
        return this.getNiveles(solicitudProyectoClasificacion);
      })
    );
  }

  public fillColumns(
    solicitudes: ISolicitudReportData[],
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsClasificacionNotExcel();
    } else {
      return this.getColumnsClasificacionExcel(solicitudes);
    }
  }

  private getColumnsClasificacionNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: CLASIFICACION_FIELD,
      title: this.translate.instant(CLASIFICACION_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(PROYECTO_KEY) + ': ' + this.translate.instant(CLASIFICACION_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);
    const columnEntidad: ISgiColumnReport = {
      name: CLASIFICACION_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsClasificacionExcel(solicitudes: ISolicitudReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumClasificaciones = Math.max(...solicitudes.map(s => s.clasificaciones ? s.clasificaciones?.length : 0));
    const titleClasificacion = this.translate.instant(PROYECTO_KEY) + ': ' + this.translate.instant(CLASIFICACION_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);

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

  public fillRows(solicitudes: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {

    const solicitud = solicitudes[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsClasificacionNotExcel(solicitud, elementsRow);
    } else {
      const maxNumClasificaciones = Math.max(...solicitudes.map(s => s.clasificaciones ? s.clasificaciones?.length : 0));
      for (let i = 0; i < maxNumClasificaciones; i++) {
        const clasificacion = solicitud.clasificaciones && solicitud.clasificaciones.length > 0 ? solicitud.clasificaciones[i] : null;
        this.fillRowsEntidadExcel(elementsRow, clasificacion);
      }
    }
    return elementsRow;
  }

  private fillRowsClasificacionNotExcel(solicitud: ISolicitudReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    solicitud.clasificaciones?.forEach(solicitudProyectoClasificacion => {
      const clasificacionElementsRow: any[] = [];

      let clasificacionContent = solicitudProyectoClasificacion?.clasificacion?.nombre ?? '';
      clasificacionContent += ' - ';
      clasificacionContent += solicitudProyectoClasificacion?.nivelesTexto ?? '';
      clasificacionContent += ' - ';
      clasificacionContent += solicitudProyectoClasificacion?.nivelSeleccionado?.nombre ?? '';

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

  private fillRowsEntidadExcel(elementsRow: any[], solicitudProyectoClasificacion: SolicitudProyectoClasificacionListado) {
    if (solicitudProyectoClasificacion) {
      let clasificacionContent = solicitudProyectoClasificacion.clasificacion?.nombre ?? '';
      clasificacionContent += '\n';
      clasificacionContent += solicitudProyectoClasificacion.nivelesTexto ?? '';
      clasificacionContent += '\n';
      clasificacionContent += solicitudProyectoClasificacion.nivelSeleccionado?.nombre ?? '';
      elementsRow.push(clasificacionContent);
    } else {
      elementsRow.push('');
    }
  }
}
