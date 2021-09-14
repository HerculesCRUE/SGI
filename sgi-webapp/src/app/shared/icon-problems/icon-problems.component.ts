import { Component, Input } from '@angular/core';

@Component({
  selector: 'sgi-icon-problems',
  templateUrl: './icon-problems.component.html',
  styleUrls: ['./icon-problems.component.scss']
})
export class IconProblemsComponent {
  @Input() tooltip: string;

  constructor() { }


}
