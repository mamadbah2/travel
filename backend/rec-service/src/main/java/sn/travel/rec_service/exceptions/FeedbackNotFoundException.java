package sn.travel.rec_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception levee lorsqu'un feedback n'est pas trouve.
 */
public class FeedbackNotFoundException extends RecServiceException {

    private static final String ERROR_CODE = "REC_001";

    public FeedbackNotFoundException(String identifier) {
        super(
                String.format("Feedback non trouve avec l'identifiant : %s", identifier),
                ERROR_CODE,
                HttpStatus.NOT_FOUND
        );
    }
}
