import { Directive, OnInit, OnDestroy, TemplateRef, ViewContainerRef } from '@angular/core';
import { Subscription } from 'rxjs';
import { SgiAuthService } from '../auth.service';

/**
 * Structural directive to hide elements if the user isn't authenticated
 */
@Directive({
  selector: '[sgiIfAuthenticated]'
})
export class IfAuthenticatedDirective implements OnInit, OnDestroy {

  private rendered = false;
  private subscription: Subscription;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authService: SgiAuthService
  ) { }

  ngOnInit(): void {
    this.subscription = this.authService.authStatus$.subscribe((status) => {
      this.updateView(status.isAuthenticated);
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private updateView(authenticated: boolean): void {
    if (authenticated && !this.rendered) {
      this.viewContainer.createEmbeddedView(this.templateRef);
      this.rendered = true;
    }
    else if (!authenticated && this.rendered) {
      this.viewContainer.clear();
      this.rendered = false;
    }
  }
}
