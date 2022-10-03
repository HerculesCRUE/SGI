import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CategoriaProfesionalPublicService } from './categoria-profesional-public.service';

describe('CategoriaProfesionalPublicService', () => {
  let service: CategoriaProfesionalPublicService;

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
    service = TestBed.inject(CategoriaProfesionalPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
