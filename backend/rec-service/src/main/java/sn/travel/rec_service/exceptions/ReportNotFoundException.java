package sn.travel.rec_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception levee lorsqu'un signalement n'est pas trouve.
 */
public class ReportNotFoundException extends RecServiceException {

    private static final String ERROR_CODE = "REC_002";

    public ReportNotFoundException(String identifier) {
        super(
                String.format("Signalement non trouve avec l'identifiant : %s", identifier),
                ERROR_CODE,
                HttpStatus.NOT_FOUND
        );
    }
}
