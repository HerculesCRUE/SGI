import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';

import { ConvocatoriaEntidadesFinanciadorasPublicComponent } from './convocatoria-entidades-financiadoras-public.component';

describe('ConvocatoriaEntidadesFinanciadorasPublicComponent', () => {
  let component: ConvocatoriaEntidadesFinanciadorasPublicComponent;
  let fixture: ComponentFixture<ConvocatoriaEntidadesFinanciadorasPublicComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaEntidadesFinanciadorasPublicComponent
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
      ],
      providers: [
        ConvocatoriaPublicActionService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaEntidadesFinanciadorasPublicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
