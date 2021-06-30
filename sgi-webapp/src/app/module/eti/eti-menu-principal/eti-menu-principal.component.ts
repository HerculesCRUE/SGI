import { Component, OnDestroy } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { LayoutService } from '@core/services/layout.service';
import { Subscription } from 'rxjs';
import { ETI_ROUTE_NAMES } from '../eti-route-names';

@Component({
  selector: 'sgi-eti-menu-principal',
  templateUrl: './eti-menu-principal.component.html',
  styleUrls: ['./eti-menu-principal.component.scss']
})
export class EtiMenuPrincipalComponent implements OnDestroy {
  ETI_ROUTE_NAMES = ETI_ROUTE_NAMES;

  opened: boolean;

  private subcription: Subscription;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(private layout: LayoutService) {
    this.subcription = this.layout.menuOpened$.subscribe((val) => this.opened = val);
  }

  ngOnDestroy(): void {
    this.subcription.unsubscribe();
  }

}
