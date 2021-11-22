import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { InvencionInventorService } from './invencion-inventor.service';

describe('InvencionInventorService', () => {
  let service: InvencionInventorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(InvencionInventorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
