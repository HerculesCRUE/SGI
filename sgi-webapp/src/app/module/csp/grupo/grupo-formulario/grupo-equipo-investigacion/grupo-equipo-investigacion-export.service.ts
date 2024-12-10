import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DEDICACION_MAP } from '@core/models/csp/grupo-equipo';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { AbstractTableExportService, IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { IMiembroGrupoInvestigacionListadoReportOptions } from './grupo-equipo-investigacion.component';
import { IGrupoEquipoListado } from './grupo-equipo-investigacion.fragment';

const EQUIPO_NOMBRE_KEY = marker('csp.grupo-equipo.nombre');
const EQUIPO_APELLIDOS_KEY = marker('csp.grupo-equipo.apellidos');
const EQUIPO_EMAIL_KEY = marker('csp.grupo-equipo.email');
const EQUIPO_ROL_KEY = marker('csp.grupo-equipo.rol');
const EQUIPO_CATEGORIA_KEY = marker('csp.grupo-equipo.categoria');
const EQUIPO_FECHA_INICIO_KEY = marker('csp.grupo-equipo.fecha-inicio');
const EQUIPO_FECHA_FIN_KEY = marker('csp.grupo-equipo.fecha-fin');
const EQUIPO_DEDICACION_KEY = marker('csp.grupo-equipo.dedicacion');
const EQUIPO_PARTICIPACION_KEY = marker('csp.grupo-equipo.participacion');

@Injectable()
export class GrupoEquipoInvestigacionExportService extends AbstractTableExportService<IGrupoEquipoListado, IMiembroGrupoInvestigacionListadoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getGroupBy(): ISgiGroupReport {
    const groupBy: ISgiGroupReport = {
      name: 'header',
      visible: true
    };
    return groupBy;
  }

  protected getDataReport(reportConfig: IReportConfig<IMiembroGrupoInvestigacionListadoReportOptions>): Observable<IGrupoEquipoListado[]> {
    return of(reportConfig.reportOptions.miembrosGrupoInvestigacion);
  }

  protected getRows(miembrosGrupoInvestigacion: IGrupoEquipoListado[], reportConfig: IReportConfig<IMiembroGrupoInvestigacionListadoReportOptions>): Observable<ISgiRowReport[]> {
    const rowsReport = miembrosGrupoInvestigacion.map(miembroGrupoInvestigacion => {
      const rowReport: ISgiRowReport = {
        elements: [
          miembroGrupoInvestigacion.persona.emails?.map(e => e.email).join(', ') ?? '',
          miembroGrupoInvestigacion.persona.nombre,
          miembroGrupoInvestigacion.persona.apellidos,
          miembroGrupoInvestigacion.rol.nombre,
          miembroGrupoInvestigacion.categoriaProfesional?.nombre ?? '',
          LuxonUtils.toBackend(miembroGrupoInvestigacion.fechaInicio),
          LuxonUtils.toBackend(miembroGrupoInvestigacion.fechaFin),
          miembroGrupoInvestigacion.dedicacion ? this.translate.instant(DEDICACION_MAP.get(miembroGrupoInvestigacion.dedicacion)) : '',
          miembroGrupoInvestigacion.participacion ?? 0
        ]
      }

      return rowReport;
    })

    return of(rowsReport);
  }
  protected getColumns(miembrosGrupoInvestigacion: IGrupoEquipoListado[], reportConfig: IReportConfig<IMiembroGrupoInvestigacionListadoReportOptions>): Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(EQUIPO_EMAIL_KEY),
        name: 'email',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(EQUIPO_NOMBRE_KEY),
        name: 'nombre',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(EQUIPO_APELLIDOS_KEY),
        name: 'apellidos',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(EQUIPO_ROL_KEY),
        name: 'rol',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(EQUIPO_CATEGORIA_KEY),
        name: 'activo',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(EQUIPO_FECHA_INICIO_KEY),
        name: 'fechaInicioParticipacion',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(EQUIPO_FECHA_FIN_KEY),
        name: 'fechaFinParticipacion',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(EQUIPO_DEDICACION_KEY),
        name: 'dedicacion',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(EQUIPO_PARTICIPACION_KEY),
        name: 'participacion',
        type: ColumnType.NUMBER
      }
    ];

    return of(columns);
  }

}
