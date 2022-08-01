import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { ISolicitudRrhhTutor } from '@core/models/csp/solicitud-rrhh-tutor';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { IPersona } from '@core/models/sgp/persona';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudRrhhService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { CategoriaProfesionalService } from '@core/services/sgp/categoria-profesional.service';
import { NivelAcademicosService } from '@core/services/sgp/nivel-academico.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, zip } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ISolicitudReportData, ISolicitudReportOptions } from './solicitud-listado-export.service';

const COLUMN_VALUE_PREFIX = ': ';

const SOLICITUD_RRHH_KEY = marker('menu.csp.solicitud.solicitud-rrhh');
const SOLICITUD_RRHH_UNIVERSIDAD_KEY = marker('csp.solicitud.solicitud-rrhh.universidad-select');
const SOLICITUD_RRHH_AREA_ANEP_KEY = marker('csp.solicitud.solicitud-rrhh.area-anep');
const SOLICITUD_RRHH_TUTOR_KEY = marker('csp.solicitud.solicitud-rrhh.tutor');
const SOLICITUD_RRHH_TUTOR_NOMBRE_KEY = marker('csp.solicitud.solicitud-rrhh.nombre');
const SOLICITUD_RRHH_TUTOR_APELLIDOS_KEY = marker('csp.solicitud.solicitud-rrhh.apellidos');
const SOLICITUD_RRHH_TUTOR_EMAIL_KEY = marker('csp.solicitud.solicitud-rrhh.email');
const SOLICITUD_RRHH_TITULO_TRABAJO_KEY = marker('csp.solicitud.solicitud-rrhh.titulo-trabajo');
const SOLICITUD_RRHH_RESUMEN_KEY = marker('csp.solicitud.solicitud-rrhh.resumen');
const SOLICITUD_RRHH_OBSERVACIONES_KEY = marker('csp.solicitud.solicitud-rrhh.observaciones');
const REQUISITO_SOLICITANTE_NIVEL_ACADEMICO_KEY = marker('csp.solicitud.solicitud-rrhh.requisitos.solicitante-nivel-academico');
const REQUISITO_SOLICITANTE_CATEGORIA_PROFESIONAL_KEY = marker('csp.solicitud.solicitud-rrhh.requisitos.solicitante-categoria-profesional');
const REQUISITO_TUTOR_NIVEL_ACADEMICO_KEY = marker('csp.solicitud.solicitud-rrhh.requisitos.tutor-nivel-academico');
const REQUISITO_TUTOR_CATEGORIA_PROFESIONAL_KEY = marker('csp.solicitud.solicitud-rrhh.requisitos.tutor-categoria-profesional');
const NOMBRE_KEY = marker('csp.solicitud.solicitud-rrhh.nombre');
const DOCUMENTO_ACREDITATIVO_KEY = marker('csp.solicitud.solicitud-rrhh.documento-acreditativo');
const NIVEL_ACADEMICO_KEY = marker('csp.solicitud.solicitud-rrhh.requisitos.nivel-academico');
const CATEGORIA_PROFESIONAL_KEY = marker('csp.solicitud.solicitud-rrhh.requisitos.categoria-profesional');

const SOLICITUD_RRHH_UNIVERSIDAD_FIELD = 'solicitudRrhhUniversidad';
const SOLICITUD_RRHH_AREA_ANEP_FIELD = 'solicitudRrhhAreaAnep';
const SOLICITUD_RRHH_TUTOR_FIELD = 'solicitudRrhhTutor';
const SOLICITUD_RRHH_TUTOR_NOMBRE_FIELD = 'solicitudRrhhTutorNombre';
const SOLICITUD_RRHH_TUTOR_APELLIDOS_FIELD = 'solicitudRrhhTutorApellidos';
const SOLICITUD_RRHH_TUTOR_EMAIL_FIELD = 'solicitudRrhhTutorEmail';
const SOLICITUD_RRHH_TITULO_TRABAJO_FIELD = 'solicitudRrhhTituloTrabajo';
const SOLICITUD_RRHH_RESUMEN_FIELD = 'solicitudRrhhResumen';
const SOLICITUD_RRHH_OBSERVACIONES_FIELD = 'solicitudRrhhObservaciones';
const SOLICITUD_RRHH_FIELD = 'solicitudRrhhDatos';
const REQUISITO_SOLICITANTE_NIVEL_ACADEMICO_NOMBRE_FIELD = 'requisitoSolicitanteNivelAcademicoNombre';
const REQUISITO_SOLICITANTE_CATEGORIA_PROFESIONAL_NOMBRE_FIELD = 'requisitoSolicitanteCategoriaProfesionalNombre';
const REQUISITO_TUTOR_NIVEL_ACADEMICO_NOMBRE_FIELD = 'requisitoTutorNivelAcademicoNombre';
const REQUISITO_TUTOR_CATEGORIA_PROFESIONAL_NOMBRE_FIELD = 'requisitoTutorCategoriaProfesionalNombre';
const REQUISITO_SOLICITANTE_NIVEL_ACADEMICO_DOCUMENTO_FIELD = 'requisitoSolicitanteNivelAcademicoDocumento';
const REQUISITO_SOLICITANTE_CATEGORIA_PROFESIONAL_DOCUMENTO_FIELD = 'requisitoSolicitanteCategoriaProfesionalDocumento';
const REQUISITO_TUTOR_NIVEL_ACADEMICO_DOCUMENTO_FIELD = 'requisitoTutorNivelAcademicoDocumento';
const REQUISITO_TUTOR_CATEGORIA_PROFESIONAL_DOCUMENTO_FIELD = 'requisitoTutorCategoriaProfesionalDocumento';

