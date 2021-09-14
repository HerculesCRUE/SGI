import { Component, Input } from '@angular/core';

@Component({
  selector: 'sgi-icon-errors',
  templateUrl: './icon-errors.component.html',
  styleUrls: ['./icon-errors.component.scss']
})
export class IconErrorsComponent {
  @Input() tooltip: string;

  constructor() { }


}
