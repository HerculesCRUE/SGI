import { ComponentFactoryResolver, Directive, ElementRef, ViewContainerRef } from "@angular/core";
import { marker } from "@biesbjerg/ngx-translate-extract-marker";
import { TranslateService } from "@ngx-translate/core";
import { NotFoundErrorDirective } from "@shared/directives/not-found-error.directive";

const SGEMP_NOT_FOUND = marker("error.sgemp.not-found");

@Directive({
  selector: '[sgempNotFoundError]'
})
export class SgempNotFoundErrorDirective extends NotFoundErrorDirective {

  constructor(
    protected element: ElementRef,
    protected viewContainerRef: ViewContainerRef,
    protected componentFactoryResolver: ComponentFactoryResolver,
    protected translate: TranslateService
  ) {
    super(element, viewContainerRef, componentFactoryResolver, translate);
    this.defaultMessage = SGEMP_NOT_FOUND;
  }

} 
