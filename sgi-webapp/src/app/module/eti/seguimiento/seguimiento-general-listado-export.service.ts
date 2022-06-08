import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ISeguimientoReportData, ISeguimientoReportOptions } from './seguimiento-listado-export.service';

const EVALUACION_COMITE_KEY = marker('eti.evaluacion.report.comite');
const EVALUACION_TIPO_KEY = marker('eti.evaluacion.report.tipo');
const EVALUACION_FECHA_EVALUACION_KEY = marker('eti.evaluacion.report.fecha-evaluacion');
const EVALUACION_MEMORIA_KEY = marker('eti.evaluacion.report.memoria');
const EVALUACION_SOLICITANTE_KEY = marker('eti.evaluacion.report.solicitante');
const EVALUACION_SOLICITANTE_NOMBRE_KEY = marker('eti.evaluacion.report.solicitante.nombre');
const EVALUACION_SOLICITANTE_APELLIDOS_KEY = marker('eti.evaluacion.report.solicitante.apellidos');
const EVALUACION_DICTAMEN_KEY = marker('eti.evaluacion.report.dictamen');
const EVALUACION_VERSION_KEY = marker('eti.evaluacion.report.version');

@Injectable()
export class SeguimientoGeneralListadoExportService extends
  AbstractTableExportFillService<ISeguimientoReportData, ISeguimientoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly personaService: PersonaService
  ) {
    super(translate);
  }

  public getData(evaluacionData: ISeguimientoReportData): Observable<ISeguimientoReportData> {
    return of(evaluacionData).pipe(
      switchMap(data => {
        if (!data.memoria?.peticionEvaluacion?.solicitante?.id) {
          return of(data);
        }
        return this.personaService.findById(data.memoria.peticionEvaluacion.solicitante.id).pipe(
          map(persona => {
            data.solicitante = persona;
            return data;
          })
        );
      })
    );
  }

  public fillColumns(
    convocatorias: ISeguimientoReportData[],
    reportConfig: IReportConfig<ISeguimientoReportOptions>
  ): ISgiColumnReport[] {

    const columns: ISgiColumnReport[] = [];
    const solicitantePrefix = this.translate.instant(EVALUACION_SOLICITANTE_KEY);
    return [
      {
        title: this.translate.instant(EVALUACION_COMITE_KEY),
        name: 'comite',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(EVALUACION_TIPO_KEY),
        name: 'tipo',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(EVALUACION_FECHA_EVALUACION_KEY),
        name: 'fechaEvaluacion',
        type: ColumnType.DATE
      }, {
        title: this.translate.instant(EVALUACION_MEMORIA_KEY),
        name: 'memoria',
        type: ColumnType.STRING
      }, {
        title: solicitantePrefix + ': ' + this.translate.instant(EVALUACION_SOLICITANTE_NOMBRE_KEY),
        name: 'solicitanteNombre',
        type: ColumnType.STRING
      }, {
        title: solicitantePrefix + ': ' + this.translate.instant(EVALUACION_SOLICITANTE_APELLIDOS_KEY),
        name: 'solicitanteApellidos',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(EVALUACION_DICTAMEN_KEY),
        name: 'dictamen',
        type: ColumnType.STRING
      }, {
        title: this.translate.instant(EVALUACION_VERSION_KEY),
        name: 'version',
        type: ColumnType.STRING
      }
    ] as ISgiColumnReport[];
  }

  public fillRows(seguimientos: ISeguimientoReportData[],
    index: number, reportConfig: IReportConfig<ISeguimientoReportOptions>): any[] {

    const seguimientoData = seguimientos[index];

    return [
      seguimientoData.memoria?.comite?.comite ?? '',
      seguimientoData.tipoEvaluacion?.nombre ?? '',
      LuxonUtils.toBackend(seguimientoData.fechaDictamen) ?? '',
      seguimientoData.memoria?.numReferencia ?? '',
      seguimientoData.solicitante?.nombre ?? '',
      seguimientoData.solicitante?.apellidos ?? '',
      seguimientoData.dictamen?.nombre ?? '',
      seguimientoData.version ?? ''
    ];
  }
}
