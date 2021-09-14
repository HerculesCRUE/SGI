import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PROYECTO_AGRUPACION_GASTO_DATA_KEY } from '../../../proyecto-agrupacion-gasto/proyecto-agrupacion-gasto-data.resolver';
import { IProyectoAgrupacionGastoData, ProyectoAgrupacionGastoActionService } from '../../../proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.action.service';
import { PROYECTO_DATA_KEY } from '../../proyecto-data.resolver';
import { IProyectoData, ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoAgrupacionesGastoComponent } from './proyecto-agrupaciones-gasto.component';


describe('ProyectoAgrupacionesGastoComponent', () => {
  let component: ProyectoAgrupacionesGastoComponent;
  let fixture: ComponentFixture<ProyectoAgrupacionesGastoComponent>;
  const routeData: Data = {
    [PROYECTO_DATA_KEY]: {} as IProyectoData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ProyectoAgrupacionesGastoComponent],
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
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        ProyectoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProyectoAgrupacionesGastoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
