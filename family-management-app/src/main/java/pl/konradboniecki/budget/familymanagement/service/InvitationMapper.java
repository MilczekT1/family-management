package pl.konradboniecki.budget.familymanagement.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.familymanagement.model.Invitation;
import pl.konradboniecki.budget.openapi.dto.model.OASCreatedInvitation;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitation;
import pl.konradboniecki.budget.openapi.dto.model.OASInvitationCreation;

@Service
public class InvitationMapper {

    Invitation toInvitation(@NonNull OASInvitationCreation oasInvitationCreation) {
        return new Invitation()
                .setFamilyId(oasInvitationCreation.getFamilyId())
                .setEmail(oasInvitationCreation.getEmail())
                .setInvitationCode(oasInvitationCreation.getInvitationCode())
                .setRegistered(oasInvitationCreation.getRegistered());
    }

    public OASInvitation toOASInvitation(@NonNull Invitation invitation) {
        return new OASInvitation()
                .id(invitation.getId())
                .created(invitation.getCreated())
                .invitationCode(invitation.getInvitationCode())
                .email(invitation.getEmail())
                .familyId(invitation.getFamilyId())
                .registered(invitation.getRegistered());
    }

    OASCreatedInvitation toOASCreatedInvitation(@NonNull Invitation invitation) {
        return new OASCreatedInvitation()
                .id(invitation.getId())
                .created(invitation.getCreated())
                .invitationCode(invitation.getInvitationCode())
                .email(invitation.getEmail())
                .familyId(invitation.getFamilyId())
                .registered(invitation.getRegistered());
    }
}
