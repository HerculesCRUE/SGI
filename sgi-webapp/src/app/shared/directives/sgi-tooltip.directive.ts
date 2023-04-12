import { AriaDescriber, FocusMonitor } from '@angular/cdk/a11y';
import { Directionality } from '@angular/cdk/bidi';
import { Overlay, ScrollDispatcher } from '@angular/cdk/overlay';
import { Platform } from '@angular/cdk/platform';
import { Directive, ElementRef, Inject, Input, NgZone, OnDestroy, OnInit, Optional, ViewContainerRef } from '@angular/core';
import {
  MatTooltip,
  MatTooltipDefaultOptions, MAT_TOOLTIP_DEFAULT_OPTIONS,
  MAT_TOOLTIP_SCROLL_STRATEGY
} from '@angular/material/tooltip';
import { fromEvent, Subscription } from 'rxjs';

@Directive({
  selector: '[sgiTooltip]',
})
export class SgiTooltipDirective extends MatTooltip implements OnInit, OnDestroy {

  private elementRef: ElementRef;
  private resizeSubscription$: Subscription;
  private _tooltipMessage: string;

  @Input()
  get sgiTooltip() {
    return this.message;
  }
  set sgiTooltip(value: string) {
    this._tooltipMessage = value;
    this.message = null;

    Promise.resolve().then(() => {
      if (this.checkShowTooltip(this.elementRef)) {
        this.message = this._tooltipMessage;
      }
    });
  }

  constructor(
    _overlay: Overlay,
    _elementRef: ElementRef,
    _scrollDispatcher: ScrollDispatcher,
    _viewContainerRef: ViewContainerRef,
    _ngZone: NgZone,
    _platform: Platform,
    _ariaDescriber: AriaDescriber,
    _focusMonitor: FocusMonitor,
    @Inject(MAT_TOOLTIP_SCROLL_STRATEGY) _scrollStrategy: any,
    @Optional() _dir: Directionality,
    @Optional() @Inject(MAT_TOOLTIP_DEFAULT_OPTIONS)
    _defaultOptions: MatTooltipDefaultOptions) {
    super(
      _overlay,
      _elementRef,
      _scrollDispatcher,
      _viewContainerRef,
      _ngZone,
      _platform,
      _ariaDescriber,
      _focusMonitor,
      _scrollStrategy,
      _dir,
      _defaultOptions,
      null
    );
    this.elementRef = _elementRef;
    this.showDelay = 200;
  }

  ngOnInit() {
    this.resizeSubscription$ = fromEvent(window, 'resize').subscribe(() => {
      if (this.checkShowTooltip(this.elementRef)) {
        this.message = this._tooltipMessage;
      } else {
        this.message = null;
      }
    })
  }

  ngOnDestroy() {
    this.resizeSubscription$?.unsubscribe();
  }

  private checkShowTooltip(e: ElementRef): boolean {
    return e.nativeElement.offsetWidth < e.nativeElement.scrollWidth
      || this.checkChildsOverflow(e.nativeElement?.childNodes, e.nativeElement.offsetWidth);
  }

  private checkChildsOverflow(childs: any, maxWidth: number): boolean {
    return [...childs]?.some(child => !!child.offsetWidth && (child.offsetWidth > maxWidth || this.checkChildsOverflow(child.childNodes, maxWidth)));
  }

}
