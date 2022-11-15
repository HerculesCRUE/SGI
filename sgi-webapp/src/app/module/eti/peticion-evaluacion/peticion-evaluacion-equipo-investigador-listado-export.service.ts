import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEquipoTrabajoWithIsEliminable } from '@core/models/eti/equipo-trabajo-with-is-eliminable';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, from, Observable, of } from 'rxjs';
import { catchError, concatMap, mergeMap, switchMap } from 'rxjs/operators';
import { IPeticionEvaluacionMiembroReportData, IPeticionEvaluacionReportData, IPeticionEvaluacionReportOptions } from './peticion-evaluacion-listado-export.service';

const EQUIPO_INVESTIGADOR_EMAIL_FIELD = 'Email';
const EQUIPO_INVESTIGADOR_NOMBRE_FIELD = 'Nombre';
const EQUIPO_INVESTIGADOR_APELLIDOS_FIELD = 'Apellidos';
const EQUIPO_INVESTIGADOR_VINCULACION_FIELD = 'Vinculacion';

const EQUIPO_INVESTIGADOR_MIEMBRO_KEY = marker('eti.peticion-evaluacion.equipo-investigador.prefix');
const EQUIPO_INVESTIGADOR_EMAIL_KEY = marker('eti.peticion-evaluacion.equipo-investigador.email');
const EQUIPO_INVESTIGADOR_NOMBRE_KEY = marker('eti.peticion-evaluacion.equipo-investigador.nombre');
const EQUIPO_INVESTIGADOR_APELLIDOS_KEY = marker('eti.peticion-evaluacion.equipo-investigador.apellidos');
const EQUIPO_INVESTIGADOR_VINCULACION_KEY = marker('eti.peticion-evaluacion.equipo-investigador.vinculacion');

@Injectable()
export class PeticionEvaluacionEquipoInvestigadorListadoExportService extends
  AbstractTableExportFillService<IPeticionEvaluacionReportData, IPeticionEvaluacionReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    protected readonly peticionEvaluacionService: PeticionEvaluacionService,
    private readonly personaService: PersonaService,
    private readonly vinculacionService: VinculacionService
  ) {
    super(translate);
  }

  public getData(peticionData: IPeticionEvaluacionReportData): Observable<IPeticionEvaluacionReportData> {

    return this.peticionEvaluacionService.findEquipoInvestigador(peticionData.id).pipe(
      switchMap(responseInvestigadores => {
        if (responseInvestigadores?.items?.length === 0) {
          return of([]);
        }
        return of(responseInvestigadores.items);
      }), switchMap(investigadores => {
        if (investigadores.length === 0) {
          return of(peticionData);
        }
        return from(investigadores).pipe(
          mergeMap((investigador: IEquipoTrabajoWithIsEliminable) => {
            return this.addInventorMiembroToInvencionData(investigador, peticionData);
          }, this.DEFAULT_CONCURRENT)
        );
      }), catchError(err => {
        this.logger.error(err);
        return of(err);
      })
    );
  }

  private addInventorMiembroToInvencionData(investigador: IEquipoTrabajoWithIsEliminable, peticionData: IPeticionEvaluacionReportData):
    Observable<IPeticionEvaluacionReportData> {
    return forkJoin({
      persona: this.personaService.findById(investigador.persona.id),
      vinculacion: this.vinculacionService.findByPersonaId(investigador.persona.id)
    }).pipe(
      concatMap(investigadorMiembro => {
        if (peticionData.equipoInvestigador === undefined || peticionData.equipoInvestigador === null) {
          peticionData.equipoInvestigador = [];
        }
        peticionData.equipoInvestigador.push(investigadorMiembro);
        return of(peticionData);
      })
    );
  }

  public fillColumns(
    peticiones: IPeticionEvaluacionReportData[],
    reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>
  ): ISgiColumnReport[] {

    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsExcel(peticiones);
    }
  }

  private getColumnsExcel(peticiones: IPeticionEvaluacionReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumInventores = Math.max(...peticiones.map(peticion => peticion.equipoInvestigador ?
      peticion.equipoInvestigador?.length : 0));

    const prefixTitleColumn = this.translate.instant(EQUIPO_INVESTIGADOR_MIEMBRO_KEY);

    for (let i = 0; i < maxNumInventores; i++) {
      const idPeticion: string = String(i + 1);

      const columnEmail: ISgiColumnReport = {
        name: EQUIPO_INVESTIGADOR_EMAIL_FIELD + idPeticion,
        title: prefixTitleColumn + idPeticion + ': ' + this.translate.instant(EQUIPO_INVESTIGADOR_EMAIL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEmail);

      const columnNombre: ISgiColumnReport = {
        name: EQUIPO_INVESTIGADOR_NOMBRE_FIELD + idPeticion,
        title: prefixTitleColumn + idPeticion + ': ' + this.translate.instant(EQUIPO_INVESTIGADOR_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombre);

      const columnApellidos: ISgiColumnReport = {
        name: EQUIPO_INVESTIGADOR_APELLIDOS_FIELD + idPeticion,
        title: prefixTitleColumn + idPeticion + ': ' + this.translate.instant(EQUIPO_INVESTIGADOR_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnApellidos);

      const columnDepartamento: ISgiColumnReport = {
        name: EQUIPO_INVESTIGADOR_VINCULACION_FIELD + idPeticion,
        title: prefixTitleColumn + idPeticion + ': ' + this.translate.instant(EQUIPO_INVESTIGADOR_VINCULACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnDepartamento);
    }

    return columns;
  }

  public fillRows(peticiones: IPeticionEvaluacionReportData[],
    index: number, reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>): any[] {

    const invencion = peticiones[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumSocio = Math.max(...peticiones.map(invencionItem => invencionItem.equipoInvestigador ?
        invencionItem.equipoInvestigador?.length : 0));
      for (let i = 0; i < maxNumSocio; i++) {
        const inventor = invencion.equipoInvestigador ? invencion.equipoInvestigador[i] ?? null : null;
        this.fillRowsExcel(elementsRow, inventor);
      }
    }
    return elementsRow;
  }

  private async fillRowsExcel(elementsRow: any[], investigador: IPeticionEvaluacionMiembroReportData) {
    if (investigador) {
      elementsRow.push(investigador.persona?.emails?.find(email => email.principal)?.email ?? '');
      elementsRow.push(investigador.persona?.nombre ?? '');
      elementsRow.push(investigador.persona?.apellidos ?? '');
      elementsRow.push(investigador.vinculacion?.categoriaProfesional?.nombre ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
