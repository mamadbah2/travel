package sn.travel.rec_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception levee lorsqu'un utilisateur tente de modifier un feedback qui ne lui appartient pas.
 */
public class UnauthorizedFeedbackException extends RecServiceException {

    private static final String ERROR_CODE = "REC_003";

    public UnauthorizedFeedbackException() {
        super(
                "Vous n'etes pas autorise a modifier ce feedback",
                ERROR_CODE,
                HttpStatus.FORBIDDEN
        );
    }
}
