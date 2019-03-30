package pl.konradboniecki.budget.familymanagement.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Document("invitation")
public class Invitation {
    @Id
    @Indexed(unique = true)
    private String id;

    private String familyId;
    private String email;
    private String invitationCode;
    private Instant created;
    private Boolean registered;
}
