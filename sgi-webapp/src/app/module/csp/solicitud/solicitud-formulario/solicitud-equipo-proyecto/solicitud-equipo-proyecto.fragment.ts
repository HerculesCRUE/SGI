import { ValidacionRequisitosEquipoIp } from '@core/enums/validaciones-requisitos-equipo-ip';
import { IConvocatoriaRequisito } from '@core/models/csp/convocatoria-requisito';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { IPersona } from '@core/models/sgp/persona';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaRequisitoEquipoService } from '@core/services/csp/convocatoria-requisito-equipo.service';
import { ConvocatoriaRequisitoIPService } from '@core/services/csp/convocatoria-requisito-ip.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { SolicitudProyectoEquipoService } from '@core/services/csp/solicitud-proyecto-equipo.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { DatosPersonalesService } from '@core/services/sgp/datos-personales.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
import { BehaviorSubject, forkJoin, from, Observable, of } from 'rxjs';
import { concatMap, filter, map, mergeMap, share, switchMap, takeLast, tap } from 'rxjs/operators';
import { SolicitudActionService } from '../../solicitud.action.service';

export enum HelpIconClass {
  DANGER = 'danger',
}

interface HelpIcon {
  class: HelpIconClass;
  tooltip: string;
}

export interface ISolicitudProyectoEquipoListado {
  solicitudProyectoEquipo: ISolicitudProyectoEquipo;
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

export class SolicitudEquipoProyectoFragment extends Fragment {
  proyectoEquipos$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoEquipoListado>[]>([]);

  requisitosConvocatoria: RequisitosConvocatoria;
  solicitudId: number;

