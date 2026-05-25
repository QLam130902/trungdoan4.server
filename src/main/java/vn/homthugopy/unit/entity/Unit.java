package vn.homthugopy.unit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "units")
public class Unit {

	@Id
	private String code;

	@Column(nullable = false)
	private String name;

	@Column(name = "parent_code")
	private String parentCode;

	public Unit() {
	}

	public Unit(String code, String name, String parentCode) {
		this.code = code;
		this.name = name;
		this.parentCode = parentCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
}
