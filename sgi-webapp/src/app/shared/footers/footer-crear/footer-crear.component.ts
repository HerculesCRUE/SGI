import { Component, Input } from '@angular/core';

@Component({
  selector: 'sgi-footer-crear',
  templateUrl: './footer-crear.component.html',
  styleUrls: ['./footer-crear.component.scss']
})
export class FooterCrearComponent {
  @Input() route: string | any[];
  @Input() texto: string;

  constructor() {
  }

}
