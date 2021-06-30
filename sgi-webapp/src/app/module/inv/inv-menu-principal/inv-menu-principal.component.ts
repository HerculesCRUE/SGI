import { Component, OnDestroy } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { LayoutService } from '@core/services/layout.service';
import { Subscription } from 'rxjs';
import { INV_ROUTE_NAMES } from '../inv-route-names';

@Component({
  selector: 'sgi-inv-menu-principal',
  templateUrl: './inv-menu-principal.component.html',
  styleUrls: ['./inv-menu-principal.component.scss']
})
export class InvMenuPrincipalComponent implements OnDestroy {

  INV_ROUTE_NAMES = INV_ROUTE_NAMES;

  opened: boolean;
  panelDesplegado: boolean;

  showEvaluaciones: boolean = false;
  showSeguimientos: boolean = false;

  private subscriptions = [] as Subscription[];

  constructor(
    private layout: LayoutService, private evaluadorService: EvaluadorService) {
    this.subscriptions.push(this.layout.menuOpened$.subscribe((val) => this.opened = val));
    this.subscriptions.push(this.evaluadorService.hasAssignedEvaluaciones().subscribe((res) => this.showEvaluaciones = res));
    this.subscriptions.push(this.evaluadorService.hasAssignedEvaluacionesSeguimiento().subscribe((res) => this.showSeguimientos = res));
  }

  /**
   * Activamos el acordeon del elemento para poder hacerle
   * un stopProgation para que el servicio al encoger menu lo cierre
   * @param $event evento lanzado
   */
  activarAcordeon($event): void {
    if (this.opened) {
      this.panelDesplegado = this.panelDesplegado;
    } else {
      this.panelDesplegado = !this.panelDesplegado;
    }
    $event.stopPropagation();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
