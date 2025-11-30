package hms.dao;

import hms.model.Patient;

import java.util.List;

public class PatientDAO extends FileBasedDAO<Patient, String> {

    public PatientDAO() {
        super("data/patients.txt");
    }

    @Override
    protected Patient parseEntity(String line) {
        try {
            String[] data = line.split(",");
            if (data.length == 5) {
                return new Patient(
                        data[0],
                        data[1],
                        Integer.parseInt(data[2]),
                        data[3],
                        data[4]
                );
            } else if (data.length >= 10) {
                return new Patient(
                        data[0],
                        data[1],
                        Integer.parseInt(data[2]),
                        data[3],
                        data[4],
                        data[5],
                        data[6],
                        data[7],
                        data[8],
                        data[9]
                );
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String entityToFileString(Patient patient) {
        return patient.toFileString();
    }

    @Override
    protected String getIdFromEntity(Patient patient) {
        return patient.getId();
    }

    @Override
    protected boolean matchesProperty(Patient patient, String propertyName, Object value) {
        if (value == null) return false;

        switch (propertyName.toLowerCase()) {
            case "идентификатор":
                return patient.getId().equals(value.toString());
            case "имя":
                return patient.getName().toLowerCase().contains(value.toString().toLowerCase());
            case "возраст":
                if (value instanceof Integer) {
                    return patient.getAge() == (Integer) value;
                }
                try {
                    return patient.getAge() == Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    return false;
                }
            case "болезнь":
                return patient.getDisease() != null &&
                        patient.getDisease().toLowerCase().contains(value.toString().toLowerCase());
            case "контакт":
                return patient.getContact() != null &&
                        patient.getContact().contains(value.toString());
            default:
                return false;
        }
    }

    public List<Patient> findByDisease(String disease) {
        return findByProperty("болезнь", disease);
    }

    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }

        String searchTerm = query.toLowerCase();
        return findByPredicate(patient ->
                (patient.getId() != null && patient.getId().toLowerCase().contains(searchTerm)) ||
                        (patient.getName() != null && patient.getName().toLowerCase().contains(searchTerm)) ||
                        (patient.getDisease() != null && patient.getDisease().toLowerCase().contains(searchTerm)) ||
                        (patient.getContact() != null && patient.getContact().contains(searchTerm))
        );
    }
}
