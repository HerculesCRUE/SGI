import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-solicitud';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { TypeColumnReportEnum } from '@core/models/rep/type-column-report-enum';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, zip } from 'rxjs';
import { map, switchMap, takeLast } from 'rxjs/operators';
import { ISolicitudListado } from './solicitud-listado/solicitud-listado.component';


interface ISolicitudReportData extends ISolicitudListado {
  proyecto: ISolicitudProyecto;
}

const CODIGO_INTERNO_KEY = marker('csp.solicitud.codigo');
const CODIGO_EXTERNO_KEY = marker('csp.solicitud.codigo-externo');
const REFERENCIA_KEY = marker('csp.solicitud.referencia-convocatoria');
const SOLICITANTE_KEY = marker('csp.solicitud.solicitante');
const ESTADO_KEY = marker('csp.solicitud.estado');
const TITULO_KEY = marker('csp.solicitud.titulo-listado');
const FECHA_ESTADO_KEY = marker('csp.solicitud.estado-solicitud.fecha');

@Injectable({
  providedIn: 'root',
})
export class SolicitudListadoService extends AbstractTableExportService<ISolicitudReportData, IReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
    protected reportService: ReportService,
    private convocatoriaService: ConvocatoriaService,
    private personaService: PersonaService
  ) {
    super(reportService);
  }

  protected getRows(solicitudes: ISolicitudReportData[], reportConfig: IReportConfig<IReportOptions>): Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    solicitudes.forEach(solicitud => {
      requestsRow.push(this.getRowsInner(solicitud, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(solicitud: ISolicitudReportData, reportConfig: IReportConfig<IReportOptions>): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {
        row.elements.push(solicitud.codigoRegistroInterno);
        row.elements.push(solicitud.codigoExterno);
        row.elements.push(solicitud.convocatoria ? solicitud.convocatoria.codigo : solicitud.convocatoriaExterna);
        row.elements.push(solicitud.solicitante?.nombre + ' ' + solicitud.solicitante?.apellidos);
        return row;
      }),
      switchMap((row) => {
        return this.getEstadoByMap(solicitud.estado?.estado).pipe(
          map(estadoTranslate => {
            row.elements.push(estadoTranslate);
            return row;
          })
        );
      }),
      map((row) => {
        row.elements.push(solicitud.titulo);
        row.elements.push(LuxonUtils.toBackend(solicitud.estado?.fechaEstado));
        return row;
      }),
    );
  }

  private getEstadoByMap(estado: Estado): Observable<string> {
    return this.translate.get(ESTADO_MAP.get(estado));
  }

  protected getDataReport(reportConfig: IReportConfig<IReportOptions>): Observable<ISolicitudReportData[]> {
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    return this.solicitudService.findAllTodos(findOptions).pipe(
      map((solicitudes) => {
        return solicitudes.items.map((solicitud) => {
          return solicitud as ISolicitudReportData;
        });
      }),
      switchMap((solicitudesReportData) => {
        if (solicitudesReportData.length === 0) {
          return of(solicitudesReportData);
        }

        const requestsSolicitud: Observable<ISolicitudReportData>[] = [];

        solicitudesReportData.forEach(solicitud => {
          requestsSolicitud.push(this.getDataReportInner(solicitud, reportConfig.reportOptions));
        });
        return zip(...requestsSolicitud);
      }),
      takeLast(1)
    );
  }

  private getDataReportInner(solicitudListado: ISolicitudReportData, reportOptions: IReportOptions): Observable<ISolicitudReportData> {
    return of(solicitudListado).pipe(
      switchMap((solicitud) => {
        if (solicitud.convocatoriaId) {

          return this.convocatoriaService.findById(solicitud.convocatoriaId).pipe(
            map(convocatoria => {
              solicitud.convocatoria = convocatoria;
              return solicitud;
            }));
        } else {
          return of(solicitud);
        }
      }),
      switchMap((solicitud) => {
        return this.personaService.findById(solicitud.solicitante.id).pipe(
          map(persona => {
            solicitud.solicitante = persona;
            return solicitud;
          })
        );
      })
    );
  }

  protected getColumns(resultados: ISolicitudReportData[], reportConfig: IReportConfig<IReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(CODIGO_INTERNO_KEY),
        name: 'codigoRegistroInterno',
        type: TypeColumnReportEnum.STRING,
      },
      {
        title: this.translate.instant(CODIGO_EXTERNO_KEY),
        name: 'codigoExterno',
        type: TypeColumnReportEnum.STRING
      },
      {
        title: this.translate.instant(REFERENCIA_KEY),
        name: 'referencia',
        type: TypeColumnReportEnum.STRING
      },
      {
        title: this.translate.instant(SOLICITANTE_KEY),
        name: 'solicitante',
        type: TypeColumnReportEnum.STRING
      },
      {
        title: this.translate.instant(ESTADO_KEY),
        name: 'estado',
        type: TypeColumnReportEnum.STRING
      },
      {
        title: this.translate.instant(TITULO_KEY),
        name: 'titulo',
        type: TypeColumnReportEnum.STRING
      },
      {
        title: this.translate.instant(FECHA_ESTADO_KEY),
        name: 'fechaEstado',
        type: TypeColumnReportEnum.DATE
      }
    ];
    return of(columns);
  }

  protected getGroupBy(): ISgiGroupReport {
    const groupBy: ISgiGroupReport = {
      name: 'titulo',
      visible: true
    };
    return groupBy;
  }
}
