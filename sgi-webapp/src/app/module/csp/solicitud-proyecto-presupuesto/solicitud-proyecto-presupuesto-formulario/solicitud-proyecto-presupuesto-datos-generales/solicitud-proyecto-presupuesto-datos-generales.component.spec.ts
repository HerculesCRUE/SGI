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
import { SolicitudProyectoPresupuestoDatosGeneralesComponent } from './solicitud-proyecto-presupuesto-datos-generales.component';

describe('SolicitudProyectoPresupuestoDatosGeneralesComponent', () => {
  let component: SolicitudProyectoPresupuestoDatosGeneralesComponent;
  let fixture: ComponentFixture<SolicitudProyectoPresupuestoDatosGeneralesComponent>;
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
      declarations: [SolicitudProyectoPresupuestoDatosGeneralesComponent],
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
    fixture = TestBed.createComponent(SolicitudProyectoPresupuestoDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
