package sn.travel.rec_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception levee lorsqu'un voyageur tente de donner un second feedback pour le meme voyage.
 */
public class DuplicateFeedbackException extends RecServiceException {

    private static final String ERROR_CODE = "REC_004";

    public DuplicateFeedbackException() {
        super(
                "Vous avez deja donne un feedback pour ce voyage",
                ERROR_CODE,
                HttpStatus.CONFLICT
        );
    }
}
