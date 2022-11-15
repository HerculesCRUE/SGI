import { IConfiguracion } from '@core/models/csp/configuracion';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { Fragment } from '@core/services/action-service';
import { ConfiguracionService } from '@core/services/csp/configuracion.service';
import { GrupoEquipoService } from '@core/services/csp/grupo-equipo/grupo-equipo.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface IGrupoEquipoListado extends IGrupoEquipo {
  categoriaProfesional: ICategoriaProfesional;
}

export class GrupoEquipoInvestigacionFragment extends Fragment {
  equipos$ = new BehaviorSubject<StatusWrapper<IGrupoEquipoListado>[]>([]);
  configuracion: IConfiguracion;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly grupoService: GrupoService,
    private readonly grupoEquipoService: GrupoEquipoService,
    private readonly personaService: PersonaService,
    private readonly vinculacionService: VinculacionService,
    public readonly readonly: boolean,
    private readonly configuracionService: ConfiguracionService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.grupoService.findMiembrosEquipo(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(element => {
                return this.personaService.findById(element.persona.id).pipe(
                  map(persona => {
                    element.persona = persona;
                    return element as IGrupoEquipoListado;
                  })
                );
              }),
              map(() => result)
            );
          }),
          map(miembrosEquipo => {
            return miembrosEquipo.items.map(miembroEquipo => {
              miembroEquipo.grupo = { id: this.getKey() } as IGrupo;
              return new StatusWrapper<IGrupoEquipoListado>(miembroEquipo as IGrupoEquipoListado);
            });
          }),
          switchMap(result => {
            return from(result).pipe(
              mergeMap(element => {
                const filter = new RSQLSgiRestFilter(
                  'fechaObtencion', SgiRestFilterOperator.LOWER_OR_EQUAL,
                  LuxonUtils.toBackend(element.value.fechaFin) ?? LuxonUtils.toBackend(element.value.fechaInicio)
                ).and('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL,
                  LuxonUtils.toBackend(element.value.fechaFin) ?? LuxonUtils.toBackend(element.value.fechaInicio));
                const options: SgiRestFindOptions = {
                  filter
                };
                return this.vinculacionService.findVinculacionesCategoriasProfesionalesByPersonaId(element.value.persona.id, options).pipe(
                  map(vinculacionCategoria => {
                    element.value.categoriaProfesional = vinculacionCategoria?.categoriaProfesional;
                    return element;
                  })
                );
              }),
              map(() => result)
            );
          })
        ).subscribe(
          result => {
            this.equipos$.next(result);
            this.checkErrors(result);
          },
          error => {
            this.logger.error(error);
          }
        )
      );

      this.subscriptions.push(
        this.configuracionService.getConfiguracion().subscribe(configuracion => this.configuracion = configuracion)
      );
    }
  }

  addGrupoEquipoInvestigacion(element: IGrupoEquipoListado) {
    this.getVinculacionPersona(element).subscribe();
    const wrapper = new StatusWrapper<IGrupoEquipoListado>(element);
    wrapper.setCreated();
    const current = this.equipos$.value;
    current.push(wrapper);
    this.equipos$.next(current);
    this.checkErrors(current);
    this.setChanges(true);
    return element;
  }

  updateGrupoEquipoInvestigacion(wrapper: StatusWrapper<IGrupoEquipoListado>): void {
    const current = this.equipos$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.getVinculacionPersona(this.equipos$.value[index].value).subscribe();
      current[index] = wrapper;
      this.equipos$.next(current);
      this.checkErrors(current);
      this.setChanges(true);
    }
  }

  deleteGrupoEquipoInvestigacion(wrapper: StatusWrapper<IGrupoEquipoListado>) {
    const current = this.equipos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.equipos$.next(current);
      this.checkErrors(current);
      this.setChanges(true);
    }
  }

  /*
    Cuando se crea un grupo se añade directamente al IP como miembro del equipo sin indicar dedicacion ni participacion,
    pero la participacion y la dedicacion son datos obligatorios a la hora de introducir un miembro del equipo. Por lo que
    nos aseguramos de que no existan registros sin dichos datos.
  */
  private checkErrors(miembrosEquipo: StatusWrapper<IGrupoEquipoListado>[]): void {
    this.setErrors(this.hasDedicacionError(miembrosEquipo));
  }

  private hasDedicacionError(miembrosEquipo: StatusWrapper<IGrupoEquipoListado>[]): boolean {
    return miembrosEquipo.some(miembroEquipo =>
      !!!miembroEquipo.value.dedicacion || !(typeof miembroEquipo.value.participacion === 'number'));
  }

  saveOrUpdate(): Observable<void> {
    const values = this.equipos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;

    return this.grupoEquipoService.updateList(id, values)
      .pipe(
        map(results => {
          return results.map(
            (value: IGrupoEquipoListado) => {
              const grupoEquipo = values.find(
                equipo => equipo.persona.id === value.persona.id
                  && equipo.fechaInicio.toMillis() === value.fechaInicio.toMillis()
                  && equipo.fechaFin?.toMillis() === value.fechaFin?.toMillis()
              );
              value.persona = grupoEquipo.persona;
              value.categoriaProfesional = grupoEquipo.categoriaProfesional;
              value.rol = grupoEquipo.rol;
              return value;
            });
        }),
        map(miembrosEquipo => {
          return miembrosEquipo.map(miembroEquipo => {
            miembroEquipo.grupo = { id: this.getKey() } as IGrupo;
            return new StatusWrapper<IGrupoEquipoListado>(miembroEquipo);
          });
        }),
        takeLast(1),
        map((results) => {
          this.equipos$.next(results);
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

  private getVinculacionPersona(element: IGrupoEquipoListado): Observable<IGrupoEquipoListado> {
    const filter = new RSQLSgiRestFilter(
      'fechaObtencion', SgiRestFilterOperator.LOWER_OR_EQUAL,
      LuxonUtils.toBackend(element.fechaFin) ?? LuxonUtils.toBackend(element.fechaInicio)
    ).and('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL,
      LuxonUtils.toBackend(element.fechaFin) ?? LuxonUtils.toBackend(element.fechaInicio));
    const options: SgiRestFindOptions = {
      filter
    };
    return this.vinculacionService.findVinculacionesCategoriasProfesionalesByPersonaId(element.persona.id, options)
      .pipe(map(vinculacionCategoria => {
        element.categoriaProfesional = vinculacionCategoria?.categoriaProfesional;
        return element;
      }));
  }

}
