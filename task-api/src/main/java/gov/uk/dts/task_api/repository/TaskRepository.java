package gov.uk.dts.task_api.repository;

import gov.uk.dts.task_api.entity.TaskDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskDao, Long> {
}
