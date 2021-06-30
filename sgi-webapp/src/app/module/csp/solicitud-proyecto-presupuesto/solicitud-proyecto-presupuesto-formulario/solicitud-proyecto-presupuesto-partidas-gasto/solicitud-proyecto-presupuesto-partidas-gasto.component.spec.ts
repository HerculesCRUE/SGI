import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SOLICITUD_PROYECTO_PRESUPUESTO_DATA_KEY } from '../../solicitud-proyecto-presupuesto-data.resolver';
import { ISolicitudProyectoPresupuestoData, SolicitudProyectoPresupuestoActionService } from '../../solicitud-proyecto-presupuesto.action.service';
import { SolicitudProyectoPresupuestoPartidasGastoComponent } from './solicitud-proyecto-presupuesto-partidas-gasto.component';

describe('SolicitudProyectoPresupuestoPartidasGastoComponent', () => {
  let component: SolicitudProyectoPresupuestoPartidasGastoComponent;
  let fixture: ComponentFixture<SolicitudProyectoPresupuestoPartidasGastoComponent>;
  const routeData: Data = {
    [SOLICITUD_PROYECTO_PRESUPUESTO_DATA_KEY]: {
      entidad: {
        empresa: {}
      },
      ajena: true,
      readonly: false
    } as ISolicitudProyectoPresupuestoData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SolicitudProyectoPresupuestoPartidasGastoComponent],
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
        SolicitudProyectoPresupuestoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProyectoPresupuestoPartidasGastoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
