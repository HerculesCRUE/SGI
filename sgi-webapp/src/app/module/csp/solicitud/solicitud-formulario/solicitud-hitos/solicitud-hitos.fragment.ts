import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { Fragment } from '@core/services/action-service';
import { SolicitudHitoService } from '@core/services/csp/solicitud-hito.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class SolicitudHitosFragment extends Fragment {
  hitos$ = new BehaviorSubject<StatusWrapper<ISolicitudHito>[]>([]);
  private hitosEliminados: StatusWrapper<ISolicitudHito>[] = [];

  constructor(
    key: number,
    private service: SolicitudHitoService,
    private solicitudService: SolicitudService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.solicitudService.findHitosSolicitud(this.getKey() as number).subscribe(
        (hitos) => {
          this.hitos$.next(hitos.items.map(listaHitos => new StatusWrapper<ISolicitudHito>(listaHitos)));
        });
      this.subscriptions.push(subscription);
    }
  }

  public addHito(hito: ISolicitudHito) {
    const wrapped = new StatusWrapper<ISolicitudHito>(hito);
    wrapped.setCreated();
    const current = this.hitos$.value;
    current.push(wrapped);
    this.hitos$.next(current);
    this.setChanges(true);
  }

  public deleteHito(wrapper: StatusWrapper<ISolicitudHito>) {
    const current = this.hitos$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.hitosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.hitos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteHitos(),
      this.updateHitos(),
      this.createHitos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteHitos(): Observable<void> {
    if (this.hitosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.hitosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.service.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.hitosEliminados = this.hitosEliminados.filter(deletedHito =>
                deletedHito.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private updateHitos(): Observable<void> {
    const updateHitos = this.hitos$.value.filter((convocatoriaHito) => convocatoriaHito.edited);
    if (updateHitos.length === 0) {
      return of(void 0);
    }
    return from(updateHitos).pipe(
      mergeMap((wrappedHitos) => {
        return this.service.update(wrappedHitos.value.id, wrappedHitos.value).pipe(
          map((updateHito) => {
            const index = this.hitos$.value.findIndex((currenthitos) => currenthitos === wrappedHitos);
            this.hitos$.value[index] = new StatusWrapper<ISolicitudHito>(updateHito);
          })
        );
      })
    );
  }

  private createHitos(): Observable<void> {
    const createdHitos = this.hitos$.value.filter((convocatoriaHito) => convocatoriaHito.created);
    if (createdHitos.length === 0) {
      return of(void 0);
    }
    createdHitos.forEach(
      (wrapper) => wrapper.value.solicitudId = this.getKey() as number
    );
    return from(createdHitos).pipe(
      mergeMap((wrappedHitos) => {
        return this.service.create(wrappedHitos.value).pipe(
          map((createHito) => {
            const index = this.hitos$.value.findIndex((currenthitos) => currenthitos === wrappedHitos);
            this.hitos$.value[index] = new StatusWrapper<ISolicitudHito>(createHito);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.hitos$.value.some((wrapper) => wrapper.touched);
    return (this.hitosEliminados.length > 0 || touched);
  }
}
