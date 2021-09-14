import { Component, ContentChild, Directive } from '@angular/core';

@Directive({
  // tslint:disable-next-line: directive-selector
  selector: 'sgi-menu-content-footer'
})
// tslint:disable-next-line: directive-class-suffix
export class MenuContentFooter { }

@Component({
  selector: 'sgi-menu-content',
  templateUrl: './menu-content.component.html',
  styleUrls: ['./menu-content.component.scss']
})
export class MenuContentComponent {
  @ContentChild(MenuContentFooter) content!: MenuContentFooter;
}
