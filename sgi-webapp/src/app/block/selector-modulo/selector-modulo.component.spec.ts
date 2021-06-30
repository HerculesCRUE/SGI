import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { SelectorModuloComponent } from './selector-modulo.component';
import { LoggerTestingModule } from 'ngx-logger/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { MatDialogRef } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';

describe('SelectorModuloComponent', () => {
  let component: SelectorModuloComponent;
  let fixture: ComponentFixture<SelectorModuloComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SelectorModuloComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        LoggerTestingModule,
        SgiAuthModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectorModuloComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
