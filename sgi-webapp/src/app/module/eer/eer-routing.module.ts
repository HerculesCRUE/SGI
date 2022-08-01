import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { EER_ROUTE_NAMES } from './eer-route-names';
import { EerInicioComponent } from './eer-inicio/eer-inicio.component';
import { EerRootComponent } from './eer-root/eer-root.component';

const MSG_ROOT_TITLE = marker('eer.root.title');
const MSG_EMPRESA_EXPLOTACION_RESULTADOS_TITLE = marker('eer.empresa-explotacion-resultados');

const routes: SgiRoutes = [
  {
    path: '',
    component: EerRootComponent,
    data: {
      title: MSG_ROOT_TITLE
    },
    children: [
      {
        path: '',
        component: EerInicioComponent,
        pathMatch: 'full',
        data: {
          title: MSG_ROOT_TITLE,
          hasAnyAuthority: ['EER-EER-V', 'EER-EER-C', 'EER-EER-E', 'EER-EER-B']
        }
      },
      {
        path: EER_ROUTE_NAMES.EMPRESAS_EXPLOTACION_RESULTADOS,
        loadChildren: () =>
          import('./empresa-explotacion-resultados/empresa-explotacion-resultados.module').then(
            (m) => m.EmpresaExplotacionResultadosModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_EMPRESA_EXPLOTACION_RESULTADOS_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['EER-EER-V', 'EER-EER-C', 'EER-EER-E', 'EER-EER-B']
        }
      },
      { path: '**', component: null }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EerRoutingModule { }
