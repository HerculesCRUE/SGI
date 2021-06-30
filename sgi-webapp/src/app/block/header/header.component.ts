import { Component, OnDestroy } from '@angular/core';
import { Module } from '@core/module';
import { LayoutService } from '@core/services/layout.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnDestroy {
  Module = Module;
  anchoPantalla: number;

  module: Module;
  private subscription: Subscription;

  constructor(
    private readonly layout: LayoutService
  ) {
    this.anchoPantalla = window.innerWidth;
    this.subscription = this.layout.activeModule$.subscribe((res) => this.module = res);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
