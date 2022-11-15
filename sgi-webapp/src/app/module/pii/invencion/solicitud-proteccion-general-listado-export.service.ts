import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ESTADO_MAP } from '@core/models/pii/solicitud-proteccion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IPais } from '@core/models/sgo/pais';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ISolicitudProteccionReportData, ISolicitudProteccionReportOptions } from './solicitud-proteccion-listado-export.service';

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
const SOLICITUD_PROTECCION_TITULO_INVENCION_FIELD = 'tituloInvencion';
const SOLICITUD_PROTECCION_TIPO_PROTECCION_FIELD = 'tipoProteccion';

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
const SOLICITUD_PROTECCION_TITULO_INVENCION_KEY = marker('pii.solicitud-proteccion.titulo-invencion');
const SOLICITUD_PROTECCION_TIPO_PROTECCION_KEY = marker('pii.solicitud-proteccion.tipo-proteccion');
@Injectable()
export class SolicitudProteccionGeneralListadoExportService extends
  AbstractTableExportFillService<ISolicitudProteccionReportData, IReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly invencionService: InvencionService,
    private readonly personaService: PersonaService,
    private readonly vinculacionService: VinculacionService,
    private readonly paisService: PaisService
  ) {
    super(translate);
  }

  public getData(solicitudData: ISolicitudProteccionReportData): Observable<ISolicitudProteccionReportData> {
    return of(solicitudData).pipe(
      switchMap(data => {
        if (!data.paisProteccion?.id) {
          return of(data);
        }
        return this.paisService.findById(solicitudData.paisProteccion.id).pipe(
          map((paisResponse: IPais) => {
            solicitudData.pais = paisResponse;
            return data;
          }));
      })
    );
  }


  public fillColumns(
    solicitudes: ISolicitudProteccionReportData[],
    reportConfig: IReportConfig<ISolicitudProteccionReportOptions>
  ): ISgiColumnReport[] {

    const columns: ISgiColumnReport[] = [];
    const columnFechaPrioridad: ISgiColumnReport = {
      name: SOLICITUD_PROTECCION_FECHA_PRIORIDAD_FIELD,
      title: this.translate.instant(SOLICITUD_PROTECCION_FECHA_PRIORIDAD_KEY),
      type: ColumnType.DATE,
    };
    columns.push(columnFechaPrioridad);

    const columnViaProteccion: ISgiColumnReport = {
      name: SOLICITUD_PROTECCION_VIA_PROTECCION_FIELD,
      title: this.translate.instant(SOLICITUD_PROTECCION_VIA_PROTECCION_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnViaProteccion);

    const columnPais: ISgiColumnReport = {
      name: SOLICITUD_PROTECCION_PAIS_FIELD,
      title: this.translate.instant(SOLICITUD_PROTECCION_PAIS_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnPais);

    const columnNumSolicitud: ISgiColumnReport = {
      name: SOLICITUD_PROTECCION_NUMERO_SOLICITUD_FIELD,
      title: this.translate.instant(SOLICITUD_PROTECCION_NUMERO_SOLICITUD_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnNumSolicitud);

    const columnTituloInvencion: ISgiColumnReport = {
      name: SOLICITUD_PROTECCION_TITULO_INVENCION_FIELD,
      title: this.translate.instant(SOLICITUD_PROTECCION_TITULO_INVENCION_KEY),
      type: ColumnType.STRING
    };
    columns.push(columnTituloInvencion);

    const columnTipoProteccion: ISgiColumnReport = {
      name: SOLICITUD_PROTECCION_TIPO_PROTECCION_FIELD,
      title: this.translate.instant(SOLICITUD_PROTECCION_TIPO_PROTECCION_KEY),
      type: ColumnType.STRING
    };
    columns.push(columnTipoProteccion);

    const columnEstado: ISgiColumnReport = {
      name: SOLICITUD_PROTECCION_ESTADO_FIELD,
      title: this.translate.instant(SOLICITUD_PROTECCION_ESTADO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnEstado);

    const columnPrioritaria: ISgiColumnReport = {
      name: SOLICITUD_PROTECCION_PRIORITARIA_FIELD,
      title: this.translate.instant(SOLICITUD_PROTECCION_PRIORITARIA_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnPrioritaria);

    const columnFechaFinPriorPresFasNacRec: ISgiColumnReport = {
      name: SOLICITUD_PROTECCION_FECHA_FIN_PRIORIDAD_FIELD,
      title: this.translate.instant(SOLICITUD_PROTECCION_FECHA_FIN_PRIORIDAD_KEY),
      type: ColumnType.DATE,
    };
    columns.push(columnFechaFinPriorPresFasNacRec);

    const columnFechaConcesion: ISgiColumnReport = {
      name: SOLICITUD_PROTECCION_FECHA_CONCECION_FIELD,
      title: this.translate.instant(SOLICITUD_PROTECCION_FECHA_CONCECION_KEY),
      type: ColumnType.DATE,
    };
    columns.push(columnFechaConcesion);

    const columnNumConcesion: ISgiColumnReport = {
      name: SOLICITUD_PROTECCION_NUMERO_CONCESION_FIELD,
      title: this.translate.instant(SOLICITUD_PROTECCION_NUMERO_CONCESION_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnNumConcesion);

    const columnNumRegistro: ISgiColumnReport = {
      name: SOLICITUD_PROTECCION_NUMERO_REGISTRO_FIELD,
      title: this.translate.instant(SOLICITUD_PROTECCION_NUMERO_REGISTRO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnNumRegistro);

    return columns;

  }

  public fillRows(solicitudes: ISolicitudProteccionReportData[],
    index: number, reportConfig: IReportConfig<ISolicitudProteccionReportOptions>): any[] {

    const solicitud = solicitudes[index];

    return [
      LuxonUtils.toBackend(solicitud.fechaPrioridadSolicitud) ?? '',
      solicitud.viaProteccion?.nombre ?? '',
      solicitud.pais?.nombre ?? '',
      solicitud.numeroSolicitud ? solicitud.numeroSolicitud.toString() : '',
      solicitud.invencion.titulo ?? '',
      solicitud.invencion.tipoProteccion.nombre ?? '',
      this.translate.instant(ESTADO_MAP.get(solicitud.estado)) ?? '',
      this.notIsNullAndNotUndefined(solicitud.fechaFinPriorPresFasNacRec) ? 'S' : 'N',
      LuxonUtils.toBackend(solicitud.fechaFinPriorPresFasNacRec) ?? '',
      LuxonUtils.toBackend(solicitud.fechaConcesion) ?? '',
      solicitud.numeroConcesion ?? '',
      solicitud.numeroRegistro ?? ''
    ];
  }
}
