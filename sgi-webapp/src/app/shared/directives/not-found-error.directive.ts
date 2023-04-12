import { ComponentFactoryResolver, Directive, ElementRef, Input, OnInit, ViewContainerRef } from "@angular/core";
import { marker } from "@biesbjerg/ngx-translate-extract-marker";
import { MSG_PARAMS } from "@core/i18n";
import { TranslateService } from "@ngx-translate/core";
import { NotFoundErrorComponent } from "@shared/not-found-error/not-found-error.component";

interface ModelWithId {
  id: string | number;
}

const NOT_FOUND = marker("error.not-found");

@Directive({
  selector: '[notFoundError]'
})
export class NotFoundErrorDirective implements OnInit {

  @Input() model: ModelWithId | ModelWithId[];
  @Input() message: string;

  protected defaultMessage: string;

  constructor(
    protected element: ElementRef,
    protected viewContainerRef: ViewContainerRef,
    protected componentFactoryResolver: ComponentFactoryResolver,
    protected translate: TranslateService
  ) {
    this.defaultMessage = NOT_FOUND;
  }

  ngOnInit() {
    const idsNotFound = this.getIdsElementsNotFound(this.model);
    if (idsNotFound.length === 0) {
      return;
    }

    if (!this.message) {
      this.getDefaultMessage(idsNotFound);
    }

    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(NotFoundErrorComponent);

    const componentRef = this.viewContainerRef.createComponent(componentFactory);
    componentRef.instance.message = this.message;

    const host = this.element.nativeElement;
    host.insertBefore(componentRef.location.nativeElement, host.firstChild)
  }

  protected getDefaultMessage(idsNotFound: (number | string)[]): void {
    this.translate.get(
      this.defaultMessage,
      {
        ids: idsNotFound,
        ...(idsNotFound.length > 1 ? MSG_PARAMS.CARDINALIRY.PLURAL : MSG_PARAMS.CARDINALIRY.SINGULAR)
      }
    ).subscribe(value => this.message = value);
  }

  private getIdsElementsNotFound(model: ModelWithId | ModelWithId[]): (number | string)[] {
    if (Array.isArray(model)) {
      return model.filter(m => this.hasOnlyId(m)).map(m => m.id);
    } else {
      return this.hasOnlyId(model) ? [model.id] : [];
    }
  }

  private hasOnlyId(model: ModelWithId): boolean {
    return !!model && Object.keys(model).length == 1 && !!model.id;
  }

} 
