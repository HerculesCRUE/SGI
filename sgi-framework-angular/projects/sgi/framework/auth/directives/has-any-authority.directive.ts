import { Directive, Input, TemplateRef, ViewContainerRef, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { SgiAuthService } from '../auth.service';

/**
 * Structural directive to hide elements if the user NOT have at lest one of the established authorities.
 *
 * The authorities can contains UO (Unidad Organizativa) prefix, and it will be checked as provided.
 *
 * To check authorities ignoring UO use {@link HasAnyAuthorityForAnyUO}
 */
@Directive({
  selector: '[sgiHasAnyAuthority]'
})
export class HasAnyAuthorityDirective implements OnInit, OnDestroy {

  private rendered = false;
  @Input('sgiHasAnyAuthority')
  private authorities: string[];
  private subscription: Subscription;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authService: SgiAuthService
  ) { }

  ngOnInit(): void {
    if (!this.authorities || this.authorities instanceof Array === false || this.authorities.length === 0) {
      throw Error('Must provide an array of authorities');
    }
    this.authorities.forEach((authority) => {
      if (!authority || typeof authority !== 'string' || authority.trim() === '') {
        throw Error('Must provide an authority');
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
    return this.authService.hasAnyAuthority(this.authorities);
  }

}
