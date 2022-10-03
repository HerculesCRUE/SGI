import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { UnidadGestionPublicService } from './unidad-gestion-public.service';

describe('UnidadGestionPublicService', () => {
  let service: UnidadGestionPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(UnidadGestionPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
