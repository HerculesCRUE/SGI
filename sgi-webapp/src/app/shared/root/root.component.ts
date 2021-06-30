import { Component } from '@angular/core';
import { LayoutService } from '@core/services/layout.service';

import { trigger, state, style, transition, animate } from '@angular/animations';

const OPEN_WITH_EM = 16;
const CLOSE_WITH_EM = 5;

@Component({
  selector: 'sgi-root',
  templateUrl: './root.component.html',
  styleUrls: ['./root.component.scss'],
  animations: [
    trigger('menu-animation', [
      state('close',
        style({
          'min-width': CLOSE_WITH_EM + 'em'
        })
      ),
      state('open',
        style({
          'min-width': OPEN_WITH_EM + 'em'
        })
      ),
      transition('close => open', animate('250ms ease-in')),
      transition('open => close', animate('250ms ease-in')),
    ]),
    trigger('content-animation', [
      state('close',
        style({
          'margin-left': CLOSE_WITH_EM + 'em'
        })
      ),
      state('open',
        style({
          'margin-left': OPEN_WITH_EM + 'em'
        })
      ),
      transition('close => open', animate('250ms ease-in')),
      transition('open => close', animate('250ms ease-in')),
    ])
  ]
})
export class RootComponent {
  // width pantalla resoluciones pequenas
  screenWidth: number;
  openedWidth = OPEN_WITH_EM;
  closedWith = CLOSE_WITH_EM;

  constructor(public layoutService: LayoutService) {
    this.screenWidth = window.innerWidth;
  }


}
