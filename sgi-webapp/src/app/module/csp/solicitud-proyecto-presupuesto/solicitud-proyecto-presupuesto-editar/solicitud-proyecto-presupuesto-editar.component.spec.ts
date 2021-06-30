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
import { SOLICITUD_PROYECTO_PRESUPUESTO_DATA_KEY } from '../solicitud-proyecto-presupuesto-data.resolver';
import { ISolicitudProyectoPresupuestoData, SolicitudProyectoPresupuestoActionService } from '../solicitud-proyecto-presupuesto.action.service';
import { SolicitudProyectoPresupuestoEditarComponent } from './solicitud-proyecto-presupuesto-editar.component';

describe('SolicitudProyectoPresupuestoEditarComponent', () => {
  let component: SolicitudProyectoPresupuestoEditarComponent;
  let fixture: ComponentFixture<SolicitudProyectoPresupuestoEditarComponent>;
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
      declarations: [
        SolicitudProyectoPresupuestoEditarComponent,
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
        SolicitudProyectoPresupuestoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProyectoPresupuestoEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
