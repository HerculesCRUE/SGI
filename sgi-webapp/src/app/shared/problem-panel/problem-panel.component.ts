import { Component, Input } from '@angular/core';
import { ProblemComponent } from '@core/component/problem.component';
import { SgiProblem } from '@core/errors/sgi-error';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'sgi-problem-panel',
  templateUrl: './problem-panel.component.html',
  styleUrls: ['./problem-panel.component.scss']
})
export class ProblemPanelComponent extends ProblemComponent {

  @Input()
  problems: SgiProblem[];

  constructor(
    readonly translate: TranslateService
  ) {
    super(translate);
  }


}
