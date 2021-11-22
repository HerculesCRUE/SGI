import { ValidacionRequisitosEquipoIp } from '@core/enums/validaciones-requisitos-equipo-ip';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaRequisitoEquipoService } from '@core/services/csp/convocatoria-requisito-equipo.service';
import { ConvocatoriaRequisitoIPService } from '@core/services/csp/convocatoria-requisito-ip.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoEquipoService } from '@core/services/csp/proyecto-equipo.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { DatosPersonalesService } from '@core/services/sgp/datos-personales.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, forkJoin, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export enum HelpIconClass {
  DANGER = 'danger',
}

enum ErroresRequisitos {
  FECHA_OBTENCION = 'fechaObtencion',
  FECHA_MAYOR = 'fechaMayorMax',
  FECHA_MENOR = 'fechaMenorMin',
  NIVEL_ACADEMICO = 'nivelAcademico',
  SEXO = 'sexo',
  DATOS_ACADEMICOS = 'datosAcademicos',
  CATEGORIAS_PROFESIONALES = 'categoriasProfesionales',
  VINCULACION = 'vinculacion',
  NO_VINCULACION = 'noVinculacion',
  FECHA_VINCULACION_MAYOR = 'fechaVinculacionMayorMax',
  FECHA_VINCULACION_MENOR = 'fechaVinculacionMenorMin',
  NO_FECHAS_VINCUALCION = 'noFechasVinculacion',
  FECHA_OBTENCION_MAYOR = 'fechaObtencionMayorMax',
  FECHA_OBTENCION_MENOR = 'fechaObtencionMenorMin',
  EDAD = 'edadMax',
}

interface HelpIcon {
  class: HelpIconClass;
  tooltip: string;
}

interface ErrorResponse {
  isValid: boolean;
  msgError: ErroresRequisitos;
}

export interface IProyectoEquipoListado {
  proyectoEquipo: IProyectoEquipo;
  help: HelpIcon;
}

interface RequisitosConvocatoria {
  nivelesAcademicosRequisitosEquipo: IRequisitoEquipoNivelAcademico[];
  nivelesAcademicosRequisitosIp: IRequisitoIPNivelAcademico[];
  categoriasProfesionalesRequisitosEquipo: ICategoriaProfesional[];
  categoriasProfesionalesRequisitosIp: ICategoriaProfesional[];
  requisitosIp: IConvocatoriaRequisitoIP;
  requisitosEquipo: IConvocatoriaRequisitoEquipo;
}

export class ProyectoEquipoFragment extends Fragment {
  equipos$ = new BehaviorSubject<StatusWrapper<IProyectoEquipoListado>[]>([]);
  msgToolTip: string;
  msgToolTipFechaObtencion: string;
  msgToolTipFechaMax: string;
  msgToolTipFechaMin: string;
  msgToolTipNivelAcademico: string;
  msgToolTipSexo: string;
  msgToolTipDatosAcademicos: string;
  msgToolTipCategoriasProfesionales: string;
  msgToolTipVinculacion: string;
  msgToolTipFechaVinculacionMayorMax: string;
  msgToolTipFechaVinculacionMenorMin: string;
  msgToolTipNoFechas: string;
  msgToolTipEdadMax: string;
  msgToolTipNoVinculacion: string;
  msgToolTipFechaObtencionMayor: string;
  msgToolTipFechaObtencionMenor: string;

