import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ActionFragmentLinkItemComponent } from './action-fragment-link-item.component';
import TestUtils from '@core/utils/test-utils';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { RouterTestingModule } from '@angular/router/testing';

describe('ActionFragmentLinkItemComponent', () => {
  let component: ActionFragmentLinkItemComponent;
  let fixture: ComponentFixture<ActionFragmentLinkItemComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        RouterTestingModule
      ],
      declarations: [ActionFragmentLinkItemComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionFragmentLinkItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
