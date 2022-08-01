import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ITareaWithIsEliminable } from '@core/models/eti/tarea-with-is-eliminable';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap } from 'rxjs/operators';
import { IPeticionEvaluacionAsignacionTareaReportData, IPeticionEvaluacionReportData, IPeticionEvaluacionReportOptions } from './peticion-evaluacion-listado-export.service';

const ASIGNACION_TAREAS_KEY = marker('eti.peticion-evaluacion.asignacion-tareas');

const ASIGNACION_TAREAS_NOMBRE_KEY = marker('eti.peticion-evaluacion.asignacion-tareas.nombre');
const ASIGNACION_TAREAS_APELLIDOS_KEY = marker('eti.peticion-evaluacion.asignacion-tareas.apellidos');
const ASIGNACION_TAREAS_MEMORIA_KEY = marker('eti.peticion-evaluacion.asignacion-tareas.memoria');
const ASIGNACION_TAREAS_TAREA_KEY = marker('eti.peticion-evaluacion.asignacion-tareas.tarea');
const ASIGNACION_TAREAS_EXPERIENCIA_FORMACION_KEY = marker('eti.peticion-evaluacion.asignacion-tareas.experiencia-formacion');

@Injectable()
export class PeticionEvaluacionAsignacionTareasListadoExportService extends
  AbstractTableExportFillService<IPeticionEvaluacionReportData, IPeticionEvaluacionReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    protected readonly peticionEvaluacionService: PeticionEvaluacionService,
    private readonly personaService: PersonaService
  ) {
    super(translate);
  }

  public getData(peticionData: IPeticionEvaluacionReportData): Observable<IPeticionEvaluacionReportData> {

    return this.peticionEvaluacionService.findTareas(peticionData.id).pipe(
      switchMap(responseTareas => {
        if (responseTareas.items?.length === 0) {
          return of([]);
        }
        return of(responseTareas.items);
      }), switchMap(tareas => {
        return from(tareas).pipe(
          mergeMap((tarea: ITareaWithIsEliminable) => {
            return this.addTareaToPeticionEvaluacionData(tarea, peticionData);
          }, this.DEFAULT_CONCURRENT)
        );
      })
    );
  }

  private addTareaToPeticionEvaluacionData(tarea: ITareaWithIsEliminable, peticionData: IPeticionEvaluacionReportData):
    Observable<IPeticionEvaluacionReportData> {
    return this.personaService.findById(tarea.equipoTrabajo.persona.id).pipe(
      map(persona => {
        if (peticionData.asignacionTareas === undefined || peticionData.asignacionTareas === null) {
          peticionData.asignacionTareas = [];
        }
        peticionData.asignacionTareas.push({
          nombre: persona.nombre,
          apellidos: persona.apellidos,
          memoria: tarea.memoria.numReferencia,
          tarea: tarea.memoria.comite.id in [1, 3] ? tarea.tarea : tarea.memoria.comite.id === 2 ? tarea.tipoTarea.nombre : '',
          experiencia: tarea.memoria.comite.id in [1, 3] ? tarea.formacion : tarea.memoria.comite.id === 2 ?
            tarea.formacionEspecifica.nombre : ''
        });
        return peticionData;
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

    const maxNumTareas = Math.max(...peticiones.map(peticion => peticion.asignacionTareas ?
      peticion.asignacionTareas?.length : 0));

    const prefixTitleColumn = this.translate.instant(ASIGNACION_TAREAS_KEY);

    for (let i = 0; i < maxNumTareas; i++) {
      const idTareaCol: string = String(i + 1);

      const columnNombre: ISgiColumnReport = {
        name: 'nombre' + idTareaCol,
        title: prefixTitleColumn + idTareaCol + ': ' + this.translate.instant(ASIGNACION_TAREAS_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombre);

      const columnApellidos: ISgiColumnReport = {
        name: 'apellidos' + idTareaCol,
        title: prefixTitleColumn + idTareaCol + ': ' + this.translate.instant(ASIGNACION_TAREAS_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnApellidos);

      const columnMemoria: ISgiColumnReport = {
        name: 'memoria' + idTareaCol,
        title: prefixTitleColumn + idTareaCol + ': ' + this.translate.instant(ASIGNACION_TAREAS_MEMORIA_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnMemoria);

      const columnTarea: ISgiColumnReport = {
        name: 'tarea' + idTareaCol,
        title: prefixTitleColumn + idTareaCol + ': ' + this.translate.instant(ASIGNACION_TAREAS_TAREA_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTarea);

      const columnExperiencia: ISgiColumnReport = {
        name: 'experiencia' + idTareaCol,
        title: prefixTitleColumn + idTareaCol + ': ' + this.translate.instant(ASIGNACION_TAREAS_EXPERIENCIA_FORMACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnExperiencia);
    }

    return columns;
  }

  public fillRows(peticiones: IPeticionEvaluacionReportData[],
    index: number, reportConfig: IReportConfig<IPeticionEvaluacionReportOptions>): any[] {

    const peticion = peticiones[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumPeticiones = Math.max(...peticiones.map(peticionItem => peticionItem.asignacionTareas ?
        peticionItem.asignacionTareas?.length : 0));
      for (let i = 0; i < maxNumPeticiones; i++) {
        const tarea = peticion.asignacionTareas ? peticion.asignacionTareas[i] ?? null : null;
        this.fillRowsExcel(elementsRow, tarea);
      }
    }
    return elementsRow;
  }

  private async fillRowsExcel(elementsRow: any[], tarea: IPeticionEvaluacionAsignacionTareaReportData) {
    if (tarea) {
      elementsRow.push(tarea.nombre ?? '');
      elementsRow.push(tarea.apellidos ?? '');
      elementsRow.push(tarea.memoria ?? '');
      elementsRow.push(tarea.tarea ?? '');
      elementsRow.push(tarea.experiencia ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
