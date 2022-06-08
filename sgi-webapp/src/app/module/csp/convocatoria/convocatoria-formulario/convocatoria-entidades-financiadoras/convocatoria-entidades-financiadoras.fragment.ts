import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaEntidadFinanciadoraService } from '@core/services/csp/convocatoria-entidad-financiadora.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ConvocatoriaEntidadesFinanciadorasFragment extends Fragment {
  convocatoriaEntidadesFinanciadoras$ = new BehaviorSubject<StatusWrapper<IConvocatoriaEntidadFinanciadora>[]>([]);
  convocatoriaEntidadesEliminadas: StatusWrapper<IConvocatoriaEntidadFinanciadora>[] = [];

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaEntidadFinanciadoraService: ConvocatoriaEntidadFinanciadoraService,
    public isConvocatoriaVinculada: boolean,
    public hasEditPerm: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.convocatoriaService.findEntidadesFinanciadoras(this.getKey() as number).pipe(
        map(response => response.items)
      ).subscribe(convocatoriaEntidadesFinanciadoras => {
        this.convocatoriaEntidadesFinanciadoras$.next(convocatoriaEntidadesFinanciadoras.map(
          entidadesFinanciadora => new StatusWrapper<IConvocatoriaEntidadFinanciadora>(entidadesFinanciadora))
        );
      });
      this.subscriptions.push(subscription);
    }
  }

  public deleteConvocatoriaEntidadFinanciadora(wrapper: StatusWrapper<IConvocatoriaEntidadFinanciadora>) {
    const current = this.convocatoriaEntidadesFinanciadoras$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      if (!wrapper.created) {
        this.convocatoriaEntidadesEliminadas.push(current[index]);
      }
      current.splice(index, 1);
      this.convocatoriaEntidadesFinanciadoras$.next(current);
      this.setChanges(true);
    }
  }

  public updateConvocatoriaEntidadFinanciadora(wrapper: StatusWrapper<IConvocatoriaEntidadFinanciadora>) {
    const current = this.convocatoriaEntidadesFinanciadoras$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.convocatoriaEntidadesFinanciadoras$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  public addConvocatoriaEntidadFinanciadora(entidadFinanciadora: IConvocatoriaEntidadFinanciadora) {
    const wrapped = new StatusWrapper<IConvocatoriaEntidadFinanciadora>(entidadFinanciadora);
    wrapped.setCreated();
    const current = this.convocatoriaEntidadesFinanciadoras$.value;
    current.push(wrapped);
    this.convocatoriaEntidadesFinanciadoras$.next(current);
    this.setChanges(true);
    this.setErrors(false);
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteConvocatoriaEntidadFinanciadoras(),
      this.updateConvocatoriaEntidadFinanciadoras(),
      this.createConvocatoriaEntidadFinanciadoras()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteConvocatoriaEntidadFinanciadoras(): Observable<void> {
    const deletedEntidades = this.convocatoriaEntidadesEliminadas.filter((value) => value.value.id);
    if (deletedEntidades.length === 0) {
      return of(void 0);
    }
    return from(deletedEntidades).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaEntidadFinanciadoraService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.convocatoriaEntidadesEliminadas = deletedEntidades.filter(deletedModelo =>
                deletedModelo.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private updateConvocatoriaEntidadFinanciadoras(): Observable<void> {
    const editedEntidades = this.convocatoriaEntidadesFinanciadoras$.value.filter((value) => value.edited);
    if (editedEntidades.length === 0) {
      return of(void 0);
    }
    return from(editedEntidades).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaEntidadFinanciadoraService.update(wrapped.value.id, wrapped.value).pipe(
          map((updatedEntidad) => {
            const index = this.convocatoriaEntidadesFinanciadoras$.value.findIndex((currentEntidad) => currentEntidad === wrapped);
            this.convocatoriaEntidadesFinanciadoras$.value[index] = new StatusWrapper<IConvocatoriaEntidadFinanciadora>(updatedEntidad);
          })
        );
      })
    );
  }

  private createConvocatoriaEntidadFinanciadoras(): Observable<void> {
    const createdEntidades = this.convocatoriaEntidadesFinanciadoras$.value.filter((value) => value.created);
    if (createdEntidades.length === 0) {
      return of(void 0);
    }
    createdEntidades.forEach(
      (wrapper: StatusWrapper<IConvocatoriaEntidadFinanciadora>) => wrapper.value.convocatoriaId = this.getKey() as number
    );
    return from(createdEntidades).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaEntidadFinanciadoraService.create(wrapped.value).pipe(
          map((createdEntidad) => {
            const index = this.convocatoriaEntidadesFinanciadoras$.value.findIndex((currentEntidad) => currentEntidad === wrapped);
            wrapped.value.id = createdEntidad.id;
            this.convocatoriaEntidadesFinanciadoras$.value[index] = new StatusWrapper<IConvocatoriaEntidadFinanciadora>(wrapped.value);
            this.convocatoriaEntidadesFinanciadoras$.next(this.convocatoriaEntidadesFinanciadoras$.value);
          })
        );
      }));
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.convocatoriaEntidadesFinanciadoras$.value.some((wrapper) => wrapper.touched);
    return (this.convocatoriaEntidadesEliminadas.length > 0 || touched);
  }
}
