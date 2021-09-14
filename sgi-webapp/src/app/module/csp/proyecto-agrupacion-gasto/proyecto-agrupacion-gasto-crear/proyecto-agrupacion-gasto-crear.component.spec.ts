import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PROYECTO_AGRUPACION_GASTO_DATA_KEY } from '../proyecto-agrupacion-gasto-data.resolver';
import { IProyectoAgrupacionGastoData, ProyectoAgrupacionGastoActionService } from '../proyecto-agrupacion-gasto.action.service';

import { ProyectoAgrupacionGastoCrearComponent } from './proyecto-agrupacion-gasto-crear.component';

describe('ProyectoAgrupacionGastoCrearComponent', () => {
  let component: ProyectoAgrupacionGastoCrearComponent;
  let fixture: ComponentFixture<ProyectoAgrupacionGastoCrearComponent>;

  const routeData: Data = {
    [PROYECTO_AGRUPACION_GASTO_DATA_KEY]: { proyecto: { id: 1 } as IProyecto } as IProyectoAgrupacionGastoData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('0', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProyectoAgrupacionGastoCrearComponent,
        ActionFooterComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        FlexLayoutModule,
        SharedModule
      ],
      providers: [
        ProyectoAgrupacionGastoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProyectoAgrupacionGastoCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
