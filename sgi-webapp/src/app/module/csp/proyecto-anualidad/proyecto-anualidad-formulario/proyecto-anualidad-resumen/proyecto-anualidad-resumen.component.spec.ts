import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PROYECTO_ANUALIDAD_DATA_KEY } from '../../proyecto-anualidad-data.resolver';
import { ProyectoAnualidadActionService } from '../../proyecto-anualidad.action.service';
import { ProyectoAnualidadResumenComponent } from './proyecto-anualidad-resumen.component';

describe('ProyectoAnualidadResumenComponent', () => {
  let component: ProyectoAnualidadResumenComponent;
  let fixture: ComponentFixture<ProyectoAnualidadResumenComponent>;

  const routeData: Data = {
    [PROYECTO_ANUALIDAD_DATA_KEY]: {
      proyecto: {
        id: 1
      }
    } as IProyectoAnualidad
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ProyectoAnualidadResumenComponent],
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
        ProyectoAnualidadActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProyectoAnualidadResumenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
