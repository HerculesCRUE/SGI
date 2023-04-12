import { ISolicitudProyectoResponsableEconomico } from '@core/models/csp/solicitud-proyecto-responsable-economico';
import { Fragment } from '@core/services/action-service';
import { SolicitudProyectoResponsableEconomicoService } from '@core/services/csp/solicitud-proyecto-responsable-economico/solicitud-proyecto-responsable-economico.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, tap } from 'rxjs/operators';

export class SolicitudProyectoResponsableEconomicoFragment extends Fragment {
  responsablesEconomicos$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoResponsableEconomico>[]>([]);

  constructor(
    private logger: NGXLogger,
    key: number,
    private solicitudService: SolicitudService,
    private solicitudProyectoResponsableEconomicoService: SolicitudProyectoResponsableEconomicoService,
    private personaService: PersonaService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    const id = this.getKey() as number;
    if (id) {
      this.subscriptions.push(
        this.solicitudService.findAllSolicitudProyectoResponsablesEconomicos(id).pipe(
          map(result => result.items),
          switchMap((result) => {
            return from(result).pipe(
              mergeMap((responsableEconomico) => {
                return this.personaService.findById(responsableEconomico.persona.id).pipe(
                  map((persona) => {
                    responsableEconomico.persona = persona;
                    return responsableEconomico;
                  }),
                  catchError((err) => {
                    this.logger.error(err);
                    return of(responsableEconomico);
                  })
                );
              })
            );
          })
        ).subscribe((responsableEconomico) => {
          this.responsablesEconomicos$.value.push(new StatusWrapper<ISolicitudProyectoResponsableEconomico>(responsableEconomico));
          this.responsablesEconomicos$.next(this.responsablesEconomicos$.value);
        })
      );
    }
  }

  saveOrUpdate(): Observable<void> {
    const responsablesEconomicos = this.responsablesEconomicos$.value.map(wrapper => wrapper.value);

    return this.solicitudProyectoResponsableEconomicoService
      .updateSolicitudProyectoResponsablesEconomicos(this.getKey() as number, responsablesEconomicos)
      .pipe(
        map((responsablesEconomicosActualizados) => {
          this.responsablesEconomicos$.next(
            responsablesEconomicosActualizados.map(
              (responsableEconomico) => {
                responsableEconomico.persona = responsablesEconomicos.find(
                  responsable => responsable.persona.id === responsableEconomico.persona.id
                ).persona;
                return new StatusWrapper<ISolicitudProyectoResponsableEconomico>(responsableEconomico);
              })
          );
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  deleteResponsableEconomico(wrapper: StatusWrapper<ISolicitudProyectoResponsableEconomico>): void {
    const current = this.responsablesEconomicos$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      current.splice(index, 1);
      this.responsablesEconomicos$.next(current);
      this.setChanges(true);
    }
  }

  addResponsableEconomico(equipoProyectoData: ISolicitudProyectoResponsableEconomico): void {
    const wrapped = new StatusWrapper<ISolicitudProyectoResponsableEconomico>(equipoProyectoData);
    wrapped.setCreated();
    const current = this.responsablesEconomicos$.value;
    current.push(wrapped);
    this.responsablesEconomicos$.next(current);
    this.setChanges(true);
  }

  updateResponsableEconomico(wrapper: StatusWrapper<ISolicitudProyectoResponsableEconomico>): void {
    if (!wrapper.created) {
      wrapper.setEdited();
      this.setChanges(true);
    }
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.responsablesEconomicos$.value.some((wrapper) => wrapper.touched);
    return !touched;
  }

}
