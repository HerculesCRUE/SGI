import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { Fragment } from '@core/services/action-service';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, Observable, of, Subject } from 'rxjs';
import { concatMap, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoProrrogasFragment extends Fragment {
  prorrogas$ = new BehaviorSubject<StatusWrapper<IProyectoProrroga>[]>([]);
  private prorrogasEliminados: StatusWrapper<IProyectoProrroga>[] = [];

  readonly ultimaProrroga$: Subject<IProyectoProrroga> = new BehaviorSubject<IProyectoProrroga>(null);

  constructor(
    key: number,
    private proyectoService: ProyectoService,
    private proyectoProrrogaService: ProyectoProrrogaService,
    private documentoService: DocumentoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.proyectoService.findAllProyectoProrrogaProyecto(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((prorrogas) => {
        this.prorrogas$.next(prorrogas.map(
          periodoSeguimiento => new StatusWrapper<IProyectoProrroga>(periodoSeguimiento))
        );
      });
    }
  }

  public deleteProrroga(wrapper: StatusWrapper<IProyectoProrroga>) {
    const current = this.prorrogas$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.prorrogasEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.prorrogas$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return this.deleteProrrogas().pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteProrrogas(): Observable<void> {
    if (this.prorrogasEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.prorrogasEliminados).pipe(
      concatMap((wrapped) => {
        return this.proyectoProrrogaService.findDocumentos(wrapped.value.id).pipe(
          switchMap((documentos) => {
            return this.proyectoProrrogaService.deleteById(wrapped.value.id)
              .pipe(
                tap(() => {
                  this.prorrogasEliminados = this.prorrogasEliminados
                    .filter(deletedProrroga => deletedProrroga.value.id !== wrapped.value.id);
                }),
                switchMap(() =>
                  from(documentos.items).pipe(
                    mergeMap(documento => {
                      return this.documentoService.eliminarFichero(documento.documentoRef);
                    }),
                    takeLast(1)
                  )
                ),
                takeLast(1)
              );
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    return this.prorrogasEliminados.length > 0;
  }

}
