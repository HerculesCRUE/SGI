import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { Fragment } from '@core/services/action-service';
import { GrupoLineaInvestigacionService } from '@core/services/csp/grupo-linea-investigacion/grupo-linea-investigacion.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { LineaInvestigacionService } from '@core/services/csp/linea-investigacion/linea-investigacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class GrupoLineaInvestigacionFragment extends Fragment {
  lineasInvestigaciones$ = new BehaviorSubject<StatusWrapper<IGrupoLineaInvestigacion>[]>([]);

  private gruposLineasInvestigacionEliminados: StatusWrapper<IGrupoLineaInvestigacion>[] = [];

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly grupoService: GrupoService,
    private readonly grupoLineaInvestigacionService: GrupoLineaInvestigacionService,
    private readonly lineaInvestigacionService: LineaInvestigacionService,
    public readonly readonly: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.grupoService.findLineasInvestigacion(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(element => {
                return this.lineaInvestigacionService.findById(element.lineaInvestigacion.id).pipe(
                  map(lineaInvestigacion => {
                    element.lineaInvestigacion = lineaInvestigacion;
                    return element;
                  })
                );
              }),
              map(() => result)
            );
          }),
          map(grupoLineasInvestigacion => {
            return grupoLineasInvestigacion.items.map(grupoLineaInvestigacion => {
              grupoLineaInvestigacion.grupo = { id: this.getKey() } as IGrupo;
              return new StatusWrapper<IGrupoLineaInvestigacion>(grupoLineaInvestigacion);
            });
          })
        ).subscribe(
          result => {
            this.lineasInvestigaciones$.next(result);
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  deleteGrupoLineaInvestigacion(wrapper: StatusWrapper<IGrupoLineaInvestigacion>) {
    const current = this.lineasInvestigaciones$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.gruposLineasInvestigacionEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.lineasInvestigaciones$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return this.deleteGruposLineasInvestigacion().pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteGruposLineasInvestigacion(): Observable<void> {
    if (this.gruposLineasInvestigacionEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.gruposLineasInvestigacionEliminados).pipe(
      mergeMap((wrapped) => {
        return this.grupoLineaInvestigacionService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.gruposLineasInvestigacionEliminados = this.gruposLineasInvestigacionEliminados.filter(deletedLineaInvestigacion =>
                deletedLineaInvestigacion.value.id !== wrapped.value.id);
            }),
            takeLast(1)
          );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.lineasInvestigaciones$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

}
