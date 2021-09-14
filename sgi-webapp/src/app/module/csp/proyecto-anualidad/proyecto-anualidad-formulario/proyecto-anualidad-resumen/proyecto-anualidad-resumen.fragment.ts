import { TipoPartida } from '@core/enums/tipo-partida';
import { IAnualidadResumen } from '@core/models/csp/anualidad-resumen';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { Fragment } from '@core/services/action-service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { BehaviorSubject, Observable, of } from 'rxjs';

export interface IAnualidad {
  tipoPartida: TipoPartida;
  proyectoPartida: IProyectoPartida;
  importePresupuesto: number;
  importeConcedido: number;
}

export class ProyectoAnualidadResumenFragment extends Fragment {

  anualidades$ = new BehaviorSubject<IAnualidadResumen[]>([]);

  constructor(
    key: number,
    private proyectoAnualidadService: ProyectoAnualidadService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.subscriptions.push(this.proyectoAnualidadService.getAnualidadesResumen(this.getKey() as number)
        .subscribe((anualidadesResumen) => this.anualidades$.next(anualidadesResumen.items)));
    }
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }

}
