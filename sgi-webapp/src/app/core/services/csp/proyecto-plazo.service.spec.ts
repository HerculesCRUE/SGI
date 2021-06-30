import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ProyectoPlazoService } from './proyecto-plazo.service';

describe('ProyectoPlazoService', () => {
  let service: ProyectoPlazoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoPlazoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
