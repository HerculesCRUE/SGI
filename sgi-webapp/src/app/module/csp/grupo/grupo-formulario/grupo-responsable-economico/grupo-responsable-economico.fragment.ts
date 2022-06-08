import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoResponsableEconomico } from '@core/models/csp/grupo-responsable-economico';
import { Fragment } from '@core/services/action-service';
import { GrupoResponsableEconomicoService } from '@core/services/csp/grupo-responsable-economico/grupo-responsable-economico.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class GrupoResponsableEconomicoFragment extends Fragment {
  responsablesEconomicos$ = new BehaviorSubject<StatusWrapper<IGrupoResponsableEconomico>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly grupoService: GrupoService,
    private readonly grupoResponsableEconomicoService: GrupoResponsableEconomicoService,
    private readonly personaService: PersonaService,
    private sgiAuthService: SgiAuthService,
    private readonly: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.grupoService.findResponsablesEconomicos(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(element => {
                return this.personaService.findById(element.persona.id).pipe(
                  map(persona => {
                    element.persona = persona;
                    if (persona.id === this.sgiAuthService.authStatus$?.getValue()?.userRefId) {
                      // Es responsable economico
                      this.readonly = false;
                    }
                    return element as IGrupoResponsableEconomico;
                  })
                );
              }),
              map(() => result)
            );
          }),
          map(miembrosEquipo => {
            return miembrosEquipo.items.map(miembroEquipo => {
              miembroEquipo.grupo = { id: this.getKey() } as IGrupo;
              return new StatusWrapper<IGrupoResponsableEconomico>(miembroEquipo as IGrupoResponsableEconomico);
            });
          })
        ).subscribe(
          result => {
            this.responsablesEconomicos$.next(result);
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addGrupoResponsableEconomico(element: IGrupoResponsableEconomico) {
    const wrapper = new StatusWrapper<IGrupoResponsableEconomico>(element);
    wrapper.setCreated();
    const current = this.responsablesEconomicos$.value;
    current.push(wrapper);
    this.responsablesEconomicos$.next(current);
    this.setChanges(true);
    return element;
  }

  updateGrupoResponsableEconomico(wrapper: StatusWrapper<IGrupoResponsableEconomico>): void {
    const current = this.responsablesEconomicos$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.responsablesEconomicos$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteGrupoResponsableEconomico(wrapper: StatusWrapper<IGrupoResponsableEconomico>) {
    const current = this.responsablesEconomicos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.responsablesEconomicos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.responsablesEconomicos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;

    return this.grupoResponsableEconomicoService.updateList(id, values)
      .pipe(
        map(results => {
          return results.map(
            (value: IGrupoResponsableEconomico) => {
              const grupoResponsableEconomico = values.find(
                equipo => equipo.persona.id === value.persona.id
                  && equipo.fechaInicio?.toMillis() === value.fechaInicio?.toMillis()
                  && equipo.fechaFin?.toMillis() === value.fechaFin?.toMillis()
              );
              value.persona = grupoResponsableEconomico.persona;
              return value;
            });
        }),
        map(miembrosEquipo => {
          return miembrosEquipo.map(miembroEquipo => {
            miembroEquipo.grupo = { id: this.getKey() } as IGrupo;
            return new StatusWrapper<IGrupoResponsableEconomico>(miembroEquipo as IGrupoResponsableEconomico);
          });
        }),
        takeLast(1),
        map((results) => {
          this.responsablesEconomicos$.next(results);
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.responsablesEconomicos$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

}
