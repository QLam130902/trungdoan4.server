package vn.homthugopy.todo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.homthugopy.todo.entity.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
	Optional<Todo> findByUsername(String userName);
}
