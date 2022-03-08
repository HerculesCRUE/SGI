import { Component, Input } from '@angular/core';

@Component({
  selector: 'sgi-action-footer-message',
  templateUrl: './action-footer-message.component.html',
  styleUrls: ['./action-footer-message.component.scss']
})
export class ActionFooterMessageComponent {

  @Input() typeMessage: string;

  constructor() { }


}
