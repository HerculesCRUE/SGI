import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { InvencionActionService } from '../../invencion.action.service';
import { InvencionInventorComponent } from './invencion-inventor.component';

describe('InvencionInventorComponent', () => {
  let component: InvencionInventorComponent;
  let fixture: ComponentFixture<InvencionInventorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InvencionInventorComponent],
      imports: [
        SharedModule,
        TestUtils.getIdiomas(),
        BrowserAnimationsModule,
        MaterialDesignModule,
        FlexModule,
        LoggerTestingModule,
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        InvencionActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvencionInventorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
