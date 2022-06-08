import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConflictoInteres } from '@core/models/eti/conflicto-interes';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IEmail } from '@core/models/sgp/email';
import { IPersona } from '@core/models/sgp/persona';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IEvaluadorReportData, IEvaluadorReportOptions } from './evaluador-listado-export.service';


const CONFLICTO_KEY = marker('eti.evaluador.report.conflicto');

const EVALUADOR_CONFLICTO_NOMBRE_KEY = marker('eti.evaluador.report.conflicto.nombre');
const EVALUADOR_CONFLICTO_APELLIDOS_KEY = marker('eti.evaluador.report.conflicto.apellidos');
const EVALUADOR_CONFLICTO_EMAIL_KEY = marker('eti.evaluador.report.conflicto.email');

@Injectable()
export class EvaluadorConflictosInteresListadoExportService extends
  AbstractTableExportFillService<IEvaluadorReportData, IEvaluadorReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly evaluadorService: EvaluadorService,
    private readonly personaService: PersonaService
  ) {
    super(translate);
  }

  public getData(evaluadorData: IEvaluadorReportData): Observable<IEvaluadorReportData> {

    return this.evaluadorService.findConflictosInteres(evaluadorData.id).pipe(
      switchMap(responseConflictos => {
        if (responseConflictos.items?.length === 0) {
          return of(evaluadorData);
        }
        evaluadorData.conflictos = responseConflictos.items;
        const idsPersonas = evaluadorData.conflictos.map(conflicto => conflicto.personaConflicto.id);

        return this.personaService.findAllByIdIn(idsPersonas).pipe(
          switchMap(result => {
            return of(result.items);
          }),
          switchMap((personas: IPersona[]) => this.assingPersonaToEachConflictoInteres(evaluadorData, personas))
        );
      })
    );
  }

  /**
   * Recorre la lista de personasConflicto dentro de la colección de conflictos del objeto evaluadorData y le asigna
   * el objeto persona de la lista personas cuyo id sea igual al de personaConflicto.
   * @param evaluadorData observable con la lista de conflictos
   * @param personas lista de personas a asignar en cada objeto IConflictoInteres
   * @returns Observable<IEvaluadorReportData>
   */
  private assingPersonaToEachConflictoInteres(evaluadorData: IEvaluadorReportData, personas: IPersona[]): Observable<IEvaluadorReportData> {
    let conflictoItem: IConflictoInteres;
    evaluadorData.conflictos.forEach(conflicto => {
      personas.forEach(persona => {
        if (persona.id === conflicto.personaConflicto.id) {
          conflictoItem = conflicto;
          conflictoItem.personaConflicto = persona;
          return;
        }
      });
    });
    return of(evaluadorData);
  }

  public fillColumns(
    evaluadoresData: IEvaluadorReportData[],
    reportConfig: IReportConfig<IEvaluadorReportOptions>
  ): ISgiColumnReport[] {

    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsExcel(evaluadoresData);
    }
  }

  private getColumnsExcel(evaluadoresData: IEvaluadorReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumConflictos = Math.max(...evaluadoresData.map(evaluadorData => evaluadorData.conflictos ?
      evaluadorData.conflictos?.length : 0));

    const prefixTitleColumn = this.translate.instant(CONFLICTO_KEY);

    for (let i = 0; i < maxNumConflictos; i++) {
      const idConflictoCol: string = String(i + 1);

      const columnNombre: ISgiColumnReport = {
        name: 'nombre' + idConflictoCol,
        title: prefixTitleColumn + idConflictoCol + ': ' + this.translate.instant(EVALUADOR_CONFLICTO_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombre);

      const columnApellidos: ISgiColumnReport = {
        name: 'apellidos' + idConflictoCol,
        title: prefixTitleColumn + idConflictoCol + ': ' + this.translate.instant(EVALUADOR_CONFLICTO_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnApellidos);

      const columnEmail: ISgiColumnReport = {
        name: 'email' + idConflictoCol,
        title: prefixTitleColumn + idConflictoCol + ': ' + this.translate.instant(EVALUADOR_CONFLICTO_EMAIL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEmail);
    }

    return columns;
  }

  public fillRows(evaluadoresData: IEvaluadorReportData[],
    index: number, reportConfig: IReportConfig<IEvaluadorReportOptions>): any[] {

    const evaluadorData = evaluadoresData[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumMemorias = Math.max(...evaluadoresData.map(evaluadorData => evaluadorData.conflictos ?
        evaluadorData.conflictos?.length : 0));
      for (let i = 0; i < maxNumMemorias; i++) {
        const conflicto = evaluadorData.conflictos ? evaluadorData.conflictos[i] ?? null : null;
        this.fillRowsExcel(elementsRow, conflicto);
      }
    }
    return elementsRow;
  }

  private async fillRowsExcel(elementsRow: any[], conflicto: IConflictoInteres) {
    if (conflicto && conflicto.personaConflicto) {
      elementsRow.push(conflicto.personaConflicto?.nombre ?? '');
      elementsRow.push(conflicto.personaConflicto?.apellidos ?? '');
      elementsRow.push(this.getEmailPrincipal(conflicto.personaConflicto));
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private getEmailPrincipal(persona: IPersona): string {
    const emails = persona.emails.filter((email: IEmail) => email.principal);

    return emails && emails.length > 0 ? emails[0].email : '';
  }
}
