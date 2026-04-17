package vn.homthugopy.todo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.homthugopy.todo.entity.Todo;
import vn.homthugopy.todo.service.TodoService;

@RestController
public class TodoController {

	// Khi lam viec nhieu class voi nhau
	private final TodoService todoService;

	public TodoController(TodoService todoService) {
		this.todoService = todoService;
	}
	// .

	@GetMapping("/todos")
	public ResponseEntity<List<Todo>> getTodos() {

		List<Todo> listTodo = this.todoService.handleGetTodo();
		return ResponseEntity.ok().body(listTodo);
	}

	@GetMapping("/todos/{id}")
	public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {

		Todo todoData = this.todoService.getTodoById(id);
		return ResponseEntity.ok().body(todoData);
	}

	@PostMapping("/todos")
	public ResponseEntity<Todo> createTodo(@RequestBody Todo input) {
//		Todo myTodo = new Todo("quanglam", true);
		Todo newTodo = this.todoService.handleCreateTodo(input);

		return ResponseEntity.status(HttpStatus.CREATED).body(newTodo);
	}

	@PutMapping("/todos/{id}")
	public ResponseEntity<String> updateTodo(@PathVariable Long id, @RequestBody Todo input) {
		this.todoService.handleUpdateTodo(id, input);
		return ResponseEntity.ok().body("update succeed");
	}

	@DeleteMapping("/todos/{id}")
	public ResponseEntity<String> deleteTodo(@PathVariable Long id) {
		this.todoService.handleDeleteTodo(id);
		return ResponseEntity.ok().body("delete a todo with " + id);
	}
}
