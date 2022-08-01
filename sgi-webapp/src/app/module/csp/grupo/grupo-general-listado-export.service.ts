import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TIPO_MAP } from '@core/models/csp/grupo-tipo';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IGrupoReportData, IGrupoReportOptions } from './grupo-listado-export.service';

const ID_SGE_KEY = marker('csp.grupo.codigo-sge');
const FECHA_INICIO_KEY = marker('csp.grupo.fecha-inicio');
const FECHA_FIN_KEY = marker('csp.grupo.fecha-fin');
const CODIGO_KEY = marker('csp.grupo.codigo');
const TIPO_GRUPO_KEY = marker('csp.grupo.tipo');
const NOMBRE_KEY = marker('csp.grupo.nombre');
const INVESTIGADOR_KEY = marker('csp.grupo.investigador-principal');
const GRUPO_ESPECIAL_INVESTIGACION_KEY = marker('csp.grupo.especial-investigacion');

@Injectable()
export class GrupoGeneralListadoExportService extends AbstractTableExportFillService<IGrupoReportData, IGrupoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private grupoService: GrupoService,
    private personaService: PersonaService
  ) {
    super(translate);
  }

  public getData(grupoData: IGrupoReportData): Observable<IGrupoReportData> {
    return this.grupoService.findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(grupoData?.id).pipe(
      map((response) => {
        grupoData.investigadoresPrincipales = '';
        return response;
      }),
      switchMap(response => {
        if (!response) {
          return of(grupoData);
        }

        return this.personaService.findAllByIdIn([...response]).pipe(
          map((result) => {
            const personas = result.items;

            const personasString = personas.map(p => {
              return p.nombre + ' ' + p.apellidos;
            }).join(', ');

            grupoData.investigadoresPrincipales = personasString;
            return grupoData;

          })
        );
      })
    );
  }

  public fillColumns(
    grupos: IGrupoReportData[],
    reportConfig: IReportConfig<IGrupoReportOptions>
  ): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(NOMBRE_KEY),
        name: 'titulo',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(INVESTIGADOR_KEY),
        name: 'investigador',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(CODIGO_KEY),
        name: 'codigo',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(ID_SGE_KEY),
        name: 'idSGE',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(FECHA_INICIO_KEY),
        name: 'fechaInicio',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(FECHA_FIN_KEY),
        name: 'fechaFin',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(TIPO_GRUPO_KEY),
        name: 'tipoGrupo',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(GRUPO_ESPECIAL_INVESTIGACION_KEY),
        name: 'grupoEspecialInvestigacion',
        type: ColumnType.STRING
      }
    ];

    return columns;
  }

  public fillRows(resultados: IGrupoReportData[], index: number, reportConfig: IReportConfig<IGrupoReportOptions>): any[] {
    const grupo = resultados[index];

    const elementsRow: any[] = [];
    elementsRow.push(grupo.nombre);
    elementsRow.push(grupo.investigadoresPrincipales);
    elementsRow.push(grupo.codigo);
    elementsRow.push(grupo.proyectoSge ? grupo.proyectoSge.id : '');
    elementsRow.push(LuxonUtils.toBackend(grupo.fechaInicio));
    elementsRow.push(LuxonUtils.toBackend(grupo.fechaFin));
    elementsRow.push(grupo.tipo ? this.translate.instant(TIPO_MAP.get(grupo.tipo)) : '');
    elementsRow.push(
      this.notIsNullAndNotUndefined(grupo.especialInvestigacion) ? this.getI18nBooleanYesNo(grupo.especialInvestigacion) : '');
    return elementsRow;
  }
}
