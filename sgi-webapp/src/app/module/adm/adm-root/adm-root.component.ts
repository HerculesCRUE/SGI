import { Component } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { ADM_ROUTE_NAMES } from '../adm-route-names';

@Component({
  selector: 'sgi-adm-root',
  templateUrl: './adm-root.component.html',
  styleUrls: ['./adm-root.component.scss']
})
export class AdmRootComponent {

  get ADM_ROUTE_NAMES() {
    return ADM_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