  constructor(
    key: number,
    private convocatoriaId: number,
    private solicitudService: SolicitudService,
    private solicitudProyectoEquipoService: SolicitudProyectoEquipoService,
    public actionService: SolicitudActionService,
    public rolProyectoService: RolProyectoService,
    private convocatoriaService: ConvocatoriaService,
    private datosAcademicosService: DatosAcademicosService,
    private convocatoriaRequisitoIpService: ConvocatoriaRequisitoIPService,
    private vinculacionService: VinculacionService,
    private convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoService,
    private datosPersonalesService: DatosPersonalesService,
    private proyectoService: ProyectoService,
    public isInvestigador: boolean,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    this.solicitudId = this.getKey() as number;
    if (this.solicitudId) {
      const existsSolicitudProyecto$ = this.solicitudService.existsSolictudProyecto(this.solicitudId).pipe(share());

      this.subscriptions.push(existsSolicitudProyecto$.pipe(
        filter(exist => !exist),
        switchMap(() => {
          return this.rolProyectoService.findPrincipal();
        }),
      ).subscribe((rol) => {
        const solicitudProyectoEquipo = {
          solicitudProyectoEquipo: {
            solicitudProyectoId: this.solicitudId,
            rolProyecto: rol,
            persona: this.actionService.solicitante
          } as ISolicitudProyectoEquipo,
          help: { class: HelpIconClass.DANGER } as HelpIcon
        } as ISolicitudProyectoEquipoListado;
        this.addProyectoEquipo(solicitudProyectoEquipo);
        return of(solicitudProyectoEquipo);
      }));

      this.subscriptions.push(existsSolicitudProyecto$.pipe(
        filter(exist => exist),
        switchMap(() => {
          return this.solicitudService.findAllSolicitudProyectoEquipo(this.solicitudId).pipe(
            map(result => result.items.map(solicitudProyectoEquipo =>
              new StatusWrapper<ISolicitudProyectoEquipoListado>({ solicitudProyectoEquipo } as ISolicitudProyectoEquipoListado))));
        }),
        switchMap(result => {
          if (!this.convocatoriaId) {
            return of(result);
          }

          return this.getRequisitosConvocatoria(this.convocatoriaId).pipe(map(() => result));
        }),
        switchMap(result => {
          return from(result).pipe(
            concatMap(element => {
              return this.validateRequisitosConvocatoria(element.value.solicitudProyectoEquipo, this.convocatoriaId).pipe(
                map(response => {
                  if (response) {
                    element.value.help = {
                      class: HelpIconClass.DANGER,
                      tooltip: response,
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
        (result) => {
          this.proyectoEquipos$.next(result);
        }
      ));
    }
  }

  public deleteProyectoEquipo(wrapper: StatusWrapper<ISolicitudProyectoEquipoListado>): void {
    const current = this.proyectoEquipos$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      this.validateRequisitosConvocatoria(wrapper.value.solicitudProyectoEquipo, this.convocatoriaId).subscribe(
        (response => {
          if (response) {
            wrapper.value.help = {
              class: HelpIconClass.DANGER,
              tooltip: response,
            } as HelpIcon;
          }
          current.splice(index, 1);
          this.proyectoEquipos$.next(current);
          this.setChanges(true);
        })
      );
    }
  }

  addProyectoEquipo(element: ISolicitudProyectoEquipoListado): void {
    const wrapper = new StatusWrapper<ISolicitudProyectoEquipoListado>({
      solicitudProyectoEquipo: element.solicitudProyectoEquipo,
      help: null
    } as ISolicitudProyectoEquipoListado);
    this.validateRequisitosConvocatoria(wrapper.value.solicitudProyectoEquipo, this.convocatoriaId).subscribe(
      (response => {
        if (response) {
          wrapper.value.help = {
            class: HelpIconClass.DANGER,
            tooltip: response,
          } as HelpIcon;
        }
        wrapper.setCreated();
        const current = this.proyectoEquipos$.value;
        current.push(wrapper);
        this.proyectoEquipos$.next(current);

        if (!this.readonly) {
          // No se marca como cambios si es un visor para el caso en el que aun no existe el solicitudProyecto
          this.setChanges(true);
        }

        return element;
      })
    );
  }

  updateProyectoEquipo(wrapper: StatusWrapper<ISolicitudProyectoEquipoListado>, index: number): void {
    if (index >= 0) {
      if (!wrapper.created) {
        wrapper.setEdited();
        this.setChanges(true);
      }
      wrapper.value.help = {
        class: HelpIconClass.DANGER
      } as HelpIcon;
      this.validateRequisitosConvocatoria(wrapper.value.solicitudProyectoEquipo, this.convocatoriaId).subscribe(
        (response => {
          if (response) {
            wrapper.value.help = {
              class: HelpIconClass.DANGER,
              tooltip: response,
            } as HelpIcon;
          }
          this.proyectoEquipos$.value[index] = wrapper;
        })
      );
    }
  }

  saveOrUpdate(): Observable<void> {
    const solicitudProyectoEquipos = this.proyectoEquipos$.value.map(wrapper => wrapper.value.solicitudProyectoEquipo);
    solicitudProyectoEquipos.forEach(solicitudProyectoEquipo => {
      solicitudProyectoEquipo.solicitudProyectoId = this.getKey() as number;
    });

    return (this.solicitudProyectoEquipoService.updateSolicitudProyectoEquipo(this.getKey() as number, solicitudProyectoEquipos)
    ).pipe(
      map(results => {
        return results.map(
          (solicitudProyectoEquipo) => {
            solicitudProyectoEquipo.persona = solicitudProyectoEquipos.find(
              equipo => equipo.persona.id === solicitudProyectoEquipo.persona.id
            ).persona;
            return new StatusWrapper<ISolicitudProyectoEquipoListado>(
              {
                solicitudProyectoEquipo,
              } as ISolicitudProyectoEquipoListado);
          });
      }),
      switchMap((results) => {
        return from(results).pipe(
          mergeMap(element => {
            return this.validateRequisitosConvocatoria(element.value.solicitudProyectoEquipo, this.convocatoriaId).pipe(
              map(response => {
                if (response) {
                  element.value.help = {
                    class: HelpIconClass.DANGER,
                    tooltip: response,
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
        this.proyectoEquipos$.next(results
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
    const touched: boolean = this.proyectoEquipos$.value.some((wrapper) => wrapper.touched);
    return !touched;
  }

  private getRequisitosConvocatoria(convocatoriaId: number): Observable<RequisitosConvocatoria> {
    const requisitosConvocatoria = {} as RequisitosConvocatoria;

    if (this.requisitosConvocatoria) {
      return of(this.requisitosConvocatoria);
    } else {
      let nivelesAcademicosRequisitosEquipo$ = this.isInvestigador
        && this.solicitudId ? this.solicitudService.findRequisitosEquipoNivelesAcademicos(this.solicitudId)
        : this.convocatoriaService.findRequisitosEquipoNivelesAcademicos(convocatoriaId);
      nivelesAcademicosRequisitosEquipo$ = nivelesAcademicosRequisitosEquipo$.pipe(
        tap(nivelesAcademicos => requisitosConvocatoria.nivelesAcademicosRequisitosEquipo = nivelesAcademicos)
      );

      let nivelesAcademicosRequisitosIp$ = this.isInvestigador
        && this.solicitudId ? this.solicitudService.findRequisitosIpNivelesAcademicos(this.solicitudId)
        : this.convocatoriaService.findRequisitosIpNivelesAcademicos(convocatoriaId);
      nivelesAcademicosRequisitosIp$ = nivelesAcademicosRequisitosIp$.pipe(
        tap(nivelesAcademicos => requisitosConvocatoria.nivelesAcademicosRequisitosIp = nivelesAcademicos)
      );

      const categoriasProfesionalesRequisitosEquipo$ = this.convocatoriaService.findRequisitosEquipoCategoriasProfesionales(convocatoriaId)
        .pipe(
          tap(categorias =>
            requisitosConvocatoria.categoriasProfesionalesRequisitosEquipo = categorias.map(element => element.categoriaProfesional))
        );

      let categoriasProfesionalesRequisitosIp$ = this.isInvestigador
        && this.solicitudId ? this.solicitudService.findRequisitosIpCategoriasProfesionales(this.solicitudId)
        : this.convocatoriaService.findRequisitosIpCategoriasProfesionales(convocatoriaId);
      categoriasProfesionalesRequisitosIp$ = categoriasProfesionalesRequisitosIp$.pipe(
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

  private validateRequisitosEquipo(
    solicitudProyectoEquipo: ISolicitudProyectoEquipo,
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

  private validateRequisitosEquipoDatosAcademicos(
    solicitudProyectoEquipo: ISolicitudProyectoEquipo,
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (requisitosConvocatoria.nivelesAcademicosRequisitosEquipo.length > 0) {
      return this.datosAcademicosService.findByPersonaId(solicitudProyectoEquipo.persona.id).pipe(
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

  private validateRequisitosEquipoDatosPersonales(
    solicitudProyectoEquipo: ISolicitudProyectoEquipo,
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (requisitosConvocatoria.requisitosEquipo?.edadMaxima) {
      return this.datosPersonalesService.findByPersonaId(solicitudProyectoEquipo.persona.id).pipe(
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

  private validateRequisitosEquipoVinculaciones(
    solicitudProyectoEquipo: ISolicitudProyectoEquipo,
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (requisitosConvocatoria.categoriasProfesionalesRequisitosEquipo.length > 0
      || (requisitosConvocatoria.requisitosEquipo && !requisitosConvocatoria.requisitosEquipo.vinculacionUniversidad)) {
      return this.vinculacionService.findByPersonaId(solicitudProyectoEquipo.persona.id).pipe(
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

          } else if (requisitosConvocatoria.requisitosEquipo && typeof requisitosConvocatoria.requisitosEquipo.vinculacionUniversidad === 'boolean'
            && !requisitosConvocatoria.requisitosEquipo.vinculacionUniversidad) {
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

  private validateRequisitosEquipoGlobales(
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    const miembrosEquipoNoPrincipales = this.proyectoEquipos$.value
      .filter(miembro => !miembro.value.solicitudProyectoEquipo.rolProyecto.rolPrincipal)
      .map(miembroWraper => miembroWraper.value.solicitudProyectoEquipo);

    if (requisitosConvocatoria.requisitosEquipo?.sexo?.id && requisitosConvocatoria.requisitosEquipo?.ratioSexo) {
      const numMiembrosSexo = miembrosEquipoNoPrincipales.filter(miembroEquipo =>
        miembroEquipo.persona.sexo?.id === requisitosConvocatoria.requisitosEquipo.sexo.id
      ).length;

      const ratioSexo = numMiembrosSexo / miembrosEquipoNoPrincipales.length * 100;
      if (ratioSexo < requisitosConvocatoria.requisitosEquipo.ratioSexo) {
        return of(ValidacionRequisitosEquipoIp.RATIO_SEXO);
      }
    }

    return this.validateRequisitosEquipoProyectosCompetitivos(requisitosConvocatoria.requisitosEquipo);
  }

  private validateRequisitosEquipoProyectosCompetitivos(
    requisitos: IConvocatoriaRequisito
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (!requisitos?.numMaximoCompetitivosActivos
      && !requisitos?.numMaximoNoCompetitivosActivos
      && !requisitos?.numMinimoCompetitivos
      && !requisitos?.numMinimoNoCompetitivos
    ) {
      return of(null);
    }

    const miembrosEquipo = this.proyectoEquipos$.value
      .map(miembroWraper => miembroWraper.value.solicitudProyectoEquipo.persona.id)
      .filter((personaId, index, personasIds) => personasIds.indexOf(personaId) === index);

    if (miembrosEquipo.length === 0) {
      return of(null);
    }

    return this.proyectoService.getProyectoCompetitivosPersona(miembrosEquipo).pipe(
      map(response => {
        if (requisitos.numMaximoCompetitivosActivos
          && response.numProyectosCompetitivosActuales > requisitos.numMaximoCompetitivosActivos) {
          return ValidacionRequisitosEquipoIp.NUM_MAX_PROYECTOS_COMPETITIVOS_ACTUALES_EQUIPO;
        } else if (requisitos.numMaximoNoCompetitivosActivos
          && response.numProyectosNoCompetitivosActuales > requisitos.numMaximoNoCompetitivosActivos) {
          return ValidacionRequisitosEquipoIp.NUM_MAX_PROYECTOS_NO_COMPETITIVOS_ACTUALES_EQUIPO;
        } else if (requisitos.numMinimoCompetitivos
          && response.numProyectosCompetitivos < requisitos.numMinimoCompetitivos) {
          return ValidacionRequisitosEquipoIp.NUM_MIN_PROYECTOS_COMPETITIVOS_EQUIPO;
        } else if (requisitos.numMinimoNoCompetitivos
          && response.numProyectosNoCompetitivos < requisitos.numMinimoNoCompetitivos) {
          return ValidacionRequisitosEquipoIp.NUM_MIN_PROYECTOS_NO_COMPETITIVOS_EQUIPO;
        } else {
          return null;
        }
      })
    );

  }

  private validateRequisitosIp(
    solicitudProyectoEquipo: ISolicitudProyectoEquipo,
    convocatoriaId: number
  ): Observable<ValidacionRequisitosEquipoIp> {
    return this.getRequisitosConvocatoria(convocatoriaId).pipe(
      switchMap(() => {
        return this.validateRequisitosIpProyectosCompetitivos(solicitudProyectoEquipo, this.requisitosConvocatoria.requisitosIp);
      }),
      switchMap(response => {
        if (!response) {
          return this.validateRequisitosIpDatosPersonales(solicitudProyectoEquipo, this.requisitosConvocatoria);
        }

        return of(response);
      }),
      switchMap(response => {
        if (!response) {
          return this.validateRequisitosIpDatosAcademicos(solicitudProyectoEquipo, this.requisitosConvocatoria);
        }

        return of(response);
      }),
      switchMap(response => {
        if (!response) {
          return this.validateRequisitosIpVinculaciones(solicitudProyectoEquipo, this.requisitosConvocatoria);
        }

        return of(response);
      })
    );
  }

  private validateRequisitosIpDatosAcademicos(
    solicitudProyectoEquipo: ISolicitudProyectoEquipo,
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (requisitosConvocatoria.nivelesAcademicosRequisitosIp.length > 0) {
      return this.datosAcademicosService.findByPersonaId(solicitudProyectoEquipo.persona.id).pipe(
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
    solicitudProyectoEquipo: ISolicitudProyectoEquipo,
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (requisitosConvocatoria.requisitosIp?.sexo?.id) {
      if (solicitudProyectoEquipo.persona.sexo?.id !== requisitosConvocatoria.requisitosIp.sexo.id) {
        return of(ValidacionRequisitosEquipoIp.SEXO);
      }
    }

    if (requisitosConvocatoria.requisitosIp?.edadMaxima) {
      return this.datosPersonalesService.findByPersonaId(solicitudProyectoEquipo.persona.id).pipe(
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
    solicitudProyectoEquipo: ISolicitudProyectoEquipo,
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (requisitosConvocatoria.categoriasProfesionalesRequisitosIp.length > 0
      || (requisitosConvocatoria.requisitosIp && !requisitosConvocatoria.requisitosIp.vinculacionUniversidad)) {
      return this.vinculacionService.findByPersonaId(solicitudProyectoEquipo.persona.id).pipe(
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

          } else if (requisitosConvocatoria.requisitosIp && typeof requisitosConvocatoria.requisitosIp.vinculacionUniversidad === 'boolean'
            && !requisitosConvocatoria.requisitosIp.vinculacionUniversidad) {
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

  private validateRequisitosIpGlobales(
    requisitosConvocatoria: RequisitosConvocatoria
  ): Observable<ValidacionRequisitosEquipoIp> {

    const miembrosEquipoPrincipales = this.proyectoEquipos$.value
      .filter(miembro => miembro.value.solicitudProyectoEquipo.rolProyecto.rolPrincipal)
      .map(miembroWraper => miembroWraper.value.solicitudProyectoEquipo);

    if (requisitosConvocatoria.requisitosIp?.numMaximoIP) {
      const numIps = miembrosEquipoPrincipales.length;

      if (numIps > requisitosConvocatoria.requisitosIp?.numMaximoIP) {
        return of(ValidacionRequisitosEquipoIp.NUM_MAX_IP);
      }
    }

    return of(null);
  }

  private validateRequisitosIpProyectosCompetitivos(
    solicitudProyectoEquipo: ISolicitudProyectoEquipo,
    requisitos: IConvocatoriaRequisito
  ): Observable<ValidacionRequisitosEquipoIp> {

    if (!requisitos?.numMaximoCompetitivosActivos
      && !requisitos?.numMaximoNoCompetitivosActivos
      && !requisitos?.numMinimoCompetitivos
      && !requisitos?.numMinimoNoCompetitivos
    ) {
      return of(null);
    }

    return this.proyectoService.getProyectoCompetitivosPersona(solicitudProyectoEquipo.persona.id, true).pipe(
      map(response => {
        if (requisitos.numMaximoCompetitivosActivos
          && response.numProyectosCompetitivosActuales > requisitos.numMaximoCompetitivosActivos) {
          return ValidacionRequisitosEquipoIp.NUM_MAX_PROYECTOS_COMPETITIVOS_ACTUALES_IP;
        } else if (requisitos.numMaximoNoCompetitivosActivos
          && response.numProyectosNoCompetitivosActuales > requisitos.numMaximoNoCompetitivosActivos) {
          return ValidacionRequisitosEquipoIp.NUM_MAX_PROYECTOS_NO_COMPETITIVOS_ACTUALES_IP;
        } else if (requisitos.numMinimoCompetitivos
          && response.numProyectosCompetitivos < requisitos.numMinimoCompetitivos) {
          return ValidacionRequisitosEquipoIp.NUM_MIN_PROYECTOS_COMPETITIVOS_IP;
        } else if (requisitos.numMinimoNoCompetitivos
          && response.numProyectosNoCompetitivos < requisitos.numMinimoNoCompetitivos) {
          return ValidacionRequisitosEquipoIp.NUM_MIN_PROYECTOS_NO_COMPETITIVOS_IP;
        } else {
          return null;
        }
      })
    );

  }

  private validateRequisitosConvocatoria(
    solicitudProyectoEquipo: ISolicitudProyectoEquipo,
    convocatoriaId: number
  ): Observable<ValidacionRequisitosEquipoIp> {
    if (solicitudProyectoEquipo.rolProyecto.rolPrincipal && convocatoriaId) {
      return this.validateRequisitosIp(solicitudProyectoEquipo, convocatoriaId);
    } else if (convocatoriaId) {
      return this.validateRequisitosEquipo(solicitudProyectoEquipo, convocatoriaId);
    } else {
      return of(null);
    }
  }

  public validateRequisitosConvocatoriaSolicitante(
    solicitante: IPersona,
    convocatoriaId: number
  ): Observable<ValidacionRequisitosEquipoIp> {
    if (!solicitante) {
      return of(null);
    }

    const solicitudProyectoEquipo = {
      persona: solicitante,
      rolProyecto: { rolPrincipal: true } as IRolProyecto,
    } as ISolicitudProyectoEquipo;
    return this.validateRequisitosConvocatoria(solicitudProyectoEquipo, convocatoriaId);
  }

  public validateRequisitosConvocatoriaGlobales(
    convocatoriaId: number
  ): Observable<ValidacionRequisitosEquipoIp> {

    return this.getRequisitosConvocatoria(convocatoriaId).pipe(
      switchMap(() => {
        return this.validateRequisitosEquipoGlobales(this.requisitosConvocatoria);
      }),
      switchMap(response => {
        if (!response) {
          return this.validateRequisitosIpGlobales(this.requisitosConvocatoria);
        }

        return of(response);
      })
    );
  }

}
