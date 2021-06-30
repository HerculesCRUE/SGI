import { Directive, Input, TemplateRef, ViewContainerRef, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { SgiAuthService } from '../auth.service';

/**
 * Structural directive to hide elements if the user NOT have the established authority.
 *
 * The authority can contains UO (Unidad Organizativa) prefix, and it will be checked as provided.
 *
 * To check authority ignoring UO use {@link HasAuthorityForAnyUO}
 */
@Directive({
  selector: '[sgiHasAuthority]'
})
export class HasAuthorityDirective implements OnInit, OnDestroy {

  private rendered = false;
  @Input('sgiHasAuthority')
  private authority: string;
  private subscription: Subscription;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authService: SgiAuthService
  ) { }

  ngOnInit(): void {
    if (!this.authority || typeof this.authority !== 'string' || this.authority.trim() === '') {
      throw Error('Must provide an authority');
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
    return this.authService.hasAuthority(this.authority);
  }

}
