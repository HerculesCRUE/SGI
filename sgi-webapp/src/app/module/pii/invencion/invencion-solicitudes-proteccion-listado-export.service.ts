import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP } from '@core/models/pii/solicitud-proteccion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IPais } from '@core/models/sgo/pais';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap } from 'rxjs/operators';
import { IInvencionReportData, IInvencionReportOptions, ISolicitudProteccionReport } from './invencion-listado-export.service';

const SOLICITUD_PROTECCION_ESTADO_FIELD = 'estado';
const SOLICITUD_PROTECCION_FECHA_PRIORIDAD_FIELD = 'fechaPrioridad';
const SOLICITUD_PROTECCION_VIA_PROTECCION_FIELD = 'viaProteccion';
const SOLICITUD_PROTECCION_PAIS_FIELD = 'pais';
const SOLICITUD_PROTECCION_NUMERO_SOLICITUD_FIELD = 'numSolicitud';
const SOLICITUD_PROTECCION_PRIORITARIA_FIELD = 'prioritaria';
const SOLICITUD_PROTECCION_FECHA_FIN_PRIORIDAD_FIELD = 'fechaFinPrioridad';
const SOLICITUD_PROTECCION_FECHA_CONCECION_FIELD = 'fechaConcecion';
const SOLICITUD_PROTECCION_NUMERO_CONCESION_FIELD = 'numConcesion';
const SOLICITUD_PROTECCION_NUMERO_REGISTRO_FIELD = 'numRegistro';

const SOLICITUD_PROTECCION_KEY = marker('pii.solicitud-proteccion');
const SOLICITUD_PROTECCION_ESTADO_KEY = marker('pii.solicitud-proteccion.estado');
const SOLICITUD_PROTECCION_FECHA_PRIORIDAD_KEY = marker('pii.solicitud-proteccion.fecha-solicitud');
const SOLICITUD_PROTECCION_VIA_PROTECCION_KEY = marker('pii.solicitud-proteccion.via-proteccion');
const SOLICITUD_PROTECCION_PAIS_KEY = marker('pii.solicitud-proteccion.pais');
const SOLICITUD_PROTECCION_NUMERO_SOLICITUD_KEY = marker('pii.solicitud-proteccion.numero-solicitud');
const SOLICITUD_PROTECCION_PRIORITARIA_KEY = marker('pii.solicitud-proteccion.prioritaria');
const SOLICITUD_PROTECCION_FECHA_FIN_PRIORIDAD_KEY = marker('pii.solicitud-proteccion.fecha-fin-prioridad-pct');
const SOLICITUD_PROTECCION_FECHA_CONCECION_KEY = marker('pii.solicitud-proteccion.fecha-concesion-form');
const SOLICITUD_PROTECCION_NUMERO_CONCESION_KEY = marker('pii.solicitud-proteccion.numero-concesion-form');
const SOLICITUD_PROTECCION_NUMERO_REGISTRO_KEY = marker('pii.solicitud-proteccion.numero-registro-form');

