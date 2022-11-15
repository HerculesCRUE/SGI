import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { Fragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoEntidadesConvocantesFragment extends Fragment {
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
    return this.updateEntidadesConvocantes()
      .pipe(
        map(updatedEntidadesConvocante => {
          this.proyectoEntidadConvocantes$.next(
            this.proyectoEntidadConvocantes$.value.map(entidadConvocante => {
              if (!!!entidadConvocante.id) {
                entidadConvocante.id = updatedEntidadesConvocante.find(e =>
                  e.entidad?.id === entidadConvocante.entidad?.id
                  && e.programa?.id === entidadConvocante.programa?.id
                  && e.programaConvocatoria?.id === entidadConvocante.programaConvocatoria?.id).id;
              }
              return entidadConvocante;
            })
          );
        }),
        tap(() => this.setChanges(false)),
      );
  }

  private updateEntidadesConvocantes(): Observable<IProyectoEntidadConvocante[]> {
    return this.proyectoService.updateEntidadesConvocantes(
      this.getKey() as number,
      this.proyectoEntidadConvocantes$.value
    );
  }

}
