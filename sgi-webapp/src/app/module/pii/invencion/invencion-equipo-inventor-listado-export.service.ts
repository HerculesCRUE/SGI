import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, from, Observable, of } from 'rxjs';
import { concatMap, mergeMap, switchMap } from 'rxjs/operators';
import { IInvencionReportData, IInvencionReportOptions, IInventorMiembro } from './invencion-listado-export.service';

const EQUIPO_INVENTOR_NOMBRE_FIELD = 'Nombre';
const EQUIPO_INVENTOR_APELLIDOS_FIELD = 'Apellidos';
const EQUIPO_INVENTOR_DEPARTAMENTO_FIELD = 'Departamento';

const EQUIPO_INVENTOR_MIEMBRO_KEY = marker('pii.invencion-equipo-inventor.miembro-equipo-inventor');
const EQUIPO_INVENTOR_NOMBRE_KEY = marker('pii.invencion-equipo-inventor.nombre');
const EQUIPO_INVENTOR_APELLIDOS_KEY = marker('pii.invencion-equipo-inventor.apellidos');
const EQUIPO_INVENTOR_DEPARTAMENTO_KEY = marker('pii.invencion-equipo-inventor.departamento');

@Injectable()
export class InvencionEquipoInventorListadoExportService extends
  AbstractTableExportFillService<IInvencionReportData, IInvencionReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly invencionService: InvencionService,
    private readonly personaService: PersonaService,
    private readonly vinculacionService: VinculacionService
  ) {
    super(translate);
  }

  public getData(invencionData: IInvencionReportData): Observable<IInvencionReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.invencionService.findInventoresWithOptions(invencionData.id, findOptions).pipe(
      switchMap(responseInventores => {
        if (responseInventores.total === 0) {
          return of([]);
        }
        return of(responseInventores.items);
      }), switchMap(inventores => {
        return from(inventores).pipe(
          mergeMap((inventor: IInvencionInventor) => {
            return this.addInventorMiembroToInvencionData(inventor, invencionData);
          }, this.DEFAULT_CONCURRENT)
        );
      })
    );
  }

  private addInventorMiembroToInvencionData(inventor: IInvencionInventor, invencionData: IInvencionReportData):
    Observable<IInvencionReportData> {
    return forkJoin({
      persona: this.personaService.findById(inventor.inventor.id),
      vinculacion: this.vinculacionService.findByPersonaId(inventor.inventor.id)
    }).pipe(
      concatMap(inventorMiembro => {
        if (invencionData.equipoInventor === undefined || invencionData.equipoInventor === null) {
          invencionData.equipoInventor = [];
        }
        invencionData.equipoInventor.push(inventorMiembro);
        return of(invencionData);
      })
    );
  }

  public fillColumns(
    invenciones: IInvencionReportData[],
    reportConfig: IReportConfig<IInvencionReportOptions>
  ): ISgiColumnReport[] {

    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsExcel(invenciones);
    }
  }

  private getColumnsExcel(invenciones: IInvencionReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumInventores = Math.max(...invenciones.map(invencion => invencion.equipoInventor ?
      invencion.equipoInventor?.length : 0));

    const prefixTitleColumn = this.translate.instant(EQUIPO_INVENTOR_MIEMBRO_KEY);

    for (let i = 0; i < maxNumInventores; i++) {
      const idSolicitud: string = String(i + 1);

      const columnNombre: ISgiColumnReport = {
        name: EQUIPO_INVENTOR_NOMBRE_FIELD + idSolicitud,
        title: prefixTitleColumn + idSolicitud + ': ' + this.translate.instant(EQUIPO_INVENTOR_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombre);

      const columnApellidos: ISgiColumnReport = {
        name: EQUIPO_INVENTOR_APELLIDOS_FIELD + idSolicitud,
        title: prefixTitleColumn + idSolicitud + ': ' + this.translate.instant(EQUIPO_INVENTOR_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnApellidos);

      const columnDepartamento: ISgiColumnReport = {
        name: EQUIPO_INVENTOR_DEPARTAMENTO_FIELD + idSolicitud,
        title: prefixTitleColumn + idSolicitud + ': ' + this.translate.instant(EQUIPO_INVENTOR_DEPARTAMENTO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnDepartamento);
    }

    return columns;
  }

  public fillRows(invenciones: IInvencionReportData[], index: number, reportConfig: IReportConfig<IInvencionReportOptions>): any[] {

    const invencion = invenciones[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumSocio = Math.max(...invenciones.map(invencionItem => invencionItem.equipoInventor ?
        invencionItem.equipoInventor?.length : 0));
      for (let i = 0; i < maxNumSocio; i++) {
        const inventor = invencion.equipoInventor ? invencion.equipoInventor[i] ?? null : null;
        this.fillRowsExcel(elementsRow, inventor);
      }
    }
    return elementsRow;
  }

  private async fillRowsExcel(elementsRow: any[], inventor: IInventorMiembro) {
    if (inventor) {
      elementsRow.push(inventor.persona?.nombre);
      elementsRow.push(inventor.persona?.apellidos);
      elementsRow.push(inventor.vinculacion?.departamento?.nombre);
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
