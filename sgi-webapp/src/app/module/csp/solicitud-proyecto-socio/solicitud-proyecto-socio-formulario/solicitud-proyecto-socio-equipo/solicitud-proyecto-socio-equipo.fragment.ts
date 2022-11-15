import { ISolicitudProyectoSocioEquipo } from '@core/models/csp/solicitud-proyecto-socio-equipo';
import { Fragment } from '@core/services/action-service';
import { SolicitudProyectoSocioEquipoService } from '@core/services/csp/solicitud-proyecto-socio-equipo.service';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class SolicitudProyectoSocioEquipoFragment extends Fragment {
  solicitudProyectoSocioEquipos$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoSocioEquipo>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private solicitudProyectoSocioService: SolicitudProyectoSocioService,
    private solicitudProyectoSocioEquipoService: SolicitudProyectoSocioEquipoService,
    private personaService: PersonaService,
    public readonly
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.solicitudProyectoSocioService.findAllSolicitudProyectoSocioEquipo(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(solicitudProyectoSocioEquipo => {
                const personaId = solicitudProyectoSocioEquipo.persona.id;
                return this.personaService.findById(personaId).pipe(
                  map(persona => {
                    solicitudProyectoSocioEquipo.persona = persona;
                    return solicitudProyectoSocioEquipo;
                  })
                );
              }),
              map(() => result)
            );
          }),
          map(response => response.items)
        ).subscribe(
          result => {
            this.solicitudProyectoSocioEquipos$.next(
              result.map(solicitudProyectoSocioEquipo =>
                new StatusWrapper<ISolicitudProyectoSocioEquipo>(solicitudProyectoSocioEquipo)
              )
            );
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addProyectoEquipoSocio(element: ISolicitudProyectoSocioEquipo): void {
    const wrapped = new StatusWrapper<ISolicitudProyectoSocioEquipo>(element);
    wrapped.setCreated();
    const current = this.solicitudProyectoSocioEquipos$.value;
    current.push(wrapped);
    this.solicitudProyectoSocioEquipos$.next(current);
    this.setChanges(true);
  }

  updateProyectoEquipoSocio(wrapper: StatusWrapper<ISolicitudProyectoSocioEquipo>): void {
    const current = this.solicitudProyectoSocioEquipos$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.solicitudProyectoSocioEquipos$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteProyectoEquipoSocio(wrapper: StatusWrapper<ISolicitudProyectoSocioEquipo>): void {
    const current = this.solicitudProyectoSocioEquipos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.solicitudProyectoSocioEquipos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.solicitudProyectoSocioEquipos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.solicitudProyectoSocioEquipoService.updateList(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          return results.map(
            (value: ISolicitudProyectoSocioEquipo) => {
              const socio = values.find(
                equipo => equipo.persona.id === value.persona.id
                  && equipo.mesInicio === value.mesInicio
                  && equipo.mesFin === value.mesFin
              );
              value.persona = socio.persona;
              return value;
            });
        }),
        takeLast(1),
        map((results) => {
          this.solicitudProyectoSocioEquipos$.next(
            results.map(value => new StatusWrapper<ISolicitudProyectoSocioEquipo>(value)));
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.solicitudProyectoSocioEquipos$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }
}
