import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEquipoInstrumental } from '@core/models/csp/grupo-equipo-instrumental';
import { Fragment } from '@core/services/action-service';
import { GrupoEquipoInstrumentalService } from '@core/services/csp/grupo-equipo-instrumental/grupo-equipo-instrumental.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, takeLast, tap } from 'rxjs/operators';


export class GrupoEquipoInstrumentalFragment extends Fragment {
  equiposInstrumentales$ = new BehaviorSubject<StatusWrapper<IGrupoEquipoInstrumental>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly grupoService: GrupoService,
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
        this.grupoService.findEquiposInstrumentales(id).pipe(
          map((response) => response.items),
        ).subscribe((equiposInstrumentales) => {
          this.equiposInstrumentales$.next(equiposInstrumentales.map(
            equipoInstrumental => {
              equipoInstrumental.grupo = { id: this.getKey() } as IGrupo;
              return new StatusWrapper<IGrupoEquipoInstrumental>(equipoInstrumental);
            })
          );
        }));
    }
  }

  addGrupoEquipoInstrumental(element: IGrupoEquipoInstrumental) {
    const wrapper = new StatusWrapper<IGrupoEquipoInstrumental>(element);
    wrapper.setCreated();
    const current = this.equiposInstrumentales$.value;
    current.push(wrapper);
    this.equiposInstrumentales$.next(current);
    this.setChanges(true);
    return element;
  }

  updateGrupoEquipoInstrumental(wrapper: StatusWrapper<IGrupoEquipoInstrumental>): void {
    const current = this.equiposInstrumentales$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.equiposInstrumentales$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteGrupoEquipoInstrumental(wrapper: StatusWrapper<IGrupoEquipoInstrumental>) {
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

    return this.grupoEquipoInstrumentalService.updateList(id, values)
      .pipe(
        map(results => {
          return results.map(
            (value: IGrupoEquipoInstrumental) => {
              return value;
            });
        }),
        map(equiposInstrumentales => {
          return equiposInstrumentales.map(equipoInstrumental => {
            equipoInstrumental.grupo = { id: this.getKey() } as IGrupo;
            return new StatusWrapper<IGrupoEquipoInstrumental>(equipoInstrumental);
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
