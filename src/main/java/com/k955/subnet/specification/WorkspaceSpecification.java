package com.k955.subnet.specification;

import com.k955.subnet.entity.Workspace;
import com.k955.subnet.entity.WorkspaceMember;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorkspaceSpecification {

    public static Specification<Workspace> filterBy(
            UUID userId, String slug, String search
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Subquery<Integer> membershipQuery = query.subquery(Integer.class);

            var membership = membershipQuery.from(WorkspaceMember.class);

            membershipQuery.select(cb.literal(1))
                    .where(
                            cb.equal(
                                    membership.get("workspace").get("id"),
                                    root.get("id")
                            ),
                            cb.equal(
                                    membership.get("user").get("id"),
                                    userId
                            )
                    );

            predicates.add(cb.exists(membershipQuery));

            if(slug != null && !slug.isBlank()) {
                predicates.add(cb.equal(root.get("slug"), slug));
            }

            if(search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("name")), pattern),
                                cb.like(cb.lower(root.get("description")), pattern)
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
