import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[sgiBaremoHost]'
})
export class BaremoDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}
