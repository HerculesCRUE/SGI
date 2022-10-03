import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SharedFormlyFormsModule } from 'src/app/esb/shared/formly-forms/shared-formly-forms.module';
import { EmpresaFormlyModalComponent, IEmpresaFormlyData } from './empresa-formly-modal.component';

describe('EmpresaFormlyModalComponent', () => {
  let component: EmpresaFormlyModalComponent;
  let fixture: ComponentFixture<EmpresaFormlyModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        EmpresaFormlyModalComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormlyFormsModule,
        SharedFormlyFormsModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: TestUtils.buildDialogActionMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: {} as IEmpresaFormlyData },

      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmpresaFormlyModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
