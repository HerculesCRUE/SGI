import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PROYECTO_PERIODO_SEGUIMIENTO_DATA_KEY } from '../proyecto-periodo-seguimiento-data.resolver';
import { IProyectoPeriodoSeguimientoData, ProyectoPeriodoSeguimientoActionService } from '../proyecto-periodo-seguimiento.action.service';
import { ProyectoPeriodoSeguimientoEditarComponent } from './proyecto-periodo-seguimiento-editar.component';

describe('ProyectoPeriodoSeguimientoEditarComponent', () => {
  let component: ProyectoPeriodoSeguimientoEditarComponent;
  let fixture: ComponentFixture<ProyectoPeriodoSeguimientoEditarComponent>;
  const routeData: Data = {
    [PROYECTO_PERIODO_SEGUIMIENTO_DATA_KEY]: {
      proyecto: {
        id: 1,
        modeloEjecucion: {
          id: 1
        }
      },
      proyectoPeriodosSeguimiento: [],
      readonly: false
    } as IProyectoPeriodoSeguimientoData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  const state = {
    convocatoriaConceptoGastoId: 1
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProyectoPeriodoSeguimientoEditarComponent,
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
        ProyectoPeriodoSeguimientoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);
    fixture = TestBed.createComponent(ProyectoPeriodoSeguimientoEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
