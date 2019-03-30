package pl.konradboniecki.budget.familymanagement.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@Document("family")
@NoArgsConstructor
public class Family implements Serializable {

    @Id
    @Indexed(unique = true)
    private String id;
    @Indexed(unique = true)
    private String ownerId;
    @Indexed(unique = true)
    private String budgetId;
    private String title;

    public Family mergeFamilies(Family family) {
        if (family.getId() != null) {
            setId(family.getId());
        }
        if (family.getOwnerId() != null) {
            setOwnerId(family.getOwnerId());
        }
        if (family.getBudgetId() != null) {
            setBudgetId(family.getBudgetId());
        }
        if (family.getTitle() != null) {
            setTitle(family.getTitle());
        }
        return this;
    }
}
