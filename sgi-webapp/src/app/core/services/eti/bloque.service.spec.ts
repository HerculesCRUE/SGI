import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { BloqueService } from './bloque.service';

describe('BloqueService', () => {
  let service: BloqueService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ]
    });
    service = TestBed.inject(BloqueService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
