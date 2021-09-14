import { Component, Input } from '@angular/core';

@Component({
  selector: 'sgi-action-fragment-link-item',
  templateUrl: './action-fragment-link-item.component.html',
  styleUrls: ['./action-fragment-link-item.component.scss'],
})
export class ActionFragmentLinkItemComponent {
  @Input() title: string;
  @Input() route: string | string[];
  @Input() queryParams: any;
}
