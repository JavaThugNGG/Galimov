package hms.dao;

import hms.model.Doctor;

import java.util.List;

public class DoctorDAO extends FileBasedDAO<Doctor, String> {

    public DoctorDAO() {
        super("data/doctors.txt");
    }

    @Override
    protected Doctor parseEntity(String line) {
        try {
            String[] data = line.split(",");
            if (data.length == 6) {
                return new Doctor(
                        data[0],
                        data[1],
                        Integer.parseInt(data[2]),
                        data[3],
                        data[4],
                        data[5]
                );
            } else if (data.length >= 11) {
                return new Doctor(
                        data[0],
                        data[1],
                        Integer.parseInt(data[2]),
                        data[3],
                        data[4],
                        data[5],
                        data[6],
                        data[7],
                        data[8],
                        data[9],
                        Double.parseDouble(data[10])
                );
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String entityToFileString(Doctor doctor) {
        return doctor.toFileString();
    }

    @Override
    protected String getIdFromEntity(Doctor doctor) {
        return doctor.getId();
    }

    @Override
    protected boolean matchesProperty(Doctor doctor, String propertyName, Object value) {
        if (value == null) return false;

        switch (propertyName.toLowerCase()) {
            case "идентификатор":
                return doctor.getId().equals(value.toString());
            case "имя":
                return doctor.getName().toLowerCase().contains(value.toString().toLowerCase());
            case "специализация":
                return doctor.getSpecialization() != null &&
                        doctor.getSpecialization().toLowerCase().contains(value.toString().toLowerCase());
            case "доступность":
                return doctor.getAvailability() != null &&
                        doctor.getAvailability().toLowerCase().contains(value.toString().toLowerCase());
            default:
                return false;
        }
    }

    public List<Doctor> findBySpecialization(String specialization) {
        return findByProperty("специализация", specialization);
    }

    public List<Doctor> searchDoctors(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }

        String searchTerm = query.toLowerCase();
        return findByPredicate(doctor ->
                (doctor.getId() != null && doctor.getId().toLowerCase().contains(searchTerm)) ||
                        (doctor.getName() != null && doctor.getName().toLowerCase().contains(searchTerm)) ||
                        (doctor.getSpecialization() != null && doctor.getSpecialization().toLowerCase().contains(searchTerm))
        );
    }
}
