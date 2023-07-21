import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { PROYECTO_SOCIO_DATA_KEY } from '../../proyecto-socio-data.resolver';
import { IProyectoSocioData, ProyectoSocioActionService } from '../../proyecto-socio.action.service';
import { ProyectoSocioDatosGeneralesComponent } from './proyecto-socio-datos-generales.component';

describe('ProyectoSocioDatosGeneralesComponent', () => {
  let component: ProyectoSocioDatosGeneralesComponent;
  let fixture: ComponentFixture<ProyectoSocioDatosGeneralesComponent>;
  const routeData: Data = {
    [PROYECTO_SOCIO_DATA_KEY]: {
      proyecto: {
        id: 1
      }
    } as IProyectoSocioData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProyectoSocioDatosGeneralesComponent
      ],
      imports: [
        CspSharedModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        SharedModule,
        SgempSharedModule
      ],
      providers: [
        ProyectoSocioActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProyectoSocioDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
