import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { IDatosAcademicos } from '@core/models/sgp/datos-academicos';
import { IDatosPersonales } from '@core/models/sgp/datos-personales';
import { IVinculacion } from '@core/models/sgp/vinculacion';
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
import { DateTime, Duration } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
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

  constructor(
    private readonly logger: NGXLogger,
    key: number,
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
                return this.validateRequisitosConvocatoria(element.value.proyectoEquipo).pipe(
                  map(response => {
                    if (!response.isValid) {
                      element.value.help = {
                        class: HelpIconClass.DANGER,
                        tooltip: this.getTooltipMessage(response),
                      } as HelpIcon;
                    }
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
    this.validateRequisitosConvocatoria(wrapper.value.proyectoEquipo).subscribe(
      (response => {
        if (!response.isValid) {
          wrapper.value.help = {
            class: HelpIconClass.DANGER,
            tooltip: this.getTooltipMessage(response),
          } as HelpIcon;
        }
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
      this.validateRequisitosConvocatoria(wrapper.value.proyectoEquipo).subscribe(
        (response => {
          if (!response.isValid) {
            wrapper.value.help = {
              class: HelpIconClass.DANGER,
              tooltip: this.getTooltipMessage(response),
            } as HelpIcon;
          }
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
      this.validateRequisitosConvocatoria(wrapper.value.proyectoEquipo).subscribe(
        (response => {
          if (!response.isValid) {
            wrapper.value.help = {
              class: HelpIconClass.DANGER,
              tooltip: this.getTooltipMessage(response),
            } as HelpIcon;
          }
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
              return this.validateRequisitosConvocatoria(element.value.proyectoEquipo).pipe(
                map(response => {
                  if (!response.isValid) {
                    element.value.help = {
                      class: HelpIconClass.DANGER,
                      tooltip: this.getTooltipMessage(response),
                    } as HelpIcon;
                  }
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

  private validateRequisitosConvocatoria(proyectoEquipo: IProyectoEquipo): Observable<ErrorResponse> {
    let datosAcademicos: IDatosAcademicos;
    let edad: Duration;
    let datosPersonales: IDatosPersonales;
    let vinculacion: IVinculacion;
    let categoriasProfesionalesConvocatoria: ICategoriaProfesional[];
    let nivelAcademicoSolicitante: IRequisitoEquipoNivelAcademico[];
    let categoriaProfesionalSolicitante: ICategoriaProfesional[];
    const response = {
      isValid: false,
      msgError: null,
    } as ErrorResponse;

    return this.proyectoService.findById(this.getKey() as number).pipe(
      switchMap((proyecto) => {
        return this.convocatoriaService.findRequisitosEquipoNivelesAcademicos(proyecto.convocatoriaId).pipe(
          switchMap((nivelesAcademicos) => {
            return this.datosAcademicosService.findByPersonaId(proyectoEquipo.persona.id).pipe(
              switchMap((datoAcademico) => {
                if (datoAcademico) {
                  datosAcademicos = datoAcademico;
                  if (nivelesAcademicos?.length > 0) {
                    nivelAcademicoSolicitante =
                      nivelesAcademicos.filter(nivelAcademico => nivelAcademico?.nivelAcademico?.id === datosAcademicos.nivelAcademico.id);
                  }
                }
                return this.datosPersonalesService.findByPersonaId(proyectoEquipo.persona.id).pipe(
                  switchMap((datoPersonal) => {
                    if (datoPersonal) {
                      datosPersonales = datoPersonal;
                      edad = DateTime.local().diff(datoPersonal?.fechaNacimiento, ['years', 'months', 'days', 'hours']);
                    }
                    return this.convocatoriaService.findRequisitosIpCategoriasProfesionales(proyecto.convocatoriaId);
                  }),
                  switchMap((categoriasProfesionales: IRequisitoIPCategoriaProfesional[]) => {
                    if (categoriasProfesionales?.length > 0) {
                      categoriasProfesionalesConvocatoria = categoriasProfesionales.map(element => element.categoriaProfesional);
                    }
                    return this.vinculacionService.findByPersonaId(proyectoEquipo.persona.id);
                  }),
                  switchMap((vinculacionSolicitante: IVinculacion) => {
                    if (categoriasProfesionalesConvocatoria?.length > 0 && vinculacionSolicitante != null) {
                      categoriaProfesionalSolicitante =
                        categoriasProfesionalesConvocatoria?.filter(categoriaProfesional => categoriaProfesional.id ===
                          vinculacionSolicitante?.categoriaProfesional.id);
                    }
                    vinculacion = vinculacionSolicitante;
                    return this.convocatoriaRequisitoIpService.getRequisitoIPConvocatoria(proyecto.convocatoriaId);
                  }));
              }),
              map((convocatoriaRequisitoIp: IConvocatoriaRequisitoIP) => {
                if (proyectoEquipo.rolProyecto.rolPrincipal) {
                  if (!convocatoriaRequisitoIp?.fechaMaximaNivelAcademico &&
                    !convocatoriaRequisitoIp?.fechaMinimaNivelAcademico &&
                    !convocatoriaRequisitoIp?.sexo.id &&
                    !convocatoriaRequisitoIp?.edadMaxima &&
                    !convocatoriaRequisitoIp?.fechaMaximaCategoriaProfesional &&
                    !convocatoriaRequisitoIp?.fechaMinimaCategoriaProfesional &&
                    convocatoriaRequisitoIp?.vinculacionUniversidad == null) {
                    response.msgError = null;
                    response.isValid = true;
                  }
                  if ((convocatoriaRequisitoIp?.fechaMaximaNivelAcademico ||
                    convocatoriaRequisitoIp?.fechaMinimaNivelAcademico ||
                    convocatoriaRequisitoIp?.sexo ||
                    convocatoriaRequisitoIp?.edadMaxima) && datosAcademicos != null) {

                    if (!datosAcademicos.fechaObtencion &&
                      (convocatoriaRequisitoIp.fechaMaximaCategoriaProfesional &&
                        convocatoriaRequisitoIp.fechaMinimaCategoriaProfesional)) {
                      response.msgError = ErroresRequisitos.FECHA_OBTENCION;
                    } else if (datosAcademicos.fechaObtencion?.startOf('day') >
                      convocatoriaRequisitoIp.fechaMaximaNivelAcademico?.startOf('day')) {
                      response.msgError = ErroresRequisitos.FECHA_MAYOR;
                    } else if (datosAcademicos.fechaObtencion?.startOf('day') <
                      convocatoriaRequisitoIp.fechaMinimaNivelAcademico?.startOf('day')) {
                      response.msgError = ErroresRequisitos.FECHA_MENOR;
                    } else if (nivelAcademicoSolicitante !== null && nivelAcademicoSolicitante?.length < 1) {
                      response.msgError = ErroresRequisitos.NIVEL_ACADEMICO;
                    } else if (convocatoriaRequisitoIp != null && (convocatoriaRequisitoIp?.sexo?.id != null &&
                      (proyectoEquipo.persona.sexo?.id !== convocatoriaRequisitoIp?.sexo?.id))) {
                      response.msgError = ErroresRequisitos.SEXO;
                    } else if (convocatoriaRequisitoIp != null && (convocatoriaRequisitoIp?.edadMaxima != null &&
                      (edad.years > convocatoriaRequisitoIp?.edadMaxima))) {
                      response.msgError = ErroresRequisitos.EDAD;
                    }

                  } else if (convocatoriaRequisitoIp != null && (convocatoriaRequisitoIp?.sexo?.id != null &&
                    (proyectoEquipo.persona.sexo?.id !== convocatoriaRequisitoIp?.sexo?.id))) {
                    response.msgError = ErroresRequisitos.SEXO;
                  } else if (convocatoriaRequisitoIp != null && (convocatoriaRequisitoIp?.edadMaxima != null &&
                    (edad.years > convocatoriaRequisitoIp?.edadMaxima))) {
                    response.msgError = ErroresRequisitos.EDAD;
                  } else if (convocatoriaRequisitoIp != null && datosAcademicos == null) {
                    response.msgError = ErroresRequisitos.DATOS_ACADEMICOS;
                  }

                  if (categoriasProfesionalesConvocatoria != null && convocatoriaRequisitoIp != null
                    && categoriasProfesionalesConvocatoria?.length !== 0 && convocatoriaRequisitoIp?.vinculacionUniversidad === true
                    && categoriaProfesionalSolicitante?.length === 0) {
                    response.msgError = ErroresRequisitos.CATEGORIAS_PROFESIONALES;
                  }
                  if (vinculacion !== null) {
                    if (convocatoriaRequisitoIp != null) {
                      if (convocatoriaRequisitoIp.vinculacionUniversidad === false && vinculacion) {
                        response.msgError = ErroresRequisitos.NO_VINCULACION;
                      } else if (convocatoriaRequisitoIp.vinculacionUniversidad === true && !vinculacion) {
                        response.msgError = ErroresRequisitos.VINCULACION;
                      } else if (convocatoriaRequisitoIp.fechaMaximaNivelAcademico != null &&
                        vinculacion.fechaObtencionCategoria >
                        convocatoriaRequisitoIp.fechaMaximaNivelAcademico) {
                        response.msgError = ErroresRequisitos.FECHA_VINCULACION_MAYOR;
                      } else if (convocatoriaRequisitoIp.fechaMinimaNivelAcademico != null &&
                        vinculacion.fechaObtencionCategoria <
                        convocatoriaRequisitoIp.fechaMinimaNivelAcademico) {
                        response.msgError = ErroresRequisitos.FECHA_VINCULACION_MENOR;
                      } else if (convocatoriaRequisitoIp.fechaMaximaCategoriaProfesional != null &&
                        vinculacion.fechaObtencionCategoria >
                        convocatoriaRequisitoIp.fechaMaximaCategoriaProfesional) {
                        response.msgError = ErroresRequisitos.FECHA_OBTENCION_MAYOR;
                      } else if (convocatoriaRequisitoIp.fechaMinimaCategoriaProfesional != null &&
                        vinculacion.fechaObtencionCategoria <
                        convocatoriaRequisitoIp.fechaMinimaCategoriaProfesional) {
                        response.msgError = ErroresRequisitos.FECHA_OBTENCION_MENOR;
                      } else if (response.msgError === null) {
                        response.isValid = true;
                      }
                    } else if (vinculacion.fechaObtencionCategoria == null) {
                      response.msgError = ErroresRequisitos.NO_FECHAS_VINCUALCION;
                    }
                  } else if (convocatoriaRequisitoIp?.vinculacionUniversidad === true) {
                    response.msgError = ErroresRequisitos.NO_VINCULACION;
                  } else if (response.msgError === null) {
                    response.isValid = true;
                  }
                } else {
                  response.msgError = null;
                  response.isValid = true;
                }
                return response;
              })
            );

          }),
          switchMap((erroresNivelesAcademicos: ErrorResponse) => {
            if (erroresNivelesAcademicos.msgError === null) {
              return this.convocatoriaService.findRequisitosEquipoCategoriasProfesionales(proyecto.convocatoriaId).pipe(
                switchMap((categoriasProfesionales) => {
                  if (categoriasProfesionales?.length > 0) {
                    categoriasProfesionalesConvocatoria = categoriasProfesionales.map(element => element.categoriaProfesional);
                  }
                  return this.vinculacionService.findByPersonaId(proyectoEquipo.persona.id);
                }),
                switchMap((vinculacionSolicitante: IVinculacion) => {
                  categoriaProfesionalSolicitante =
                    categoriasProfesionalesConvocatoria?.filter(categoriaProfesional => categoriaProfesional.id ===
                      vinculacionSolicitante?.categoriaProfesional.id);
                  vinculacion = vinculacionSolicitante;
                  return this.convocatoriaRequisitoEquipoService.findByConvocatoriaId(proyecto.convocatoriaId);
                }),
                map((convocatoriaRequisitoEquipo: IConvocatoriaRequisitoEquipo) => {

                  if (convocatoriaRequisitoEquipo?.sexo.id &&
                    proyectoEquipo.persona?.sexo.id !== convocatoriaRequisitoEquipo?.sexo.id) {
                    response.msgError = ErroresRequisitos.SEXO;
                  }

                  if (categoriasProfesionalesConvocatoria != null && convocatoriaRequisitoEquipo != null
                    && categoriasProfesionalesConvocatoria?.length !== 0 && convocatoriaRequisitoEquipo?.vinculacionUniversidad
                    && categoriaProfesionalSolicitante?.length === 0) {
                    response.msgError = ErroresRequisitos.CATEGORIAS_PROFESIONALES;
                  }

                  if (vinculacion !== null) {
                    if (convocatoriaRequisitoEquipo != null) {
                      if (convocatoriaRequisitoEquipo.vinculacionUniversidad === false && vinculacion) {
                        response.msgError = ErroresRequisitos.NO_VINCULACION;
                      } else if (convocatoriaRequisitoEquipo.vinculacionUniversidad === true && !vinculacion) {
                        response.msgError = ErroresRequisitos.VINCULACION;
                      } else if (convocatoriaRequisitoEquipo.fechaMaximaNivelAcademico != null &&
                        vinculacion.fechaObtencionCategoria >
                        convocatoriaRequisitoEquipo.fechaMaximaNivelAcademico) {
                        response.msgError = ErroresRequisitos.FECHA_VINCULACION_MAYOR;
                      } else if (convocatoriaRequisitoEquipo.fechaMinimaNivelAcademico != null &&
                        vinculacion.fechaObtencionCategoria <
                        convocatoriaRequisitoEquipo.fechaMinimaNivelAcademico) {
                        response.msgError = ErroresRequisitos.FECHA_VINCULACION_MENOR;
                      } else if (convocatoriaRequisitoEquipo.fechaMaximaCategoriaProfesional != null &&
                        vinculacion.fechaObtencionCategoria >
                        convocatoriaRequisitoEquipo.fechaMaximaCategoriaProfesional) {
                        response.msgError = ErroresRequisitos.FECHA_OBTENCION_MAYOR;
                      } else if (convocatoriaRequisitoEquipo.fechaMinimaCategoriaProfesional != null &&
                        vinculacion.fechaObtencionCategoria <
                        convocatoriaRequisitoEquipo.fechaMinimaCategoriaProfesional) {
                        response.msgError = ErroresRequisitos.FECHA_OBTENCION_MENOR;
                      } else if (response.msgError === null) {
                        response.isValid = true;
                      }
                    } else if (vinculacion.fechaObtencionCategoria == null) {
                      response.msgError = ErroresRequisitos.NO_FECHAS_VINCUALCION;
                    }
                  } else if (convocatoriaRequisitoEquipo?.vinculacionUniversidad != null &&
                    convocatoriaRequisitoEquipo.vinculacionUniversidad === true) {
                    response.msgError = ErroresRequisitos.NO_VINCULACION;
                  } else if (response.msgError === null) {
                    response.isValid = true;
                  }
                  return response;
                })
              );
            }
            return of(response);
          })
        );
      })
    );
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

}


