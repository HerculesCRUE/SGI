import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ActionFooterButtonComponent } from './action-footer-button.component';
import TestUtils from '@core/utils/test-utils';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { MaterialDesignModule } from '@material/material-design.module';

describe('ActionFooterButtonComponent', () => {
  let component: ActionFooterButtonComponent;
  let fixture: ComponentFixture<ActionFooterButtonComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ActionFooterButtonComponent],
      imports: [
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionFooterButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});