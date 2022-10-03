import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { Module } from '@core/module';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { MemoriaCrearComponent } from './memoria-crear/memoria-crear.component';
import { MemoriaCrearGuard } from './memoria-crear/memoria-crear.guard';
import { MemoriaEditarComponent } from './memoria-editar/memoria-editar.component';
import { MemoriaDatosGeneralesComponent } from './memoria-formulario/memoria-datos-generales/memoria-datos-generales.component';
import { MemoriaDocumentacionComponent } from './memoria-formulario/memoria-documentacion/memoria-documentacion.component';
import { MemoriaEvaluacionesComponent } from './memoria-formulario/memoria-evaluaciones/memoria-evaluaciones.component';
import { MemoriaFormularioComponent } from './memoria-formulario/memoria-formulario/memoria-formulario.component';
import { MemoriaInformesComponent } from './memoria-formulario/memoria-informes/memoria-informes.component';
import { MemoriaRetrospectivaComponent } from './memoria-formulario/memoria-retrospectiva/memoria-retrospectiva.component';
import { MemoriaSeguimientoAnualComponent } from './memoria-formulario/memoria-seguimiento-anual/memoria-seguimiento-anual.component';
import { MemoriaSeguimientoFinalComponent } from './memoria-formulario/memoria-seguimiento-final/memoria-seguimiento-final.component';
import { MemoriaListadoInvComponent } from './memoria-listado-inv/memoria-listado-inv.component';
import { MEMORIA_ROUTE_NAMES } from './memoria-route-names';
import { MemoriaResolver } from './memoria.resolver';

const MSG_NEW_TITLE = marker('title.new.entity');
const MEMORIA_KEY = marker('eti.memoria');

const routes: SgiRoutes = [
  {
    path: '',
    component: MemoriaListadoInvComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MEMORIA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAuthorityForAnyUO: 'ETI-MEM-INV-VR',
      module: Module.INV
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: MemoriaCrearComponent,
    canActivate: [SgiAuthGuard, MemoriaCrearGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: MEMORIA_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthorityForAnyUO: 'ETI-MEM-INV-CR',
      module: Module.INV
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: MEMORIA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: MEMORIA_ROUTE_NAMES.DATOS_GENERALES,
        component: MemoriaDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:id`,
    component: MemoriaEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      memoria: MemoriaResolver
    },
    data: {
      title: MEMORIA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAuthorityForAnyUO: 'ETI-MEM-INV-ER',
      readonly: false,
      module: Module.INV
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: MEMORIA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: MEMORIA_ROUTE_NAMES.DATOS_GENERALES,
        component: MemoriaDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MEMORIA_ROUTE_NAMES.FORMULARIO,
        component: MemoriaFormularioComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MEMORIA_ROUTE_NAMES.DOCUMENTACION,
        component: MemoriaDocumentacionComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MEMORIA_ROUTE_NAMES.SEGUIMIENTO_ANUAL,
        component: MemoriaSeguimientoAnualComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MEMORIA_ROUTE_NAMES.SEGUIMIENTO_FINAL,
        component: MemoriaSeguimientoFinalComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MEMORIA_ROUTE_NAMES.RETROSPECTIVA,
        component: MemoriaRetrospectivaComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MEMORIA_ROUTE_NAMES.EVALUACIONES,
        component: MemoriaEvaluacionesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: MEMORIA_ROUTE_NAMES.VERSIONES,
        component: MemoriaInformesComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MemoriaRoutingInvModule {
}
