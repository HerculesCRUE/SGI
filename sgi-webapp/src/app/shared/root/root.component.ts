import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component } from '@angular/core';
import { LayoutService } from '@core/services/layout.service';

const OPEN_WITH = 225;
const CLOSE_WITH = 45;

@Component({
  selector: 'sgi-root',
  templateUrl: './root.component.html',
  styleUrls: ['./root.component.scss'],
  animations: [
    trigger('menu-animation', [
      state('close',
        style({
          'min-width': CLOSE_WITH + 'px'
        })
      ),
      state('open',
        style({
          'min-width': OPEN_WITH + 'px'
        })
      ),
      transition('close => open', animate('250ms ease-in')),
      transition('open => close', animate('250ms ease-in')),
    ]),
    trigger('content-animation', [
      state('close',
        style({
          'margin-left': CLOSE_WITH + 'px'
        })
      ),
      state('open',
        style({
          'margin-left': OPEN_WITH + 'px'
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
  openedWidth = OPEN_WITH;
  closedWith = CLOSE_WITH;

  constructor(public layoutService: LayoutService) {
    this.screenWidth = window.innerWidth;
  }

}
