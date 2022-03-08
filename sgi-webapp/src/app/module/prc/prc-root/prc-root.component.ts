import { Component } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { PRC_ROUTE_NAMES } from '../prc-route-names';

@Component({
  selector: 'sgi-prc-root',
  templateUrl: './prc-root.component.html',
  styleUrls: ['./prc-root.component.scss']
})
export class PrcRootComponent {

  get PRC_ROUTE_NAMES() {
    return PRC_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
