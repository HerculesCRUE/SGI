import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IPersona } from '@core/models/sgp/persona';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { IPeticionEvaluacionReportData, IPeticionEvaluacionReportOptions } from './peticion-evaluacion-listado-export.service';

const SOLICITANTE_NOMBRE_KEY = marker('eti.peticion-evaluacion.report.solicitante.nombre');
const SOLICITANTE_APELLIDOS_KEY = marker('eti.peticion-evaluacion.report.solicitante.apellidos');
const CODIGO_KEY = marker('eti.peticion-evaluacion.report.codigo');
const CODIGO_SOLICITUD_CONVOCATORIA_KEY = marker('eti.peticion-evaluacion.report.codigo-solicitud-convocatoria');
const TITULO_PROYECTO_KEY = marker('eti.peticion-evaluacion.proyecto-titulo');
const TIPO_ACTIVIDAD_KEY = marker('eti.peticion-evaluacion.report.tipo-actividad');
const TIPO_INVESTIGACION_TUTELADA_KEY = marker('eti.peticion-evaluacion.tipo-investigacion-tutelada');
const ORGANO_FINANCIADOR_KEY = marker('eti.peticion-evaluacion.organo-financiador');
const IMPORTE_FINANCIACION_KEY = marker('eti.peticion-evaluacion.importe-financiacion');
const FECHA_INICIO_KEY = marker('eti.peticion-evaluacion.fecha-inicio');
const FECHA_FIN_KEY = marker('eti.peticion-evaluacion.fecha-fin');

@Injectable()
export class PeticionEvaluacionGeneralListadoExportService extends
  AbstractTableExportFillService<IPeticionEvaluacionReportData, IPeticionEvaluacionReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
    private readonly personaService: PersonaService
  ) {
    super(translate);
  }

  public getData(peticionEvaluacionData: IPeticionEvaluacionReportData): Observable<IPeticionEvaluacionReportData> {
    return of(peticionEvaluacionData).pipe(
      switchMap((data: IPeticionEvaluacionReportData) => {
        if (!data.solicitante?.id) {
          return of(data);
        }
        return this.personaService.findById(data.solicitante.id).pipe(
          map((persona: IPersona) => {
            data.solicitante = persona;
            return data;
          }), catchError(err => {
            this.logger.error(err);
            return of(err);
          })
        );
      }), switchMap((data: IPeticionEvaluacionReportData) => {
        if (!data.solicitudConvocatoriaRef) {
          return of(data);
        }
        return this.solicitudService.getCodigoRegistroInterno(data.solicitudConvocatoriaRef as unknown as number).pipe(
          map((codigo: string) => {
            data.codigoSolicitudConvocatoria = codigo;
            return data;
          }), catchError(err => {
            this.logger.error(err);
            return of(err);
          })
        );
      })
    );
  }

  public fillColumns(
    peticiones: IPeticionEvaluacionReportData[],
    reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>
  ): ISgiColumnReport[] {

    const columns: ISgiColumnReport[] = [];

    return [
      {
        title: this.translate.instant(SOLICITANTE_NOMBRE_KEY),
        name: 'solicitanteNombre',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(SOLICITANTE_APELLIDOS_KEY),
        name: 'solicitanteApellidos',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(CODIGO_KEY),
        name: 'codigo',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(CODIGO_SOLICITUD_CONVOCATORIA_KEY),
        name: 'codigoSolicitudConvocatoria',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(TITULO_PROYECTO_KEY),
        name: 'tituloProyecto',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(TIPO_ACTIVIDAD_KEY),
        name: 'tipoActividad',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(TIPO_INVESTIGACION_TUTELADA_KEY),
        name: 'tipoInvestigacionTutelada',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(ORGANO_FINANCIADOR_KEY),
        name: 'organoFinanciador',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(IMPORTE_FINANCIACION_KEY),
        name: 'importeFinanciacion',
        type: ColumnType.NUMBER
      }, {
        title: this.translate.instant(FECHA_INICIO_KEY),
        name: 'fechaInicio',
        type: ColumnType.DATE
      }, {
        title: this.translate.instant(FECHA_FIN_KEY),
        name: 'fechaFin',
        type: ColumnType.DATE
      }
    ] as ISgiColumnReport[];
  }

  public fillRows(peticiones: IPeticionEvaluacionReportData[],
    index: number, reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>): any[] {

    const peticionData = peticiones[index];

    return [
      peticionData.solicitante?.nombre ?? '',
      peticionData.solicitante?.apellidos ?? '',
      peticionData.codigo ?? '',
      peticionData.codigoSolicitudConvocatoria ?? '',
      peticionData.titulo ?? '',
      peticionData.tipoActividad?.nombre ?? '',
      peticionData.tipoInvestigacionTutelada?.nombre ?? '',
      peticionData.fuenteFinanciacion ?? '',
      peticionData.importeFinanciacion ?? '',
      LuxonUtils.toBackend(peticionData.fechaInicio) ?? '',
      LuxonUtils.toBackend(peticionData.fechaFin) ?? ''
    ];
  }
}
