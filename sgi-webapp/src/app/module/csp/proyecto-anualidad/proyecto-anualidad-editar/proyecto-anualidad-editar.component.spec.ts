import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PROYECTO_ANUALIDAD_DATA_KEY } from '../proyecto-anualidad-data.resolver';
import { ProyectoAnualidadActionService } from '../proyecto-anualidad.action.service';

import { ProyectoAnualidadEditarComponent } from './proyecto-anualidad-editar.component';

describe('ProyectoAnualidadEditarComponent', () => {
  let component: ProyectoAnualidadEditarComponent;
  let fixture: ComponentFixture<ProyectoAnualidadEditarComponent>;

  const routeData: Data = {
    [PROYECTO_ANUALIDAD_DATA_KEY]: {
      proyecto: {
        id: 1
      }
    } as IProyectoAnualidad
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProyectoAnualidadEditarComponent],
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
        SharedModule
      ],
      providers: [
        ProyectoAnualidadActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProyectoAnualidadEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
