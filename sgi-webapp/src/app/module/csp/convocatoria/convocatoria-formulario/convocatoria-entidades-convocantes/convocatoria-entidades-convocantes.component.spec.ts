import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';

import { ConvocatoriaEntidadesConvocantesComponent } from './convocatoria-entidades-convocantes.component';

describe('ConvocatoriaEntidadesConvocantesComponent', () => {
  let component: ConvocatoriaEntidadesConvocantesComponent;
  let fixture: ComponentFixture<ConvocatoriaEntidadesConvocantesComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaEntidadesConvocantesComponent
      ],
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        FormsModule,
        ReactiveFormsModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        MaterialDesignModule,
        BrowserAnimationsModule
      ],
      providers: [
        ConvocatoriaActionService,
        SgiAuthService
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaEntidadesConvocantesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
