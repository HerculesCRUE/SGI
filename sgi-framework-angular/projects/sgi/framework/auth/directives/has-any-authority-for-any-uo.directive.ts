import { Directive, Input, TemplateRef, ViewContainerRef, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { SgiAuthService } from '../auth.service';

/**
 * Structural directive to hide elements if the user NOT have at lest one of the established authorities.
 *
 * The authorities must not contain an UO (Unidad Organizativa) prefix, because the validation ignore it.
 *
 * To check authorities against UO use {@link HasAnyAuthority}
 */
@Directive({
  selector: '[sgiHasAnyAuthorityForAnyUO]'
})
export class HasAnyAuthorityForAnyUODirective implements OnInit, OnDestroy {

  private rendered = false;
  @Input('sgiHasAnyAuthorityForAnyUO')
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
      if (authority.indexOf('_') >= 0) {
        throw Error('Authority cannot contain an underscore');
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
    return this.authService.hasAnyAuthorityForAnyUO(this.authorities);
  }

}
