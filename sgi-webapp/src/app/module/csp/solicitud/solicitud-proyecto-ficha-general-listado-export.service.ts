import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { RolSocioService } from '@core/services/csp/rol-socio/rol-socio.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { LanguageService } from '@core/services/language.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ISolicitudReportData, ISolicitudReportOptions } from './solicitud-listado-export.service';

const PROYECTO_FIELD = 'proyecto';
const PROYECTO_KEY = marker('menu.csp.solicitudes.datos-proyecto');
const REFERENCIA_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.codigo-externo');
const ACRONIMO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.acronimo');
const DURACION_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.duracion');
const COORDINADO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.proyecto-coordinado');
const ROL_UNIVERSIDAD_KEY = marker('csp.solicitud.rol-participacion-universidad');
const COLABORATIVO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.proyecto-colaborativo');
const AREA_TEMATICA_KEY = marker('csp.area-tematica.nombre');

@Injectable()
export class SolicitudProyectoFichaGeneralListadoExportService extends
  AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,
    private readonly rolSocioService: RolSocioService,
    private readonly languageService: LanguageService
  ) {
    super(translate);
  }

  public getData(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    return this.solicitudService.findSolicitudProyecto(solicitudData.id).pipe(
      map(solicitudProyecto => {
        solicitudData.proyecto = solicitudProyecto;
        return solicitudData;
      }),
      switchMap(data => {
        if (!data.proyecto?.rolUniversidad) {
          return of(data);
        }

        return this.rolSocioService.findById(data.proyecto.rolUniversidad.id).pipe(
          map(rolSocio => {
            data.proyecto.rolUniversidad = rolSocio;
            return data;
          })
        )
      })
    );
  }

  public fillColumns(
    solicitudes: ISolicitudReportData[],
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): ISgiColumnReport[] {
    return this.getColumnsProyectoExcel();
  }

  public getColumnsProyectoExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(REFERENCIA_KEY),
        name: 'codigoExternoSolicitudProyecto',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(ACRONIMO_KEY),
        name: 'acronimo',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(DURACION_KEY),
        name: 'duracion',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(COORDINADO_KEY),
        name: 'coordinado',
        type: ColumnType.STRING,
      },
      {
        title: this.translate.instant(ROL_UNIVERSIDAD_KEY),
        name: 'rolUniversidad',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(COLABORATIVO_KEY),
        name: 'colaborativo',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(AREA_TEMATICA_KEY),
        name: 'areaTematica',
        type: ColumnType.STRING,
        format: '#'
      },
    ];

    return columns;
  }

  public fillRows(solicitudes: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {
    const solicitud = solicitudes[index];
    const elementsRow: any[] = [];
    this.fillRowsProyectoExcel(elementsRow, solicitud.proyecto);
    return elementsRow;
  }

  private fillRowsProyectoExcel(elementsRow: any[], proyecto: ISolicitudProyecto) {
    if (proyecto) {
      elementsRow.push(proyecto?.codExterno ?? '');
      elementsRow.push(proyecto?.acronimo ?? '');
      elementsRow.push(proyecto?.duracion ? proyecto?.duracion.toString() : '');
      elementsRow.push(this.notIsNullAndNotUndefined(proyecto?.coordinado) ?
        this.getI18nBooleanYesNo(proyecto?.coordinado) : '');
      elementsRow.push(this.languageService.getFieldValue(proyecto?.rolUniversidad?.nombre));
      elementsRow.push(this.notIsNullAndNotUndefined(proyecto?.colaborativo) ?
        this.getI18nBooleanYesNo(proyecto?.colaborativo) : '');
      elementsRow.push(this.languageService.getFieldValue(proyecto?.areaTematica?.nombre));
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
    return elementsRow;
  }

}
