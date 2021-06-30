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
import { SOLICITUD_PROYECTO_SOCIO_DATA_KEY } from '../solicitud-proyecto-socio-data.resolver';
import { ISolicitudProyectoSocioData, SolicitudProyectoSocioActionService } from '../solicitud-proyecto-socio.action.service';
import { SolicitudProyectoSocioEditarComponent } from './solicitud-proyecto-socio-editar.component';

describe('SolicitudProyectoSocioEditarComponent', () => {
  let component: SolicitudProyectoSocioEditarComponent;
  let fixture: ComponentFixture<SolicitudProyectoSocioEditarComponent>;
  const routeData: Data = {
    [SOLICITUD_PROYECTO_SOCIO_DATA_KEY]: {
      solicitudProyecto: {
        id: 1
      },
      readonly: false
    } as ISolicitudProyectoSocioData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudProyectoSocioEditarComponent,
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
        SolicitudProyectoSocioActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProyectoSocioEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
