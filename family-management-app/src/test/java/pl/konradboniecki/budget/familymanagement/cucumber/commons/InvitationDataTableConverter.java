package pl.konradboniecki.budget.familymanagement.cucumber.commons;

import io.cucumber.java.DataTableType;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitation;

import java.time.Instant;
import java.util.Map;

public class InvitationDataTableConverter {

    private static final String ID_KEY = "id";
    private static final String FAMILY_ID_KEY = "familyId";
    private static final String EMAIL_KEY = "email";
    private static final String INVITATION_CODE_KEY = "invitationCode";
    private static final String REGISTERED_KEY = "registered";
    private static final String CREATED_KEY = "created";

    /*
        | familyId      | email          | invitationCode   | created | registered |
        | {{family1id}} | test1@mail.com | {{invitCodeId1}} | {{now}} | false      |
     */
    @DataTableType
    public OASInvitation invitationRow(Map<String, String> entry) {
        String id = processId(entry);
        String familyId = processFamilyId(entry);
        String email = processEmail(entry);
        String invitationCode = processInvitationCode(entry);
        Instant created = processCreated(entry);
        Boolean registered = processRegistered(entry);
        return new OASInvitation()
                .id(id)
                .familyId(familyId)
                .email(email)
                .invitationCode(invitationCode)
                .created(created)
                .registered(registered);
    }

    private String processId(Map<String, String> entry) {
        return entry.getOrDefault(ID_KEY, null);
    }

    private String processFamilyId(Map<String, String> entry) {
        if (entry.containsKey(FAMILY_ID_KEY)) {
            String familyIdEntry = entry.get(FAMILY_ID_KEY);
            return String.valueOf(SharedData.getUuidForReplacement(familyIdEntry));
        } else {
            return null;
        }
    }

    private String processEmail(Map<String, String> entry) {
        return entry.getOrDefault(EMAIL_KEY, null);
    }

    private String processInvitationCode(Map<String, String> entry) {
        if (entry.containsKey(INVITATION_CODE_KEY)) {
            String invitationIdEntry = entry.get(INVITATION_CODE_KEY);
            return String.valueOf(SharedData.getUuidForReplacement(invitationIdEntry));
        } else {
            return null;
        }
    }

    private Instant processCreated(Map<String, String> entry) {
        if (entry.containsKey(CREATED_KEY)) {
            if (entry.get(CREATED_KEY).equals("{{now}}")) {
                return Instant.now();
            } else {
                return Instant.parse(entry.getOrDefault(CREATED_KEY, null));
            }
        } else {
            return null;
        }
    }

    private Boolean processRegistered(Map<String, String> entry) {
        return Boolean.valueOf(entry.getOrDefault(REGISTERED_KEY, null));
    }

    private String removeBracketsIfReplacement(String value) {
        if (value.startsWith("{{") && value.endsWith("}}")) {
            return value.substring(2, value.length() - 2);
        } else {
            return value;
        }
    }
}
