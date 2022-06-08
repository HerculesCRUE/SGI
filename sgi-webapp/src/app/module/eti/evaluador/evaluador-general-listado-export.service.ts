import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IPersona } from '@core/models/sgp/persona';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { IEmail } from '../../../core/models/sgp/email';
import { IEvaluadorReportData, IEvaluadorReportOptions } from './evaluador-listado-export.service';
import { EstadoEvaluadorPipe } from './pipes/estado-evaluador.pipe';

const EVALUADOR_NOMBRE_KEY = marker('eti.evaluador.report.nombre');
const EVALUADOR_APELLIDOS_KEY = marker('eti.evaluador.report.apellidos');
const EVALUADOR_EMAIL_KEY = marker('eti.evaluador.report.email');
const EVALUADOR_COMITE_KEY = marker('eti.evaluador.report.comite');
const EVALUADOR_CARGO_COMITE_KEY = marker('eti.evaluador.report.cargo-comite');
const EVALUADOR_FECHA_ALTA_KEY = marker('eti.evaluador.report.fecha-alta');
const EVALUADOR_FECHA_BAJA_KEY = marker('eti.evaluador.report.fecha-baja');
const EVALUADOR_ESTADO_KEY = marker('eti.evaluador.report.estado');

@Injectable()
export class EvaluadorGeneralListadoExportService extends
  AbstractTableExportFillService<IEvaluadorReportData, IEvaluadorReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly personaService: PersonaService,
    private readonly estadoEvaluadorPipe: EstadoEvaluadorPipe
  ) {
    super(translate);
  }

  public getData(memoriaData: IEvaluadorReportData): Observable<IEvaluadorReportData> {
    return of(memoriaData).pipe(
      switchMap(data => this.getPersonaData(data))
    );
  }

  private getPersonaData(data: IEvaluadorReportData): Observable<IEvaluadorReportData> {
    if (!data.persona?.id) {
      return of(data);
    }
    return this.personaService.findById(data.persona?.id).pipe(
      map((persona: IPersona) => {
        data.persona = persona;
        return data;
      }), catchError(err => {
        this.logger.error(err);
        return of(err);
      })
    );
  }

  public fillColumns(
    peticiones: IEvaluadorReportData[],
    reportConfig: IReportConfig<IEvaluadorReportOptions>
  ): ISgiColumnReport[] {

    const columns: ISgiColumnReport[] = [];

    return [
      {
        title: this.translate.instant(EVALUADOR_NOMBRE_KEY),
        name: 'nombre',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(EVALUADOR_APELLIDOS_KEY),
        name: 'apellidos',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(EVALUADOR_EMAIL_KEY),
        name: 'email',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(EVALUADOR_COMITE_KEY),
        name: 'comite',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(EVALUADOR_CARGO_COMITE_KEY),
        name: 'cargoComite',
        type: ColumnType.STRING,
      }, {
        title: this.translate.instant(EVALUADOR_FECHA_ALTA_KEY),
        name: 'fechaAlta',
        type: ColumnType.DATE,
      }, {
        title: this.translate.instant(EVALUADOR_FECHA_BAJA_KEY),
        name: 'fechaBaja',
        type: ColumnType.DATE,
      }, {
        title: this.translate.instant(EVALUADOR_ESTADO_KEY),
        name: 'estado',
        type: ColumnType.STRING,
      }
    ] as ISgiColumnReport[];
  }

  public fillRows(evaluadores: IEvaluadorReportData[],
    index: number, reportConfig: IReportConfig<IEvaluadorReportOptions>): any[] {

    const evaluadorData = evaluadores[index];

    return [
      evaluadorData.persona?.nombre ?? '',
      evaluadorData.persona?.apellidos ?? '',
      this.getEmailPrincipal(evaluadorData),
      evaluadorData.comite?.comite ?? '',
      evaluadorData.cargoComite?.nombre ?? '',
      LuxonUtils.toBackend(evaluadorData.fechaAlta) ?? '',
      LuxonUtils.toBackend(evaluadorData.fechaBaja) ?? '',
      this.estadoEvaluadorPipe.transform(evaluadorData.fechaBaja) ?? ''
    ];
  }

  private getEmailPrincipal(evaluadorData: IEvaluadorReportData): any {
    const emails = evaluadorData.persona?.emails.filter((email: IEmail) => email.principal);

    return emails && emails.length > 0 ? emails[0].email : '';
  }
}
