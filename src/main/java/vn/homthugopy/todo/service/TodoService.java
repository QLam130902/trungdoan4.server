package vn.homthugopy.todo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.homthugopy.todo.entity.Todo;
import vn.homthugopy.todo.repository.TodoRepository;

@Service
public class TodoService {

	private final TodoRepository todoRepository;

	public TodoService(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	public Todo getTodoById(Long id) {
		Optional<Todo> todoOptional = this.todoRepository.findById(id);
		return todoOptional.isPresent() ? todoOptional.get() : null;
	}

	// thuc hien o day
	public Todo handleCreateTodo(Todo todo) {

		Todo createTodo = this.todoRepository.save(todo);
		return createTodo;
	}

	public List<Todo> handleGetTodo() {
		return this.todoRepository.findAll();
	}

	public void handleUpdateTodo(Long id, Todo inputTodo) {
		Optional<Todo> todoOptional = this.todoRepository.findById(id);
		if (todoOptional.isPresent()) {
			Todo currentTodo = todoOptional.get();

			currentTodo.setCompleted(inputTodo.isCompleted());
			currentTodo.setUsername(inputTodo.getUsername());

			this.todoRepository.save(currentTodo);
		}
	}

	public void handleDeleteTodo(Long id) {
		this.todoRepository.deleteById(id);
	}

}