@Injectable()
export class InvencionSolicitudesProteccionListadoExportService extends
  AbstractTableExportFillService<IInvencionReportData, IInvencionReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly invencionService: InvencionService,
    private readonly paisService: PaisService
  ) {
    super(translate);
  }

  public getData(invencionData: IInvencionReportData): Observable<IInvencionReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.invencionService.findAllSolicitudesProteccion(invencionData.id, findOptions).pipe(
      switchMap(responseSolicitudes => {
        if (responseSolicitudes.total === 0) {
          return of(invencionData);
        }
        invencionData.solicitudesDeProteccion = responseSolicitudes.items;
        return of(invencionData);

      }), switchMap((data: IInvencionReportData) => {
        if (!data.solicitudesDeProteccion) {
          return of(data);
        }
        return from(data.solicitudesDeProteccion).pipe(
          mergeMap(solicitud => {
            if (!solicitud.paisProteccion?.id) {
              return of(data);
            }
            return this.paisService.findById(solicitud.paisProteccion.id).pipe(
              map((paisResponse: IPais) => {
                solicitud.pais = paisResponse;
                return data;
              }));
          }, this.DEFAULT_CONCURRENT)
        );
      })
    );
  }

  public fillColumns(
    invenciones: IInvencionReportData[],
    reportConfig: IReportConfig<IInvencionReportOptions>
  ): ISgiColumnReport[] {

    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsNotExcel();
    } else {
      return this.getColumnsExcel(invenciones);
    }
  }

  private getColumnsNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    return [];
  }

  private getColumnsExcel(invenciones: IInvencionReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumSolicitudes = Math.max(...invenciones.map(invencion => invencion.solicitudesDeProteccion ?
      invencion.solicitudesDeProteccion?.length : 0));

    const titleSolicitud = this.translate.instant(SOLICITUD_PROTECCION_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);
    for (let i = 0; i < maxNumSolicitudes; i++) {
      const idSolicitud: string = String(i + 1);

      const columnFechaPrioridad: ISgiColumnReport = {
        name: SOLICITUD_PROTECCION_FECHA_PRIORIDAD_FIELD + idSolicitud,
        title: titleSolicitud + idSolicitud + ': ' + this.translate.instant(SOLICITUD_PROTECCION_FECHA_PRIORIDAD_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaPrioridad);

      const columnViaProteccion: ISgiColumnReport = {
        name: SOLICITUD_PROTECCION_VIA_PROTECCION_FIELD + idSolicitud,
        title: titleSolicitud + idSolicitud + ': ' + this.translate.instant(SOLICITUD_PROTECCION_VIA_PROTECCION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnViaProteccion);

      const columnPais: ISgiColumnReport = {
        name: SOLICITUD_PROTECCION_PAIS_FIELD + idSolicitud,
        title: titleSolicitud + idSolicitud + ': ' + this.translate.instant(SOLICITUD_PROTECCION_PAIS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnPais);

      const columnNumSolicitud: ISgiColumnReport = {
        name: SOLICITUD_PROTECCION_NUMERO_SOLICITUD_FIELD + idSolicitud,
        title: titleSolicitud + idSolicitud + ': ' + this.translate.instant(SOLICITUD_PROTECCION_NUMERO_SOLICITUD_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNumSolicitud);

      const columnEstado: ISgiColumnReport = {
        name: SOLICITUD_PROTECCION_ESTADO_FIELD + idSolicitud,
        title: titleSolicitud + idSolicitud + ': ' + this.translate.instant(SOLICITUD_PROTECCION_ESTADO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEstado);

      const columnPrioritaria: ISgiColumnReport = {
        name: SOLICITUD_PROTECCION_PRIORITARIA_FIELD + idSolicitud,
        title: titleSolicitud + idSolicitud + ': ' + this.translate.instant(SOLICITUD_PROTECCION_PRIORITARIA_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnPrioritaria);

      const columnFechaFinPriorPresFasNacRec: ISgiColumnReport = {
        name: SOLICITUD_PROTECCION_FECHA_FIN_PRIORIDAD_FIELD + idSolicitud,
        title: titleSolicitud + idSolicitud + ': ' + this.translate.instant(SOLICITUD_PROTECCION_FECHA_FIN_PRIORIDAD_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinPriorPresFasNacRec);

      const columnFechaConcesion: ISgiColumnReport = {
        name: SOLICITUD_PROTECCION_FECHA_CONCECION_FIELD + idSolicitud,
        title: titleSolicitud + idSolicitud + ': ' + this.translate.instant(SOLICITUD_PROTECCION_FECHA_CONCECION_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaConcesion);

      const columnNumConcesion: ISgiColumnReport = {
        name: SOLICITUD_PROTECCION_NUMERO_CONCESION_FIELD + idSolicitud,
        title: titleSolicitud + idSolicitud + ': ' + this.translate.instant(SOLICITUD_PROTECCION_NUMERO_CONCESION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNumConcesion);

      const columnNumRegistro: ISgiColumnReport = {
        name: SOLICITUD_PROTECCION_NUMERO_REGISTRO_FIELD + idSolicitud,
        title: titleSolicitud + idSolicitud + ': ' + this.translate.instant(SOLICITUD_PROTECCION_NUMERO_REGISTRO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNumRegistro);
    }

    return columns;
  }

  public fillRows(invenciones: IInvencionReportData[], index: number, reportConfig: IReportConfig<IInvencionReportOptions>): any[] {

    const invencion = invenciones[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumSocio = Math.max(...invenciones.map(invencionItem => invencionItem.solicitudesDeProteccion ?
        invencionItem.solicitudesDeProteccion?.length : 0));
      for (let i = 0; i < maxNumSocio; i++) {
        const solicitud = invencion.solicitudesDeProteccion ? invencion.solicitudesDeProteccion[i] ?? null : null;
        this.fillRowsExcel(elementsRow, solicitud);
      }
    }
    return elementsRow;
  }

  private fillRowsExcel(elementsRow: any[], solicitud: ISolicitudProteccionReport) {
    if (solicitud) {
      elementsRow.push(LuxonUtils.toBackend(solicitud.fechaPrioridadSolicitud) ?? '');
      elementsRow.push(solicitud.viaProteccion?.nombre ?? '');
      elementsRow.push(solicitud.pais?.nombre ?? '');
      elementsRow.push(solicitud.numeroSolicitud ? solicitud.numeroSolicitud.toString() : '');
      elementsRow.push(this.translate.instant(ESTADO_MAP.get(solicitud.estado)) ?? '');
      elementsRow.push(this.notIsNullAndNotUndefined(solicitud.fechaFinPriorPresFasNacRec) ? 'S' : 'N');
      elementsRow.push(LuxonUtils.toBackend(solicitud.fechaFinPriorPresFasNacRec) ?? '');
      elementsRow.push(LuxonUtils.toBackend(solicitud.fechaConcesion) ?? '');
      elementsRow.push(solicitud.numeroConcesion ?? '');
      elementsRow.push(solicitud.numeroRegistro ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
