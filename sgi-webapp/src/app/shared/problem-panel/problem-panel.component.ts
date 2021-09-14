import { Component, Input } from '@angular/core';
import { Problem } from '@core/errors/http-problem';

@Component({
  selector: 'sgi-problem-panel',
  templateUrl: './problem-panel.component.html',
  styleUrls: ['./problem-panel.component.scss']
})
export class ProblemPanelComponent {

  @Input()
  problems: Problem[];

  constructor() { }

}
