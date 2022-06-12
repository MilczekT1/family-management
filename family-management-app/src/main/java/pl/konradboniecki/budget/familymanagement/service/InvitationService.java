package pl.konradboniecki.budget.familymanagement.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.familymanagement.exceptions.InvitationNotFoundException;
import pl.konradboniecki.budget.familymanagement.model.Invitation;
import pl.konradboniecki.budget.openapi.dto.model.*;
import pl.konradboniecki.chassis.exceptions.BadRequestException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class InvitationService {

    private static final String PARAM_EMAIL_KEY = "email";
    private static final String PARAM_FAMILY_ID_KEY = "familyId";

    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;

    public void removeInvitationByIdOrThrow(String invitationId) {
        if (existsById(invitationId)) {
            invitationRepository.deleteById(invitationId);
        } else {
            throw new InvitationNotFoundException("Invitation with id: " + invitationId + " not found.");
        }
    }

    private boolean existsById(String id) {
        return invitationRepository.existsById(id);
    }

    public OASCreatedInvitation saveInvitationOrThrow(OASInvitationCreation oasInvitationCreation) {
        final var invitationToSave = invitationMapper.toInvitation(oasInvitationCreation);
        invitationToSave.setId(UUID.randomUUID().toString());
        invitationToSave.setCreated(Instant.now());
        return invitationMapper.toOASCreatedInvitation(invitationRepository.save(invitationToSave));
    }

    public OASInvitationPage findAllBy(@NonNull Map<String, String> params, Pageable pageable) {
        checkParamsOrThrow(params);
        Page<Invitation> invitationPage;
        if (containsEmailAndFamilyId(params)) {
            String email = params.get(PARAM_EMAIL_KEY);
            String familyId = params.get(PARAM_FAMILY_ID_KEY);
            invitationPage = findAllByEmailAndFamilyId(email, familyId, pageable);
        } else if (params.containsKey(PARAM_FAMILY_ID_KEY)) {
            invitationPage = findAllByFamilyId(params.get(PARAM_FAMILY_ID_KEY), pageable);
        } else if (params.containsKey(PARAM_EMAIL_KEY)) {
            invitationPage = findAllByEmail(params.get(PARAM_EMAIL_KEY), pageable);
        } else {
            throw new BadRequestException("Invalid request parameters. Use familyId, email or both");
        }
        final var items = invitationPage.get()
                .map(invitationMapper::toOASInvitation)
                .collect(Collectors.toList());
        final var paginationMetadata = new OASPaginationMetadata()
                .elements(invitationPage.getNumberOfElements())
                .pageSize(pageable.getPageSize())
                .page(pageable.getPageNumber())
                .totalPages(invitationPage.getTotalPages())
                .totalElements((int) invitationPage.getTotalElements());
        return new OASInvitationPage()
                .items(items)
                .meta(paginationMetadata);
    }

    private void checkParamsOrThrow(Map<String, String> params) {
        int size = params.size();
        if (size != 1 && size != 2) {
            throw new BadRequestException("Invalid request parameters. Use familyId, email or both");
        }
    }

    private boolean containsEmailAndFamilyId(Map<String, String> params) {
        return params.size() == 2 &&
                params.containsKey(PARAM_FAMILY_ID_KEY) &&
                params.containsKey(PARAM_EMAIL_KEY);
    }

    public Page<Invitation> findAllByEmail(String email, Pageable pageable) {
        return invitationRepository.findAllByEmail(email, pageable);
    }

    public Page<Invitation> findAllByFamilyId(String familyId, Pageable pageable) {
        return invitationRepository.findAllByFamilyId(familyId, pageable);
    }

    public Page<Invitation> findAllByEmailAndFamilyId(String email, String familyId, Pageable pageable) {
        return invitationRepository.findAllByEmailAndFamilyId(email, familyId, pageable);
    }

    public OASInvitation findById(String invitationId) throws BadRequestException {
        return invitationRepository.findById(invitationId)
                .map(invitationMapper::toOASInvitation)
                .orElseThrow(() -> new InvitationNotFoundException("invitation with id: " + invitationId + " not found."));
    }
}
