import { Component, Input } from '@angular/core';
import { ActionService } from '@core/services/action-service';

@Component({
  selector: 'sgi-action',
  templateUrl: './action.component.html',
  styleUrls: ['./action.component.scss']
})
export class ActionComponent {

  @Input()
  actionService: ActionService;

}
