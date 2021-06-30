import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

import { ConfigService } from './config.service';

describe('ConfigService', () => {
  let service: ConfigService;

  // Allow http call mocking
  let http: HttpClient;
  let httpMock: HttpTestingController;

  // We store the service and the httpmock in variables we can access in every test and gets instanciated every time before a test runs
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);

    service = TestBed.inject(ConfigService);
  });

  afterEach(() => {
    // then: verify that there are not pending http calls
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
