import { GlobalPositionStrategy, Overlay, OverlayConfig, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { Injectable } from '@angular/core';
import { SpinnerComponent } from '@block/spinner/spinner.component';

@Injectable({
  providedIn: 'root'
})
export class SpinnerService {
  private overlayRef: OverlayRef = null;
  private shows = 0;
  private readonly overlayConfing: OverlayConfig;

  constructor(private overlay: Overlay) {
    const positionStrategy = new GlobalPositionStrategy();
    this.overlayConfing = new OverlayConfig({
      hasBackdrop: true,
      positionStrategy,
    });
    positionStrategy.centerHorizontally();
    positionStrategy.centerVertically();
  }

  public show() {
    if (!this.isShowing()) {
      Promise.resolve().then(() => {
        if (!this.overlayRef) {
          this.overlayRef = this.overlay.create(this.overlayConfing);
        }
        const spinner = new ComponentPortal(SpinnerComponent);
        this.overlayRef.attach(spinner);
      });
    }
    this.shows++;
  }

  private isShowing(): boolean {
    return this.shows > 0;
  }

  public hide() {
    this.shows--;
    if (!!this.overlayRef && !this.isShowing()) {
      this.overlayRef.detach();
    }
  }
}
