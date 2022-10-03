import { IGrupoLineaEquipoInstrumental } from '@core/models/csp/grupo-linea-equipo-instrumental';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { Fragment } from '@core/services/action-service';
import { GrupoEquipoInstrumentalService } from '@core/services/csp/grupo-equipo-instrumental/grupo-equipo-instrumental.service';
import { GrupoLineaEquipoInstrumentalService } from '@core/services/csp/grupo-linea-equipo-instrumental/grupo-linea-equipo-instrumental.service';
import { GrupoLineaInvestigacionService } from '@core/services/csp/grupo-linea-investigacion/grupo-linea-investigacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';


export class GrupoLineaEquipoInstrumentalFragment extends Fragment {
  equiposInstrumentales$ = new BehaviorSubject<StatusWrapper<IGrupoLineaEquipoInstrumental>[]>([]);

  get idGrupo(): number {
    return this.grupoId;
  }

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private grupoId: number,
    private readonly grupoLineaInvestigacionService: GrupoLineaInvestigacionService,
    private readonly grupoLineaEquipoInstrumentalService: GrupoLineaEquipoInstrumentalService,
    private readonly grupoEquipoInstrumentalService: GrupoEquipoInstrumentalService,
    public readonly readonly: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.grupoLineaInvestigacionService.findLineasEquiposInstrumentales(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(element => {
                return this.grupoEquipoInstrumentalService.findById(element.grupoEquipoInstrumental.id).pipe(
                  map(equipoInstrumental => {
                    element.grupoEquipoInstrumental = equipoInstrumental;
                    return element;
                  })
                );
              }),
              map(() => result)
            );
          }),
          map(lineaEquiposInstrumentales => {
            return lineaEquiposInstrumentales.items.map(lineaEquipoInstrumental => {
              lineaEquipoInstrumental.grupoLineaInvestigacion = { id: this.getKey() } as IGrupoLineaInvestigacion;
              return new StatusWrapper<IGrupoLineaEquipoInstrumental>(lineaEquipoInstrumental);
            });
          })
        ).subscribe(
          result => {
            this.equiposInstrumentales$.next(result);
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addGrupoLineaEquipoInstrumental(element: IGrupoLineaEquipoInstrumental) {
    const wrapper = new StatusWrapper<IGrupoLineaEquipoInstrumental>(element);
    wrapper.setCreated();
    const current = this.equiposInstrumentales$.value;
    current.push(wrapper);
    this.equiposInstrumentales$.next(current);
    this.setChanges(true);
    return element;
  }

  updateGrupoLineaEquipoInstrumental(wrapper: StatusWrapper<IGrupoLineaEquipoInstrumental>): void {
    const current = this.equiposInstrumentales$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.equiposInstrumentales$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteGrupoLineaEquipoInstrumental(wrapper: StatusWrapper<IGrupoLineaEquipoInstrumental>) {
    const current = this.equiposInstrumentales$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.equiposInstrumentales$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.equiposInstrumentales$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;

    return this.grupoLineaEquipoInstrumentalService.updateList(id, values)
      .pipe(
        map(results => {
          return results.map(
            (value: IGrupoLineaEquipoInstrumental) => {
              const grupoLineaEquipoInstrumental = values.find(
                lineaEquipoInstrumental => lineaEquipoInstrumental.grupoEquipoInstrumental.id === value.grupoEquipoInstrumental.id
              );
              value.grupoEquipoInstrumental = grupoLineaEquipoInstrumental.grupoEquipoInstrumental;
              return value;
            });
        }),
        map(equiposInstrumentales => {
          return equiposInstrumentales.map(equipoInstrumental => {
            equipoInstrumental.grupoLineaInvestigacion = { id: this.getKey() } as IGrupoLineaInvestigacion;
            return new StatusWrapper<IGrupoLineaEquipoInstrumental>(equipoInstrumental);
          });
        }),
        takeLast(1),
        map((results) => {
          this.equiposInstrumentales$.next(results);
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.equiposInstrumentales$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

}
