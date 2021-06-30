import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ActionFragmentMenuGroupComponent } from './action-fragment-menu-group.component';
import TestUtils from '@core/utils/test-utils';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ActionFragmentMenuGroupComponent', () => {
  let component: ActionFragmentMenuGroupComponent;
  let fixture: ComponentFixture<ActionFragmentMenuGroupComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        RouterTestingModule,
        NoopAnimationsModule
      ],
      declarations: [ActionFragmentMenuGroupComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionFragmentMenuGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