@Injectable()
export class SolicitudRrhhListadoExportService extends AbstractTableExportFillService<ISolicitudReportData, ISolicitudReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private solicitudService: SolicitudService,
    private solicitudRrhhService: SolicitudRrhhService,
    private personaService: PersonaService,
    private empresaService: EmpresaService,
    private clasificacionService: ClasificacionService,
    private convocatoriaService: ConvocatoriaService,
    private categoriasProfesionalesService: CategoriaProfesionalService,
    private nivelAcademicoService: NivelAcademicosService
  ) {
    super(translate);
  }

  public getData(solicitudData: ISolicitudReportData): Observable<ISolicitudReportData> {
    return this.solicitudService.findSolicitudRrhh(solicitudData?.id).pipe(
      map(solicitudRrhh => {
        solicitudData.solicitudRrhh = solicitudRrhh;
        return solicitudData;
      }),
      switchMap((data) => {
        if (!data.solicitudRrhh) {
          return of(data);
        }
        return this.solicitudRrhhService.findTutor(data.solicitudRrhh.id).pipe(
          map(tutorSolicitudRrhh => {
            solicitudData.solicitudRrhhTutor = tutorSolicitudRrhh;
            return tutorSolicitudRrhh ?? {} as ISolicitudRrhhTutor;
          }),
          switchMap(tutorSolicitudRrhh => this.getDatosTutor(tutorSolicitudRrhh?.tutor)
            .pipe(
              map(tutor => {
                solicitudData.solicitudRrhhTutor.tutor = tutor;
                return solicitudData;
              }))
          )
        );
      }),
      switchMap((data) => {
        if (!data.solicitudRrhh) {
          return of(data);
        }
        return this.getDatosUniversidad(data.solicitudRrhh.universidad?.id).pipe(
          map(universidad => {
            solicitudData.solicitudRrhh.universidad = universidad;
            return solicitudData;
          }),
        );
      }),
      switchMap((data) => {
        if (!data.solicitudRrhh) {
          return of(data);
        }
        return this.getDatosAreaAnep(data.solicitudRrhh.areaAnep?.id).pipe(
          map(areaAnep => {
            solicitudData.solicitudRrhh.areaAnep = areaAnep;
            return solicitudData;
          }),
        );
      }),
      switchMap((data) => {
        if (!data.solicitudRrhh) {
          return of(data);
        }
        return this.solicitudRrhhService.findMemoria(data.solicitudRrhh.id).pipe(
          map(solicitudRrhhMemoria => {
            solicitudData.solicitudRrhhMemoria = solicitudRrhhMemoria;
            return solicitudData;
          }),
        );
      }),
      switchMap((data) => {
        if (!data.convocatoriaId) {
          return of(data);
        }
        return this.getRequisitosIpNivelesAcademicos(data.convocatoriaId).pipe(
          map(requisitosSolicitanteNivelAcademico => {
            solicitudData.requisitosSolicitanteNivelAcademico = requisitosSolicitanteNivelAcademico;
            return solicitudData;
          }),
        );
      }),
      switchMap((data) => {
        if (!data.convocatoriaId) {
          return of(data);
        }
        return this.getRequisitosIpCategoriasProfesionales(data.convocatoriaId).pipe(
          map(requisitosSolicitanteCategoriaProfesional => {
            solicitudData.requisitosSolicitanteCategoriaProfesional = requisitosSolicitanteCategoriaProfesional;
            return solicitudData;
          }),
        );
      }),
      switchMap((data) => {
        if (!data.convocatoriaId) {
          return of(data);
        }
        return this.getRequisitosEquipoNivelesAcademicos(data.convocatoriaId).pipe(
          map(requisitosTutorNivelAcademico => {
            solicitudData.requisitosTutorNivelAcademico = requisitosTutorNivelAcademico;
            return solicitudData;
          }),
        );
      }),
      switchMap((data) => {
        if (!data.convocatoriaId) {
          return of(data);
        }
        return this.getRequisitosEquipoCategoriasProfesionales(data.convocatoriaId).pipe(
          map(requisitosTutorCategoriaProfesional => {
            solicitudData.requisitosTutorCategoriaProfesional = requisitosTutorCategoriaProfesional;
            return solicitudData;
          }),
        );
      }),
    ).pipe(
      switchMap((data) => {
        if (!data.solicitudRrhh) {
          return of(data);
        }
        return this.solicitudRrhhService.findAllRequisitosNivelAcademicoAcreditados(data.solicitudRrhh.id).pipe(
          map(nivelesAcreditados => {
            solicitudData.requisitosAcreditadosNivelAcademico = nivelesAcreditados.items;
            return solicitudData;
          }),
        );
      }),
      switchMap((data) => {
        if (!data.solicitudRrhh) {
          return of(data);
        }
        return this.solicitudRrhhService.findAllRequisitosCategoriaAcreditados(data.solicitudRrhh.id).pipe(
          map(categoriasAcreditados => {
            solicitudData.requisitosAcreditadosCategoriaProfesional = categoriasAcreditados.items;
            return solicitudData;
          }),
        );
      }),
    );
  }


  private getDatosTutor(tutor: IPersona): Observable<IPersona> {
    return tutor?.id ? this.personaService.findById(tutor.id) : of(null);
  }

  private getDatosUniversidad(id: string): Observable<IEmpresa> {
    return id ? this.empresaService.findById(id) : of(null);
  }

  private getDatosAreaAnep(id: string): Observable<IClasificacion> {
    return id ? this.clasificacionService.findById(id) : of(null);
  }

  private getRequisitosEquipoCategoriasProfesionales(convocatoriaId: number): Observable<IRequisitoEquipoCategoriaProfesional[]> {
    if (!!!convocatoriaId) {
      return of([]);
    }

    return this.convocatoriaService.findRequisitosEquipoCategoriasProfesionales(convocatoriaId)
      .pipe(
        switchMap((requisitoEquipoCategoria) => {
          if (requisitoEquipoCategoria.length === 0) {
            return of([]);
          }
          const categoriasProfesionalesObservable = requisitoEquipoCategoria.
            map(requisitoEquipoCategoriaProfesional => {

              return this.categoriasProfesionalesService.findById(requisitoEquipoCategoriaProfesional.categoriaProfesional.id).pipe(
                map(categoriaProfesional => {
                  requisitoEquipoCategoriaProfesional.categoriaProfesional = categoriaProfesional;
                  return requisitoEquipoCategoriaProfesional;
                }),
              );
            });
          return zip(...categoriasProfesionalesObservable);
        })
      );
  }

  private getRequisitosIpCategoriasProfesionales(convocatoriaId: number): Observable<IRequisitoIPCategoriaProfesional[]> {
    if (!!!convocatoriaId) {
      return of([]);
    }

    return this.convocatoriaService.findRequisitosIpCategoriasProfesionales(convocatoriaId)
      .pipe(
        switchMap((requisitoIpCategoria) => {
          if (requisitoIpCategoria.length === 0) {
            return of([]);
          }
          const categoriasProfesionalesObservable = requisitoIpCategoria.
            map(requisitoIpCategoriaProfesional => {

              return this.categoriasProfesionalesService.findById(requisitoIpCategoriaProfesional.categoriaProfesional.id).pipe(
                map(categoriaProfesional => {
                  requisitoIpCategoriaProfesional.categoriaProfesional = categoriaProfesional;
                  return requisitoIpCategoriaProfesional;
                }),
              );
            });
          return zip(...categoriasProfesionalesObservable);
        })
      );
  }

  private getRequisitosEquipoNivelesAcademicos(convocatoriaId: number): Observable<IRequisitoEquipoNivelAcademico[]> {
    return this.convocatoriaService.findRequisitosEquipoNivelesAcademicos(convocatoriaId)
      .pipe(
        switchMap((requisitoEquipoNivelesAcademicos) => {
          if (requisitoEquipoNivelesAcademicos.length === 0) {
            return of([]);
          }
          const nivelesAcademicosObservable = requisitoEquipoNivelesAcademicos.
            map(requisitoIpNivelAcademico => {

              return this.nivelAcademicoService.findById(requisitoIpNivelAcademico.nivelAcademico.id).pipe(
                map(nivelAcademico => {
                  requisitoIpNivelAcademico.nivelAcademico = nivelAcademico;
                  return requisitoIpNivelAcademico;
                }),
              );
            });
          return zip(...nivelesAcademicosObservable);
        })
      );
  }

  private getRequisitosIpNivelesAcademicos(convocatoriaId: number): Observable<IRequisitoIPNivelAcademico[]> {
    return this.convocatoriaService.findRequisitosIpNivelesAcademicos(convocatoriaId)
      .pipe(
        switchMap((requisitoIpNivelesAcademicos) => {
          if (requisitoIpNivelesAcademicos.length === 0) {
            return of([]);
          }
          const nivelesAcademicosObservable = requisitoIpNivelesAcademicos.
            map(requisitoIpNivelAcademico => {

              return this.nivelAcademicoService.findById(requisitoIpNivelAcademico.nivelAcademico.id).pipe(
                map(nivelAcademico => {
                  requisitoIpNivelAcademico.nivelAcademico = nivelAcademico;
                  return requisitoIpNivelAcademico;
                }),
              );
            });
          return zip(...nivelesAcademicosObservable);
        })
      );
  }


  public fillColumns(
    solicitudes: ISolicitudReportData[],
    reportConfig: IReportConfig<ISolicitudReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsRrhhNotExcel(solicitudes);
    } else {
      const rrhhTitlePrefix = this.translate.instant(SOLICITUD_RRHH_KEY) + COLUMN_VALUE_PREFIX;
      return this.getColumnsRrhh(rrhhTitlePrefix, false, solicitudes);
    }
  }

  private getColumnsRrhhNotExcel(solicitudes: ISolicitudReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = this.getColumnsRrhh('', true, solicitudes);

    const titleI18n = this.translate.instant(SOLICITUD_RRHH_KEY);

    const titleReqSolicitanteNivelAcademico = this.translate.instant(REQUISITO_SOLICITANTE_NIVEL_ACADEMICO_KEY) + COLUMN_VALUE_PREFIX +
      ' (' + this.translate.instant(NOMBRE_KEY) +
      ' - ' + this.translate.instant(DOCUMENTO_ACREDITATIVO_KEY) +
      ')';

    const columnsHorReqSolicitanteNivelAcademico: ISgiColumnReport[] = [];
    const columnHorReqSolicitanteNivelAcademico: ISgiColumnReport = {
      name: REQUISITO_SOLICITANTE_NIVEL_ACADEMICO_NOMBRE_FIELD,
      title: this.translate.instant(NIVEL_ACADEMICO_KEY),
      type: ColumnType.STRING,
    };
    columnsHorReqSolicitanteNivelAcademico.push(columnHorReqSolicitanteNivelAcademico);

    const columnReqSolicitanteNivelAcademico: ISgiColumnReport = {
      name: REQUISITO_SOLICITANTE_NIVEL_ACADEMICO_NOMBRE_FIELD,
      title: titleReqSolicitanteNivelAcademico,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns: columnsHorReqSolicitanteNivelAcademico
    };

    const titleReqSolicitanteCategoriaProfesional = this.translate.instant(REQUISITO_SOLICITANTE_CATEGORIA_PROFESIONAL_KEY) + COLUMN_VALUE_PREFIX +
      ' (' + this.translate.instant(NOMBRE_KEY) +
      ' - ' + this.translate.instant(DOCUMENTO_ACREDITATIVO_KEY) +
      ')';

    const columnsHorReqSolicitanteCategoriaProfesional: ISgiColumnReport[] = [];
    const columnHorReqSolicitanteCategoriaProfesional: ISgiColumnReport = {
      name: REQUISITO_SOLICITANTE_CATEGORIA_PROFESIONAL_NOMBRE_FIELD,
      title: this.translate.instant(CATEGORIA_PROFESIONAL_KEY),
      type: ColumnType.STRING,
    };
    columnsHorReqSolicitanteCategoriaProfesional.push(columnHorReqSolicitanteCategoriaProfesional);

    const columnReqSolicitanteCategoriaProfesional: ISgiColumnReport = {
      name: REQUISITO_SOLICITANTE_CATEGORIA_PROFESIONAL_NOMBRE_FIELD,
      title: titleReqSolicitanteCategoriaProfesional,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns: columnsHorReqSolicitanteCategoriaProfesional
    };

    const titleReqTutorNivelAcademico = this.translate.instant(REQUISITO_TUTOR_NIVEL_ACADEMICO_KEY) + COLUMN_VALUE_PREFIX +
      ' (' + this.translate.instant(NOMBRE_KEY) +
      ' - ' + this.translate.instant(DOCUMENTO_ACREDITATIVO_KEY) +
      ')';

    const columnsHorReqTutorNivelAcademico: ISgiColumnReport[] = [];
    const columnHorReqTutorNivelAcademico: ISgiColumnReport = {
      name: REQUISITO_TUTOR_NIVEL_ACADEMICO_NOMBRE_FIELD,
      title: this.translate.instant(NIVEL_ACADEMICO_KEY),
      type: ColumnType.STRING,
    };
    columnsHorReqTutorNivelAcademico.push(columnHorReqTutorNivelAcademico);

    const columnReqTutorNivelAcademico: ISgiColumnReport = {
      name: REQUISITO_TUTOR_NIVEL_ACADEMICO_NOMBRE_FIELD,
      title: titleReqTutorNivelAcademico,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns: columnsHorReqTutorNivelAcademico
    };

    const titleReqTutorCategoriaProfesional = this.translate.instant(REQUISITO_TUTOR_CATEGORIA_PROFESIONAL_KEY) + COLUMN_VALUE_PREFIX +
      ' (' + this.translate.instant(NOMBRE_KEY) +
      ' - ' + this.translate.instant(DOCUMENTO_ACREDITATIVO_KEY) +
      ')';

    const columnsReqTutorCategoriaProfesional: ISgiColumnReport[] = [];
    const columnHorReqTutorCategoriaProfesional: ISgiColumnReport = {
      name: REQUISITO_TUTOR_CATEGORIA_PROFESIONAL_NOMBRE_FIELD,
      title: this.translate.instant(CATEGORIA_PROFESIONAL_KEY),
      type: ColumnType.STRING,
    };
    columnsReqTutorCategoriaProfesional.push(columnHorReqTutorCategoriaProfesional);

    const columnReqTutorCategoriaProfesional: ISgiColumnReport = {
      name: REQUISITO_TUTOR_CATEGORIA_PROFESIONAL_NOMBRE_FIELD,
      title: titleReqTutorCategoriaProfesional,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns: columnsReqTutorCategoriaProfesional
    };

    const columnEntidad: ISgiColumnReport = {
      name: SOLICITUD_RRHH_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };

    const columnsVerticales: ISgiColumnReport[] = [];
    columnsVerticales.push(columnEntidad);
    columnsVerticales.push(columnReqSolicitanteNivelAcademico);
    columnsVerticales.push(columnReqSolicitanteCategoriaProfesional);
    columnsVerticales.push(columnReqTutorNivelAcademico);
    columnsVerticales.push(columnReqTutorCategoriaProfesional);
    return columnsVerticales;
  }

  private getColumnsRrhh(prefix: string, allString: boolean, solicitudes: ISolicitudReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumReqSolicitanteNivelAcademico = Math.max(...solicitudes.map(s => s.requisitosSolicitanteNivelAcademico ? s.requisitosSolicitanteNivelAcademico?.length : 0));
    const titleReqSolicitanteNivelAcademico = prefix + this.translate.instant(REQUISITO_SOLICITANTE_NIVEL_ACADEMICO_KEY) + this.getValuePrefix(prefix);

    const maxNumReqSolicitanteCategoriaProfesional = Math.max(...solicitudes.map(s => s.requisitosSolicitanteCategoriaProfesional ? s.requisitosSolicitanteCategoriaProfesional?.length : 0));
    const titleReqSolicitanteCategoriaProfesional = prefix + this.translate.instant(REQUISITO_SOLICITANTE_CATEGORIA_PROFESIONAL_KEY) + this.getValuePrefix(prefix);

    const maxNumReqTutorNivelAcademico = Math.max(...solicitudes.map(s => s.requisitosTutorNivelAcademico ? s.requisitosTutorNivelAcademico?.length : 0));
    const titleReqTutorNivelAcademico = prefix + this.translate.instant(REQUISITO_TUTOR_NIVEL_ACADEMICO_KEY) + this.getValuePrefix(prefix);

    const maxNumReqTutorCategoriaProfesional = Math.max(...solicitudes.map(s => s.requisitosTutorCategoriaProfesional ? s.requisitosTutorCategoriaProfesional?.length : 0));
    const titleReqTutorCategoriaProfesional = prefix + this.translate.instant(REQUISITO_TUTOR_CATEGORIA_PROFESIONAL_KEY) + this.getValuePrefix(prefix);

    const columnUniversidad: ISgiColumnReport = {
      name: SOLICITUD_RRHH_UNIVERSIDAD_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_RRHH_UNIVERSIDAD_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnUniversidad);

    const columnArea: ISgiColumnReport = {
      name: SOLICITUD_RRHH_AREA_ANEP_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_RRHH_AREA_ANEP_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnArea);

    const columnTutorNombre: ISgiColumnReport = {
      name: SOLICITUD_RRHH_TUTOR_NOMBRE_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_RRHH_TUTOR_KEY) + COLUMN_VALUE_PREFIX + this.translate.instant(SOLICITUD_RRHH_TUTOR_NOMBRE_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnTutorNombre);

    const columnTutorApellidos: ISgiColumnReport = {
      name: SOLICITUD_RRHH_TUTOR_APELLIDOS_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_RRHH_TUTOR_KEY) + COLUMN_VALUE_PREFIX + this.translate.instant(SOLICITUD_RRHH_TUTOR_APELLIDOS_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnTutorApellidos);

    const columnTutorEmail: ISgiColumnReport = {
      name: SOLICITUD_RRHH_TUTOR_EMAIL_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_RRHH_TUTOR_KEY) + COLUMN_VALUE_PREFIX + this.translate.instant(SOLICITUD_RRHH_TUTOR_EMAIL_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnTutorEmail);

    const columnTituloTrabajo: ISgiColumnReport = {
      name: SOLICITUD_RRHH_TITULO_TRABAJO_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_RRHH_TITULO_TRABAJO_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnTituloTrabajo);

    const columnResumen: ISgiColumnReport = {
      name: SOLICITUD_RRHH_RESUMEN_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_RRHH_RESUMEN_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnResumen);

    const columnObservaciones: ISgiColumnReport = {
      name: SOLICITUD_RRHH_OBSERVACIONES_FIELD,
      title: prefix + this.translate.instant(SOLICITUD_RRHH_OBSERVACIONES_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnObservaciones);

    if (!allString) {
      for (let i = 0; i < maxNumReqSolicitanteNivelAcademico; i++) {
        const idReqSolicitanteNivelAcademico: string = String(i + 1);
        const columnNombreReqSolicitanteNivelAcademico: ISgiColumnReport = {
          name: REQUISITO_SOLICITANTE_NIVEL_ACADEMICO_NOMBRE_FIELD + idReqSolicitanteNivelAcademico,
          title: titleReqSolicitanteNivelAcademico + idReqSolicitanteNivelAcademico + ': ' + this.translate.instant(NOMBRE_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnNombreReqSolicitanteNivelAcademico);

        const columnDocumentoReqSolicitanteNivelAcademico: ISgiColumnReport = {
          name: REQUISITO_SOLICITANTE_NIVEL_ACADEMICO_DOCUMENTO_FIELD + idReqSolicitanteNivelAcademico,
          title: titleReqSolicitanteNivelAcademico + idReqSolicitanteNivelAcademico + ': ' + this.translate.instant(DOCUMENTO_ACREDITATIVO_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnDocumentoReqSolicitanteNivelAcademico);
      }

      for (let i = 0; i < maxNumReqSolicitanteCategoriaProfesional; i++) {
        const idReqSolicitanteCategoriaProfesional: string = String(i + 1);
        const columnNombreReqSolicitanteCategoriaProfesional: ISgiColumnReport = {
          name: REQUISITO_SOLICITANTE_CATEGORIA_PROFESIONAL_NOMBRE_FIELD + idReqSolicitanteCategoriaProfesional,
          title: titleReqSolicitanteCategoriaProfesional + idReqSolicitanteCategoriaProfesional + ': ' + this.translate.instant(NOMBRE_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnNombreReqSolicitanteCategoriaProfesional);

        const columnDocumentoReqSolicitanteCategoriaProfesional: ISgiColumnReport = {
          name: REQUISITO_SOLICITANTE_CATEGORIA_PROFESIONAL_DOCUMENTO_FIELD + idReqSolicitanteCategoriaProfesional,
          title: titleReqSolicitanteCategoriaProfesional + idReqSolicitanteCategoriaProfesional + ': ' + this.translate.instant(DOCUMENTO_ACREDITATIVO_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnDocumentoReqSolicitanteCategoriaProfesional);
      }

      for (let i = 0; i < maxNumReqTutorNivelAcademico; i++) {
        const idReqTutorNivelAcademico: string = String(i + 1);
        const columnNombreReqTutorNivelAcademico: ISgiColumnReport = {
          name: REQUISITO_TUTOR_NIVEL_ACADEMICO_NOMBRE_FIELD + idReqTutorNivelAcademico,
          title: titleReqTutorNivelAcademico + idReqTutorNivelAcademico + ': ' + this.translate.instant(NOMBRE_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnNombreReqTutorNivelAcademico);

        const columnDocumentoReqTutorNivelAcademico: ISgiColumnReport = {
          name: REQUISITO_TUTOR_NIVEL_ACADEMICO_DOCUMENTO_FIELD + idReqTutorNivelAcademico,
          title: titleReqTutorNivelAcademico + idReqTutorNivelAcademico + ': ' + this.translate.instant(DOCUMENTO_ACREDITATIVO_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnDocumentoReqTutorNivelAcademico);
      }

      for (let i = 0; i < maxNumReqTutorCategoriaProfesional; i++) {
        const idReqTutorCategoriaProfesional: string = String(i + 1);
        const columnNombreReqTutorCategoriaProfesional: ISgiColumnReport = {
          name: REQUISITO_TUTOR_CATEGORIA_PROFESIONAL_NOMBRE_FIELD + idReqTutorCategoriaProfesional,
          title: titleReqTutorCategoriaProfesional + idReqTutorCategoriaProfesional + ': ' + this.translate.instant(NOMBRE_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnNombreReqTutorCategoriaProfesional);

        const columnDocumentoReqTutorCategoriaProfesional: ISgiColumnReport = {
          name: REQUISITO_TUTOR_CATEGORIA_PROFESIONAL_DOCUMENTO_FIELD + idReqTutorCategoriaProfesional,
          title: titleReqTutorCategoriaProfesional + idReqTutorCategoriaProfesional + ': ' + this.translate.instant(DOCUMENTO_ACREDITATIVO_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnDocumentoReqTutorCategoriaProfesional);
      }
    }

    return columns;
  }

  public fillRows(solicitudes: ISolicitudReportData[], index: number, reportConfig: IReportConfig<ISolicitudReportOptions>): any[] {
    const solicitud = solicitudes[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsRrhhNotExcel1(solicitud, elementsRow);
      this.fillRowsRrhhNotExcel2(solicitud, elementsRow);
      this.fillRowsRrhhNotExcel3(solicitud, elementsRow);
      this.fillRowsRrhhNotExcel4(solicitud, elementsRow);
      this.fillRowsRrhhNotExcel5(solicitud, elementsRow);
    } else {
      this.fillRowsRrhhExcel(elementsRow, solicitud, solicitudes);
    }
    return elementsRow;
  }

  private fillRowsRrhhNotExcel1(solicitud: ISolicitudReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];
    if (solicitud.solicitudRrhh) {
      const datosRrhh: any[] = [];
      datosRrhh.push(solicitud.solicitudRrhh.universidad?.nombre ?? solicitud.solicitudRrhh.universidadDatos);
      datosRrhh.push(solicitud.solicitudRrhh.areaAnep?.nombre ?? '');
      datosRrhh.push(solicitud.solicitudRrhhTutor?.tutor?.nombre ?? '');
      datosRrhh.push(solicitud.solicitudRrhhTutor?.tutor?.apellidos ?? '');
      datosRrhh.push(solicitud.solicitudRrhhTutor?.tutor?.emails ? solicitud.solicitudRrhhTutor?.tutor?.emails[0].email : '');
      datosRrhh.push(solicitud.solicitudRrhhMemoria?.tituloTrabajo ?? '');
      datosRrhh.push(solicitud.solicitudRrhhMemoria?.resumen ?? '');
      datosRrhh.push(solicitud.solicitudRrhhMemoria?.observaciones ?? '');

      const rowReport: ISgiRowReport = {
        elements: datosRrhh
      };
      rowsReport.push(rowReport);
    }
    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsRrhhNotExcel2(solicitud: ISolicitudReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];
    if (solicitud.solicitudRrhh) {

      if (solicitud.requisitosSolicitanteNivelAcademico) {
        solicitud.requisitosSolicitanteNivelAcademico.forEach(requisitoSolicitanteNivelAcademico => {
          const datosRrhh: any[] = [];
          const solicitanteNivelAcademicoDocumentoAcreditativo =
            solicitud.requisitosAcreditadosNivelAcademico.some(req => req.requisitoIpNivelAcademico.id === requisitoSolicitanteNivelAcademico.id);
          let requisitosSolicitanteNivelAcademicoTable = requisitoSolicitanteNivelAcademico.nivelAcademico?.nombre ?? '';
          requisitosSolicitanteNivelAcademicoTable += '\n';
          requisitosSolicitanteNivelAcademicoTable += this.getI18nBooleanYesNo(solicitanteNivelAcademicoDocumentoAcreditativo);

          datosRrhh.push(requisitosSolicitanteNivelAcademicoTable);

          const rowReport: ISgiRowReport = {
            elements: datosRrhh
          };
          rowsReport.push(rowReport);
        });
      }
    }
    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsRrhhNotExcel3(solicitud: ISolicitudReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];
    if (solicitud.solicitudRrhh) {

      if (solicitud.requisitosSolicitanteCategoriaProfesional) {
        solicitud.requisitosSolicitanteCategoriaProfesional.forEach(requisitoSolicitanteCategoriaProfesional => {
          const datosRrhh: any[] = [];
          const solicitanteCategoriaDocumentoAcreditativo =
            solicitud.requisitosAcreditadosCategoriaProfesional.some(req => req.requisitoIpCategoria.id === requisitoSolicitanteCategoriaProfesional.id);
          let requisitosSolicitanteCategoriaProfesionalTable = requisitoSolicitanteCategoriaProfesional.categoriaProfesional?.nombre ?? '';
          requisitosSolicitanteCategoriaProfesionalTable += '\n';
          requisitosSolicitanteCategoriaProfesionalTable += this.getI18nBooleanYesNo(solicitanteCategoriaDocumentoAcreditativo);

          datosRrhh.push(requisitosSolicitanteCategoriaProfesionalTable);

          const rowReport: ISgiRowReport = {
            elements: datosRrhh
          };
          rowsReport.push(rowReport);
        });
      }
    }
    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsRrhhNotExcel4(solicitud: ISolicitudReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];
    if (solicitud.solicitudRrhh) {

      if (solicitud.requisitosTutorNivelAcademico) {
        solicitud.requisitosTutorNivelAcademico.forEach(requisitoTutorNivelAcademico => {
          const datosRrhh: any[] = [];
          const tutorNivelAcademicoDocumentoAcreditativo =
            solicitud.requisitosAcreditadosNivelAcademico.some(req => req.requisitoIpNivelAcademico.id === requisitoTutorNivelAcademico.id);
          let requisitosTutorNivelAcademicoTable = requisitoTutorNivelAcademico.nivelAcademico?.nombre ?? '';
          requisitosTutorNivelAcademicoTable += '\n';
          requisitosTutorNivelAcademicoTable += this.getI18nBooleanYesNo(tutorNivelAcademicoDocumentoAcreditativo);

          datosRrhh.push(requisitosTutorNivelAcademicoTable);

          const rowReport: ISgiRowReport = {
            elements: datosRrhh
          };
          rowsReport.push(rowReport);
        });
      }
    }
    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsRrhhNotExcel5(solicitud: ISolicitudReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];
    if (solicitud.solicitudRrhh) {

      if (solicitud.requisitosTutorCategoriaProfesional) {
        solicitud.requisitosTutorCategoriaProfesional.forEach(requisitoTutorCategoriaProfesional => {
          const datosRrhh: any[] = [];
          const tutorCategoriaDocumentoAcreditativo =
            solicitud.requisitosAcreditadosCategoriaProfesional.some(req => req.requisitoIpCategoria.id === requisitoTutorCategoriaProfesional.id);
          let requisitosTutorCategoriaProfesionalTable = requisitoTutorCategoriaProfesional.categoriaProfesional?.nombre ?? '';
          requisitosTutorCategoriaProfesionalTable += '\n';
          requisitosTutorCategoriaProfesionalTable += this.getI18nBooleanYesNo(tutorCategoriaDocumentoAcreditativo);

          datosRrhh.push(requisitosTutorCategoriaProfesionalTable);

          const rowReport: ISgiRowReport = {
            elements: datosRrhh
          };
          rowsReport.push(rowReport);
        });
      }

    }
    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsRrhhExcel(elementsRow: any[], solicitud: ISolicitudReportData, solicitudes: ISolicitudReportData[]) {
    if (solicitud.solicitudRrhh) {
      elementsRow.push(solicitud.solicitudRrhh.universidad?.nombre ?? solicitud.solicitudRrhh.universidadDatos);
      elementsRow.push(solicitud.solicitudRrhh.areaAnep?.nombre ?? '');
      elementsRow.push(solicitud.solicitudRrhhTutor?.tutor?.nombre ?? '');
      elementsRow.push(solicitud.solicitudRrhhTutor?.tutor?.apellidos ?? '');
      elementsRow.push(solicitud.solicitudRrhhTutor?.tutor?.emails ? solicitud.solicitudRrhhTutor?.tutor?.emails[0].email : '');
      elementsRow.push(solicitud.solicitudRrhhMemoria?.tituloTrabajo ?? '');
      elementsRow.push(solicitud.solicitudRrhhMemoria?.resumen ?? '');
      elementsRow.push(solicitud.solicitudRrhhMemoria?.observaciones ?? '');

      if (solicitud.requisitosSolicitanteNivelAcademico) {
        solicitud.requisitosSolicitanteNivelAcademico.forEach(requisitoSolicitanteNivelAcademico => {
          const solicitanteNivelAcademicoDocumentoAcreditativo =
            solicitud.requisitosAcreditadosNivelAcademico.some(req => req.requisitoIpNivelAcademico.id === requisitoSolicitanteNivelAcademico.id);
          elementsRow.push(requisitoSolicitanteNivelAcademico.nivelAcademico?.nombre ?? '');
          elementsRow.push(this.getI18nBooleanYesNo(solicitanteNivelAcademicoDocumentoAcreditativo));
        });
      } else {
        const maxNumReqSolicitanteNivelAcademico = Math.max(...solicitudes.map(s => s.requisitosSolicitanteNivelAcademico ? s.requisitosSolicitanteNivelAcademico?.length : 0));
        for (let i = 0; i < maxNumReqSolicitanteNivelAcademico; i++) {
          elementsRow.push('');
          elementsRow.push('');
        }
      }

      if (solicitud.requisitosSolicitanteCategoriaProfesional) {
        solicitud.requisitosSolicitanteCategoriaProfesional.forEach(requisitoSolicitanteCategoriaProfesional => {
          const solicitanteCategoriaDocumentoAcreditativo =
            solicitud.requisitosAcreditadosCategoriaProfesional.some(req => req.requisitoIpCategoria.id === requisitoSolicitanteCategoriaProfesional.id);
          elementsRow.push(requisitoSolicitanteCategoriaProfesional.categoriaProfesional?.nombre ?? '');
          elementsRow.push(this.getI18nBooleanYesNo(solicitanteCategoriaDocumentoAcreditativo));
        });
      } else {
        const maxNumReqSolicitanteCategoriaProfesional = Math.max(...solicitudes.map(s => s.requisitosSolicitanteCategoriaProfesional ? s.requisitosSolicitanteCategoriaProfesional?.length : 0));
        for (let i = 0; i < maxNumReqSolicitanteCategoriaProfesional; i++) {
          elementsRow.push('');
          elementsRow.push('');
        }
      }

      if (solicitud.requisitosTutorNivelAcademico) {
        solicitud.requisitosTutorNivelAcademico.forEach(requisitoTutorNivelAcademico => {
          const tutorNivelAcademicoDocumentoAcreditativo =
            solicitud.requisitosAcreditadosNivelAcademico.some(req => req.requisitoIpNivelAcademico.id === requisitoTutorNivelAcademico.id);
          elementsRow.push(requisitoTutorNivelAcademico.nivelAcademico?.nombre ?? '');
          elementsRow.push(this.getI18nBooleanYesNo(tutorNivelAcademicoDocumentoAcreditativo));
        });
      } else {
        const maxNumReqTutorNivelAcademico = Math.max(...solicitudes.map(s => s.requisitosTutorNivelAcademico ? s.requisitosTutorNivelAcademico?.length : 0));
        for (let i = 0; i < maxNumReqTutorNivelAcademico; i++) {
          elementsRow.push('');
          elementsRow.push('');
        }
      }

      if (solicitud.requisitosTutorCategoriaProfesional) {
        solicitud.requisitosTutorCategoriaProfesional.forEach(requisitoTutorCategoriaProfesional => {
          const tutorCategoriaDocumentoAcreditativo =
            solicitud.requisitosAcreditadosCategoriaProfesional.some(req => req.requisitoIpCategoria.id === requisitoTutorCategoriaProfesional.id);
          elementsRow.push(requisitoTutorCategoriaProfesional.categoriaProfesional?.nombre ?? '');
          elementsRow.push(this.getI18nBooleanYesNo(tutorCategoriaDocumentoAcreditativo));
        });
      } else {
        const maxNumReqTutorCategoriaProfesional = Math.max(...solicitudes.map(s => s.requisitosTutorCategoriaProfesional ? s.requisitosTutorCategoriaProfesional?.length : 0));
        for (let i = 0; i < maxNumReqTutorCategoriaProfesional; i++) {
          elementsRow.push('');
          elementsRow.push('');
        }
      }

    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');

      const maxNumReqSolicitanteNivelAcademico = Math.max(...solicitudes.map(s => s.requisitosSolicitanteNivelAcademico ? s.requisitosSolicitanteNivelAcademico?.length : 0));
      for (let i = 0; i < maxNumReqSolicitanteNivelAcademico; i++) {
        elementsRow.push('');
        elementsRow.push('');
      }
      const maxNumReqSolicitanteCategoriaProfesional = Math.max(...solicitudes.map(s => s.requisitosSolicitanteCategoriaProfesional ? s.requisitosSolicitanteCategoriaProfesional?.length : 0));
      for (let i = 0; i < maxNumReqSolicitanteCategoriaProfesional; i++) {
        elementsRow.push('');
        elementsRow.push('');
      }
      const maxNumReqTutorNivelAcademico = Math.max(...solicitudes.map(s => s.requisitosTutorNivelAcademico ? s.requisitosTutorNivelAcademico?.length : 0));
      for (let i = 0; i < maxNumReqTutorNivelAcademico; i++) {
        elementsRow.push('');
        elementsRow.push('');
      }
      const maxNumReqTutorCategoriaProfesional = Math.max(...solicitudes.map(s => s.requisitosTutorCategoriaProfesional ? s.requisitosTutorCategoriaProfesional?.length : 0));
      for (let i = 0; i < maxNumReqTutorCategoriaProfesional; i++) {
        elementsRow.push('');
        elementsRow.push('');
      }
    }
  }

  private getValuePrefix(prefix: string): string {
    if (!prefix) {
      return COLUMN_VALUE_PREFIX;
    }
    return '';
  }
}
