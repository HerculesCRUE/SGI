import { Component } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { CSP_ROUTE_NAMES } from '../csp-route-names';

@Component({
  selector: 'sgi-csp-root',
  templateUrl: './csp-root.component.html',
  styleUrls: ['./csp-root.component.scss']
})
export class CspRootComponent {

  get CSP_ROUTE_NAMES() {
    return CSP_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }
}
