package vn.homthugopy.unit.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.homthugopy.unit.entity.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, String> {
	List<Unit> findByParentCode(String parentCode);
}
