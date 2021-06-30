import { Directive, Input, TemplateRef, ViewContainerRef, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { SgiAuthService } from '../auth.service';

/**
 * Structural directive to hide elements if the user NOT have at lest one authority to an module.
 */
@Directive({
  selector: '[sgiHasModuleAccess]'
})
export class HasModuleAccessDirective implements OnInit, OnDestroy {

  private rendered = false;
  @Input('sgiHasModuleAccess')
  private moduleName: string;
  private subscription: Subscription;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authService: SgiAuthService
  ) { }

  ngOnInit(): void {
    if (!this.moduleName || typeof this.moduleName !== 'string' || this.moduleName.trim() === '') {
      throw Error('Must provide a module name');
    }
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
    return this.authService.hasModuleAccess(this.moduleName);
  }

}
