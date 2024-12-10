import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IPersona } from '@core/models/sgp/persona';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { from, Observable } from 'rxjs';
import { map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const GRUPO_IP_GRUPO = marker('csp.proyecto.export.grupos-investigacion-ip.grupo');
const GRUPO_IP_IP = marker('csp.proyecto.export.grupos-investigacion-ip.ip');
const GRUPO_IP_NOMBRE = marker('csp.proyecto.export.grupos-investigacion-ip.nombre');
const GRUPO_IP_APELLIDOS = marker('csp.proyecto.export.grupos-investigacion-ip.apellidos');
const GRUPO_IP_PARTICIPACION_PROYECTO = marker('csp.proyecto.export.grupos-investigacion-ip.participacion-proyecto');
const GRUPO_IP_FECHA_INICIO = marker('csp.proyecto.export.grupos-investigacion-ip.fecha-inicio');
const GRUPO_IP_FECHA_FIN = marker('csp.proyecto.export.grupos-investigacion-ip.fecha-fin');
const GRUPO_IP_FECHA_INICIO_PARTICIPACION_GRUPO = marker('csp.proyecto.export.grupos-investigacion-ip.fecha-inicio-participacion-grupo');
const GRUPO_IP_FECHA_FIN_PARTICIPACION_GRUPO = marker('csp.proyecto.export.grupos-investigacion-ip.fecha-fin-participacion-grupo');

export interface IProyectoGruposInvestigacionIpsData {
  persona: IPersona;
  participacionesProyecto: IProyectoEquipoParticipacionData[];
}

export interface IProyectoEquipoParticipacionData extends IProyectoEquipo {
  participacionesGrupos: IGrupoEquipoParticipacionData[];
}

export interface IGrupoEquipoParticipacionData extends IGrupo {
  participaciones: IGrupoEquipo[];
}

@Injectable()
export class ProyectoGruposInvestigacionIpListadoExportService
  extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private readonly grupoService: GrupoService,
    private readonly personaService: PersonaService,
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    return this.proyectoService.findInvestigadoresPrincipales(proyectoData.id).pipe(
      map(investigadoresPrincipales =>
        investigadoresPrincipales.reduce((acc, miembroEquipo) => {
          let proyectoIp = acc.find(item => item.persona.id === miembroEquipo.persona.id);

          if (!proyectoIp) {
            proyectoIp = {
              persona: miembroEquipo.persona,
              participacionesProyecto: []
            };
            acc.push(proyectoIp);
          }

          const participacion: IProyectoEquipoParticipacionData = {
            ...miembroEquipo,
            fechaInicio: miembroEquipo.fechaInicio ?? proyectoData.fechaInicio,
            fechaFin: miembroEquipo.fechaFin ?? proyectoData.fechaFinDefinitiva ?? proyectoData.fechaFin,
            participacionesGrupos: []
          };

          proyectoIp.participacionesProyecto.push(participacion);

          return acc;
        }, [] as IProyectoGruposInvestigacionIpsData[])
      ),
      switchMap(gruposInvestigacionIpsData => {
        return from(gruposInvestigacionIpsData).pipe(
          mergeMap(grupoInvestigacionIpData => this.personaService.findById(grupoInvestigacionIpData.persona.id).pipe(
            map(persona => {
              grupoInvestigacionIpData.persona = persona;
              return grupoInvestigacionIpData;
            })
          ), this.DEFAULT_CONCURRENT),
          toArray()
        )
      }),
      switchMap(gruposInvestigacionIpsData => {
        return from(gruposInvestigacionIpsData).pipe(
          mergeMap(grupoInvestigacionIpData => {
            return from(grupoInvestigacionIpData.participacionesProyecto).pipe(
              mergeMap(participacionProyecto => {
                return this.grupoService.findGruposPersona(participacionProyecto.persona.id, participacionProyecto.fechaInicio, participacionProyecto.fechaFin).pipe(
                  switchMap(grupos => from(grupos).pipe(
                    mergeMap(grupo => {
                      const filter = new RSQLSgiRestFilter('personaRef', SgiRestFilterOperator.EQUALS, participacionProyecto.persona.id);

                      if (participacionProyecto.fechaInicio || participacionProyecto.fechaFin) {
                        if (participacionProyecto.fechaInicio) {
                          filter.and('fechaParticipacionPosterior', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(participacionProyecto.fechaInicio, false));
                        } else {
                          filter.and('fechaParticipacionPosterior', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(participacionProyecto.fechaFin, false));
                        }

                        if (participacionProyecto.fechaFin) {
                          filter.and('fechaParticipacionAnterior', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(participacionProyecto.fechaFin, false));
                        } else {
                          filter.and('fechaParticipacionAnterior', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(participacionProyecto.fechaInicio, false));
                        }
                      }

                      const options: SgiRestFindOptions = {
                        filter
                      };

                      return this.grupoService.findMiembrosEquipo(grupo.id, options).pipe(
                        map(response => {
                          participacionProyecto.participacionesGrupos.push({
                            ...grupo,
                            participaciones: response.items
                          });

                          return participacionProyecto;
                        })
                      );
                    }, this.DEFAULT_CONCURRENT)
                  ))
                )
              }, this.DEFAULT_CONCURRENT),
            )
          }, this.DEFAULT_CONCURRENT),
          toArray(),
          map(() => gruposInvestigacionIpsData)
        )
      }),
      map(gruposInvestigacionIps => {
        proyectoData.gruposInvestigacionIps = gruposInvestigacionIps.filter(ip =>
          ip.participacionesProyecto.some(participacionProyecto =>
            participacionProyecto.participacionesGrupos.length > 0 && participacionProyecto.participacionesGrupos.some(participacionGrupo =>
              participacionGrupo.participaciones.length > 0)));
        return proyectoData;
      })
    );
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumGruposInvestigacionIps = Math.max(...proyectos.map(p => p.gruposInvestigacionIps?.length));
    for (let i = 0; i < maxNumGruposInvestigacionIps; i++) {
      const grupoInvestigacionIpIndex: string = String(i + 1);
      const titleGrupoIp = this.translate.instant(GRUPO_IP_GRUPO) + ' - ' + this.translate.instant(GRUPO_IP_IP) + ' ' + grupoInvestigacionIpIndex;

      const columnNombreIp: ISgiColumnReport = {
        name: 'ip' + grupoInvestigacionIpIndex + 'nombre',
        title: titleGrupoIp + ' - ' + this.translate.instant(GRUPO_IP_NOMBRE),
        type: ColumnType.STRING
      };
      columns.push(columnNombreIp);

      const columnApellidosIp: ISgiColumnReport = {
        name: 'ip' + grupoInvestigacionIpIndex + 'apellidos',
        title: titleGrupoIp + ' - ' + this.translate.instant(GRUPO_IP_APELLIDOS),
        type: ColumnType.STRING
      };
      columns.push(columnApellidosIp);

      const maxNumParticipacionesProyectoIpX = Math.max(...proyectos.map(proyecto => {
        return proyecto.gruposInvestigacionIps[i]?.participacionesProyecto?.length ?? 0;
      }));

      for (let j = 0; j < maxNumParticipacionesProyectoIpX; j++) {
        const participacionProyectoIpIndex: string = String(j + 1);

        const columnFechaInicioParticipacionProyecto: ISgiColumnReport = {
          name: 'ip' + grupoInvestigacionIpIndex + 'participacion' + participacionProyectoIpIndex + 'fechaInicioParticipacionProyecto',
          title: titleGrupoIp + ' - ' + this.translate.instant(GRUPO_IP_PARTICIPACION_PROYECTO, { index: participacionProyectoIpIndex }) + ' - ' + this.translate.instant(GRUPO_IP_FECHA_INICIO),
          type: ColumnType.DATE
        };
        columns.push(columnFechaInicioParticipacionProyecto);

        const columnFechaFinParticipacionProyecto: ISgiColumnReport = {
          name: 'ip' + grupoInvestigacionIpIndex + 'participacion' + participacionProyectoIpIndex + 'fechaFinParticipacionProyecto',
          title: titleGrupoIp + ' - ' + this.translate.instant(GRUPO_IP_PARTICIPACION_PROYECTO, { index: participacionProyectoIpIndex }) + ' - ' + this.translate.instant(GRUPO_IP_FECHA_FIN),
          type: ColumnType.DATE
        };
        columns.push(columnFechaFinParticipacionProyecto);


        const maxNumGruposX = Math.max(...proyectos.map(proyecto => {
          return proyecto.gruposInvestigacionIps[i]?.participacionesProyecto[j]?.participacionesGrupos?.length ?? 0;
        }));

        for (let k = 0; k < maxNumGruposX; k++) {
          const grupoIndex: string = String(k + 1);

          const columnGrupoNombre: ISgiColumnReport = {
            name: 'ip' + grupoInvestigacionIpIndex + 'participacion' + participacionProyectoIpIndex + 'grupo' + grupoIndex,
            title: titleGrupoIp + ' - ' + this.translate.instant(GRUPO_IP_PARTICIPACION_PROYECTO, { index: participacionProyectoIpIndex }) + ' - ' + this.translate.instant(GRUPO_IP_GRUPO) + ' ' + grupoIndex + ' - ' + this.translate.instant(GRUPO_IP_NOMBRE),
            type: ColumnType.STRING
          };
          columns.push(columnGrupoNombre);

          const maxNumParticipacionesGruposX = Math.max(...proyectos.map(proyecto => {
            return proyecto.gruposInvestigacionIps[i]?.participacionesProyecto[j]?.participacionesGrupos[k]?.participaciones?.length ?? 0;
          }));

          for (let l = 0; l < maxNumParticipacionesGruposX; l++) {
            const participaciongrupoIpIndex: string = String(l + 1);

            const columnFechaInicioParticipacionGrupo: ISgiColumnReport = {
              name: 'ip' + grupoInvestigacionIpIndex + 'participacion' + participacionProyectoIpIndex + 'grupo' + grupoIndex + 'participacion' + participaciongrupoIpIndex,
              title: titleGrupoIp + ' - ' + this.translate.instant(GRUPO_IP_PARTICIPACION_PROYECTO, { index: participacionProyectoIpIndex }) + ' - ' + this.translate.instant(GRUPO_IP_GRUPO) + ' ' + grupoIndex + ' - ' + this.translate.instant(GRUPO_IP_FECHA_INICIO_PARTICIPACION_GRUPO, { index: participaciongrupoIpIndex }),
              type: ColumnType.DATE
            };
            columns.push(columnFechaInicioParticipacionGrupo);

            const columnFechaFinParticipacionGrupo: ISgiColumnReport = {
              name: 'ip' + grupoInvestigacionIpIndex + 'participacion' + participacionProyectoIpIndex + 'grupo' + grupoIndex + 'participacion' + participaciongrupoIpIndex,
              title: titleGrupoIp + ' - ' + this.translate.instant(GRUPO_IP_PARTICIPACION_PROYECTO, { index: participacionProyectoIpIndex }) + ' - ' + this.translate.instant(GRUPO_IP_GRUPO) + ' ' + grupoIndex + ' - ' + this.translate.instant(GRUPO_IP_FECHA_FIN_PARTICIPACION_GRUPO, { index: participaciongrupoIpIndex }),
              type: ColumnType.DATE
            };
            columns.push(columnFechaFinParticipacionGrupo);
          }
        }
      }
    }

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = proyectos[index];

    const elementsRow: any[] = [];

    const maxNumGruposInvestigacionIps = Math.max(...proyectos.map(p => p.gruposInvestigacionIps?.length));
    for (let i = 0; i < maxNumGruposInvestigacionIps; i++) {
      const ipX = proyecto.gruposInvestigacionIps[i];
      elementsRow.push(ipX?.persona.nombre ?? '');
      elementsRow.push(ipX?.persona.apellidos ?? '');

      const maxNumParticipacionesProyectoIpX = Math.max(...proyectos.map(proyecto => {
        return proyecto.gruposInvestigacionIps[i]?.participacionesProyecto?.length ?? 0;
      }));

      for (let j = 0; j < maxNumParticipacionesProyectoIpX; j++) {
        const participacionProyectoX = ipX?.participacionesProyecto[j];
        elementsRow.push(LuxonUtils.toBackend(participacionProyectoX?.fechaInicio) ?? '');
        elementsRow.push(LuxonUtils.toBackend(participacionProyectoX?.fechaFin) ?? '');

        const maxNumGruposX = Math.max(...proyectos.map(proyecto => {
          return proyecto.gruposInvestigacionIps[i]?.participacionesProyecto[j]?.participacionesGrupos?.length ?? 0;
        }));

        for (let k = 0; k < maxNumGruposX; k++) {
          const grupoX = participacionProyectoX?.participacionesGrupos[k];
          elementsRow.push(grupoX?.nombre ?? '');

          const maxNumParticipacionesGruposX = Math.max(...proyectos.map(proyecto => {
            return proyecto.gruposInvestigacionIps[i]?.participacionesProyecto[j]?.participacionesGrupos[k]?.participaciones.length ?? 0;
          }));

          for (let l = 0; l < maxNumParticipacionesGruposX; l++) {
            const participacionGrupoX = grupoX?.participaciones[l];
            elementsRow.push(LuxonUtils.toBackend(participacionGrupoX?.fechaInicio ?? grupoX?.fechaInicio) ?? '');
            elementsRow.push(LuxonUtils.toBackend(participacionGrupoX?.fechaFin ?? grupoX?.fechaFin) ?? '');
          }

        }
      }
    }

    return elementsRow;
  }

}
