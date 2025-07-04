package tn.smartech.smarteckhrapptasks.repositories.Survey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.smartech.smarteckhrapptasks.entities.Survey.Response;


@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
}
