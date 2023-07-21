import { Component, OnDestroy } from '@angular/core';
import { Module } from '@core/module';
import { ConfigPublicService } from '@core/services/cnf/config-public.service';
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
  numLogosCabecera: number;
  private subscriptions: Subscription[] = [];

  constructor(
    private readonly layout: LayoutService,
    private readonly resourceService: ResourcePublicService,
    private configService: ConfigPublicService
  ) {
    this.anchoPantalla = window.innerWidth;
    this.subscriptions.push(this.layout.activeModule$.subscribe((res) => this.module = res));
    this.subscriptions.push(this.configService.getNumeroLogosCabecera().subscribe((num) => this.numLogosCabecera = Number(num)));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(x => x.unsubscribe());
  }

  getUrlResource(id: string): string {
    return this.resourceService.getUrlResource(id);
  }

  getUrlSetResource(id: string, versiones: string[]): string {
    return versiones.map(version => `${this.getUrlResource(version ? id + version : id)} ${version}`).join(', ');
  }

}
