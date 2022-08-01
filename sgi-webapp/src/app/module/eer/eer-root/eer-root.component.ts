import { Component } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { EER_ROUTE_NAMES } from '../eer-route-names';

@Component({
  selector: 'sgi-eer-root',
  templateUrl: './eer-root.component.html',
  styleUrls: ['./eer-root.component.scss']
})
export class EerRootComponent {

  get EER_ROUTE_NAMES() {
    return EER_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
