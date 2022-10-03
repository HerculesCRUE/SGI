import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaEntidadesConvocantesPublicComponent } from './convocatoria-entidades-convocantes-public.component';

describe('ConvocatoriaEntidadesConvocantesPublicComponent', () => {
  let component: ConvocatoriaEntidadesConvocantesPublicComponent;
  let fixture: ComponentFixture<ConvocatoriaEntidadesConvocantesPublicComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaEntidadesConvocantesPublicComponent
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
        ConvocatoriaPublicActionService
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaEntidadesConvocantesPublicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
