import { Component, OnInit } from '@angular/core';
import { Module } from '@core/module';
import { SgiAuthService } from '@sgi/framework/auth';
import { Router } from '@angular/router';

@Component({
  selector: 'sgi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  modules: Module[] = Module.values;

  constructor(private authService: SgiAuthService, private router: Router) { }

  ngOnInit(): void {
    const userModules = this.authService.authStatus$.value.modules;
    if (userModules.length === 1) {
      const module = Module.fromCode(userModules[0]);
      this.router.navigate([module.path]);
    }
  }

}
