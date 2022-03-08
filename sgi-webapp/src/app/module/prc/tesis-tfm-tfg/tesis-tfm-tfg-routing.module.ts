import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SgiRoutes } from '@core/route';

const routes: SgiRoutes = [
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TesisTfmTfgRoutingModule {
}
