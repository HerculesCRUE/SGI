import { Component, OnDestroy } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { LayoutService } from '@core/services/layout.service';
import { Subscription } from 'rxjs';
import { PII_ROUTE_NAMES } from '../pii-route-names';

@Component({
  selector: 'sgi-pii-menu-principal',
  templateUrl: './pii-menu-principal.component.html',
  styleUrls: ['./pii-menu-principal.component.scss']
})
export class PiiMenuPrincipalComponent implements OnDestroy {

  opened: boolean;
  panelDesplegado: boolean;

  private subcription: Subscription;

  get PII_ROUTE_NAMES() {
    return PII_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

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

}
