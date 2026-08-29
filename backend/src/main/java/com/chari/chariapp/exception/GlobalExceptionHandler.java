package com.chari.chariapp.exception;

import com.chari.chariapp.account.application.AccountAlreadyExistsException;
import com.chari.chariapp.account.application.EnrollmentProofUnavailableException;
import com.chari.chariapp.account.application.EnrollmentUnavailableException;
import com.chari.chariapp.account.application.InvalidEnrollmentOtpException;
import com.chari.chariapp.account.application.InvalidCredentialsException;
import com.chari.chariapp.account.application.InvalidRefreshTokenException;
import com.chari.chariapp.account.application.WeakPasswordException;
import com.chari.chariapp.citizen.application.CitizenPhoneVerificationException;
import com.chari.chariapp.request.application.PassportRequestConflictException;
import com.chari.chariapp.request.application.AttachmentUploadException;
import com.chari.chariapp.request.domain.PassportRequestTransitionException;
import com.chari.chariapp.identityrequest.application.NationalIdentityRequestConflictException;
import com.chari.chariapp.identityrequest.domain.NationalIdentityRequestTransitionException;
import com.chari.chariapp.birthrequest.application.BirthCertificateRequestConflictException;
import com.chari.chariapp.birthrequest.domain.BirthCertificateRequestTransitionException;
import com.chari.chariapp.appointment.application.AppointmentConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage(), 404));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage(), 400));
    }

    @ExceptionHandler({
            AccountAlreadyExistsException.class,
            EnrollmentProofUnavailableException.class,
            EnrollmentUnavailableException.class,
            CitizenPhoneVerificationException.class,
            InvalidEnrollmentOtpException.class,
            WeakPasswordException.class,
            AttachmentUploadException.class,
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleEnrollmentInput(Exception ex) {
        log.warn("Client input rejected: {} ({})", ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Unable to complete enrollment", HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler({InvalidCredentialsException.class, InvalidRefreshTokenException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationFailure(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Authentication failed", HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler({
            PassportRequestConflictException.class,
            PassportRequestTransitionException.class,
            NationalIdentityRequestConflictException.class,
            NationalIdentityRequestTransitionException.class,
            BirthCertificateRequestConflictException.class,
            BirthCertificateRequestTransitionException.class,
            AppointmentConflictException.class
    })
    public ResponseEntity<ErrorResponse> handlePassportRequestConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Something went wrong", 500));
    }
}
