
import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaPublicService } from './convocatoria-public.service';

describe('ConvocatoriaPublicService', () => {
  let service: ConvocatoriaPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
