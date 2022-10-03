import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SexoPublicService } from './sexo-public.service';

describe('SexoPublicService', () => {
  let service: SexoPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ],
      providers: [
        SgiAuthService
      ],
    });
    service = TestBed.inject(SexoPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
