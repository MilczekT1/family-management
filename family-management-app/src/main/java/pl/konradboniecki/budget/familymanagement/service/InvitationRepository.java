package pl.konradboniecki.budget.familymanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.konradboniecki.budget.familymanagement.model.Invitation;

import java.util.Optional;

@Repository
public interface InvitationRepository extends MongoRepository<Invitation, String> {

    /**
     * /api/family-mgt/v1/invitations/{invitationId}
     *
     * @param invitationId
     */
    Optional<Invitation> findById(String invitationId);

    /**
     * /api/family-mgt/v1/invitations?email=XX&familyId=YY
     *
     * @param familyId
     */
    Page<Invitation> findAllByEmailAndFamilyId(String email, String familyId, Pageable pageable);

    /**
     * /api/family-mgt/v1/invitations?familyId=XX
     *
     * @param familyId
     */
    Page<Invitation> findAllByFamilyId(String familyId, Pageable pageable);

    /**
     * /api/family-mgt/v1/invitations?email=XX
     *
     * @param email
     */
    Page<Invitation> findAllByEmail(String email, Pageable pageable);

    /**
     * /api/family-mgt/v1/invitations/{invitationId}
     *
     * @param invitationId
     */
    void deleteById(String invitationId);

    /**
     * /api/family-mgt/v1/invitations
     *
     * @param invitation
     * @return
     */
    Invitation save(Invitation invitation);
}
