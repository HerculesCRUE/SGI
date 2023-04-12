import { Component, OnDestroy } from '@angular/core';
import { Module } from '@core/module';
import { ResourcePublicService } from '@core/services/cnf/resource-public.service';
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
    private readonly layout: LayoutService,
    private readonly resourceService: ResourcePublicService
  ) {
    this.anchoPantalla = window.innerWidth;
    this.subscription = this.layout.activeModule$.subscribe((res) => this.module = res);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  getUrlResource(id: string): string {
    return this.resourceService.getUrlResource(id);
  }

  getUrlSetResource(id: string, versiones: string[]): string {
    return versiones.map(version => `${this.getUrlResource(version ? id + version : id)} ${version}`).join(', ');
  }

}
