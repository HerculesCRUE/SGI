import { Component, Input } from '@angular/core';
import { SgiProblem } from '@core/errors/sgi-error';

@Component({
  selector: 'sgi-problem-panel',
  templateUrl: './problem-panel.component.html',
  styleUrls: ['./problem-panel.component.scss']
})
export class ProblemPanelComponent {

  @Input()
  problems: SgiProblem[];

  constructor() { }

}
