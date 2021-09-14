import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { Fragment } from '@core/services/action-service';
import { BehaviorSubject, Observable, of } from 'rxjs';

export class ProyectosFragment extends Fragment {
  proyectosSge$ = new BehaviorSubject<IProyectoProyectoSge[]>([]);

  constructor(
    key: number,
    private proyectoSge: IProyectoSge,
    private proyectosRelacionados: IProyecto[],
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    const proyectoProyectoSge = this.proyectosRelacionados.map(proyecto => {
      return {
        proyecto,
        proyectoSge: this.proyectoSge
      } as IProyectoProyectoSge;
    });
    this.proyectosSge$.next(proyectoProyectoSge);
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }

}