  requisitosConvocatoria: RequisitosConvocatoria;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private convocatoriaId: number,
    private proyectoService: ProyectoService,
    private proyectoEquipoService: ProyectoEquipoService,
    private personaService: PersonaService,
    private convocatoriaService: ConvocatoriaService,
    private datosAcademicosService: DatosAcademicosService,
    private convocatoriaRequisitoIpService: ConvocatoriaRequisitoIPService,
    private vinculacionService: VinculacionService,
    private convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoService,
    private datosPersonalesService: DatosPersonalesService,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoService.findAllProyectoEquipo(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(element => {
                return this.personaService.findById(element.persona.id).pipe(
                  map(persona => {
                    element.persona = persona;
                    return element;
                  })
                );
              }),
              map(() => result)
            );
          }),
          map(proyectosEquipo => {
            return proyectosEquipo.items.map(proyectoEquipo => new StatusWrapper<IProyectoEquipoListado>(
              {
                proyectoEquipo,

              } as IProyectoEquipoListado));
          }),
          switchMap(result => {
            return from(result).pipe(
              mergeMap(element => {
                return this.validateRequisitosConvocatoria(element.value.proyectoEquipo, this.convocatoriaId).pipe(
                  map(response => {
                    this.buildHelpIconIfNeeded(response, element);
                    return element;
                  })
                );
              }),
              map(() => result)
            );
          }),
        ).subscribe(
          result => {
            this.equipos$.next(result);
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addProyectoEquipo(element: IProyectoEquipoListado) {
    const wrapper = new StatusWrapper<IProyectoEquipoListado>({
      proyectoEquipo: element.proyectoEquipo,
      help: null
    } as IProyectoEquipoListado);
    this.validateRequisitosConvocatoria(wrapper.value.proyectoEquipo, this.convocatoriaId).subscribe(
      (response => {
        this.buildHelpIconIfNeeded(response, wrapper);
        wrapper.setCreated();
        const current = this.equipos$.value;
        current.push(wrapper);
        this.equipos$.next(current);
        this.setChanges(true);
        return element;
      })
    );

  }

  updateProyectoEquipo(wrapper: StatusWrapper<IProyectoEquipoListado>): void {
    const current = this.equipos$.value;
    const index = current.findIndex(value => value.value.proyectoEquipo.id === wrapper.value.proyectoEquipo.id);
    if (index >= 0) {
      wrapper.setEdited();
      wrapper.value.help = {
        class: HelpIconClass.DANGER
      } as HelpIcon;
      this.validateRequisitosConvocatoria(wrapper.value.proyectoEquipo, this.convocatoriaId).subscribe(
        (response => {
          this.buildHelpIconIfNeeded(response, wrapper);
          this.equipos$.value[index] = wrapper;
          this.setChanges(true);
        })
      );
    }
  }

  deleteProyectoEquipo(wrapper: StatusWrapper<IProyectoEquipoListado>) {
    const current = this.equipos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      this.validateRequisitosConvocatoria(wrapper.value.proyectoEquipo, this.convocatoriaId).subscribe(
        (response => {
          this.buildHelpIconIfNeeded(response, wrapper);
          current.splice(index, 1);
          this.equipos$.next(current);
          this.setChanges(true);
        })
      );
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.equipos$.value.map(wrapper => wrapper.value.proyectoEquipo);
    const id = this.getKey() as number;

    return this.proyectoEquipoService.updateList(id, values)
      .pipe(
        map(results => {
          return results.map(
            (value) => {
              value.persona = values.find(
                equipo => equipo.persona.id === value.persona.id
              ).persona;
              return new StatusWrapper<IProyectoEquipoListado>(
                {
                  proyectoEquipo: value,
                } as IProyectoEquipoListado);
            });
        }),
        switchMap((results) => {
          return from(results).pipe(
            mergeMap(element => {
              return this.validateRequisitosConvocatoria(element.value.proyectoEquipo, this.convocatoriaId).pipe(
                map(response => {
                  this.buildHelpIconIfNeeded(response, element);
                  return element;
                })
              );
            }),
            map(() => results)
          );
        }),
        takeLast(1),
        map((results) => {
          this.equipos$.next(results
          );
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.equipos$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

  private getTooltipMessage(input: ErrorResponse): string {
    switch (input.msgError) {
      case ErroresRequisitos.FECHA_OBTENCION:
        return this.msgToolTipFechaObtencion;
      case ErroresRequisitos.FECHA_MAYOR:
        return this.msgToolTipFechaMax;
      case ErroresRequisitos.FECHA_MENOR:
        return this.msgToolTipFechaMin;
      case ErroresRequisitos.NIVEL_ACADEMICO:
        return this.msgToolTipNivelAcademico;
      case ErroresRequisitos.SEXO:
        return this.msgToolTipSexo;
      case ErroresRequisitos.DATOS_ACADEMICOS:
        return this.msgToolTipDatosAcademicos;
      case ErroresRequisitos.EDAD:
        return this.msgToolTipEdadMax;
      case ErroresRequisitos.NO_FECHAS_VINCUALCION:
        return this.msgToolTipNoFechas;
      case ErroresRequisitos.FECHA_VINCULACION_MAYOR:
        return this.msgToolTipFechaVinculacionMayorMax;
      case ErroresRequisitos.FECHA_VINCULACION_MENOR:
        return this.msgToolTipFechaVinculacionMenorMin;
      case ErroresRequisitos.VINCULACION:
        return this.msgToolTipVinculacion;
      case ErroresRequisitos.NO_VINCULACION:
        return this.msgToolTipNoVinculacion;
      case ErroresRequisitos.CATEGORIAS_PROFESIONALES:
        return this.msgToolTipCategoriasProfesionales;
      default:
        return null;
    }
  }

  private validateRequisitosConvocatoria(
    proyectoEquipo: IProyectoEquipo,
    convocatoriaId: number
  ): Observable<ValidacionRequisitosEquipoIp> {
    if (proyectoEquipo.rolProyecto.rolPrincipal && convocatoriaId) {
      return this.validateRequisitosIp(proyectoEquipo, convocatoriaId);
    } else if (convocatoriaId) {
      return this.validateRequisitosEquipo(proyectoEquipo, convocatoriaId);
    } else {
      return of(null);
    }
  }

  private validateRequisitosIp(
    proyectoEquipo: IProyectoEquipo,
    convocatoriaId: number
  ): Observable<ValidacionRequisitosEquipoIp> {
    return this.getRequisitosConvocatoria(convocatoriaId).pipe(
      switchMap(() => {
        return this.validateRequisitosIpDatosPersonales(proyectoEquipo, this.requisitosConvocatoria);
      }),
      switchMap(response => {
        if (!response) {
          return this.validateRequisitosIpDatosAcademicos(proyectoEquipo, this.requisitosConvocatoria);
        }

        return of(response);
      }),
      switchMap(response => {
        if (!response) {
          return this.validateRequisitosIpVinculaciones(proyectoEquipo, this.requisitosConvocatoria);
        }

        return of(response);
      })
    );
  }

  private validateRequisitosEquipo(
    solicitudProyectoEquipo: IProyectoEquipo,
    convocatoriaId: number
  ): Observable<ValidacionRequisitosEquipoIp> {
    return this.getRequisitosConvocatoria(convocatoriaId).pipe(
      switchMap(() => {
        return this.validateRequisitosEquipoDatosPersonales(solicitudProyectoEquipo, this.requisitosConvocatoria);
      }),
      switchMap(response => {
        if (!response) {
          return this.validateRequisitosEquipoDatosAcademicos(solicitudProyectoEquipo, this.requisitosConvocatoria);
        }

        return of(response);
      }),
      switchMap(response => {
        if (!response) {
          return this.validateRequisitosEquipoVinculaciones(solicitudProyectoEquipo, this.requisitosConvocatoria);
        }

        return of(response);
      })
    );
  }

  private getRequisitosConvocatoria(convocatoriaId: number): Observable<RequisitosConvocatoria> {
    const requisitosConvocatoria = {} as RequisitosConvocatoria;

    if (this.requisitosConvocatoria) {
      return of(this.requisitosConvocatoria);
    } else {
      const nivelesAcademicosRequisitosEquipo$ = this.convocatoriaService.findRequisitosEquipoNivelesAcademicos(convocatoriaId).pipe(
        tap(nivelesAcademicos => requisitosConvocatoria.nivelesAcademicosRequisitosEquipo = nivelesAcademicos)
      );

      const nivelesAcademicosRequisitosIp$ = this.convocatoriaService.findRequisitosIpNivelesAcademicos(convocatoriaId).pipe(
        tap(nivelesAcademicos => requisitosConvocatoria.nivelesAcademicosRequisitosIp = nivelesAcademicos)
      );

      const categoriasProfesionalesRequisitosEquipo$ = this.convocatoriaService.findRequisitosEquipoCategoriasProfesionales(convocatoriaId)
        .pipe(
          tap(categorias =>
            requisitosConvocatoria.categoriasProfesionalesRequisitosEquipo = categorias.map(element => element.categoriaProfesional))
        );

      const categoriasProfesionalesRequisitosIp$ = this.convocatoriaService.findRequisitosIpCategoriasProfesionales(convocatoriaId)
        .pipe(
          tap(categorias =>
            requisitosConvocatoria.categoriasProfesionalesRequisitosIp = categorias.map(element => element.categoriaProfesional))
        );


      const requisitosIp$ = this.convocatoriaRequisitoIpService.getRequisitoIPConvocatoria(convocatoriaId)
        .pipe(
          tap(requisitosIp => requisitosConvocatoria.requisitosIp = requisitosIp)
        );

      const requisitosEquipo$ = this.convocatoriaRequisitoEquipoService.findByConvocatoriaId(convocatoriaId)
        .pipe(
          tap(requisitosEquipo => requisitosConvocatoria.requisitosEquipo = requisitosEquipo)
        );

      return forkJoin([
        nivelesAcademicosRequisitosEquipo$,
        nivelesAcademicosRequisitosIp$,
        categoriasProfesionalesRequisitosEquipo$,
        categoriasProfesionalesRequisitosIp$,
        requisitosEquipo$,
        requisitosIp$
      ]).pipe(
        map(() => {
          this.requisitosConvocatoria = requisitosConvocatoria;
          return requisitosConvocatoria;
        })
      );
    }
  }

  private validateRequisitosIpDatosAcademicos(
    proyectoEquipo: IProyectoEquipo,
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (requisitosConvocatoria.nivelesAcademicosRequisitosIp.length > 0) {
      return this.datosAcademicosService.findByPersonaId(proyectoEquipo.persona.id).pipe(
        map(datosAcademicos => {
          if (datosAcademicos?.nivelAcademico) {
            const nivelAcademicoInRequisitos = requisitosConvocatoria.nivelesAcademicosRequisitosIp
              .find(nivelAcademico => nivelAcademico?.nivelAcademico?.id === datosAcademicos.nivelAcademico.id);
            if (nivelAcademicoInRequisitos) {
              if (!datosAcademicos.fechaObtencion) {
                return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_NIVEL_ACADEMICO_DESCONOCIDA;
              } else if (requisitosConvocatoria.requisitosIp.fechaMinimaNivelAcademico
                && datosAcademicos.fechaObtencion < requisitosConvocatoria.requisitosIp.fechaMinimaNivelAcademico) {
                return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_NIVEL_ACADEMICO_MIN;
              } else if (requisitosConvocatoria.requisitosIp.fechaMaximaNivelAcademico
                && datosAcademicos.fechaObtencion > requisitosConvocatoria.requisitosIp.fechaMaximaNivelAcademico) {
                return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_NIVEL_ACADEMICO_MAX;
              }
            } else {
              return ValidacionRequisitosEquipoIp.NO_NIVEL_ACADEMICO;
            }
          } else {
            return ValidacionRequisitosEquipoIp.NO_NIVEL_ACADEMICO;
          }

          return null;
        })
      );
    } else {
      return of(null);
    }
  }

  private validateRequisitosIpDatosPersonales(
    proyectoEquipo: IProyectoEquipo,
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (requisitosConvocatoria.requisitosIp?.sexo?.id) {
      if (proyectoEquipo.persona.sexo?.id !== requisitosConvocatoria.requisitosIp.sexo.id) {
        return of(ValidacionRequisitosEquipoIp.SEXO);
      }
    }

    if (requisitosConvocatoria.requisitosIp?.edadMaxima) {
      return this.datosPersonalesService.findByPersonaId(proyectoEquipo.persona.id).pipe(
        map(datosPersonales => {

          if (!datosPersonales.fechaNacimiento) {
            return ValidacionRequisitosEquipoIp.EDAD_DESCONOCIDA;
          } else {
            const edad = DateTime.local().diff(datosPersonales?.fechaNacimiento, ['years', 'months', 'days', 'hours']);
            if (edad.years > requisitosConvocatoria.requisitosIp.edadMaxima) {
              return ValidacionRequisitosEquipoIp.EDAD_MAX;
            }
          }

          return null;
        })
      );
    }

    return of(null);
  }

  private validateRequisitosIpVinculaciones(
    proyectoEquipo: IProyectoEquipo,
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (requisitosConvocatoria.categoriasProfesionalesRequisitosIp.length > 0
      || (requisitosConvocatoria.requisitosIp && !requisitosConvocatoria.requisitosIp.vinculacionUniversidad)) {
      return this.vinculacionService.findByPersonaId(proyectoEquipo.persona.id).pipe(
        map(vinculaciones => {

          if (requisitosConvocatoria.categoriasProfesionalesRequisitosIp.length > 0) {
            if (vinculaciones) {
              const categoriaProfesionalInRequisitos =
                requisitosConvocatoria.categoriasProfesionalesRequisitosIp?.find(categoriaProfesional => categoriaProfesional.id ===
                  vinculaciones?.categoriaProfesional.id);

              if (categoriaProfesionalInRequisitos) {
                if (!vinculaciones.fechaObtencionCategoria) {
                  return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_DESCONOCIDA;
                } else if (requisitosConvocatoria.requisitosIp.fechaMinimaCategoriaProfesional
                  && vinculaciones.fechaObtencionCategoria < requisitosConvocatoria.requisitosIp.fechaMinimaCategoriaProfesional) {
                  return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MIN;
                } else if (requisitosConvocatoria.requisitosIp.fechaMaximaCategoriaProfesional
                  && vinculaciones.fechaObtencionCategoria > requisitosConvocatoria.requisitosIp.fechaMaximaCategoriaProfesional) {
                  return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MAX;
                }
              } else {
                return ValidacionRequisitosEquipoIp.NO_CATEGORIA_PROFESIONAL;
              }
            } else {
              return ValidacionRequisitosEquipoIp.NO_CATEGORIA_PROFESIONAL;
            }

          } else if (requisitosConvocatoria.requisitosIp && !requisitosConvocatoria.requisitosIp.vinculacionUniversidad) {
            if (vinculaciones?.categoriaProfesional) {
              return ValidacionRequisitosEquipoIp.VINCULACION_UNIVERSIDAD;
            }
          }

          return null;
        })
      );
    } else {
      return of(null);
    }
  }

  private validateRequisitosEquipoDatosPersonales(
    proyectoEquipo: IProyectoEquipo,
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (requisitosConvocatoria.requisitosEquipo?.sexo?.id) {
      if (proyectoEquipo.persona.sexo?.id !== requisitosConvocatoria.requisitosEquipo.sexo.id) {
        return of(ValidacionRequisitosEquipoIp.SEXO);
      }
    }

    if (requisitosConvocatoria.requisitosEquipo?.edadMaxima) {
      return this.datosPersonalesService.findByPersonaId(proyectoEquipo.persona.id).pipe(
        map(datosPersonales => {

          if (!datosPersonales.fechaNacimiento) {
            return ValidacionRequisitosEquipoIp.EDAD_DESCONOCIDA;
          } else {
            const edad = DateTime.local().diff(datosPersonales?.fechaNacimiento, ['years', 'months', 'days', 'hours']);
            if (edad.years > requisitosConvocatoria.requisitosEquipo.edadMaxima) {
              return ValidacionRequisitosEquipoIp.EDAD_MAX;
            }
          }

          return null;
        })
      );
    }

    return of(null);
  }

  private validateRequisitosEquipoDatosAcademicos(
    proyectoEquipo: IProyectoEquipo,
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (requisitosConvocatoria.nivelesAcademicosRequisitosEquipo.length > 0) {
      return this.datosAcademicosService.findByPersonaId(proyectoEquipo.persona.id).pipe(
        map(datosAcademicos => {
          if (datosAcademicos?.nivelAcademico) {
            const nivelAcademicoInRequisitos = requisitosConvocatoria.nivelesAcademicosRequisitosEquipo
              .find(nivelAcademico => nivelAcademico?.nivelAcademico?.id === datosAcademicos.nivelAcademico.id);
            if (nivelAcademicoInRequisitos) {
              if (!datosAcademicos.fechaObtencion) {
                return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_NIVEL_ACADEMICO_DESCONOCIDA;
              } else if (requisitosConvocatoria.requisitosEquipo.fechaMinimaNivelAcademico
                && datosAcademicos.fechaObtencion < requisitosConvocatoria.requisitosEquipo.fechaMinimaNivelAcademico) {
                return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_NIVEL_ACADEMICO_MIN;
              } else if (requisitosConvocatoria.requisitosEquipo.fechaMaximaNivelAcademico
                && datosAcademicos.fechaObtencion > requisitosConvocatoria.requisitosEquipo.fechaMaximaNivelAcademico) {
                return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_NIVEL_ACADEMICO_MAX;
              }
            } else {
              return ValidacionRequisitosEquipoIp.NO_NIVEL_ACADEMICO;
            }
          } else {
            return ValidacionRequisitosEquipoIp.NO_NIVEL_ACADEMICO;
          }

          return null;
        })
      );
    } else {
      return of(null);
    }
  }

  private validateRequisitosEquipoVinculaciones(
    proyectoEquipo: IProyectoEquipo,
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (requisitosConvocatoria.categoriasProfesionalesRequisitosEquipo.length > 0
      || (requisitosConvocatoria.requisitosEquipo && !requisitosConvocatoria.requisitosEquipo.vinculacionUniversidad)) {
      return this.vinculacionService.findByPersonaId(proyectoEquipo.persona.id).pipe(
        map(vinculaciones => {

          if (requisitosConvocatoria.categoriasProfesionalesRequisitosEquipo.length > 0) {
            if (vinculaciones) {
              const categoriaProfesionalInRequisitos =
                requisitosConvocatoria.categoriasProfesionalesRequisitosEquipo?.find(categoriaProfesional => categoriaProfesional.id ===
                  vinculaciones?.categoriaProfesional.id);

              if (categoriaProfesionalInRequisitos) {
                if (!vinculaciones.fechaObtencionCategoria) {
                  return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_DESCONOCIDA;
                } else if (requisitosConvocatoria.requisitosEquipo.fechaMinimaCategoriaProfesional
                  && vinculaciones.fechaObtencionCategoria < requisitosConvocatoria.requisitosEquipo.fechaMinimaCategoriaProfesional) {
                  return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MIN;
                } else if (requisitosConvocatoria.requisitosEquipo.fechaMaximaCategoriaProfesional
                  && vinculaciones.fechaObtencionCategoria > requisitosConvocatoria.requisitosEquipo.fechaMaximaCategoriaProfesional) {
                  return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MAX;
                }
              } else {
                return ValidacionRequisitosEquipoIp.NO_CATEGORIA_PROFESIONAL;
              }
            } else {
              return ValidacionRequisitosEquipoIp.NO_CATEGORIA_PROFESIONAL;
            }

          } else if (requisitosConvocatoria.requisitosEquipo && !requisitosConvocatoria.requisitosEquipo.vinculacionUniversidad) {
            if (vinculaciones?.categoriaProfesional) {
              return ValidacionRequisitosEquipoIp.VINCULACION_UNIVERSIDAD;
            }
          }

          return null;
        })
      );
    } else {
      return of(null);
    }
  }

  private buildHelpIconIfNeeded(response: ValidacionRequisitosEquipoIp, wrapper: StatusWrapper<IProyectoEquipoListado>) {
    if (response) {
      wrapper.value.help = {
        class: HelpIconClass.DANGER,
        tooltip: response,
      } as HelpIcon;
    }
  }

}
