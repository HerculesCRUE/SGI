import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { IEmail } from '@core/models/sgp/email';
import { IPersona } from '@core/models/sgp/persona';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ISolicitudReportData, ISolicitudReportOptions } from './solicitud-listado-export.service';

const CODIGO_INTERNO_KEY = marker('csp.solicitud.codigo');
const CODIGO_EXTERNO_KEY = marker('csp.solicitud.codigo-externo');
const REFERENCIA_KEY = marker('csp.solicitud.referencia-convocatoria.no-registrada-sgi');
const CONVOCATORIA_SGI_KEY = marker('csp.solicitud.convocatoria-sgi');
const SOLICITANTE_KEY = marker('csp.solicitud.solicitante');
const NOMBRE_KEY = marker('sgp.nombre');
const APELLIDOS_KEY = marker('sgp.apellidos');
const EMAIL_KEY = marker('sgp.email');
const ESTADO_KEY = marker('csp.solicitud.estado');
const TITULO_KEY = marker('csp.solicitud.titulo-listado');
const FECHA_ESTADO_KEY = marker('csp.solicitud.estado-solicitud.fecha');

@Injectable()
export class SolicitudGeneralListadoExportService extends AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly convocatoriaService: ConvocatoriaService,
    private readonly solicitudService: SolicitudService,
    private readonly personaService: PersonaService
  ) {
    super(translate);
  }

  public getData(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    return of(solicitudData).pipe(
      switchMap(() => {
        if (!!!solicitudData.solicitante?.id && !!solicitudData.id) {
          return this.solicitudService.findSolicitanteExterno(solicitudData?.id).pipe(
            map(solicitanteExterno => {
              solicitudData.solicitanteExterno = solicitanteExterno;
              return solicitudData;
            })
          );
        } else {
          return this.getSolicitante(solicitudData.solicitante).pipe(
            map(solicitante => {
              solicitudData.solicitante = solicitante;
              return solicitudData;
            })
          );
        }
      }),
      switchMap(() => {
        if (solicitudData.convocatoriaId) {
          return this.convocatoriaService.findById(solicitudData.convocatoriaId).pipe(
            map(convocatoria => {
              solicitudData.convocatoria = convocatoria;
              return solicitudData;
            })
          );
        } else {
          return of(solicitudData);
        }
      })
    );
  }

  private getSolicitante(solicitante: IPersona): Observable<IPersona> {
    return solicitante?.id ? this.personaService.findById(solicitante.id) : of(null);
  }

  public fillColumns(
    solicitudes: ISolicitudReportData[],
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(TITULO_KEY),
        name: 'titulo',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(CODIGO_INTERNO_KEY),
        name: 'codigoRegistroInterno',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(CODIGO_EXTERNO_KEY),
        name: 'codigoExterno',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(SOLICITANTE_KEY) + ': ' + this.translate.instant(NOMBRE_KEY),
        name: 'nombreSolicitante',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(SOLICITANTE_KEY) + ': ' + this.translate.instant(APELLIDOS_KEY),
        name: 'apellidosSolicitante',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(SOLICITANTE_KEY) + ': ' + this.translate.instant(EMAIL_KEY),
        name: 'emailSolicitante',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(CONVOCATORIA_SGI_KEY),
        name: 'convocatoria',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(REFERENCIA_KEY),
        name: 'convocatoriaExterna',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(ESTADO_KEY),
        name: 'estado',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(FECHA_ESTADO_KEY),
        name: 'fechaEstado',
        type: ColumnType.DATE
      }
    ];

    return columns;
  }

  public fillRows(resultados: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {
    const solicitud = resultados[index];

    const solicitante = solicitud.solicitante ?? {
      nombre: solicitud.solicitanteExterno?.nombre,
      apellidos: solicitud.solicitanteExterno?.apellidos,
      emails: [
        {
          email: solicitud.solicitanteExterno?.email
        } as IEmail
      ]
    } as IPersona;

    const elementsRow: any[] = [];
    elementsRow.push(solicitud.titulo);
    elementsRow.push(solicitud.codigoRegistroInterno ?? '');
    elementsRow.push(solicitud.codigoExterno ?? '');
    elementsRow.push(solicitante?.nombre ?? '');
    elementsRow.push(solicitante?.apellidos ?? '');
    elementsRow.push(solicitante?.emails[0] ? solicitante?.emails[0].email : '');
    elementsRow.push(solicitud.convocatoria?.titulo ?? '');
    elementsRow.push(solicitud.convocatoriaExterna ?? '');
    elementsRow.push(solicitud.estado?.estado ?? '');
    elementsRow.push(LuxonUtils.toBackend(solicitud.estado?.fechaEstado));
    return elementsRow;
  }
}
