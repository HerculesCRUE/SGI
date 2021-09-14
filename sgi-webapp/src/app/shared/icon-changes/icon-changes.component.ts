import { Component, Input } from '@angular/core';

@Component({
  selector: 'sgi-icon-changes',
  templateUrl: './icon-changes.component.html',
  styleUrls: ['./icon-changes.component.scss']
})
export class IconChangesComponent {
  @Input() tooltip: string;

  constructor() { }


}
