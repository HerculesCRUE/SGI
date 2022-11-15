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
import { IEvaluacionReportData, IEvaluacionReportOptions } from './evaluacion-listado-export.service';

const EVALUACION_COMITE_KEY = marker('eti.evaluacion.report.comite');
const EVALUACION_TIPO_EVALUACION_KEY = marker('eti.evaluacion.report.tipo-evaluacion');
const EVALUACION_FECHA_EVALUACION_KEY = marker('eti.evaluacion.report.fecha-evaluacion');
const EVALUACION_MEMORIA_KEY = marker('eti.evaluacion.report.memoria');
const EVALUACION_SOLICITANTE_KEY = marker('eti.evaluacion.report.solicitante');
const EVALUACION_SOLICITANTE_NOMBRE_KEY = marker('eti.evaluacion.report.solicitante.nombre');
const EVALUACION_SOLICITANTE_APELLIDOS_KEY = marker('eti.evaluacion.report.solicitante.apellidos');
const EVALUACION_DICTAMEN_KEY = marker('eti.evaluacion.report.dictamen');
const EVALUACION_VERSION_KEY = marker('eti.evaluacion.report.version');
const EVALUACION_TIPO_MEMORIA_KEY = marker('eti.evaluacion.report.tipo-memoria')

@Injectable()
export class EvaluacionGeneralListadoExportService extends
  AbstractTableExportFillService<IEvaluacionReportData, IEvaluacionReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly personaService: PersonaService
  ) {
    super(translate);
  }

  public getData(evaluacionData: IEvaluacionReportData): Observable<IEvaluacionReportData> {
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
    convocatorias: IEvaluacionReportData[],
    reportConfig: IReportConfig<IEvaluacionReportOptions>
  ): ISgiColumnReport[] {

    const columns: ISgiColumnReport[] = [];
    const solicitantePrefix = this.translate.instant(EVALUACION_SOLICITANTE_KEY);
    return [
      {
        title: this.translate.instant(EVALUACION_COMITE_KEY),
        name: 'comite',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(EVALUACION_TIPO_EVALUACION_KEY),
        name: 'tipoEvaluacion',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(EVALUACION_TIPO_MEMORIA_KEY),
        name: 'tipoMemoria',
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

  public fillRows(convocatorias: IEvaluacionReportData[],
    index: number, reportConfig: IReportConfig<IEvaluacionReportOptions>): any[] {

    const convocatoriaData = convocatorias[index];

    return [
      convocatoriaData.memoria?.comite?.comite ?? '',
      convocatoriaData.tipoEvaluacion?.nombre ?? '',
      convocatoriaData.memoria?.tipoMemoria?.nombre ?? '',
      LuxonUtils.toBackend(convocatoriaData.fechaDictamen) ?? '',
      convocatoriaData.memoria?.numReferencia ?? '',
      convocatoriaData.solicitante?.nombre ?? '',
      convocatoriaData.solicitante?.apellidos ?? '',
      convocatoriaData.dictamen?.nombre ?? '',
      convocatoriaData.version ?? ''
    ];
  }
}
