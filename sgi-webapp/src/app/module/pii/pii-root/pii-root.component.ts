import { Component } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { PII_ROUTE_NAMES } from '../pii-route-names';

@Component({
  selector: 'sgi-pii-root',
  templateUrl: './pii-root.component.html',
  styleUrls: ['./pii-root.component.scss']
})
export class PiiRootComponent {

  get PII_ROUTE_NAMES() {
    return PII_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
