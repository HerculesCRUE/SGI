import { Component } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { ETI_ROUTE_NAMES } from '../eti-route-names';

@Component({
  selector: 'sgi-eti-root',
  templateUrl: './eti-root.component.html',
  styleUrls: ['./eti-root.component.scss']
})
export class EtiRootComponent {

  get ETI_ROUTE_NAMES() {
    return ETI_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
