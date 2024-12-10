import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Estado } from '@core/models/csp/estado-solicitud';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap } from 'rxjs/operators';
import { ISolicitudReportData, ISolicitudReportOptions } from './solicitud-listado-export.service';

const GRUPO_IP_GRUPO = marker('csp.proyecto.export.grupos-investigacion-ip.grupo');
const GRUPO_IP_GRUPO_NOMBRE = marker('csp.proyecto.export.grupos-investigacion-ip.nombre');
const GRUPO_IP_FECHA_INICIO = marker('csp.solicitud.export.grupos-investigacion-ip.fecha-inicio-participacion');
const GRUPO_IP_FECHA_FIN = marker('csp.solicitud.export.grupos-investigacion-ip.fecha-fin-participacion');

export interface ISolicitudGruposInvestigacionIpsData extends IGrupo {
  participacion: IGrupoEquipo;
}

@Injectable()
export class SolicitudGruposInvestigacionIpListadoExportService
  extends AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly solicitudService: SolicitudService,

    private readonly proyectoService: ProyectoService,
    private readonly personaService: PersonaService,
    private readonly grupoService: GrupoService,
  ) {
    super(translate);
  }

  public getData(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    return this.solicitudService.findEstadosSolicitud(solicitudData.id).pipe(
      switchMap(response => {
        const lastEstadoSolicitada = response.items
          .filter(estado => estado.estado === Estado.SOLICITADA)
          .reduce((lastEstado, estado) => (!lastEstado || estado.fechaEstado > lastEstado.fechaEstado) ? estado : lastEstado, null);

        if (!lastEstadoSolicitada) {
          return of(solicitudData);
        }

        return this.grupoService.findGruposPersona(solicitudData.solicitante.id, lastEstadoSolicitada.fechaEstado, lastEstadoSolicitada.fechaEstado).pipe(
          switchMap(grupos => from(grupos).pipe(
            mergeMap(grupo => {
              const options: SgiRestFindOptions = {
                filter: new RSQLSgiRestFilter('personaRef', SgiRestFilterOperator.EQUALS, solicitudData.solicitante.id)
                  .and('fechaParticipacionAnterior', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(lastEstadoSolicitada.fechaEstado, false))
                  .and('fechaParticipacionPosterior', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(lastEstadoSolicitada.fechaEstado, false))
              };

              return this.grupoService.findMiembrosEquipo(grupo.id, options).pipe(
                map(response => {
                  if (response.items?.length) {
                    solicitudData.gruposInvestigacionIp.push({
                      ...grupo,
                      participacion: response.items[0]
                    });
                  }

                  return solicitudData;
                })
              );
            }, this.DEFAULT_CONCURRENT),
          ))
        )
      })
    );
  }

  public fillColumns(
    solicitudes: ISolicitudReportData[],
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumGruposInvestigacionIp = Math.max(...solicitudes.map(p => p.gruposInvestigacionIp?.length));
    for (let i = 0; i < maxNumGruposInvestigacionIp; i++) {
      const grupoInvestigacionIpIndex: string = String(i + 1);
      const titleGrupoIp = this.translate.instant(GRUPO_IP_GRUPO) + ' ' + grupoInvestigacionIpIndex;

      const columnNombreIp: ISgiColumnReport = {
        name: 'ip' + grupoInvestigacionIpIndex + 'nombre',
        title: titleGrupoIp + ' - ' + this.translate.instant(GRUPO_IP_GRUPO_NOMBRE),
        type: ColumnType.STRING
      };
      columns.push(columnNombreIp);

      const columnFechaInicioParticipacion: ISgiColumnReport = {
        name: 'ip' + grupoInvestigacionIpIndex + 'fechaInicioParticipacion',
        title: titleGrupoIp + ' - ' + this.translate.instant(GRUPO_IP_FECHA_INICIO),
        type: ColumnType.DATE
      };
      columns.push(columnFechaInicioParticipacion);

      const columnFechaFinParticipacion: ISgiColumnReport = {
        name: 'ip' + grupoInvestigacionIpIndex + 'fechaFinParticipacion',
        title: titleGrupoIp + ' - ' + this.translate.instant(GRUPO_IP_FECHA_FIN),
        type: ColumnType.DATE
      };
      columns.push(columnFechaFinParticipacion);

    }

    return columns;
  }

  public fillRows(solicitudes: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {
    const solicitud = solicitudes[index];

    const elementsRow: any[] = [];

    const maxNumGruposInvestigacionIp = Math.max(...solicitudes.map(p => p.gruposInvestigacionIp?.length));
    for (let i = 0; i < maxNumGruposInvestigacionIp; i++) {
      elementsRow.push(solicitud.gruposInvestigacionIp[i]?.nombre ?? '');
      elementsRow.push(LuxonUtils.toBackend(solicitud.gruposInvestigacionIp[i]?.participacion?.fechaInicio) ?? '');
      elementsRow.push(LuxonUtils.toBackend(solicitud.gruposInvestigacionIp[i]?.participacion?.fechaFin) ?? '');
    }

    return elementsRow;
  }

}
