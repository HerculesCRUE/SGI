import { IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { Fragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

export class ProyectoHistoricoEstadosFragment extends Fragment {

  historicoEstado$ = new BehaviorSubject<IEstadoProyecto[]>([]);

  constructor(
    key: number,
    private proyectoService: ProyectoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.subscriptions.push(this.proyectoService.findEstadoProyecto(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((historicoEstados) => {
        this.historicoEstado$.next(historicoEstados);
      }));
    }
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }

}
