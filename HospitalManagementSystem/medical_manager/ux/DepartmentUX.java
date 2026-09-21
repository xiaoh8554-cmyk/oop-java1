package medical_manager.ux;

import common.FileHandler;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class DepartmentUX {
	public static List<String[]> loadDepartments() {
		return new ArrayList<>(FileHandler.read(FileHandler.DEPARTMENTS));
	}

	public static List<String> loadDepartmentNames() {
		LinkedHashSet<String> names = new LinkedHashSet<>();
		for (String[] department : loadDepartments()) {
			if (department.length > 1 && !department[1].trim().isEmpty()) {
				names.add(department[1]);
			}
		}
		return new ArrayList<>(names);
	}

	public static List<String> loadSpecializations() {
		LinkedHashSet<String> specializations = new LinkedHashSet<>();
		for (String[] department : loadDepartments()) {
			if (department.length > 2 && !department[2].trim().isEmpty()) {
				specializations.add(department[2]);
			}
		}
		return new ArrayList<>(specializations);
	}

	public static List<String[]> filterDepartments(String departmentName, String specialization) {
		String nameFilter = value(departmentName).toLowerCase();
		String specializationFilter = value(specialization).toLowerCase();
		List<String[]> matches = new ArrayList<>();
		for (String[] department : loadDepartments()) {
			if (department.length < 4) {
				continue;
			}
			if (department[1].toLowerCase().contains(nameFilter)
					&& department[2].toLowerCase().contains(specializationFilter)) {
				matches.add(department);
			}
		}
		return matches;
	}

	public static void createDepartment(String name, String specialization, String doctor) {
		String cleanName = name == null ? "" : name.trim();
		String cleanDoctor = value(doctor);
		if (cleanName.isEmpty()) {
			throw new IllegalArgumentException("Department name is required.");
		}
		for (String[] department : loadDepartments()) {
			if (department.length > 1 && department[1].equalsIgnoreCase(cleanName)) {
				throw new IllegalArgumentException("Department name already exists.");
			}
		}
		validateDoctorAssignment("", cleanDoctor);
		String id = FileHandler.nextId(FileHandler.DEPARTMENTS, "DEP", 3);
		FileHandler.append(FileHandler.DEPARTMENTS,
				new String[]{id, cleanName, value(specialization), cleanDoctor});
	}

	public static void updateDepartment(String id, String name, String specialization, String doctor) {
		String cleanName = name == null ? "" : name.trim();
		String cleanDoctor = value(doctor);
		if (cleanName.isEmpty()) {
			throw new IllegalArgumentException("Department name is required.");
		}
		List<String[]> departments = loadDepartments();
		for (String[] department : departments) {
			if (department.length > 1 && department[0].equals(id)) {
				continue;
			}
			if (department.length > 1 && department[1].equalsIgnoreCase(cleanName)) {
				throw new IllegalArgumentException("Department name already exists.");
			}
		}
		validateDoctorAssignment(id, cleanDoctor);
		boolean updated = false;
		for (String[] department : departments) {
			if (department.length > 0 && department[0].equals(id)) {
				department[1] = cleanName;
				department[2] = value(specialization);
				department[3] = cleanDoctor;
				updated = true;
				break;
			}
		}
		if (!updated) {
			throw new IllegalArgumentException("Department was not found.");
		}
		FileHandler.writeAll(FileHandler.DEPARTMENTS, departments);
	}

	public static void deleteDepartment(String id) {
		List<String[]> departments = loadDepartments();
		if (!departments.removeIf(department -> department.length > 0 && department[0].equals(id))) {
			throw new IllegalArgumentException("Department was not found.");
		}
		FileHandler.writeAll(FileHandler.DEPARTMENTS, departments);
	}

	private static String value(String text) {
		return text == null ? "" : text.trim();
	}

	private static void validateDoctorAssignment(String departmentId, String doctor) {
		if (doctor.isEmpty()) {
			throw new IllegalArgumentException("Doctor name is required.");
		}
		String normalizedDoctor = normalizeDoctor(doctor);
		for (String[] department : loadDepartments()) {
			if (department.length > 3
					&& !department[0].equals(departmentId)
					&& normalizeDoctor(department[3]).equals(normalizedDoctor)) {
				throw new IllegalArgumentException(
						"This doctor is already assigned to another department.");
			}
		}
	}

	private static String normalizeDoctor(String doctor) {
		String normalized = value(doctor).toLowerCase();
		return normalized.startsWith("dr.") ? normalized.substring(3).trim() : normalized;
	}
}
