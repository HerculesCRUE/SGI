import { IProyectoResponsableEconomico } from '@core/models/csp/proyecto-responsable-economico';
import { Fragment } from '@core/services/action-service';
import { ProyectoResponsableEconomicoService } from '@core/services/csp/proyecto-responsable-economico/proyecto-responsable-economico.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { map, mergeMap, switchMap, tap } from 'rxjs/operators';

export class ProyectoResponsableEconomicoFragment extends Fragment {
  responsablesEconomicos$ = new BehaviorSubject<StatusWrapper<IProyectoResponsableEconomico>[]>([]);

  constructor(
    key: number,
    private proyectoService: ProyectoService,
    private proyectoResponsableEconomicoService: ProyectoResponsableEconomicoService,
    private personaService: PersonaService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  public reloadData(): void {
    this.loadTableData();
  }

  protected onInitialize(): void {
    this.loadTableData();
  }

  private loadTableData(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;

      this.subscriptions.push(
        this.proyectoService.findAllProyectoResponsablesEconomicos(id).pipe(
          map(response => response.items),
          switchMap((result) => {
            return from(result).pipe(
              mergeMap((responsableEconomico) => {
                return this.personaService.findById(responsableEconomico.persona.id).pipe(
                  map((persona) => {
                    responsableEconomico.persona = persona;
                    return responsableEconomico;
                  })
                );
              })
            );
          })
        ).subscribe((responsableEconomico) => {
          this.responsablesEconomicos$.value.push(new StatusWrapper<IProyectoResponsableEconomico>(responsableEconomico));
          this.responsablesEconomicos$.next(this.responsablesEconomicos$.value);
        })
      );
    }
  }

  saveOrUpdate(): Observable<void> {
    const responsablesEconomicos = this.responsablesEconomicos$.value.map(wrapper => wrapper.value);

    return this.proyectoResponsableEconomicoService
      .updateProyectoResponsablesEconomicos(this.getKey() as number, responsablesEconomicos)
      .pipe(
        map((responsablesEconomicosActualizados) => {
          this.responsablesEconomicos$.next(
            responsablesEconomicosActualizados.map(
              (responsableEconomico) => {
                responsableEconomico.persona = responsablesEconomicos.find(
                  responsable => responsable.persona.id === responsableEconomico.persona.id
                ).persona;
                return new StatusWrapper<IProyectoResponsableEconomico>(responsableEconomico);
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

  deleteResponsableEconomico(wrapper: StatusWrapper<IProyectoResponsableEconomico>): void {
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

  addResponsableEconomico(equipoProyectoData: IProyectoResponsableEconomico): void {
    const wrapped = new StatusWrapper<IProyectoResponsableEconomico>(equipoProyectoData);
    wrapped.setCreated();
    const current = this.responsablesEconomicos$.value;
    current.push(wrapped);
    this.responsablesEconomicos$.next(current);
    this.setChanges(true);
  }

  updateResponsableEconomico(wrapper: StatusWrapper<IProyectoResponsableEconomico>): void {
    if (!wrapper.created) {
      wrapper.setEdited();
      this.setChanges(true);
    }
    this.responsablesEconomicos$.next(this.responsablesEconomicos$.value);
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.responsablesEconomicos$.value.some((wrapper) => wrapper.touched);
    return !touched;
  }
}
