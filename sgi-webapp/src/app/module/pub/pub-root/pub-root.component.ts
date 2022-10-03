import { Component } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { PUB_ROUTE_NAMES } from '../pub-route-names';

@Component({
  selector: 'sgi-pub-root',
  templateUrl: './pub-root.component.html',
  styleUrls: ['./pub-root.component.scss']
})
export class PubRootComponent {

  get PUB_ROUTE_NAMES() {
    return PUB_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
