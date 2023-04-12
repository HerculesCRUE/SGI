import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoPersonaAutorizada } from '@core/models/csp/grupo-persona-autorizada';
import { Fragment } from '@core/services/action-service';
import { GrupoPersonaAutorizadaService } from '@core/services/csp/grupo-persona-autorizada/grupo-persona-autorizada.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class GrupoPersonaAutorizadaFragment extends Fragment {
  personasAutorizadas$ = new BehaviorSubject<StatusWrapper<IGrupoPersonaAutorizada>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly grupoService: GrupoService,
    private readonly grupoPersonaAutorizadaService: GrupoPersonaAutorizadaService,
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
        this.grupoService.findPersonasAutorizadas(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(element => {
                return this.personaService.findById(element.persona.id).pipe(
                  map(persona => {
                    element.persona = persona;
                    return element;
                  }),
                  catchError((err) => {
                    this.logger.error(err);
                    return of(element);
                  })
                );
              }),
              map(() => result)
            );
          }),
          map(miembrosPersonaAutorizada => {
            return miembrosPersonaAutorizada.items.map(miembroPersonaAutorizada => {
              miembroPersonaAutorizada.grupo = { id: this.getKey() } as IGrupo;
              return new StatusWrapper<IGrupoPersonaAutorizada>(miembroPersonaAutorizada);
            });
          })
        ).subscribe(
          result => {
            this.personasAutorizadas$.next(result);
          },
          error => {
            this.processError(error);
          }
        )
      );
    }
  }

  addGrupoPersonaAutorizada(element: IGrupoPersonaAutorizada) {
    const wrapper = new StatusWrapper<IGrupoPersonaAutorizada>(element);
    wrapper.setCreated();
    const current = this.personasAutorizadas$.value;
    current.push(wrapper);
    this.personasAutorizadas$.next(current);
    this.setChanges(true);
    return element;
  }

  updateGrupoPersonaAutorizada(wrapper: StatusWrapper<IGrupoPersonaAutorizada>): void {
    const current = this.personasAutorizadas$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.personasAutorizadas$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteGrupoPersonaAutorizada(wrapper: StatusWrapper<IGrupoPersonaAutorizada>) {
    const current = this.personasAutorizadas$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.personasAutorizadas$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.personasAutorizadas$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;

    return this.grupoPersonaAutorizadaService.updateList(id, values)
      .pipe(
        map(results => {
          return results.map(
            (value: IGrupoPersonaAutorizada) => {
              const grupoPersonaAutorizada = values.find(
                personaAutorizada => personaAutorizada.persona.id === value.persona.id
                  && personaAutorizada.fechaInicio?.toMillis() === value.fechaInicio?.toMillis()
                  && personaAutorizada.fechaFin?.toMillis() === value.fechaFin?.toMillis()
              );
              value.persona = grupoPersonaAutorizada.persona;
              return value;
            });
        }),
        map(miembrosPersonaAutorizada => {
          return miembrosPersonaAutorizada.map(miembroPersonaAutorizada => {
            miembroPersonaAutorizada.grupo = { id: this.getKey() } as IGrupo;
            return new StatusWrapper<IGrupoPersonaAutorizada>(miembroPersonaAutorizada);
          });
        }),
        takeLast(1),
        map((results) => {
          this.personasAutorizadas$.next(results);
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.personasAutorizadas$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

}
