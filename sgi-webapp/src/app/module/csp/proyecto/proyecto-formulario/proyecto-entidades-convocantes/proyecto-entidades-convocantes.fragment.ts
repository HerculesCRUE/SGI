import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { Fragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoEntidadesConvocantesFragment extends Fragment {
  private deleted: Set<number> = new Set<number>();
  private edited: Set<number> = new Set<number>();
  proyectoEntidadConvocantes$ = new BehaviorSubject<IProyectoEntidadConvocante[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private proyectoService: ProyectoService,
    private empresaService: EmpresaService,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.proyectoService.findAllEntidadConvocantes(this.getKey() as number).pipe(
        map((response) => response.items),
        mergeMap(proyectoEntidadConvocanteData => {
          return from(proyectoEntidadConvocanteData).pipe(
            mergeMap((data) => {
              return this.loadEmpresa(data);
            })
          );
        }),
      ).subscribe((proyectoEntidadConvocanteData) => {
        const current = this.proyectoEntidadConvocantes$.value;
        current.push(proyectoEntidadConvocanteData);
        this.proyectoEntidadConvocantes$.next(current);
      });
      this.subscriptions.push(subscription);
    }
  }

  private loadEmpresa(data: IProyectoEntidadConvocante): Observable<IProyectoEntidadConvocante> {
    return this.empresaService.findById(data.entidad.id).pipe(
      map(empresa => {
        data.entidad = empresa;
        return data;
      }),
      catchError((error) => {
        this.logger.error(error);
        return of(data);
      })
    );
  }

  public deleteProyectoEntidadConvocante(proyectoEntidadConvocante: IProyectoEntidadConvocante) {
    if (proyectoEntidadConvocante.id) {
      this.deleted.add(proyectoEntidadConvocante.id);
    }

    const current = this.proyectoEntidadConvocantes$.value;
    const index = current.findIndex(value => value === proyectoEntidadConvocante);
    current.splice(index, 1);
    this.proyectoEntidadConvocantes$.next(current);

    this.setChanges(true);
  }

  public updateProyectoEntidadConvocante(data: IProyectoEntidadConvocante) {
    let current = this.proyectoEntidadConvocantes$.value;
    current = current.map((proyectoEntidadConvocante) => {
      if (proyectoEntidadConvocante.id === data.id) {
        // Creamos un nuevo ProyectoEntidadConvocante para forzar la actualización de la fila
        // (de otro modo el pipe "proyectoEntidadConvocantePlan" no se re-evalúa)
        const newProyectoEntidadConvocante = { ...data };
        if (data.id) {
          this.edited.add(data.id);
        }
        return newProyectoEntidadConvocante;
      }
      return proyectoEntidadConvocante;
    });

    this.proyectoEntidadConvocantes$.next(current);
    this.setChanges(true);
  }

  public addProyectoEntidadConvocante(data: IProyectoEntidadConvocante) {
    const current: IProyectoEntidadConvocante[] = this.proyectoEntidadConvocantes$.value;
    current.push(data);
    this.proyectoEntidadConvocantes$.next(current);
    this.setChanges(true);
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteProyectoEntidadConvocantes(),
      this.updateProyectoEntidadConvocantes(),
      this.createProyectoEntidadConvocantes()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteProyectoEntidadConvocantes(): Observable<void> {
    if (this.deleted.size === 0) {
      return of(void 0);
    }
    return from(this.deleted).pipe(
      mergeMap((id) => {
        return this.proyectoService.deleteEntidadConvocanteById(this.getKey() as number, id)
          .pipe(
            tap(() => {
              this.deleted.delete(id);
            })
          );
      })
    );
  }

  private updateProyectoEntidadConvocantes(): Observable<void> {
    const editedEntidades = this.proyectoEntidadConvocantes$.value.filter((value) => value.id && this.edited.has(value.id));
    if (editedEntidades.length === 0) {
      return of(void 0);
    }
    return from(editedEntidades).pipe(
      mergeMap((data) => {
        return this.proyectoService.setEntidadConvocantePrograma(this.getKey() as number,
          data.id, data.programa).pipe(
            map((updatedEntidad) => {
              this.edited.delete(updatedEntidad.id);
              let current: IProyectoEntidadConvocante[] = this.proyectoEntidadConvocantes$.value;
              current = current.map((proyectoEntidadConvocante) =>
                proyectoEntidadConvocante.id === updatedEntidad.id ? updatedEntidad : proyectoEntidadConvocante);
              this.proyectoEntidadConvocantes$.next(current);
            })
          );
      })
    );
  }

  private createProyectoEntidadConvocantes(): Observable<void> {
    const createdEntidades = this.proyectoEntidadConvocantes$.value.filter((value) => !value.id);
    if (createdEntidades.length === 0) {
      return of(void 0);
    }
    return from(createdEntidades).pipe(
      mergeMap((data) => {
        return this.proyectoService.createEntidadConvocante(this.getKey() as number, data).pipe(
          map((createdEntidad) => {
            let current: IProyectoEntidadConvocante[] = this.proyectoEntidadConvocantes$.value;
            current = current.map((proyectoEntidadConvocante) =>
              proyectoEntidadConvocante === data ? createdEntidad : proyectoEntidadConvocante);
            this.proyectoEntidadConvocantes$.next(current);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const created: boolean = this.proyectoEntidadConvocantes$.value.some((proyectoEntidadConvocate) => !proyectoEntidadConvocate.id);
    return (this.deleted.size > 0 || this.edited.size > 0 || created);
  }
}
