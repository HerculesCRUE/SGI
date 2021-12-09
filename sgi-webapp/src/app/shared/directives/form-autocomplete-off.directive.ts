import { Directive, ElementRef, Renderer2 } from '@angular/core';

@Directive({
  // tslint:disable-next-line:directive-selector
  selector: 'form:not([autocomplete])'
})
export class FormAutocompleteOffDirective {

  constructor(element: ElementRef, renderer: Renderer2) {
    renderer.setAttribute(element.nativeElement, 'autocomplete', 'off');
  }
}
