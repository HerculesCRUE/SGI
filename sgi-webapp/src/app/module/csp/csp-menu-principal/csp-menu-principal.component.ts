import { Component, OnDestroy } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { LayoutService } from '@core/services/layout.service';
import { Subscription } from 'rxjs';
import { CSP_ROUTE_NAMES } from '../csp-route-names';
@Component({
  selector: 'sgi-csp-menu-principal',
  templateUrl: './csp-menu-principal.component.html',
  styleUrls: ['./csp-menu-principal.component.scss']
})
export class CspMenuPrincipalComponent implements OnDestroy {
  CSP_ROUTE_NAMES = CSP_ROUTE_NAMES;

  opened: boolean;
  panelDesplegado: boolean;

  private subcription: Subscription;

  constructor(
    public layout: LayoutService) {
    this.subcription = this.layout.menuOpened$.subscribe((val) => this.opened = val);
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
    this.subcription.unsubscribe();
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
