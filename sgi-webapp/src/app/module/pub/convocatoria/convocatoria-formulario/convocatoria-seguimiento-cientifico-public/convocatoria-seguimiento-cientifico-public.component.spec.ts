import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';

import { ConvocatoriaSeguimientoCientificoPublicComponent } from './convocatoria-seguimiento-cientifico-public.component';

describe('ConvocatoriaPublicSeguimientoCientificoComponent', () => {
  let component: ConvocatoriaSeguimientoCientificoPublicComponent;
  let fixture: ComponentFixture<ConvocatoriaSeguimientoCientificoPublicComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConvocatoriaSeguimientoCientificoPublicComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
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
    fixture = TestBed.createComponent(ConvocatoriaSeguimientoCientificoPublicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
