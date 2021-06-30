import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ActionFragmentMenuItemComponent } from './action-fragment-menu-item.component';
import TestUtils from '@core/utils/test-utils';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { RouterTestingModule } from '@angular/router/testing';

describe('ActionFragmentMenuItemComponent', () => {
  let component: ActionFragmentMenuItemComponent;
  let fixture: ComponentFixture<ActionFragmentMenuItemComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        RouterTestingModule
      ],
      declarations: [ActionFragmentMenuItemComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionFragmentMenuItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
