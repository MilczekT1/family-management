package pl.konradboniecki.budget.familymanagement.cucumber.commons;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

@Data
@Component
public class SharedData {

    private ResponseEntity<?> lastResponseEntity;

    private static Map<String, String> userNameToOwnerIdMap = new HashMap<>();

    public static String getUserIdForName(String userName) {
        userNameToOwnerIdMap.putIfAbsent(userName, UUID.randomUUID().toString());
        return userNameToOwnerIdMap.get(userName);
    }

    /*

     */
    private static Map<String, String> userNameToFamilyIdMap = new HashMap<>();

    public void addUserNameToFamilyIdEntry(String userName, String familyId) {
        userNameToFamilyIdMap.put(userName, familyId);
    }

    public String getFamilyIdForUserName(String userName) {
        return userNameToFamilyIdMap.get(userName);
    }

    /*

     */
    private static List<String> familyIdsToDelete = new ArrayList<>();

    public void addFamilyIdToDelete(String familyId) {
        familyIdsToDelete.add(familyId);
    }

    public List<String> getFamilyIdsToDelete() {
        return familyIdsToDelete;
    }

    private static List<String> invitationIdsToDelete = new ArrayList<>();

    public void addInvitationIdToDelete(String invitationId) {
        invitationIdsToDelete.add(invitationId);
    }

    public List<String> getInvitationsIdsToDelete() {
        return invitationIdsToDelete;
    }

    private static Map<String, String> replacementsToUuidMap = new HashMap<>();

    public static String getUuidForReplacement(String stringWithBrackets) {
        replacementsToUuidMap.putIfAbsent(stringWithBrackets, UUID.randomUUID().toString());
        return replacementsToUuidMap.get(stringWithBrackets);
    }

    private static Map<String, String> invitationCodeToInvitationIdMap = new HashMap<>();

    public void addInvitationCodeToInvitationIdEntry(String code, String invitationId) {
        invitationCodeToInvitationIdMap.putIfAbsent(code, invitationId);
    }

    public String getInvitationIdForInvitationCode(String invitationCode) {
        return invitationCodeToInvitationIdMap.get(invitationCode);
    }

    public void clearAfterScenario() {
        familyIdsToDelete.clear();
        userNameToFamilyIdMap.clear();
        userNameToOwnerIdMap.clear();

        invitationIdsToDelete.clear();
        invitationCodeToInvitationIdMap.clear();
        replacementsToUuidMap.clear();
    }
}
