import { Directive, Input, TemplateRef, ViewContainerRef, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { SgiAuthService } from '../auth.service';

/**
 * Structural directive to hide elements if the user NOT have at lest one authority to any of the provided modules.
 */
@Directive({
  selector: '[sgiHasAnyModuleAccess]'
})
export class HasAnyModuleAccessDirective implements OnInit, OnDestroy {

  private rendered = false;
  @Input('sgiHasAnyModuleAccess')
  private moduleNames: string[];
  private subscription: Subscription;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authService: SgiAuthService
  ) { }

  ngOnInit(): void {
    if (!this.moduleNames || this.moduleNames instanceof Array === false || this.moduleNames.length === 0) {
      throw Error('Must provide an array of module names');
    }
    this.moduleNames.forEach((moduleName) => {
      if (!moduleName || typeof moduleName !== 'string' || moduleName.trim() === '') {
        throw Error('Must provide an module name');
      }
    });
    this.subscription = this.authService.authStatus$.subscribe((status) => {
      this.updateView();
    });
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  private updateView(): void {
    if (this.checkAuthority() && !this.rendered) {
      this.viewContainer.createEmbeddedView(this.templateRef);
      this.rendered = true;
    }
    else if (!this.checkAuthority() && this.rendered) {
      this.viewContainer.clear();
      this.rendered = false;
    }
  }

  private checkAuthority(): boolean {
    return this.authService.hasAnyModuleAccess(this.moduleNames);
  }

}
