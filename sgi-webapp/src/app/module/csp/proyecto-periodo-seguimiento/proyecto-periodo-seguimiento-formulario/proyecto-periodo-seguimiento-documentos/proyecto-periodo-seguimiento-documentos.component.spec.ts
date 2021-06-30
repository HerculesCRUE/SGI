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
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PROYECTO_PERIODO_SEGUIMIENTO_DATA_KEY } from '../../proyecto-periodo-seguimiento-data.resolver';
import {
  IProyectoPeriodoSeguimientoData,
  ProyectoPeriodoSeguimientoActionService
} from '../../proyecto-periodo-seguimiento.action.service';
import { ProyectoPeriodoSeguimientoDocumentosComponent } from './proyecto-periodo-seguimiento-documentos.component';

describe('ProyectoPeriodoSeguimientoDocumentosComponent', () => {
  let component: ProyectoPeriodoSeguimientoDocumentosComponent;
  let fixture: ComponentFixture<ProyectoPeriodoSeguimientoDocumentosComponent>;
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
        ProyectoPeriodoSeguimientoDocumentosComponent
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
        SgiAuthModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        ProyectoPeriodoSeguimientoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);
    fixture = TestBed.createComponent(ProyectoPeriodoSeguimientoDocumentosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
