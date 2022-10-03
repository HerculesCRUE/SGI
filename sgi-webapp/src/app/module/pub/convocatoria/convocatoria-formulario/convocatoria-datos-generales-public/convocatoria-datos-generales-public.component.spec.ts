import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { CspSharedModule } from 'src/app/module/csp/shared/csp-shared.module';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaDatosGeneralesPublicComponent } from './convocatoria-datos-generales-public.component';

describe('ConvocatoriaDatosGeneralesPublicComponent', () => {
  let component: ConvocatoriaDatosGeneralesPublicComponent;
  let fixture: ComponentFixture<ConvocatoriaDatosGeneralesPublicComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaDatosGeneralesPublicComponent
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
        SharedModule,
        CspSharedModule,
        SgempSharedModule
      ],
      providers: [
        ConvocatoriaPublicActionService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaDatosGeneralesPublicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
