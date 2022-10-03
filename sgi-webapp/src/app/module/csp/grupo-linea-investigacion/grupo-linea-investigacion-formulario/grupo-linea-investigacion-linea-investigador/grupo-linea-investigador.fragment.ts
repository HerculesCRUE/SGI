import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { IGrupoLineaInvestigador } from '@core/models/csp/grupo-linea-investigador';
import { Fragment } from '@core/services/action-service';
import { GrupoLineaInvestigacionService } from '@core/services/csp/grupo-linea-investigacion/grupo-linea-investigacion.service';
import { GrupoLineaInvestigadorService } from '@core/services/csp/grupo-linea-investigador/grupo-linea-investigador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class GrupoLineaInvestigadorFragment extends Fragment {
  lineasInvestigadores$ = new BehaviorSubject<StatusWrapper<IGrupoLineaInvestigador>[]>([]);

  get idGrupo(): number {
    return this.grupoId;
  }

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private grupoId: number,
    private readonly grupoLineaInvestigacionService: GrupoLineaInvestigacionService,
    private readonly grupoLineaInvestigadorService: GrupoLineaInvestigadorService,
    private readonly personaService: PersonaService,
    public readonly readonly: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.grupoLineaInvestigacionService.findLineasInvestigadores(id).pipe(
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
          map(miembrosLineaInvestigador => {
            return miembrosLineaInvestigador.items.map(miembroLineaInvestigador => {
              miembroLineaInvestigador.grupoLineaInvestigacion = { id: this.getKey() } as IGrupoLineaInvestigacion;
              return new StatusWrapper<IGrupoLineaInvestigador>(miembroLineaInvestigador);
            });
          })
        ).subscribe(
          result => {
            this.lineasInvestigadores$.next(result);
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addGrupoLineaInvestigador(element: IGrupoLineaInvestigador) {
    const wrapper = new StatusWrapper<IGrupoLineaInvestigador>(element);
    wrapper.setCreated();
    const current = this.lineasInvestigadores$.value;
    current.push(wrapper);
    this.lineasInvestigadores$.next(current);
    this.setChanges(true);
    return element;
  }

  updateGrupoLineaInvestigador(wrapper: StatusWrapper<IGrupoLineaInvestigador>): void {
    const current = this.lineasInvestigadores$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.lineasInvestigadores$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteGrupoLineaInvestigador(wrapper: StatusWrapper<IGrupoLineaInvestigador>) {
    const current = this.lineasInvestigadores$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.lineasInvestigadores$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.lineasInvestigadores$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;

    return this.grupoLineaInvestigadorService.updateList(id, values)
      .pipe(
        map(results => {
          return results.map(
            (value: IGrupoLineaInvestigador) => {
              const grupoLineaInvestigador = values.find(
                lineaInvestigador => lineaInvestigador.persona.id === value.persona.id
                  && lineaInvestigador.fechaInicio?.toMillis() === value.fechaInicio?.toMillis()
                  && lineaInvestigador.fechaFin?.toMillis() === value.fechaFin?.toMillis()
              );
              value.persona = grupoLineaInvestigador.persona;
              return value;
            });
        }),
        map(miembrosLineaInvestigador => {
          return miembrosLineaInvestigador.map(miembroLineaInvestigador => {
            miembroLineaInvestigador.grupoLineaInvestigacion = { id: this.getKey() } as IGrupoLineaInvestigacion;
            return new StatusWrapper<IGrupoLineaInvestigador>(miembroLineaInvestigador);
          });
        }),
        takeLast(1),
        map((results) => {
          this.lineasInvestigadores$.next(results);
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.lineasInvestigadores$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

}
