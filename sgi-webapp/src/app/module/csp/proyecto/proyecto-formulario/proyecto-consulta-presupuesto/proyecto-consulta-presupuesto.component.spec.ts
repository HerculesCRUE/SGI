import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PROYECTO_DATA_KEY } from '../../proyecto-data.resolver';
import { IProyectoData, ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoConsultaPresupuestoComponent } from './proyecto-consulta-presupuesto.component';

describe('ProyectoConsultaPresupuestoComponent', () => {
  let component: ProyectoConsultaPresupuestoComponent;
  let fixture: ComponentFixture<ProyectoConsultaPresupuestoComponent>;

  const routeData: Data = {
    [PROYECTO_DATA_KEY]: {} as IProyectoData
  };

  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProyectoConsultaPresupuestoComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        LoggerTestingModule,
        SharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        ProyectoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProyectoConsultaPresupuestoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
