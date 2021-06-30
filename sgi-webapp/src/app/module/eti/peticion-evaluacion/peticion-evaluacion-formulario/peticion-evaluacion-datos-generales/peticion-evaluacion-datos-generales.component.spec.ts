import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PeticionEvaluacionActionService } from '../../peticion-evaluacion.action.service';
import { PeticionEvaluacionDatosGeneralesComponent } from './peticion-evaluacion-datos-generales.component';


describe('PeticionEvaluacionDatosGeneralesComponent', () => {
  let component: PeticionEvaluacionDatosGeneralesComponent;
  let fixture: ComponentFixture<PeticionEvaluacionDatosGeneralesComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [PeticionEvaluacionDatosGeneralesComponent],
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        SharedModule
      ],
      providers: [
        PeticionEvaluacionActionService,
        SgiAuthService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
